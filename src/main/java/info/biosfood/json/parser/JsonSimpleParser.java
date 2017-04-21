package info.biosfood.json.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonSimpleParser {

    public static <T> T parse(Class<T> clazz, String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, clazz);
    }

}
