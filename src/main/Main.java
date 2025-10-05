package main;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("BSP Dungeon Generator Starting...");
        
        SwingUtilities.invokeLater(() -> {
            new DungeonGUI();
        });
    }
}
