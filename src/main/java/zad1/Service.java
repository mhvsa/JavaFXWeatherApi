/**
 * @author Sabiniarz Michał S15092
 */

package zad1;

import com.neovisionaries.i18n.CountryCode;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import zad1.fixer.FixerUtils;
import zad1.nbp.pojo.Pozycja;
import zad1.nbp.pojo.PozycjeLista;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Service {

    private static final String PROTOCOL = "http";

    private static final String OPENWEATHER_HOST = "api.openweathermap.org";
    private static final String OPENWEATHER_PATH = "/data/2.5/weather";
    private static final String OPENWEATHER_AUTH_TOKEN = Configuration.OPENWEATHER_AUTH_TOKEN;

    private static final String FIXER_HOST = "data.fixer.io";
    private static final String FIXER_PATH_LATEST = "/api/latest";
    private static final String FIXER_AUTH_TOKEN = Configuration.FIXER_AUTH_TOKEN;

    private static final String NBP_HOST = "www.nbp.pl";

    private static final List<String> CURRENCIES = Arrays.asList("USD", "EUR");

    private final String country;
    private final URIBuilder openWeatherUriBuilder;
    private final URIBuilder fixerUriBuilder;
    private final URIBuilder nbpUriBuilder;

    public Service(String country) {
        this.country = country;
        this.openWeatherUriBuilder = new URIBuilder()
                .setScheme(PROTOCOL)
                .setHost(OPENWEATHER_HOST)
                .setPath(OPENWEATHER_PATH);
        this.fixerUriBuilder = new URIBuilder()
                .setScheme(PROTOCOL)
                .setHost(FIXER_HOST)
                .setPath(FIXER_PATH_LATEST);
        this.nbpUriBuilder = new URIBuilder()
                .setScheme(PROTOCOL)
                .setHost(NBP_HOST);
    }

    public String getWeather(String city) {

        URI uri = null;

        String cc = CountryCode.findByName(country).get(0).getAlpha2();

        try {
            uri = this.openWeatherUriBuilder
                    .addParameter("q", String.format("%s,%s", city, this.country))
//                    .addParameter("q", String.format("%s,%s", city, cc))
                    .addParameter("units", "metric")
                    .addParameter("APPID", OPENWEATHER_AUTH_TOKEN)
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String json = getRequest(uri); // TODO nullPointerException handling

        return json;
    }

    public String getWeatherByCode(Integer cityCode) {

        URI uri = null;

        String cc = CountryCode.findByName(country).get(0).getAlpha2();

        try {
            uri = this.openWeatherUriBuilder
                    .addParameter("id", cityCode.toString())
                    .addParameter("units", "metric")
                    .addParameter("APPID", OPENWEATHER_AUTH_TOKEN)
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String json = getRequest(uri); // TODO nullPointerException handling

        return json;
    }

    public Double getRateFor(String currency) {
        URI uri = null;
        try {
            uri = this.fixerUriBuilder
                    .addParameter("access_key", FIXER_AUTH_TOKEN)
                    .addParameter("symbols", currency)
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String json = getRequest(uri);

        return FixerUtils.getFixerExchangePOJOFromJson(json).getRates().get(currency);
    }

    public Double getNBPRate() {

        if (country.equals("Poland")) {
            return 1.0;
        }

        try {

            String a = getRequest(nbpUriBuilder.setPath("/kursy/kursya.html").build()).split("<p class=\"file print_hidden\"><a href=\"")[1].split("\"")[0];
            String b = getRequest(nbpUriBuilder.setPath("/kursy/kursyb.html").build()).split("<p class=\"file print_hidden\"><a href=\"")[1].split("\"")[0];

            String tableA = getRequest(nbpUriBuilder.setPath(a).build());
            String tableB = getRequest(nbpUriBuilder.setPath(b).build());


            JAXBContext jc = JAXBContext.newInstance(PozycjeLista.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();

            PozycjeLista pozycjeListaA = (PozycjeLista) unmarshaller.unmarshal(new InputSource(new StringReader(tableA)));

            String currency = CountryCode.findByName(this.country).get(0).getCurrency().toString();

            Optional<Pozycja> waluta = pozycjeListaA.getPozycje().stream()
                    .filter(pozycja -> pozycja.getKod_waluty().equals(currency))
                    .findFirst();

            if (waluta.isPresent()) {
                return Double.valueOf(waluta.get().getPrzelicznik()) * Double.valueOf(waluta.get().getKurs_sredni().replace(",", "."));
            }

            PozycjeLista pozycjeListaB = (PozycjeLista) unmarshaller.unmarshal(new InputSource(new StringReader(tableB)));

            waluta = pozycjeListaB.getPozycje().stream()
                    .filter(pozycja -> pozycja.getKod_waluty().equals(currency))
                    .findFirst();

            if (waluta.isPresent()) {
                return Double.valueOf(waluta.get().getPrzelicznik()) * Double.valueOf(waluta.get().getKurs_sredni().replace(",", "."));
            }

            return null;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getRequest(URI uri) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(1000)
                .setConnectTimeout(1000)
                .build();

        HttpClientContext clientContext = HttpClientContext.create();

        httpGet.setConfig(requestConfig);
        String json = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet, clientContext);
            json = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

}
