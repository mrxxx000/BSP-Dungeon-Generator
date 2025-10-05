package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DungeonGUI extends JFrame {
    
    private static final int CELL_SIZE = 12;
    private DungeonPanel dungeonPanel;
    private JButton generateButton;
    private JLabel statusLabel;
    
    public DungeonGUI() {
        initializeGUI();
        generateInitialDungeon();
    }
    
    private void initializeGUI() {
        setTitle("BSP Dungeon Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create the dungeon display panel
        dungeonPanel = new DungeonPanel();
        dungeonPanel.setPreferredSize(new Dimension(
            BSPDungeon.DUNGEON_WIDTH * CELL_SIZE, 
            BSPDungeon.DUNGEON_HEIGHT * CELL_SIZE
        ));
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        
        // Create status label
        statusLabel = new JLabel("Dungeon generated successfully!");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add components to frame
        add(new JScrollPane(dungeonPanel), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        generateButton = new JButton("Generate New Dungeon");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateNewDungeon();
            }
        });
        
        JButton saveButton = new JButton("Save as Text");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDungeonAsText();
            }
        });
        
        panel.add(generateButton);
        panel.add(saveButton);
        
        return panel;
    }
    
    private void generateInitialDungeon() {
        BSPDungeon.generate();
        if (dungeonPanel != null) {
            dungeonPanel.repaint();
        }
    }
    
    private void generateNewDungeon() {
        statusLabel.setText("Generating new dungeon...");
        
        // Generate new dungeon in a separate thread to keep UI responsive
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                BSPDungeon.generate();
                return null;
            }
            
            @Override
            protected void done() {
                dungeonPanel.repaint();
                statusLabel.setText("New dungeon generated successfully!");
            }
        };
        
        worker.execute();
    }
    
    private void saveDungeonAsText() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("dungeon.txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.PrintWriter writer = new java.io.PrintWriter(file);
                
                char[][] map = BSPDungeon.dungeonMap;
                for (int y = 0; y < BSPDungeon.DUNGEON_HEIGHT; y++) {
                    for (int x = 0; x < BSPDungeon.DUNGEON_WIDTH; x++) {
                        writer.print(map[y][x]);
                    }
                    writer.println();
                }
                
                writer.close();
                statusLabel.setText("Dungeon saved to: " + file.getName());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving file: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Custom panel to draw the dungeon
    private class DungeonPanel extends JPanel {
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            char[][] map = BSPDungeon.dungeonMap;
            
            for (int y = 0; y < BSPDungeon.DUNGEON_HEIGHT; y++) {
                for (int x = 0; x < BSPDungeon.DUNGEON_WIDTH; x++) {
                    char cell = map[y][x];
                    
                    // Set color based on cell type
                    if (cell == '#') {
                        g.setColor(Color.DARK_GRAY); // Walls
                    } else if (cell == '.') {
                        g.setColor(Color.LIGHT_GRAY); // Floor/corridors
                    } else {
                        g.setColor(Color.BLACK); // Unknown
                    }
                    
                    // Draw the cell
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    
                    // Draw border for better visibility
                    g.setColor(Color.BLACK);
                    g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
}