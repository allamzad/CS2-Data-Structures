package edu.caltech.cs2.lab07;

import edu.caltech.cs2.interfaces.IStack;
import edu.caltech.cs2.libraries.StdDraw;
import edu.caltech.cs2.datastructures.ArrayDeque;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Maze {
    public int n;                 // dimension of maze
    public boolean[][] north;     // is there a wall to north of cell i, j
    public boolean[][] east;
    public boolean[][] south;
    public boolean[][] west;
    public boolean[][] visited;
    public boolean done = false;
    public Point end;
    private static final int DRAW_WAIT = 0;

    public Maze(int n, String mazeFile) throws FileNotFoundException {
        this.n = n;
        end = new Point(n / 2, n / 2);
        StdDraw.setXscale(0, n + 2);
        StdDraw.setYscale(0, n + 2);
        init();

        Scanner scanner = new Scanner(new File(mazeFile));
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] tokens = line.split(" ");
            assert tokens.length == 3;
            String direction = tokens[0];
            int x = Integer.valueOf(tokens[1]);
            int y = Integer.valueOf(tokens[2]);
            switch (direction) {
                case "N":
                    north[x][y] = false;
                    break;
                case "S":
                    south[x][y] = false;
                    break;
                case "E":
                    east[x][y] = false;
                    break;
                case "W":
                    west[x][y] = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void init() {
        // initialze all walls as present
        north = new boolean[n+2][n+2];
        east  = new boolean[n+2][n+2];
        south = new boolean[n+2][n+2];
        west  = new boolean[n+2][n+2];
        for (int x = 0; x < n+2; x++) {
            for (int y = 0; y < n+2; y++) {
                north[x][y] = true;
                east[x][y]  = true;
                south[x][y] = true;
                west[x][y]  = true;
            }
        }
    }

    // draw the maze
    public void draw() {
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledCircle(n / 2.0 + 0.5, n / 2.0 + 0.5, 0.375);
        StdDraw.filledCircle(1.5, 1.5, 0.375);

        StdDraw.setPenColor(StdDraw.BLACK);
        for (int x = 1; x <= n; x++) {
            for (int y = 1; y <= n; y++) {
                if (south[x][y]) StdDraw.line(x, y, x + 1, y);
                if (north[x][y]) StdDraw.line(x, y + 1, x + 1, y + 1);
                if (west[x][y]) StdDraw.line(x, y, x, y + 1);
                if (east[x][y]) StdDraw.line(x + 1, y, x + 1, y + 1);
            }
        }
        StdDraw.show();
        StdDraw.pause(1000);
    }

    // Draws a blue circle at coordinates (x, y)
    private void selectPoint(Point point) {
        int x = point.x;
        int y = point.y;
        System.out.println("Selected point: (" + x + ", " + y + ")");
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
        StdDraw.pause(DRAW_WAIT);
    }

    /*
     * Returns an array of all children to a given point
     */
    public Point[] getChildren(Point point) {
        boolean[] walls = new boolean[4];
        walls[0] = this.north[point.x][point.y];
        walls[1] = this.east[point.x][point.y];
        walls[2] = this.south[point.x][point.y];
        walls[3] = this.west[point.x][point.y];
        int wallNum = 0;
        for (int i = 0; i < 4; i++) {
            if (walls[i]) {
                wallNum++;
            }
        }
        Point[] children = new Point[4 - wallNum];
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            if (!walls[i]) {
                Point newPoint;
                if (i == 0) {
                    newPoint = new Point(point.x, point.y + 1);
                } else if (i == 1) {
                    newPoint = new Point(point.x + 1, point.y);
                } else if (i == 2) {
                    newPoint = new Point(point.x, point.y - 1);
                } else {
                    newPoint = new Point(point.x - 1, point.y);
                }
                newPoint.parent = point;
                children[counter] = newPoint;
                counter++;
            }
        }
        return children;
    }

    public void solveDFSRecursiveStart() {
        Point start = new Point(1, 1);
        this.visited = new boolean[n + 1][n + 1];
        solveDFSRecursive(start);
    }

    /*
     * Solves the maze using a recursive DFS. Calls selectPoint()
     * when a point to move to is selected.
     */
    private void solveDFSRecursive(Point point) {
        selectPoint(point);
        if (point.equals(this.end)) {
            this.done = true;
        }
        this.visited[point.x][point.y] = true;
        Point[] children = this.getChildren(point);
        for (int i = 0; i < children.length; i++) {
            if (!this.visited[children[i].x][children[i].y] && !this.done) {
                solveDFSRecursive(children[i]);
            }
        }
    }

    /*
     * Solves the maze using an iterative DFS using a stack. Calls selectPoint()
     * when a point to move to is selected.
     */
    public void solveDFSIterative() {
        Point start = new Point(1, 1);
        this.visited = new boolean[n + 1][n + 1];
        IStack<Point> stack = new ArrayDeque<>();
        stack.push(start);
        selectPoint(start);
        this.visited[start.x][start.y] = true;

        while (!stack.peek().equals(this.end)) {
            ArrayList<Point> children = this.getUniqueChildren(stack.peek());
            if (children.isEmpty()) {
                stack.pop();
            } else {
                Point child = children.get(0);
                stack.push(child);
                selectPoint(child);
                this.visited[child.x][child.y] = true;
            }
        }
        this.done = true;
    }

    public ArrayList<Point> getUniqueChildren(Point point) {
        ArrayList<Point> pointArrayList = new ArrayList<>();
        int x = point.x;
        int y = point.y;
        if (!this.north[x][y] && !this.visited[x][y+1]) {
            pointArrayList.add(new Point(x, y+1));
        }
        if (!this.south[x][y] && !this.visited[x][y-1]) {
            pointArrayList.add(new Point(x, y-1));
        }
        if (!this.east[x][y] && !this.visited[x+1][y]) {
            pointArrayList.add(new Point(x+1, y));
        }
        if (!this.west[x][y] && !this.visited[x-1][y]) {
            pointArrayList.add(new Point(x-1, y));
        }
        return pointArrayList;
    }

}

