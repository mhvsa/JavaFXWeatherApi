package zad1;

import com.jfoenix.controls.*;
import com.neovisionaries.i18n.CountryCode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import zad1.fixer.FixerUtils;
import zad1.openWeatherMap.CountryCityListUtils;
import zad1.openWeatherMap.OpenWeatherUtils;
import zad1.openWeatherMap.pojo.City;
import zad1.openWeatherMap.pojo.OpenWeatherPOJO;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;


public class Controller {

    private Map<String, List<City>> countryCities;
    private Map<String, String> currencies;

    @FXML
    private JFXComboBox<String> cityField;
    @FXML
    private JFXComboBox<String> countryField;
    @FXML
    private JFXButton okButton;
    @FXML
    private Label labelIcon;
    @FXML
    private Label temperatureLabel;
    @FXML
    private Label pressureLabel;
    @FXML
    private Label humidityLabel;
    @FXML
    private Label windLabel;
    @FXML
    private Label descLabel;
    @FXML
    private WebView webView;
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private JFXComboBox<String> currencyPicker;
    @FXML
    private Label fixerLabel;
    @FXML
    private Label nbpLabel;


    @FXML
    public void fixerChanged() {
        if (currencies.containsKey(currencyPicker.getValue())) {
            String code = currencies.get(currencyPicker.getValue());
            Double val = new Service("Poland").getRateFor(code);
            fixerLabel.setText(new StringBuilder().append("1 EUR = ").append(val).append(" ").append(code).toString());
        }
    }


    public void initTabPane() {
        Tab nbpTab = new Tab();
        nbpTab.setText("NBP");
        AnchorPane nbpView = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NBPView.fxml"));
        loader.setController(this);
        try {
            nbpView = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        nbpTab.setContent(nbpView);

        Tab fixerTab = new Tab("Fixer");
        AnchorPane fixerView = null;
        loader = new FXMLLoader(getClass().getResource("/FixerView.fxml"));
        loader.setController(this);
        try {
            fixerView = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fixerTab.setContent(fixerView);

        this.currencies = new FixerUtils().getAvailableCurrencies();
        this.currencyPicker.getItems().addAll(currencies.keySet());
        this.currencyPicker.setValue("Euro");
        AutoCompleteForComboBox.autoCompleteComboBoxPlus(this.currencyPicker, (typedText, itemToCompare) -> itemToCompare.toLowerCase().contains(typedText.toLowerCase()));

        nbpTab.getStyleClass().addAll(getClass().getResource("/style.css").toExternalForm());
        fixerTab.getStyleClass().addAll(getClass().getResource("/style.css").toExternalForm());

        tabPane.getTabs().addAll(nbpTab, fixerTab);
    }

    @FXML
    public void countryChanged() {
        if (countryCities.containsKey(countryField.getValue())) {
            List<City> cities = this.countryCities.get(this.countryField.getValue());
            this.cityField.getItems().clear();
            cities.forEach(city -> this.cityField.getItems().add(city.toString()));
            this.cityField.setValue(cities.get(0).toString());
            AutoCompleteForComboBox.autoCompleteComboBoxPlus(this.cityField, (typedText, itemToCompare) -> itemToCompare.toLowerCase().contains(typedText.toLowerCase()));
            this.nbpLabel.setText("1 PLN = " + new Service(countryField.getValue()).getNBPRate() + " " + CountryCode.getByCode(countryCities.get(countryField.getValue()).get(0).getCountry()).getCurrency());
        }
    }

    public void initComboBox() {
        this.countryCities = new CountryCityListUtils().getCountryCity();
        this.countryField.getItems().addAll(this.countryCities.keySet());
        this.cityField.setEditable(true);
        this.countryField.setEditable(true);
        this.countryField.setValue("Poland");
//        this.cityField.setValue("Warsaw");
        countryChanged();
        this.cityField.setValue("Warsaw");
        AutoCompleteForComboBox.autoCompleteComboBoxPlus(this.countryField, (typedText, itemToCompare) -> itemToCompare.toLowerCase().contains(typedText.toLowerCase()));
        AutoCompleteForComboBox.autoCompleteComboBoxPlus(this.cityField, (typedText, itemToCompare) -> itemToCompare.toLowerCase().contains(typedText.toLowerCase()));
    }

    @FXML
    public void loadWeather() {

        City city = countryCities.get(this.countryField.getValue()).stream().filter(c -> c.getName().equals(cityField.getValue())).findFirst().orElse(null);
        Integer id = city.getId();
        String country = countryField.getValue();

        Service service = new Service(country);
        String json = service.getWeatherByCode(id);
        OpenWeatherPOJO weatherPOJO = OpenWeatherUtils.getOpenWeatherPOJOFromJSON(json);
        String iconId = weatherPOJO.getWeather().get(0).getIcon();

        try {
            String file = getClass().getResource("/weather-icons/").getFile() + iconId + ".png";
            FileInputStream input = new FileInputStream(file);
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            labelIcon.setGraphic(imageView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Double tempVal = weatherPOJO.getMain().getTemp();

        DecimalFormat decimalFormat = new DecimalFormat("#");
        String temperatureRounded = decimalFormat.format(tempVal);

        descLabel.setText(weatherPOJO.getWeather().get(0).getDescription());
        temperatureLabel.setText(temperatureRounded + "°C");
        pressureLabel.setText("Pressure: " + weatherPOJO.getMain().getPressure() + " hPa");
        humidityLabel.setText("Humidity: " + weatherPOJO.getMain().getHumidity() + "%");

        windLabel.setText("Wind: " + weatherPOJO.getWind().getSpeed() + " m/s");

        loadWebView();

    }


    public void loadWebView() {
        webView.getEngine().load("https://en.wikipedia.org/wiki/" + this.cityField.getValue());
    }

    @FXML
    public void enterPressed(){
        if(cityField.getItems().contains(cityField.getValue())){
            loadWeather();
        }
    }

}
