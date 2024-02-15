package Filters;

import Interfaces.PixelFilter;
import Interfaces.*;
import Filters.*;
import core.DImage;

import javax.swing.*;
import java.util.ArrayList;


//TODO: Downscaling/NoiseReduction
//TODO: MultiColor Ball Finding
public class BasicColorMask implements PixelFilter, Interactive {
    ArrayList<short[]> targets;
    int k = 35;
    double kRatios = 0.01;

    @Override
    public DImage processImage(DImage img) {

//        img = new Convolution(Convolution.generateBoxBlurKernel(5)).processImage(img);

        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();
        short[][] newRed = new short[red.length][red[0].length];
        short[][] newGreen = new short[green.length][green[0].length];
        short[][] newBlue = new short[blue.length][blue[0].length];

        if (targets == null) targets = new ArrayList<>();
        System.out.println("Starting mask");
        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[r].length; c++) {
                for (short[] target : targets) {
                    mask(red[r][c], green[r][c], blue[r][c], target, k, newRed, newGreen, newBlue, c, r);
                }
            }
        }
        System.out.println("Finished mask");


        System.out.println("Threshold: " + k);

        for (short[] target : targets) {
            System.out.println("finding center for " + target[0] + ", " + target[1] + ", " + target[2]);
            int sumR = 0, sumC = 0, count = 0;
            for (int r = 0; r < red.length; r++) {
                for (int c = 0; c < red[r].length; c++) {
                    if (newRed[r][c] == target[0] && newGreen[r][c] == target[1] && newBlue[r][c] == target[2]) {
                        sumR += r;
                        sumC += c;
                        count++;
                    }
                }
            }
            int rCenter = sumR/ (count != 0 ? count : 1), cCenter = sumC/ (count != 0 ? count : 1);
//            for (int r = Math.max(rCenter - 2, 0); r < Math.min(rCenter + 3, img.getHeight()); r++) {
//                for (int c = Math.max(cCenter - 2, 0); c < Math.min(cCenter + 3, img.getWidth()); c++) {
//                    red[r][c] = 0;
//                    green[r][c] = 0;
//                    blue[r][c] = 0;
//                    newRed[r][c] = 255;
//                    newGreen[r][c] = 255;
//                    newBlue[r][c] = 255;
//                }
//            }
            System.out.println("Center: " + rCenter + ", " + cCenter);
        }
        System.out.println("have center");
        for (short[] target : targets) {
            Clustering clustering = new Clustering(red, green, blue, target, k);
            clustering.cluster();
            for (int[] loc : clustering.getCenters()) {
                for (int r = Math.max(loc[0] - 2, 0); r < Math.min(loc[0] + 3, img.getHeight()); r++) {
                    for (int c = Math.max(loc[1] - 2, 0); c < Math.min(loc[1] + 3, img.getWidth()); c++) {
                        red[r][c] = 0;
                        green[r][c] = 0;
                        blue[r][c] = 0;
                        newRed[r][c] = 255;
                        newGreen[r][c] = 255;
                        newBlue[r][c] = 255;
                    }
                }
            }
        }
        System.out.println("done marking centers");

        img.setColorChannels(newRed, newGreen, newBlue);
        System.out.println("Finished first filter");
//        img.setColorChannels(red, green, blue);
//        img = new FindCenters().processImage(img);
        return img;
    }

    public void mask(int r, int g, int b, short[] target, int k, short[][] red, short[][] green, short[][] blue, int x, int y){
        if (distance(target[0], target[1], target[2], r, g, b) <= k) {
            red[y][x] = target[0];
            green[y][x] = target[1];
            blue[y][x] = target[2];
        }
    }

    public void maskRatios(int r, int g, int b, short[] target, int k, short[][] red, short[][] green, short[][] blue, int x, int y){
        if (distanceRatios(target[0], target[1], target[2], r, g, b) <= k) {
            red[y][x] = target[0];
            green[y][x] = target[1];
            blue[y][x] = target[2];
        }
    }

    public double distance(int r1, int g1, int b1, int r2, int g2, int b2) {
        return Math.sqrt(Math.pow(r1-r2, 2) + Math.pow(g1-g2, 2) + Math.pow(b1-b2, 2));
    }

    public double distanceRatios(int r1, int g1, int b1, int r2, int g2, int b2) {
        double tRB = (double) r1/b1;
        double tRG = (double) r1/g1;
        double oRB = (double) r2/b2;
        double oRG = (double) r2/g2;
        double diffA = Math.abs(tRB - oRB);
        double diffB = Math.abs(tRG - oRG);
        return (diffA + diffB)/2;
    }


    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

//        short num = Short.parseShort(JOptionPane.showInputDialog("How many balls?"));
        targets.add(new short[]{red[mouseY][mouseX], green[mouseY][mouseX], blue[mouseY][mouseX]});
        System.out.println("added target");
    }

    @Override
    public void keyPressed(char key) {
        if (key == '='){
            k++;
        }
        if (key == '-'){
            k--;
        }
    }
}

