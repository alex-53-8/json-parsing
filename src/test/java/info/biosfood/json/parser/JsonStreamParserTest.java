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
