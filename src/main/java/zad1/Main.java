/**
 * @author Sabiniarz Michał S15092
 */

package zad1;


import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        Service s = new Service("Germany");
        String weatherJson = s.getWeather("Berlin");
        Double rate1 = s.getRateFor("PLN");
        Double rate2 = s.getNBPRate();


        // ...
        // część uruchamiająca GUI

        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ApplicationView.fxml"));
        Controller controller = new Controller();
        loader.setController(controller);
        Parent root = loader.load();

        JFXDecorator decorator = new JFXDecorator(stage, root);
        decorator.setCustomMaximize(true);

        stage.setTitle("Weather API");
        stage.setScene(new Scene(decorator));

        String cssURI = getClass().getResource("/style.css").toExternalForm();
        decorator.getStylesheets().add(cssURI);

        StackPane stack = (StackPane) decorator.getChildren().get(1);

        stack.widthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                SplitPane s = (SplitPane) stack.getChildren().get(0);
                s.setPrefWidth((Double) newValue);
            }
        });


        stack.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                SplitPane s = (SplitPane) stack.getChildren().get(0);
                s.setPrefHeight((Double) newValue);
            }
        });

        controller.initTabPane();
        controller.initComboBox();
        controller.loadWeather();


        stage.show();
    }
}
