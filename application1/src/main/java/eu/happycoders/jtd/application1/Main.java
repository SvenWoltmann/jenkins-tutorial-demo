package eu.happycoders.jtd.application1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line;
        while (scanner.hasNext()
                && !(line = scanner.nextLine()).equals("exit")) {
            System.out.println(LineEvaluator.eval(line));
        }
    }

}
