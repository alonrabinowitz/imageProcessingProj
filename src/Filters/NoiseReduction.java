package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class NoiseReduction implements PixelFilter {

    @Override
    public DImage processImage(DImage img) {
        short[][] grid = img.getBWPixelGrid();

        for (int r = 0; r < grid.length-3; r+=3) {
            for (int c = 0; c < grid[r].length-3; c+=3) {

            }
        }

        img.setPixels(grid);
        return img;
    }
}
