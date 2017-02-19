package uk.co.jakelee.blacksmithslots.helper;

import java.util.ArrayList;
import java.util.List;

public class MatchHelper {
    public static List<List<Integer>> getRoutes(int numColumns, int maxRoutes) {
        List<List<Integer>> routes;
        switch (numColumns) {
            case 3:
                routes = get3ColumnRoutes();
                break;
            case 2:
                routes = get2ColumnRoutes();
                break;
            default:
                routes = get3ColumnRoutes();
                break;
        }

        if (maxRoutes <= routes.size()) {
            return routes.subList(0, maxRoutes);
        }
        return routes;
    }

    private static List<List<Integer>> get2ColumnRoutes() {
        // Total routes: 5
        List<List<Integer>> allRoutes = new ArrayList<>();

        // Straight rows
        for (int i = 0; i < Constants.ROWS; i++) {
            List<Integer> route = new ArrayList<>();
            route.add(i);
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
