package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;

public class FindCenters implements PixelFilter {

    public FindCenters() {
    }
    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();
        short[][] tempRed = img.getRedChannel();
        short[][] tempGreen = img.getGreenChannel();
        short[][] tempBlue = img.getBlueChannel();
        short[][][] temp = new short[][][]{tempRed, tempGreen, tempBlue};

        ArrayList<int[]> centers = new ArrayList<>();

        while (!isEmpty(temp)) {
            int initialX = 0, initialY = 0;
            while (isBlack(temp, initialY, initialX)) {
                initialX = (int) (Math.random() * red[0].length);
                initialY = (int) (Math.random() * red.length);
            }
            int left = initialX, right = initialX, top = initialY, bottom = initialY;
            while (left >= 0 && isBlack(temp, initialY, left)) left--;
            while (right < red[0].length && isBlack(temp, initialY, right)) right++;
            int x = (left+right)/2;
            while (top >= 0 && isBlack(temp, top, x)) top--;
            while (bottom < red.length && isBlack(temp, bottom, x)) bottom++;
            int y = (top+bottom)/2;
            centers.add(new int[]{x, y});
            for (short[][] color : temp) {
                for (int r = top; r < bottom; r++) {
                    for (int c = left; c < right; c++) {
                        color[r][c] = 0;
                    }
                }
            }
        }

//        img.setColorChannels(red, green, blue);
        img.setColorChannels(tempRed, tempGreen, tempBlue);
        return img;
    }

    private boolean isEmpty(short[][][] img) {
        for(short[][] color : img) {
            for (int r = 0; r < color.length; r++) {
                for (int c = 0; c < color[r].length; c++) {
                    if (color[r][c] != 0) return false;
                }
            }
        }
        return true;
    }

    private boolean isBlack(short[][][] img, int r, int c) {
        for(short[][] color : img) {
            if (color[r][c] != 0) return false;
        }
        return true;
    }
}
