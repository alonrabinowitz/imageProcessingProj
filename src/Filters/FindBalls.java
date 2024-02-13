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
        ballList = findBallCluster(red, green, blue, colors);

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
            for (int i = 0; i < ballList.size(); i++) {
                int num = (int) (Math.random() * ballList.size());
                int[] loc = ballList.get(num)[i];
                points.add(loc);
            }
        }
    }
    public  ArrayList<int[][]> findBallCluster(ArrayList<int[][]> ballList, ArrayList<int[]> points) {
        ArrayList<int[][]> coloredBallsLoc = new ArrayList<>();
        //find close points by canceling points that are far from the average

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
