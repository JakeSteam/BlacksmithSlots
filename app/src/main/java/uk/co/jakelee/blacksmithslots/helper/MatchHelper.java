package uk.co.jakelee.blacksmithslots.helper;

import java.util.ArrayList;
import java.util.List;

public class MatchHelper {
    public static List<List<Integer>> getRoutes(int numColumns, int maxRoutes) {
        List<List<Integer>> routes = new ArrayList<>();
        switch (numColumns) {
            case 5: routes.addAll(get5ColumnRoutes());
            case 4: routes.addAll(get4ColumnRoutes());
            case 3: routes.addAll(get3ColumnRoutes());
            default:
                break;
        }

        if (maxRoutes <= routes.size() && maxRoutes > 0) {
            return routes.subList(0, maxRoutes);
        }
        return routes;
    }

    private static List<List<Integer>> get5ColumnRoutes() {
        // Total routes: 11
        List<List<Integer>> allRoutes = new ArrayList<>();

        // Straight rows
        for (int i = 0; i < Constants.ROWS; i++) {
            List<Integer> route = new ArrayList<>();
            route.add(i);
            route.add(i);
            route.add(i);
            route.add(i);
            route.add(i);
            allRoutes.add(route);
        }

        // Middle dipping down
        for (int i = 0; i < Constants.ROWS - 2; i++) {
            List<Integer> route = new ArrayList<>();
            route.add(i);
            route.add(i + 1);
            route.add(i + 2);
            route.add(i + 1);
            route.add(i);
            allRoutes.add(route);
        }

        // Middle peaking up
        for (int i = 2; i < Constants.ROWS; i++) {
            List<Integer> route = new ArrayList<>();
            route.add(i);
            route.add(i - 1);
            route.add(i - 2);
            route.add(i - 1);
            route.add(i);
            allRoutes.add(route);
        }

        return allRoutes;
    }

    private static List<List<Integer>> get4ColumnRoutes() {
        // Total routes: 9
        List<List<Integer>> allRoutes = new ArrayList<>();

        // Straight rows
        for (int i = 0; i < Constants.ROWS; i++) {
            List<Integer> route = new ArrayList<>();
            route.add(i);
            route.add(i);
            route.add(i);
            route.add(i);
            allRoutes.add(route);
        }

        // Middle dipping down
        for (int i = 0; i < Constants.ROWS - 1; i++) {
            List<Integer> route = new ArrayList<>();
            route.add(i);
            route.add(i + 1);
            route.add(i + 1);
            route.add(i);
            allRoutes.add(route);
        }

        return allRoutes;
    }

    private static List<List<Integer>> get3ColumnRoutes() {
        // Total routes: 9
        List<List<Integer>> allRoutes = new ArrayList<>();

        // Straight rows
        for (int i = 0; i < Constants.ROWS; i++) {
            List<Integer> route = new ArrayList<>();
            route.add(i);
            route.add(i);
            route.add(i);
            allRoutes.add(route);
        }

        // Middle dipping down
        for (int i = 0; i < Constants.ROWS - 1; i++) {
            List<Integer> route = new ArrayList<>();
            route.add(i);
            route.add(i + 1);
            route.add(i);
            allRoutes.add(route);
        }

        return allRoutes;
    }
}
