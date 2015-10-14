package com.github.thomasridd.flatsy.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thomasridd on 14/10/15.
 * <p/>
 * Utility methods for use by Flatsy
 */
public class FlatsyUtil {

    /**
     * Split a command line into
     *
     * @param command a space separated string where substrings with spaces are contained in " quotes
     * @return a list of substrings
     */
    public static List<String> commandArguments(String command) {

        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
        while (m.find())
            list.add(m.group(1).replace("\"", ""));
        return list;
    }

}
