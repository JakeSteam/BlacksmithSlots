package uk.co.jakelee.blacksmithslots.helper;

public class CalculationHelper {
    static int increaseByPercentage(int number, int percent) {
        double percentMultiplier = percent + 100;
        return (int)Math.ceil(number * (percentMultiplier / 100));
    }
}
