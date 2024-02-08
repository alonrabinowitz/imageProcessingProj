package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class NoiseReduction implements PixelFilter {

    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        for (int r = 0; r < red.length-3; r+=3) {
            for (int c = 0; c < red[r].length-3; c+=3) {
                short[] reds = new short[9];
                short[] greens = new short[9];
                short[] blues = new short[9];

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        reds[i*3+j] = red[r+i][c+j];
                        greens[i*3+j] = green[r+i][c+j];
                        blues[i*3+j] = blue[r+i][c+j];
                    }
                }

                short[] newVals = new short[3];
                newVals[0] = getMode(reds);
                newVals[1] = getMode(greens);
                newVals[2] = getMode(blues);

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        red[r+i][c+j] = newVals[0];
                        green[r+i][c+j] = newVals[1];
                        blue[r+i][c+j] = newVals[2];
                    }
                }
            }
        }

        img.setColorChannels(red, green, blue);
        return img;
    }

    private short getMode(short[] vals) {
        short mode = 0;
        int maxCount = 0;
        for (short val : vals) {
            int count = 0;
            for (short val2 : vals) {
                if (val == val2) count++;
            }
            if (count > maxCount) {
                maxCount = count;
                mode = val;
            }
        }
        return mode;
    }
}
