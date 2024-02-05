package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class BasicColorMask implements PixelFilter {
    short[] target;
    short[][] red;
    short[][] green;
    short[][] blue;

    @Override
    public DImage processImage(DImage img) {
        red = img.getRedChannel();
        green = img.getGreenChannel();
        blue = img.getBlueChannel();

        target = new short[]{200, 200, 200};
        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[r].length; c++) {
                if (distance(target[0], target[1], target[2], red[r][c], green[r][c], blue[r][c]) <= 30) {
                    red[r][c] = 255;
                    green[r][c] = 255;
                    blue[r][c] = 255;
                } else {
                    red[r][c] = 0;
                    green[r][c] = 0;
                    blue[r][c] = 0;
                }
            }
        }

        img.setColorChannels(red, green, blue);
        return img;
    }

    public double distance(int r1, int g1, int b1, int r2, int g2, int b2) {
        return Math.sqrt(Math.pow(r1-r2, 2) + Math.pow(g1-g2, 2) + Math.pow(b1-b2, 2));
    }


    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        target[0] = red[mouseY][mouseX];
        target[1] = green[mouseY][mouseX];
        target[2] = blue[mouseY][mouseX];
    }
}

