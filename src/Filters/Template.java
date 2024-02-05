package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class Template implements PixelFilter {

    @Override
    public DImage processImage(DImage img) {
        short[][] grid = img.getBWPixelGrid();

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {

            }
        }

        img.setPixels(grid);
        return img;
    }
}
