package Filters;

import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;

public class BetterColorMask implements PixelFilter, Interactive {

    private double targetHue;

    public BetterColorMask() {
        targetHue = 100.0;
    }
    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        short[] target = new short[]{200, 200, 200};
        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[r].length; c++) {
                if (diff(targetHue, getHue(red[r][c], green[r][c], blue[r][c])) <= 6) {
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

    public double getHue(double r, double g, double b) {
        double R = (r / 255.0);
        double G = (g / 255.0);
        double B = (b / 255.0);

        double hue = 0;
        double max = Math.max(R , Math.max(G, B));
        double min = Math.min(R , Math.min(G, B));
        if (max - min == 0) hue = 0;
        else if (max == R) hue = ((G - B) /(max-min)) % 6;
        else if (max == G) hue = 2.0 + (B - R) /(max-min);
        else hue = 4.0 + (R - G) /(max-min);

        hue *= 60;
        while (hue < 0) hue += 360;
        return (short) hue;
    }

    public double distance(double r1, double g1, double b1, double r2, double g2, double b2) {
        return Math.sqrt(Math.pow(r1-r2, 2) + Math.pow(g1-g2, 2) + Math.pow(b1-b2, 2));
    }

    public double diff(double a, double b) {
        return Math.abs(a - b);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        targetHue = getHue(red[mouseY][mouseX], green[mouseY][mouseX], blue[mouseY][mouseX]);
    }

    @Override
    public void keyPressed(char key) {

    }
}

