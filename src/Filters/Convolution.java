package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.util.Arrays;

public class Convolution implements PixelFilter {
    private double[][] boxBlurKernel = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
    private double[][] prewittEdgeKernel = {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}};
    private double[][] sharpen = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
    private double[][] gaussianBlur = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};
    private double[][] betterGaussianBlur = {{0, 0, 0, 5, 0, 0, 0}, {0, 5, 18, 32, 18, 5, 0}, {0, 18, 64, 100, 64, 18, 0}, {5, 32, 100, 100, 100, 32, 5}, {0, 18, 64, 100, 64, 18, 0}, {0, 5, 18, 32, 18, 5, 0}, {0, 0, 0, 5, 0, 0, 0}};
    private double[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    private double[][] sobelY = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};
    private short[][] thinningA = {{0, 0, 0}, {-1, 255, -1}, {255, 255, 255}};
    private short[][] thinningB = {{-1, 0, 0}, {255, 255, 0}, {-1, 255, -1}};

    public Convolution() {
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] grid = img.getBWPixelGrid();
        short[][] newGrid = new short[grid.length][grid[0].length];
        short[][] rGrid = img.getRedChannel();
        short[][] gGrid = img.getGreenChannel();
        short[][] bGrid = img.getBlueChannel();
        short[][] newRGrid = new short[rGrid.length][rGrid[0].length];
        short[][] newGGrid = new short[gGrid.length][gGrid[0].length];
        short[][] newBGrid = new short[bGrid.length][bGrid[0].length];
        double[][] kernel = generateBoxBlurKernel(15);


        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
//                newGrid[r][c] = (short) Math.sqrt(Math.pow(getValue(grid, sobelX, r, c), 2) + Math.pow(getValue(grid, sobelY, r, c), 2));
//                newGrid[r][c] = (short) (newGrid[r][c] >= 80 ? 255 : 0);
                newRGrid[r][c] = (short) getValue(rGrid, kernel, r, c);
                newGGrid[r][c] = (short) getValue(gGrid, kernel, r, c);
                newBGrid[r][c] = (short) getValue(bGrid, kernel, r, c);
//                newRGrid[r][c] = (short) (newRGrid[r][c] >= 80 ? 255 : 0);
//                newGGrid[r][c] = (short) (newGGrid[r][c] >= 80 ? 255 : 0);
//                newBGrid[r][c] = (short) (newBGrid[r][c] >= 80 ? 255 : 0);
            }
        }
//        thinning(newGrid);

//        img.setPixels(newGrid);
        img.setColorChannels(newRGrid, newGGrid, newBGrid);
        return img;
    }

    private void thinning(short[][] grid) {
        for (int r = 1; r < grid.length - 1; r++) {
            for (int c = 1; c < grid[r].length - 1; c++) {
                System.out.println("Testing for (" + c + ", " + r + ")");
                if (grid[r][c] == 255) {
                    if (!totalThinning(grid, r, c)) grid[r][c] = 0;
                }
            }
        }
    }

    private boolean totalThinning(short[][] grid, int r, int c) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                if (!thinningMatch(grid, i == 0 ? thinningA : thinningB, j, r, c)) return false;
            }
        }
        return true;
    }

    public boolean thinningMatch(short[][] grid, short[][] ogKernel, int rotation, int y, int x) {
        short[][] subGrid = {Arrays.copyOfRange(grid[y-1], x-1, x+2), Arrays.copyOfRange(grid[y], x-1, x+2), Arrays.copyOfRange(grid[y+1], x-1, x+2)};
        short[][] kernel = new short[3][3];
        switch (rotation) {
            case 0:
                kernel = ogKernel;
                break;
            case 1:
                for (int r = 0; r < ogKernel.length; r++) {
                    for (int c = 0; c < ogKernel[r].length; c++) {
                        kernel[r][c] = ogKernel[2-c][r];
                    }
                }
                break;
            case 2:
                for (int r = 0; r < ogKernel.length; r++) {
                    for (int c = 0; c < ogKernel[r].length; c++) {
                        kernel[r][c] = ogKernel[2-r][2-c];
                    }
                }
                break;
            case 3:
                for (int r = 0; r < ogKernel.length; r++) {
                    for (int c = 0; c < ogKernel[r].length; c++) {
                        kernel[r][c] = ogKernel[c][2-r];
                    }
                }
                break;

        }
        for (int r = 0; r < subGrid.length; r++) {
            for (int c = 0; c < subGrid[r].length; c++) {
                if (kernel[r][c] >= 0) {
                    if (subGrid[r][c] != kernel[r][c]) return false;
                }
            }
        }
        return true;
    }

    public static short getValue(short[][] grid, double[][] kernel, int r, int c) {
        double sum = 0;
        double kernelWeight = 0;

        for (int kR = 0; kR < kernel.length; kR++) {
            for (int kC = 0; kC < kernel[kR].length; kC++) {
                try {
                    double weight = kernel[kR][kC];
                    sum += grid[r - (kernel.length/2) + kR][c - (kernel[kR].length/2) + kC] * weight;
                    kernelWeight += weight;
                } catch (IndexOutOfBoundsException ignored) {}
            }
        }

        int output;
        if (kernelWeight != 0) output = (int) (sum / kernelWeight);
        else output = (int) sum;
        if (output < 0) output = 0;
        else if (output > 255) output = 255;
        return (short) output;
    }

    public static double[][] generateBoxBlurKernel(int n) {
        double[][] arr = new double[n][n];
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[r].length; c++) {
                arr[r][c] = 1;
            }
        }
        return arr;
    }

    public static double[][] horizEdgeDetection(int n) {
        double[][] arr = new double[n][n];
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[r].length; c++) {
                if (r == arr.length/2) arr[r][c] = 2;
                else arr[r][c] = -1;
            }
        }
        return arr;
    }


    public static double[][] vertEdgeDetection(int n) {
        double[][] arr = new double[n][n];
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[r].length; c++) {
                if (c == arr.length/2) arr[r][c] = 2;
                else arr[r][c] = -1;
            }
        }
        return arr;
    }

    public static double[][] oneThirtyFiveEdgeDetection(int n) {
        double[][] arr = new double[n][n];
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[r].length; c++) {
                if (c == r) arr[r][c] = 2;
                else arr[r][c] = -1;
            }
        }
        return arr;
    }

    public static double[][] fourtyFiveEdgeDetection(int n) {
        double[][] arr = new double[n][n];
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[r].length; c++) {
                if (c + r == n-1) arr[r][c] = 2;
                else arr[r][c] = -1;
            }
        }
        return arr;
    }
}
