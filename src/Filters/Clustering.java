package Filters;

import java.util.ArrayList;

public class Clustering {
    private ArrayList<Cluster> clusters;
    private ArrayList<Point> points;
    private int k;
    private int[] imageSize;
    private ArrayList<int[]> centers;

    public Clustering(short[][] red, short[][] green, short[][] blue, short[] targetColor, int k){
        this.imageSize = new int[]{red[0].length, red.length};
        this.k = k;
        points = getPoints(red, green, blue, targetColor);
        clusters = initClusters(k, imageSize);
        centers = new ArrayList<>();
    }

    private ArrayList<Point> getPoints(short[][] red, short[][] green, short[][] blue, short[] targetColor) {
        ArrayList<Point> points = new ArrayList<>();
        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[r].length; c++) {
                if (red[r][c] == targetColor[0] && green[r][c] == targetColor[1] && blue[r][c] == targetColor[2]) {
                    points.add(new Point(c, r));
                }
            }
        }
        return points;
    }

    public void cluster() {
        while (true) {
//        for (int i = 0; i < 20; i++) {
            clearClusters(clusters);
            assignPointsToClusters(points, clusters);
            reCalculateClusters(clusters);
            boolean done = true;
            for (Cluster c : clusters) {
                if (!c.sameLoc()) done = false;
            }
            if (done) break;
        }
        for (Cluster c : clusters) {
            centers.add(new int[]{c.getX(), c.getY()});
        }
    }

    public ArrayList<int[]> getCenters() {
        return centers;
    }

    private static ArrayList<Cluster> initClusters(int k, int[] imageSize) {
        ArrayList<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            int x = (int) (Math.random()*imageSize[0]);
            int y = (int) (Math.random()*imageSize[1]);
            clusters.add(new Cluster(x, y));
        }
        return clusters;
    }

    private static void reCalculateClusters(ArrayList<Cluster> clusters) {
        for (Cluster c : clusters) {
            c.reclaculateCenter();
        }
    }

    private static void assignPointsToClusters(ArrayList<Point> points, ArrayList<Cluster> clusters) {
        for (Point p : points) {
            double[] distances = new double[clusters.size()];
            for (int j = 0; j < clusters.size(); j++) {
                distances[j] = p.distance(clusters.get(j));
            }
            clusters.get(minIndex(distances)).addPoint(p);
        }
    }

    private static void clearClusters(ArrayList<Cluster> clusters) {
        for (Cluster c : clusters) {
            c.clearPoints();
        }
    }

    public static int minIndex(double[] list) {
        double min = list[0];
        int minIndex = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i] < min) {
                min = list[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
}

class Cluster {
    private int x, y, lastX, lastY;
    ArrayList<Point> points;

    public Cluster(int x, int y) {
        this.x = x;
        this.y = y;
        this.lastX = 0;
        this.lastY = 0;
        points = new ArrayList<>();
    }

    public void addPoint(Point p) {
        points.add(p);
    }

    public void clearPoints() {
        points = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public void reclaculateCenter() {
        double sumX = 0, sumY = 0;
        for (Point p : points) {
            sumX += p.getX();
            sumY += p.getY();
        }
        this.x = (int) (sumX/points.size());
        this.y = (int) (sumY/points.size());
    }

    public boolean sameLoc(){
        return distance(x, y, lastX, lastY) < 0.01;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }
}

class Point {
    private int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Cluster cluster) {
        return Math.sqrt(Math.pow(cluster.getX() - x, 2) + Math.pow(cluster.getY() - y, 2));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}