package eu.happycoders.jtd.application1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LineEvaluatorTest {

    @Test
    void addition() {
        assertEquals(Integer.valueOf(7), LineEvaluator.eval("3 + 4"));
    }


    @Test
    void subtraction() {
        assertEquals(Integer.valueOf(8), LineEvaluator.eval("15 - 7"));
    }


    @Test
    void multiplication() {
        assertEquals(Integer.valueOf(63), LineEvaluator.eval("9 * 7"));
    }

    @Test
    void division() {
        assertEquals(Double.valueOf(4.5), LineEvaluator.eval("9 / 2"));
    }

}
