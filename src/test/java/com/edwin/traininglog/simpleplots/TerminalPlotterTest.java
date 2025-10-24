package com.edwin.traininglog.simpleplots;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TerminalPlotterTest {
    @Test
    public void plotTest() {
        Random random = new Random();
        TerminalPlotter plotter = new TerminalPlotter();
        Map<Integer, Integer> points = new HashMap<>();
        for (int i = 0; i < 365; i++) {
            if (random.nextBoolean()) {
                points.put(i, 100 + random.nextInt(10) * 5);
            }
        }
        plotter.plot(points);
    }

}