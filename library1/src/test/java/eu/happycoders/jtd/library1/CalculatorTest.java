package eu.happycoders.jtd.library1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {

    @Test
    void addition() {
        assertEquals(3, Calculator.add(1, 2));
    }

    @Test
    void subtraction() {
        assertEquals(3, Calculator.subtract(7, 4));
    }

    @Test
    void multiplication() {
        assertEquals(6, Calculator.multiply(2, 3));
    }

    @Test
    void division() {
        assertEquals(1.428571, Calculator.divide(10, 7), 0.000001);
    }

}
