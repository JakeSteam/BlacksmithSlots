package uk.co.jakelee.blacksmithslots.helper;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MatchHelper {
    public static List<List<Pair<Integer, Integer>>> getRoutes(int numColumns, int maxRoutes) {
        List<List<Pair<Integer, Integer>>> routes;
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

    private static List<List<Pair<Integer, Integer>>> get2ColumnRoutes() {
        // Total routes: 5
        List<List<Pair<Integer, Integer>>> allRoutes = new ArrayList<>();

        // Straight rows
        for (int i = 0; i < Constants.ROWS; i++) {
            List<Pair<Integer, Integer>> route = new ArrayList<>();
            route.add(new Pair<>(i, 0));
            route.add(new Pair<>(i, 1));
            allRoutes.add(route);
        }

        return allRoutes;
    }

    private static List<List<Pair<Integer, Integer>>> get3ColumnRoutes() {
        // Total routes: 9
        List<List<Pair<Integer, Integer>>> allRoutes = new ArrayList<>();

        // Straight rows
        for (int i = 0; i < Constants.ROWS; i++) {
            List<Pair<Integer, Integer>> route = new ArrayList<>();
            route.add(new Pair<>(i, 0));
            route.add(new Pair<>(i, 1));
            route.add(new Pair<>(i, 2));
            allRoutes.add(route);
        }

        // Middle dipping down
        for (int i = 0; i < Constants.ROWS - 1; i++) {
            List<Pair<Integer, Integer>> route = new ArrayList<>();
            route.add(new Pair<>(i, 0));
            route.add(new Pair<>(i + 1, 1));
            route.add(new Pair<>(i, 2));
            allRoutes.add(route);
        }

        return allRoutes;
    }
}
