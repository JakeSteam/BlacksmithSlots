package uk.co.jakelee.blacksmithslots.constructs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WinRoute {
    private final List<Integer> list;

    public WinRoute(Integer... positions) {
        this.list = new ArrayList<>();
        Collections.addAll(list, positions);
    }

    public int size() {
        return list.size();
    }

    public Integer get(int position) {
        return list.get(position);
    }

    public String toString() {
        StringBuilder route = new StringBuilder();
        for (Integer position : list) {
            route.append(position);
        }
        return route.toString();
    }
}
