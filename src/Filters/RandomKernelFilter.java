package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class RandomKernelFilter implements PixelFilter {
    int n = 3; // n x n kernel

    public DImage processImage(DImage img) {
        short[][] grid = img.getBWPixelGrid();
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        short[][] reGrid = edgeKernel(grid, n);
        reGrid = thershold(reGrid);
        reGrid = thinning(reGrid, n);

        short[][] reRed = edgeKernel(red, n);
        short[][] reGreen = edgeKernel(green, n);
        short[][] reBlue = edgeKernel(blue, n);


        img.setPixels(reGrid);
//        img.setColorChannels(reRed, reGreen, reBlue);

        return img;

    }
    public static short[][] thershold(short[][] img){
        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[i].length; j++) {
                if (img[i][j] < 120){
                    img[i][j] = 0;
                } else {
                    img[i][j] = 255;
                }
            }
        }
        return img;
    }

    public static short[][] thinning(short[][] img, int size){
        short[][] returnImg = new short[img.length][img[0].length];

        int[][] kernel = new int[size][size];
        int[][] secKernel = new int[size][size];
        int kernelWeight = 0;
        int secKernelWeight = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                secKernel[i][j] = 999;
                secKernel[0][size/2] = 0;
                secKernel[0][size-1] = 0;
                secKernel[size/2][size-1] = 0;
                secKernel[size/2][0] = 255;
                secKernel[size/2][size/2] = 255;
                secKernel[size - 1][size/2] = 255;
                secKernelWeight += secKernel[i][j];

            }
        }


        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] = 999;
                kernel[0][i] = 0;
                kernel[size/2][size/2] = 255;
                kernel[size-1][j] = 255;
                kernelWeight += kernel[i][j];
            }
        }



        for (int redo = 0; redo < 4; redo++) {

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    System.out.print(kernel[i][j] + "  ");
                }
                System.out.println();
            }
            System.out.println("======================================");
//            for (int i = 0; i < size; i++) {
//                for (int j = 0; j < size; j++) {
//                    System.out.print(secKernel[i][j] + "  ");
//                }
//                System.out.println();
//            }
//            System.out.println("========================================");

            for (int row = 0; row < img.length - size; row++) {
                for (int col = 0; col < img[row].length - size; col++) {
                    int output = 0;

                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            int kernelVal = kernel[i][j];
                            int secKernelVal = secKernel[i][j];
                            int pixVal = img[i + row][j + col];
                            output += kernelVal * secKernelVal * pixVal;
                        }
                    }
                    if (kernelWeight != 0) {
                        output /= kernelWeight;
                    } else output /= 1;


                    if (output < 0) output = 0;
                    if (output > 255) output = 255;
                    returnImg[row + (size / 2)][col + size / 2] = (short) output;
                }
            }
            kernel = rotate(kernel, size);
            secKernel = rotate(kernel, size);
        }
        return returnImg;
    }

    public static short[][] edgeKernel(short[][] array, int size){
        short[][] returnImg = new short[array.length][array[0].length];

        int[][] kernel = new int[size][size];
        int kernelWeight = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] = -1;
                kernel[size/2][size/2] = (size*size)-1;
                kernelWeight += kernel[i][j];
            }
        }

        for (int row = 0; row < array.length-size; row++) {
            for (int col = 0; col < array[row].length-size; col++) {
                int output = 0;

                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {

                        int kernelVal = kernel[i][j];
                        int pixVal = array[i+row][j+col];

                        output += kernelVal*pixVal;
                    }
                }
                if (kernelWeight != 0) {
                    output /= kernelWeight;
                } else output /= 1;

                if (output < 0) output = 0;
                if (output > 255) output = 255;
                returnImg[row+(size/2)][col+size/2] = (short) output;
            }
        }

        return returnImg;
    }

    public static int[][] rotate(int[][] grid, int size){
        int[][] rotatedGrid = new int[grid.length][grid[0].length];

        int rl = grid.length;
        int cl = grid[0].length;
        for (int i = 0; i < rl; i++) {
            for (int j = 0; j < cl; j++) {
                rotatedGrid[rl-1-j][i] = grid[i][j];
            }
        }

        return rotatedGrid;
    }
}
