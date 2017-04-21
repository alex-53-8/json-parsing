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

    public String readFile() throws URISyntaxException, IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource(jsonFile).toURI()));
        return new String(bytes);
    }

    @Test
    public void testHugeJsonFileParsing() throws URISyntaxException, IOException {
        Metrics.printMemory("---- Metrics before parsing ----");

        String json = readFile();
        Stopwatch s = Stopwatch.start();

        Item[] result = JsonSimpleParser.parse(Item[].class, json);

        LOG.debug(s.stop().toString());
        Metrics.printMemory("---- Metrics after parsing ----");

        assertEquals(22736, result.length);
    }

}
