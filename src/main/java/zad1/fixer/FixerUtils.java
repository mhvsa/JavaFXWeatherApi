package zad1.fixer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import zad1.fixer.pojo.Currency;
import zad1.fixer.pojo.FixerExchange;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class FixerUtils {


    public static FixerExchange getFixerExchangePOJOFromJson(String json) {
        Gson gson = new Gson();
        FixerExchange fixerExchange = gson.fromJson(json, FixerExchange.class);
        return fixerExchange;
    }

    public Map<String, String> getAvailableCurrencies() {
        Currency[] currencies = new Currency[0];
        try {
            currencies = new Gson().fromJson(new FileReader(getClass().getResource("/fixerCurrencies.json").getFile()), Currency[].class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LinkedHashMap<String, String> currencyCode = new LinkedHashMap<>();
        for (Currency currency : currencies) {
            currencyCode.put(currency.getName(), currency.getCode());
        }

        return currencyCode;
    }
}
