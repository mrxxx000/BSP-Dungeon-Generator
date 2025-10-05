# BSP Dungeon Generator

A procedural dungeon generator using Binary Space Partitioning (BSP) algorithm with a simple Swing GUI.

## Features

- **BSP Algorithm**: Uses recursive space partitioning to generate dungeon layouts
- **Visual GUI**: Simple Swing-based interface to visualize generated dungeons
- **Interactive Controls**: Generate new dungeons and save them as text files
- **Customizable Parameters**: Adjustable map size, room size, and recursion depth

## How to Run
Run: `java -cp build main.Main`

## How It Works

The BSP algorithm works by:
1. Starting with a rectangular area (the entire map)
2. Recursively splitting the area into smaller rectangles
3. Creating rooms within the leaf nodes of the BSP tree
4. Connecting rooms with corridors
