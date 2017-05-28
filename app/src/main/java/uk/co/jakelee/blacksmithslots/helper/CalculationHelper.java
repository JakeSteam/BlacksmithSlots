package uk.co.jakelee.blacksmithslots.helper;

import java.util.Random;

public class CalculationHelper {
    static int increaseByPercentage(int number, int percent) {
        double percentMultiplier = percent + 100;
        return (int)Math.ceil(number * (percentMultiplier / 100));
    }

    public static boolean randomBoolean() {
        return new Random().nextInt(2) == 1;
    }

    public static int randomNumber(int minimum, int maximum) {
        Random random = new Random();
        return random.nextInt((maximum - minimum) + 1) + minimum;
    }
}
