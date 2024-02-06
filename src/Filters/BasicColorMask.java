package Filters;

import Interfaces.PixelFilter;
import Interfaces.*;
import Filters.*;
import core.DImage;

import java.util.ArrayList;

public class BasicColorMask implements PixelFilter, Interactive {
    short[] target;
    ArrayList<short[]> targets;
    int k = 30;

    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();
        short[][] newRed = new short[red.length][red[0].length];
        short[][] newGreen = new short[green.length][green[0].length];
        short[][] newBlue = new short[blue.length][blue[0].length];

        img = new Convolution().processImage(img);

        if (targets == null) targets = new ArrayList<>();

//        if (target == null) target = new short[]{200, 200, 200};
        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[r].length; c++) {
                for (short[] target : targets) {
                    mask(red[r][c], green[r][c], blue[r][c], target, k, newRed, newGreen, newBlue, c, r);
                }
//                if (distance(target[0], target[1], target[2], red[r][c], green[r][c], blue[r][c]) <= k) {
//                    red[r][c] = target[0];
//                    green[r][c] = target[1];
//                    blue[r][c] = target[2];
//                } else {
//                    red[r][c] = 0;
//                    green[r][c] = 0;
//                    blue[r][c] = 0;
//                }
            }
        }

        System.out.println("Threshold: " + k);

        img.setColorChannels(newRed, newGreen, newBlue);
        return img;
    }

    public void mask(int r, int g, int b, short[] target, int k, short[][] red, short[][] green, short[][] blue, int x, int y){
        if (distance(target[0], target[1], target[2], r, g, b) <= k) {
            red[y][x] = target[0];
            green[y][x] = target[1];
            blue[y][x] = target[2];
        }
    }

    public double distance(int r1, int g1, int b1, int r2, int g2, int b2) {
        return Math.sqrt(Math.pow(r1-r2, 2) + Math.pow(g1-g2, 2) + Math.pow(b1-b2, 2));
    }


    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        targets.add(new short[]{red[mouseY][mouseX], green[mouseY][mouseX], blue[mouseY][mouseX]});
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

