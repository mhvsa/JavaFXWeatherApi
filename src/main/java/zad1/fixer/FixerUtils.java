package zad1.fixer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import zad1.fixer.pojo.FixerExchange;

public class FixerUtils {


    public static FixerExchange getFixerExchangePOJOFromJson(String json) {
        Gson gson = new Gson();
        FixerExchange fixerExchange = gson.fromJson(json, FixerExchange.class);
        return fixerExchange;
    }


}
