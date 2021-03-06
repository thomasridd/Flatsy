package com.github.thomasridd.flatsy.util;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thomasridd on 14/10/15.
 *
 * Utility methods for use by Flatsy
 */
public class FlatsyUtil {

    /**
     * Split a command line into arguments
     *
     * @param command a space separated string where substrings with spaces are contained in " quotes
     * @return a list of substrings
     */
    public static List<String> commandArguments(String command) {

        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+[?\"])\\s*").matcher(command);
        while (m.find()) {
            String group = m.group(1);
            if (group.endsWith("\"") && group.startsWith("\"")) {
                list.add(group.substring(1, group.length() - 1));
            } else {
                list.add(group);
            }
        }
        return list;
    }

    /**
     * Build a string using a primitive string builder
     *
     * Syntax
     * plaintext + \"plain text\" + ~.uri + $.jsonpath
     *
     * ++ = single space
     *
     * @param expression an expression
     * @param object a flatsy object to use for dynamic sections
     * @return a string built using object
     * @throws IOException if flatsy objects cannot be accessed
     */
    public static String stringExpression(String expression, FlatsyObject object) throws IOException {
        List<String> list = new ArrayList<>();

        String replaced = expression.replace("++", "+ \" \" +");

        // hideous regex splits on +'s outside quotation marks and then does a bit of trimming
        String[] split = replaced.split("\\+(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
        for (String item: split) { list.add(item.trim().replace("\"", "")); }

        String result = "";
        for(String item: list) {
            if (item.equalsIgnoreCase("~.uri")) {
                // add the object uri
                result += object.uri;
            } else if(item.equalsIgnoreCase("~.file")) {
                // add the object filename
                if (object.getType() == FlatsyObjectType.JSONFile || object.getType() == FlatsyObjectType.OtherFile) {
                    Path p = Paths.get(object.uri);
                    result += p.getFileName().toString();
                }
            } else if(item.equalsIgnoreCase("~.parent")) {
                // add the object filename
                if (object.parent() != null) {
                    result += object.parent().uri;
                }

            } else if(item.startsWith("$.")) {
                // add a jsonpath string
                DocumentContext context = JsonPath.parse(object.retrieveStream());
                try {
                    String value = context.read(item);
                    value = value.replace("\n", " ").replace("\r", " ");
                    result += value;
                } catch (com.jayway.jsonpath.PathNotFoundException e) {
                    result += ".";
                }

            } else {
                // straight string
                result += item;
            }
        }
        return result;
    }

    /**
     * Resolve a file string with user home directory
     *
     * @param filePath a file path (possibly relative to user)
     * @return an absolute file path
     */
    public static Path pathsGet(String filePath) {
        if (filePath.length() == 0) {
            return null;
        } else if (filePath.startsWith("/")) {
            return Paths.get(filePath);
        } else {
            Path home = Paths.get(System.getProperty("user.home"));
            return home.resolve(filePath);
        }
    }
}
