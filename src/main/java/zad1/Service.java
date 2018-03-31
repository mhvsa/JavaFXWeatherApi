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
    private static final String OPENWEATHER_AUTH_TOKEN = "cc9951e58e20b6567f9821d43026823f"; // TODO make a configuration file

    private static final String FIXER_HOST = "data.fixer.io";
    private static final String FIXER_PATH_LATEST = "/api/latest";
    private static final String FIXER_AUTH_TOKEN = "930874f33f3b06d0cf62ee5cfb22e09c";

    private static final String NBP_HOST = "www.nbp.pl";
    private static final String NBP_TABLE_A_XML_PATH = "/kursy/xml/a064z180330.xml";
    private static final String NBP_TABLE_B_XML_PATH = "/kursy/xml/b013z180328.xml";


    private static final List<String> CURRENCIES = Arrays.asList("USD", "EUR");

    private final String country;
    private final URIBuilder openWeatherUriBuilder;
    private final URIBuilder fixerUriBuilder;
    private final URIBuilder nbpAUriBuilder;
    private final URIBuilder nbpBUriBuilder;


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
        this.nbpAUriBuilder = new URIBuilder()
                .setScheme(PROTOCOL)
                .setHost(NBP_HOST)
                .setPath(NBP_TABLE_A_XML_PATH);
        this.nbpBUriBuilder = new URIBuilder()
                .setScheme(PROTOCOL)
                .setHost(NBP_HOST)
                .setPath(NBP_TABLE_B_XML_PATH);
    }

    public String getWeather(String city) {

        URI uri = null;

        try {
            uri = this.openWeatherUriBuilder
                    .addParameter("q", String.format("%s,%s", this.country, city))
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
            String tableA = getRequest(nbpAUriBuilder.build());
            String tableB = getRequest(nbpBUriBuilder.build());

            JAXBContext jc = JAXBContext.newInstance(PozycjeLista.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();

            PozycjeLista pozycjeListaA = (PozycjeLista) unmarshaller.unmarshal(new InputSource(new StringReader(tableA)));

            String currency = CountryCode.findByName(this.country).get(0).getCurrency().toString();

            Optional<Pozycja> waluta = pozycjeListaA.getPozycje().stream()
                    .filter(pozycja -> pozycja.getKod_waluty().equals(currency))
                    .findFirst();

            if (waluta.isPresent()) {
                return Double.valueOf(waluta.get().getPrzelicznik());
            }

            PozycjeLista pozycjeListaB = (PozycjeLista) unmarshaller.unmarshal(new InputSource(new StringReader(tableB)));

            waluta = pozycjeListaB.getPozycje().stream()
                    .filter(pozycja -> pozycja.getKod_waluty().equals(currency))
                    .findFirst();

            if (waluta.isPresent()) {
                return Double.valueOf(waluta.get().getPrzelicznik());
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
