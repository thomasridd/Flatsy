package com.github.thomasridd.flatsy.operations.operators;

import com.github.davidcarboni.restolino.json.Serialiser;

import java.util.ArrayList;

public class FlatsyOperatorBuilder {
    /**
     * tries to parse a query string to a FlatsyOperator object
     * @param operator a string of the form {OperatorType:Arguments}
     * @return an Operator
     */
    public static FlatsyOperator operatorStringToOperator(String operator) {
        String cleanedString = operator.trim();

        String operatorType = null;
        String arguments = null;
        if (cleanedString.startsWith("{") && cleanedString.endsWith("}") && cleanedString.contains(":")) {
            cleanedString = cleanedString.substring(1, cleanedString.length() - 1);
            operatorType = cleanedString.substring(0, cleanedString.indexOf(":"));
            arguments = cleanedString.substring(cleanedString.indexOf(":") + 1);
        } else if (cleanedString.startsWith("{") && cleanedString.endsWith("}")) {
            operatorType = cleanedString.substring(1, cleanedString.length() - 1).trim();
        } else {
            System.out.println("Format for operator engine is {<Query Type>:<Arguments>}");
            System.out.println("Operator types include json_validate, json_paths_to_console, uri_to_console/sout");
            return null;
        }

        FlatsyOperator flatsyOperator;
        if (operatorType.equalsIgnoreCase("json_validate")) {
            flatsyOperator = new JSONValidate();
        } else if (operatorType.equalsIgnoreCase("json_paths_to_console")) {
            flatsyOperator = new JSONPathsToOutput(System.out, Serialiser.deserialise(arguments, ArrayList.class));
        } else if (operatorType.equalsIgnoreCase("uri_to_console") || operatorType.equalsIgnoreCase("sout")) {
            flatsyOperator = new UriToOutput(System.out);
        } else {
            return null;
        }

        return flatsyOperator;
    }
}
