package info.biosfood.json.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonStreamParser<T> {

    private JsonReader reader;
    private Gson gson;
    private Class<T> targetClass;

    public JsonStreamParser(Class<T> targetClass, InputStream in) {
        reader = new JsonReader(new InputStreamReader(in));
        this.targetClass = targetClass;
    }

    public void open() throws IOException {
        reader.beginArray();

        gson = new GsonBuilder().create();
    }

    public void close() throws IOException {
        reader.close();
        gson = null;
    }

    public boolean hasNext() throws IOException {
        return reader.hasNext();
    }

    public T next() {
        return gson.fromJson(reader, targetClass);
    }

}
