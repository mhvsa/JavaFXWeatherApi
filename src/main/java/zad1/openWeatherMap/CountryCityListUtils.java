package zad1.openWeatherMap;

import com.google.gson.*;
import com.neovisionaries.i18n.CountryCode;
import zad1.openWeatherMap.pojo.City;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class CountryCityListUtils {


    private Map<String, List<City>> countryCity;

    public CountryCityListUtils() {

        Gson gson = new Gson();
        this.countryCity = new LinkedHashMap<>();

        try {
            City[] cities = gson.fromJson(new FileReader(getClass().getResource("/city.list.json").getFile()), City[].class);
            for (City city : cities) {
                if (city.getId() != -1) {
                    if (!city.getCountry().equals("")) {
                        String country = CountryCode.getByCode(city.getCountry()).getName();
                        if (this.countryCity.containsKey(country)) {
                            this.countryCity.get(country).add(city);
                        } else {
                            this.countryCity.put(country, new LinkedList<>(Collections.singletonList(city)));
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public Map<String, List<City>> getCountryCity() {
        return countryCity;
    }
}
