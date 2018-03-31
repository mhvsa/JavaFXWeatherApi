/**
 * @author Sabiniarz Michał S15092
 */

package zad1;


import zad1.openWeatherMap.OpenWeatherUtils;

public class Main {
    public static void main(String[] args) {
        Service s = new Service("Germany");
        String weatherJson = s.getWeather("Berlin");
        Double rate1 = s.getRateFor("PLN");
        Double rate2 = s.getNBPRate();


        // ...
        // część uruchamiająca GUI

        OpenWeatherUtils.getOpenWeatherPOJOFromJSON(weatherJson).getWeather().forEach(weather -> System.out.println(weather.getDescription()));

    }
}
