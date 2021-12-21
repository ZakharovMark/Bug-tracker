package util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Util {
    private static Object clazz;

    static {
        clazz = new Object();
    }

    public static String readHTMLToString(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(Util.class.getClassLoader().getResource("../../").toString().substring(6) + "resources/html/" + fileName + ".html")));
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
