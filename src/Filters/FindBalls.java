package Filters;

import java.util.ArrayList;

public class FindBalls{
    int colorCount;
    ArrayList<int[][]> ballCenters;
    ArrayList<int[][]> ballList;
    ArrayList<int[]> points = new ArrayList<>();
    public ArrayList<int[][]> findClusters(short[][] red, short[][] green, short[][] blue, ArrayList<short[]> colors) {
        setColorCount(colors);
        setBallList(red, green, blue, colors);
        setRandomPoints();
        ballList = findBallCluster();

        return ballCenters;
    }
    public void setColorCount(ArrayList<short[]> colors){
        colorCount = colors.size();
    }
    public void setBallList(short[][] red, short[][] green, short[][] blue, ArrayList<short[]> colors) {
        int[][] loc = new int[colorCount][2];
        for (int color = 0; color < colorCount; color++) {
            for (int i = 0; i < red.length; i++) {
                for (int j = 0; j < red[i].length; j++) {
                    if(checkColor(red[i][j], green[i][j], blue[i][j], colors.get(color))){
                        loc[color][0] = i;
                        loc[color][1] = j;
                        ballList.add(loc);
                    }
                }
            }
        }
    }

    public void setRandomPoints(){
        for (int i = 0; i < colorCount; i++) {
            for (int j = 0; j < ballList.size(); j++) {
                int num = (int) (Math.random() * ballList.size());
                int[] loc = ballList.get(num)[i];
                points.add(loc);
            }
        }
    }
    public  ArrayList<int[][]> findBallCluster() {
        //ballList --- listed balls
        //points --- set points that needed ball points to be assigned
        ArrayList<int[][]> coloredBallsLoc = new ArrayList<>();
        //Loop over the points, and find closest ballList points
        //find close points by canceling points that are far from the average of the given point
        //Add points that are far from the average but close to the given point to the coloredBallLoc list for each color.
        int[][] loc = new int[points.size()][2];
        for (int i = 0; i < points.size(); i++) {
            double rowSum = 0;
            double colSum = 0;
            double count = 0;
            for (int[][] ball : ballList){
                if (ball[i] == points.get(i)){
                    rowSum += ball[i][0];
                    colSum += ball[i][1];
                    count++;
                }
            }
            double rowAvg = rowSum / count;
            double colAvg = colSum / count;

            for (int[][] ball : ballList) {
                if (ball[i][0] - points.get(i)[0] < rowAvg){
                    if (ball[i][1] - points.get(i)[1] < colAvg){
                        loc[i][0] = ball[i][0];
                        loc[i][1] = ball[i][0];
                        coloredBallsLoc.add(loc);
                    }
                }
            }

        }


        return coloredBallsLoc;
    }


    //find the center of the clusters


    public static boolean checkColor(short red, short green, short blue, short[] color){
        if (red == color[0]){
            if (green == color[1]){
                if (blue == color[2]){
                    return true;
                }
            }
        }
        return false;
    }


}
