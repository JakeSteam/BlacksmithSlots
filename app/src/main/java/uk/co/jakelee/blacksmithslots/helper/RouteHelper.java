package uk.co.jakelee.blacksmithslots.helper;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.constructs.WinRoute;

class RouteHelper {
    public static List<WinRoute> getRoutes(int numColumns, int maxRoutes) {
        List<WinRoute> routes = new ArrayList<>();
        switch (numColumns) {
            case 5: routes.addAll(get5ColumnRoutes());
            case 4: routes.addAll(get4ColumnRoutes());
            case 3: routes.addAll(get3ColumnRoutes());
            case 2: routes.addAll(get2ColumnRoutes());
            default:
                break;
        }

        if (maxRoutes <= routes.size() && maxRoutes > 0) {
            return routes.subList(0, maxRoutes);
        }
        return routes;
    }

    private static List<WinRoute> get5ColumnRoutes() {
        // Total routes: 11
        List<WinRoute> allRoutes = new ArrayList<>();

        // Straight rows
        for (int i = 0; i < Constants.ROWS; i++) {
            allRoutes.add(new WinRoute(i, i, i, i, i));
        }

        // Middle dipping down
        for (int i = 0; i < Constants.ROWS - 2; i++) {
            allRoutes.add(new WinRoute(i, i + 1, i + 2, i + 1, i));
        }

        // Middle peaking up
        for (int i = 2; i < Constants.ROWS; i++) {
            allRoutes.add(new WinRoute(i, i - 1, i - 2, i - 1, i));
        }

        return allRoutes;
    }

    private static List<WinRoute> get4ColumnRoutes() {
        // Total routes: 9
        List<WinRoute> allRoutes = new ArrayList<>();

        // Straight rows
        for (int i = 0; i < Constants.ROWS; i++) {
            allRoutes.add(new WinRoute(i, i, i, i));
        }

        // Middle dipping down
        for (int i = 0; i < Constants.ROWS - 1; i++) {
            allRoutes.add(new WinRoute(i, i + 1, i + 1, i));
        }

        return allRoutes;
    }

    private static List<WinRoute> get3ColumnRoutes() {
        // Total routes: 9
        List<WinRoute> allRoutes = new ArrayList<>();

        // Straight rows
        for (int i = 0; i < Constants.ROWS; i++) {
            allRoutes.add(new WinRoute(i, i, i));
        }

        // Middle dipping down
        for (int i = 0; i < Constants.ROWS - 1; i++) {
            allRoutes.add(new WinRoute(i, i + 1, i));
        }

        return allRoutes;
    }

    private static List<WinRoute> get2ColumnRoutes() {
        // Total routes: 5
        List<WinRoute> allRoutes = new ArrayList<>();

        // Straight rows
        for (int i = 0; i < Constants.ROWS; i++) {
            allRoutes.add(new WinRoute(i, i));
        }

        return allRoutes;
    }
}
