package com.edwin.traininglog.simpleplots;

import java.util.*;

public class TerminalPlotter {
    public void plot(Map<Integer, Integer> pointsXY) {
        int xMin = pointsXY.keySet().stream().min(Integer::compare).orElseThrow(RuntimeException::new);
        int xMax = pointsXY.keySet().stream().max(Integer::compare).orElseThrow(RuntimeException::new);
        int yMin = pointsXY.values().stream().min(Integer::compare).orElseThrow(RuntimeException::new);
        int yMax  = pointsXY.values().stream().max(Integer::compare).orElseThrow(RuntimeException::new);

        int xStep = 1; // Usually days, so 1 is fine for now.
        int yStep = 5;

        // Round yMax to a multiple of yStep
        int roundedYMax = yMin;
        while (roundedYMax < yMax) {
            roundedYMax += yStep;
        }
        int roundedYMin = 0;
        while (roundedYMin < yMin) {
            roundedYMin += yStep;
        }
        roundedYMin -= yStep;

        int effectiveXMin = xMin - 1;
        plot(pointsXY, roundedYMin, roundedYMax, yStep, effectiveXMin, xMax, xStep);
    }

    public void plot(Map<Integer, Integer> pointsXY,
                     int yMin,
                     int yMax,
                     int yStep,
                     int xMin,
                     int xMax,
                     int xStep) {
        char marker = '*';
        Map<Integer, Set<Integer>> pointsYX = invert(pointsXY);

        int yAxisSize = (yMax - yMin) / yStep;
        int xAxisSize = (xMax - xMin) / xStep;

        for (int y = yMax; y >= yMin; y-= yStep) {
            StringBuilder currentLine = new StringBuilder();
            Set<Integer> xValues = pointsYX.get(y);
            for (int x = xMin; x < xMin + xAxisSize; x++) {
                if (xValues != null && xValues.contains(x)) {
                    currentLine.append(marker);
                } else if (x == xMin) {
                    currentLine.append(getCorrectedMarker(y, yMax)).append("|");
                } else if (y == yMin) {
                    currentLine.append("-");
                } else {
                    currentLine.append(" ");
                }
            }
            System.out.println(currentLine);
        }
    }

    static String getCorrectedMarker(int yMarker, int yMax) {
        int length = String.valueOf(yMax).length();
        StringBuilder resultStr = new StringBuilder(String.valueOf(yMarker));
        while (resultStr.length() < length) {
            resultStr.append(" ");
        }
        return resultStr.toString();
    }

//    public void plot(int xAxisSize,
//                     int yAxisSize,
//                     Map<Integer, Integer> pointsXY,
//                     int yMin,
//                     int yStep,
//                     int xMin,
//                     int xStep) {
//        char marker = '*';
//        Map<Integer, Set<Integer>> pointsYX = invert(pointsXY);
//
//        for (int y = yAxisSize + yMin; y >= yMin; y--) {
//            StringBuilder currentLine = new StringBuilder();
//            Set<Integer> xValues = pointsYX.get(y);
//            for (int x = xMin; x < xMin + xAxisSize; x++) {
//                if (xValues != null && xValues.contains(x)) {
//                    currentLine.append(marker);
//                } else if (x == 0) {
//                    currentLine.append("|");
//                } else if (y == 0) {
//                    currentLine.append("-");
//                } else {
//                    currentLine.append(" ");
//                }
//            }
//            System.out.println(currentLine);
//        }
//    }

    static Map<Integer, Set<Integer>> invert(Map<Integer, Integer> pointsXY) {
        Map<Integer, Set<Integer>> pointsYX = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : pointsXY.entrySet()) {
            int x = entry.getKey();
            int y = entry.getValue();
            if (pointsYX.containsKey(y)) {
                pointsYX.get(y).add(x);
            } else {
                Set<Integer> set = new HashSet<>();
                set.add(x);
                pointsYX.put(y, set);
            }
        }
        return pointsYX;
    }
}
