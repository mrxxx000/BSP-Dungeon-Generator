package main;

import java.util.*;

public class BSPDungeon {

    static final int DUNGEON_WIDTH = 60;
    static final int DUNGEON_HEIGHT = 30;
    static final int MINIMUM_ROOM_SIZE = 4;
    static final int MAXIMUM_SPLIT_DEPTH = 6;

    static char[][] dungeonMap = new char[DUNGEON_HEIGHT][DUNGEON_WIDTH];
    static Random randomGenerator = new Random();

    static class Rectangle {
        int x, y, width, height;
        Rectangle leftChild, rightChild;
        Room containedRoom;

        Rectangle(int x, int y, int width, int height) {
            this.x = x; this.y = y; this.width = width; this.height = height;
        }

        boolean isLeafNode() {
            return leftChild == null && rightChild == null;
        }

        void splitRecursively(int currentDepth) {
            if (currentDepth >= MAXIMUM_SPLIT_DEPTH) return;

            boolean shouldSplitHorizontally = randomGenerator.nextBoolean();
            if (width > height && width / (float)height >= 1.25f) shouldSplitHorizontally = false;
            else if (height > width && height / (float)width >= 1.25f) shouldSplitHorizontally = true;

            if (shouldSplitHorizontally) {
                if (height < MINIMUM_ROOM_SIZE * 2) return;
                int splitPosition = randomGenerator.nextInt(height - MINIMUM_ROOM_SIZE * 2) + MINIMUM_ROOM_SIZE;
                leftChild = new Rectangle(x, y, width, splitPosition);
                rightChild = new Rectangle(x, y + splitPosition, width, height - splitPosition);
            } else {
                if (width < MINIMUM_ROOM_SIZE * 2) return;
                int splitPosition = randomGenerator.nextInt(width - MINIMUM_ROOM_SIZE * 2) + MINIMUM_ROOM_SIZE;
                leftChild = new Rectangle(x, y, splitPosition, height);
                rightChild = new Rectangle(x + splitPosition, y, width - splitPosition, height);
            }

            leftChild.splitRecursively(currentDepth + 1);
            rightChild.splitRecursively(currentDepth + 1);
        }

        List<Rectangle> getAllLeafNodes() {
            List<Rectangle> leafNodes = new ArrayList<>();
            if (isLeafNode()) leafNodes.add(this);
            else {
                if (leftChild != null) leafNodes.addAll(leftChild.getAllLeafNodes());
                if (rightChild != null) leafNodes.addAll(rightChild.getAllLeafNodes());
            }
            return leafNodes;
        }
    }

    static class Room {
        int leftX, topY, rightX, bottomY;

        Room(int leftX, int topY, int rightX, int bottomY) {
            this.leftX = leftX; this.topY = topY; this.rightX = rightX; this.bottomY = bottomY;
        }

        int getCenterX() { return (leftX + rightX) / 2; }
        int getCenterY() { return (topY + bottomY) / 2; }
    }

    static void carveRoom(Room room) {
        for (int y = room.topY; y < room.bottomY; y++) {
            for (int x = room.leftX; x < room.rightX; x++) {
                dungeonMap[y][x] = '.';
            }
        }
    }

    static void connectRooms(Room firstRoom, Room secondRoom) {
        int startX = firstRoom.getCenterX();
        int startY = firstRoom.getCenterY();
        int endX = secondRoom.getCenterX();
        int endY = secondRoom.getCenterY();

        if (randomGenerator.nextBoolean()) {
            for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) dungeonMap[startY][x] = '.';
            for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) dungeonMap[y][endX] = '.';
        } else {
            for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) dungeonMap[y][startX] = '.';
            for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) dungeonMap[endY][x] = '.';
        }
    }

    public static char[][] generate() {
        for (int y = 0; y < DUNGEON_HEIGHT; y++)
            Arrays.fill(dungeonMap[y], '#');

        Rectangle rootSpace = new Rectangle(0, 0, DUNGEON_WIDTH, DUNGEON_HEIGHT);
        rootSpace.splitRecursively(0);

        List<Rectangle> finalSpaces = rootSpace.getAllLeafNodes();
        List<Room> allRooms = new ArrayList<>();

        for (Rectangle space : finalSpaces) {
            int roomWidth = randomGenerator.nextInt(space.width - 3) + 3;
            int roomHeight = randomGenerator.nextInt(space.height - 3) + 3;
            int roomX = space.x + randomGenerator.nextInt(Math.max(1, space.width - roomWidth));
            int roomY = space.y + randomGenerator.nextInt(Math.max(1, space.height - roomHeight));

            Room room = new Room(roomX, roomY, roomX + roomWidth, roomY + roomHeight);
            carveRoom(room);
            allRooms.add(room);
        }

        for (int i = 0; i < allRooms.size() - 1; i++) {
            connectRooms(allRooms.get(i), allRooms.get(i + 1));
        }
        
        return dungeonMap;
    }

    static void printMap() {
        for (int y = 0; y < DUNGEON_HEIGHT; y++) {
            for (int x = 0; x < DUNGEON_WIDTH; x++) {
                System.out.print(dungeonMap[y][x]);
            }
            System.out.println();
        }
    }
}