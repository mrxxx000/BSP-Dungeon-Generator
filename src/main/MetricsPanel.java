package main;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MetricsPanel extends JPanel {
    
    private static final int PADDING = 40;
    private static final int LABEL_PADDING = 25;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Dungeon Generation Metrics", PADDING, PADDING / 2);
        
        // Draw current metrics
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        int currentRooms = BSPDungeon.getRoomCount();
        double currentCoverage = BSPDungeon.getMapCoverage();
        
        g2d.drawString("Current Room Count: " + currentRooms, PADDING, PADDING + 20);
        g2d.drawString(String.format("Current Map Coverage: %.1f%%", currentCoverage), 
                       PADDING, PADDING + 40);
        
        // Draw graph area
        int graphWidth = width - 2 * PADDING;
        int graphHeight = height - PADDING - 100;
        
        drawGraph(g2d, PADDING, PADDING + 60, graphWidth, graphHeight);
    }
    
    private void drawGraph(Graphics2D g2d, int x, int y, int width, int height) {
        List<Integer> roomCounts = BSPDungeon.getRoomCountHistory();
        List<Double> coverages = BSPDungeon.getMapCoverageHistory();
        
        if (roomCounts.isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.drawString("Generate a dungeon to see metrics", x + 10, y + 20);
            return;
        }
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x, y + height, x, y); // Y-axis
        g2d.drawLine(x, y + height, x + width, y + height); // X-axis
        
        // Draw axis labels
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Generation #", x + width - 50, y + height + 20);
        
        // Draw room count graph (left side)
        drawLineGraph(g2d, x, y, width, height, roomCounts, 
                     0, 35, Color.BLUE, "Rooms");
        
        // Draw coverage graph (right side)
        drawLineGraph(g2d, x + width/2, y, width/2, height, coverages, 
                     0, 100, new Color(0, 180, 0), "Coverage %");
    }
    
    private void drawLineGraph(Graphics2D g2d, int x, int y, int width, int height,
                              List<? extends Number> data, double minVal, double maxVal,
                              Color color, String label) {
        if (data.isEmpty()) return;
        
        // Find actual min/max
        double actualMin = minVal;
        double actualMax = maxVal;
        
        for (Number n : data) {
            double val = n.doubleValue();
            if (val < actualMin) actualMin = val;
            if (val > actualMax) actualMax = val;
        }
        
        double range = actualMax - actualMin;
        if (range == 0) range = 1;
        actualMin -= range * 0.1;
        actualMax += range * 0.1;
        
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        
        int pointWidth = Math.max(2, width / Math.max(1, data.size()));
        
        for (int i = 0; i < data.size() - 1; i++) {
            double val1 = data.get(i).doubleValue();
            double val2 = data.get(i + 1).doubleValue();
            
            int x1 = x + (i * width) / data.size();
            int x2 = x + ((i + 1) * width) / data.size();
            
            int y1 = y + height - (int) ((val1 - actualMin) / (actualMax - actualMin) * height);
            int y2 = y + height - (int) ((val2 - actualMin) / (actualMax - actualMin) * height);
            
            g2d.drawLine(x1, y1, x2, y2);
            
            // Draw points
            g2d.fillOval(x1 - 2, y1 - 2, 4, 4);
        }
        
        // Draw last point
        if (!data.isEmpty()) {
            double lastVal = data.get(data.size() - 1).doubleValue();
            int lastX = x + width - pointWidth;
            int lastY = y + height - (int) ((lastVal - actualMin) / (actualMax - actualMin) * height);
            g2d.fillOval(lastX - 2, lastY - 2, 4, 4);
        }
        
        // Draw label
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.drawString(label, x + 5, y - 5);
        
        // Draw Y-axis labels
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        g2d.setColor(Color.DARK_GRAY);
        String minLabel = String.format("%.0f", actualMin);
        String maxLabel = String.format("%.0f", actualMax);
        
        g2d.drawString(maxLabel, x - LABEL_PADDING, y + 5);
        g2d.drawString(minLabel, x - LABEL_PADDING, y + height);
    }
}
