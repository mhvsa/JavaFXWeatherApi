package zad1.openWeatherMap;

import com.google.gson.Gson;
import zad1.openWeatherMap.pojo.OpenWeatherPOJO;

public class OpenWeatherUtils {

    public static OpenWeatherPOJO getOpenWeatherPOJOFromJSON(String json) {
        return new Gson().fromJson(json, OpenWeatherPOJO.class);
    }
}
