# Parsing big JSON files
## Introduction
If you need to parse big json files, how would you do that?
Parse a json in usual way? Store a json in a string and then parse it? That way works okay for short strings. 
In case of big file I would use streaming parsing to avoid memory issues. This article covers parsing from a string and parsing directly from a stream.

### Test cases
I chose GSON parser for parsing as first thing popes up in my head. You can use any library, I believe results will be quite close.
For both test cases the same json file 32MB is used and the test cases store objects in a `List` instance. 
Memory consumption and time for the file processing will be measured. The json file contains an array of 22736 objects with many fields, 
but only three fields will be extracted: _id, name, registered. The fields will be stored in class `Item` which you can find below.

```java
package info.biosfood.json.parser;

import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("_id")
    private String id;

    private String name;

    @SerializedName("registered")
    private String timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return new StringBuilder().append("\n").append(this.getClass().getSimpleName()).append(" {\n")
                .append(" id: ").append(id).append(",\n")
                .append(" name: ").append(name).append(",\n")
                .append(" timestamp: ").append(timestamp)
                .append("\n}")
                .toString();
    }

}
```

### Simple string parsing
First test case is parsing large json file from a string. File's content will be read and stored in a string and then the string will be parsed.
It usually works good for short strings, if a string is stored in a database and a json is transformed from the string to an object.
You can find an implementation of a parser and a test which uses the parser and contains metrics calculating below.

##### Simple parser
```java
package info.biosfood.json.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonSimpleParser {

    public static <T> T parse(Class<T> clazz, String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, clazz);
    }

}
```

##### Test for simple parsing
```java
package info.biosfood.json.parser;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class JsonSimpleParserTest {

    public static final Logger LOG = Logger.getLogger(JsonSimpleParserTest.class);

        String jsonFile = "generated.json";

    @Test
    public void testHugeJsonFileParsing() throws URISyntaxException, IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource(jsonFile).toURI()));

        Metrics.printMemory("---- Metrics before parsing ----");
        Stopwatch s = Stopwatch.start();

        Item[] result = JsonSimpleParser.parse(Item[].class, new String(bytes));

        LOG.debug(s.stop().toString());
        Metrics.printMemory("---- Metrics after parsing ----");

        assertEquals(22736, result.length);
    }

}
```

##### Results of parsing from string without objects iteration
Lets discover results of parsing. The parsing took 479ms, but memory consumption is bad - 112Mb were used for storing mediate data and list of 22736 objects.

```text
DEBUG Metrics: [main] ---- Metrics before parsing ---- {
  free memory      : 234,140
  allocated memory : 245,760
  max memory       : 3,626,496
  total free memory: 3,614,876
}

DEBUG JsonSimpleParserTest: [main] Stopwatch: {
  started at: 1492785679022,
  ended at  : 1492785679375,
  duration  : 479
}

DEBUG Metrics: [main] ---- Metrics after parsing ---- {
  free memory      : 122,058
  allocated memory : 245,760
  max memory       : 3,626,496
  total free memory: 3,502,794
}
```

### Stream parsing
The second test case is to parse the same json file, but the file not be read in a one shot. The objects will be handled 
from an input stream with no storing mediate result in a string.

##### Stream parser
```java
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
```

##### Test for stream parser
```java
package info.biosfood.json.parser;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JsonStreamParserTest {

    public static final Logger LOG = Logger.getLogger(JsonStreamParserTest.class);

    JsonStreamParser<Item> subject;

    String jsonFile = "generated.json";

    public InputStream getInputStream() throws IOException {
        return this.getClass().getClassLoader().getResource(jsonFile).openStream();
    }

    @Test
    public void testStreamParsingWithSampleFile() throws IOException, InterruptedException {
        Metrics.printMemory("---- Metrics before parsing ----");

        Stopwatch s = Stopwatch.start();

        subject = new JsonStreamParser<Item>(Item.class, getInputStream());

        subject.open();

        List<Item> result = new ArrayList<>();

        while(subject.hasNext()) {
            result.add(subject.next());
        }

        subject.close();

        LOG.debug(s.stop().toString());

        Metrics.printMemory("---- Metrics after parsing ----");

        assertEquals(22736, result.size());
    }

}
```

##### Results of parsing from stream without objects iteration
Take a look at results of parsing. The parsing took 385ms and memory consumption is good enough - 12Mb were used for storing mediate data and list of 22736 objects
```text
DEBUG Metrics: [main] ---- Metrics before parsing ---- {
  free memory      : 234,147
  allocated memory : 245,760
  max memory       : 3,626,496
  total free memory: 3,614,883
}

DEBUG JsonStreamParserTest: [main] Stopwatch: {
  started at: 1492782880417,
  ended at  : 1492782880802,
  duration  : 385
}

DEBUG Metrics: [main] ---- Metrics after parsing ---- {
  free memory      : 222,600
  allocated memory : 245,760
  max memory       : 3,626,496
  total free memory: 3,603,336
}
```

### Conclusion
In this article I covered two approaches of parsing big json files. Parsing from a string and from a stream. 
Parsing from a stream obviously looks better than parsing a big string. An advantage of the parsing from a stream 
is the program consumes less memory. Memory consumption in case of parsing big file is crucial as many of files 
are parsed at once.

### Useful links
- [GSON library](https://github.com/google/gson)
- [org.json library](https://github.com/stleary/JSON-java)
- [Jackson library](https://github.com/FasterXML/jackson-databind/)
