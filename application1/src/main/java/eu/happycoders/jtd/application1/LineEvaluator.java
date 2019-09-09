package eu.happycoders.jtd.application1;

import eu.happycoders.jtd.library1.Calculator;

public class LineEvaluator {

    public static Number eval(String line) {
        String[] tokens = line.split("\\s");
        if (tokens.length != 3) {
            throw new IllegalArgumentException();
        }

        int a = Integer.parseInt(tokens[0]);
        int b = Integer.parseInt(tokens[2]);

        switch (tokens[1]) {
            case "+":
                return Calculator.add(a, b);

            case "-":
                return Calculator.subtract(a, b);

            case "*":
                return Calculator.multiply(a, b);

            case "/":
                return Calculator.divide(a, b);

            default:
                throw new IllegalArgumentException();
        }
    }

}
