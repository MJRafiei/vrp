package ir.viratech.qaaf.thesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class DenseRegionLocator {

    ArrayList<Point> points;
    double minX, maxX, minY, maxY;
    double area;
    int population;

    public void readPoints(String path) {
        ArrayList<Point> points = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String[] data = scanner.nextLine().split(",");
            if (data.length < 3)
                continue;
            Point p = new Point(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Integer.parseInt(data[3]));
            p.deliverCapacity = Integer.parseInt(data[2]);
            points.add(p);
        }

        points.sort(new PointComparatorByY());

        this.minY = points.get(0).y;
        this.maxY = points.get(points.size() - 1).y;

        points.sort(new PointComparatorByX());

        this.minX = points.get(0).x;
        this.maxX = points.get(points.size() - 1).x;

        this.points = points;
        this.area = (this.maxX - this.minX) * (this.maxY - this.minY);
        System.out.println(this.area);

        this.generateLoyalty();

        for (Point p : points) {
            this.population += p.loyalty;
        }

    }

    
    private double getGaussian(double aMean, double aVariance){
        Random fRandom = new Random();
        return aMean + fRandom.nextGaussian() * aVariance;
    }
  

    public void generateLoyalty () {
        int seed = 0, tmp;
        for (Point p : this.points) {
            if  (p.x > 51.34 && p.x < 51.48 && p.y > 35.75) {
                seed = 3;
            }
            else {
                seed = 1;
            }
            tmp = (int) getGaussian(seed, 2); 
            p.loyalty = tmp < 0 ? 0 : tmp;
        }
    }

    public double getDistance (Point p1, Point p2) {
        return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
    }

    public List<Rectangle> findDenseRegions(double countThreshold, double populationThreshold, double areaThreshold) {
        Point[] points = new Point[1];
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("out.txt"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ArrayList<Rectangle> result = new ArrayList<>();
        points = this.points.toArray(points);
        // for (Point pp : points) {
        // System.out.println(pp);
        // }
        // try {
        // TimeUnit.SECONDS.sleep(55);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        for (int i = 0; i < points.length - 1; i++) {
            for (int j = i + (int) (populationThreshold * points.length); j < points.length; j++) {
                System.out.println("(" + i + ", " + j + ") --> " + result.size());
                List<Point> partialPoints = new ArrayList<>();
                for (int t = i; t <= j; t++) {
                    partialPoints.add(points[t]);
                }
                partialPoints.sort(new PointComparatorByY());
                int[] partialSum = new int[partialPoints.size()];
                int sum = 0;
                for (int k = 0; k < partialPoints.size(); k++) {
                    sum += partialPoints.get(k).loyalty;
                    partialSum[k] = sum;
                }
                for (int a = 0; a < partialPoints.size(); a++) {
                    for (int b = a + 1; b < partialPoints.size(); b++) {
                        if (partialPoints.get(a).y <= points[i].y && partialPoints.get(a).y <= points[j].y
                                && partialPoints.get(b).y >= points[i].y && partialPoints.get(b).y >= points[j].y) {
                            int weightedPopulation = partialSum[b] - partialSum[a] + points[i].loyalty + points[b].loyalty;
                            int count = b - a + 2;
                            Rectangle tmp = new Rectangle(points[i], points[j], partialPoints.get(b),
                                    partialPoints.get(a), weightedPopulation);
                            double popRatio = (double) tmp.population / this.population;
                            double countRatio = (double) count / this.points.size();
                            double areaRatio = tmp.area / this.area;
                            // pw.write(popRatio + " , " + areaRatio + "\n");
                            // System.out.println(popRatio + " , " + areaRatio);
                            if (countRatio > countThreshold && popRatio > populationThreshold && areaRatio < areaThreshold) {
                                if (result.size() < 1)
                                    result.add(tmp);
                                else if (tmp.compareTo(result.get(result.size() - 1)) < 0)
                                    result.add(tmp);
                            }

                        }
                    }
                }
            }
        }

        result.sort(new Comparator<Rectangle>() {

            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                if (o1.density < o2.density)
                    return 1;
                else
                    return -1;
            }

        });
        ;

        return result;
    }

    public ArrayList<Point> cropRectangle(Rectangle rectangle) {
        System.out.println(rectangle);
        ArrayList<Point> result = new ArrayList<>();
        for (Point p : this.points) {
            if (isPointInRectangle(rectangle, p)) {
                result.add(p);
            }
        }
        return result;
    }

    public boolean isPointInRectangle(Rectangle rectangle, Point point) {
        return point.x <= rectangle.right.x && point.x >= rectangle.left.x && point.y <= rectangle.up.y
                && point.y >= rectangle.bottom.y;
    }

    public void generateOutput(ArrayList<Point> points, String path) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String headers = "lng,lat,deliverCapacity,loyalty";
        pw.write(headers);
        pw.write("\n");
        for (Point point : points) {
            pw.write(point.x + "," + point.y + "," + point.deliverCapacity + "," + point.loyalty);
            if (!points.get(points.size() - 1).equals(point)) {
                pw.write("\n");
            }
        }
        pw.flush();
        pw.close();
    }

    public ArrayList<Point> subtractLists(ArrayList<Point> list1, ArrayList<Point> list2) {
        ArrayList<Point> result = new ArrayList<>(list1);
        result.removeAll(list2);
        return result;
    }

    public static void main(String[] args) {
        DenseRegionLocator obj = new DenseRegionLocator();
        obj.readPoints("Points.csv");
        Point[] points = new Point[1];
        points = obj.points.toArray(points);
        for (Point p : points)
            System.out.println(p.x + ", " + p.y + ", " + p.loyalty);
        List<Rectangle> result = obj.findDenseRegions(0.25, 0.55, 0.6);
        ArrayList<Point> loyalCustomers = obj.cropRectangle(result.get(0));
        obj.generateOutput(loyalCustomers, "loyal.csv");
        ArrayList<Point> nonLoyalCustomers = obj.subtractLists(obj.points, loyalCustomers);
        obj.generateOutput(nonLoyalCustomers, "nonloyal.csv");
        obj.generateOutput(obj.points, "pointswithloyalty");
    }
}

class Point {
    double x, y;
    int loyalty, deliverCapacity;

    public Point(double x, double y, int loyalty) {
        this.x = x;
        this.y = y;
        this.loyalty = loyalty;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

}

class Rectangle implements Comparable<Rectangle>, Comparator<Rectangle> {
    Point left, right, up, bottom;
    double area, density;
    int population;

    public Rectangle(Point left, Point right, Point up, Point bottom, int population) {
        this.left = left;
        this.right = right;
        this.up = up;
        this.bottom = bottom;
        this.population = population;
        this.area = (this.right.x - this.left.x) * (this.up.y - this.bottom.y);
        this.density = this.population / this.area;
    }

    @Override
    public String toString() {
        return this.area + " , " + this.population + " , " + this.density + " , " + this.left + " , " + this.right
                + " , " + this.up + " , " + this.bottom;
    }

    @Override
    public int compareTo(Rectangle o) {
        if (this.density < o.density)
            return 1;
        else
            return -1;
    }

    @Override
    public int compare(Rectangle o1, Rectangle o2) {
        if (o1.density < o2.density)
            return 1;
        else
            return -1;
    }

}

class PointComparatorByX implements Comparator<Point> {

    @Override
    public int compare(Point o1, Point o2) {
        if (o1.x > o2.x)
            return 1;
        else
            return -1;
    }
}

class PointComparatorByY implements Comparator<Point> {

    @Override
    public int compare(Point o1, Point o2) {
        if (o1.y > o2.y)
            return 1;
        else
            return -1;
    }
}
