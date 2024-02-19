package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FindCenters implements PixelFilter {

    public FindCenters() {
    }
    @Override
    public DImage processImage(DImage img) {
//        short[][] red = img.getRedChannel();
//        short[][] green = img.getGreenChannel();
//        short[][] blue = img.getBlueChannel();
//        short[][] tempRed = img.getRedChannel();
//        short[][] tempGreen = img.getGreenChannel();
//        short[][] tempBlue = img.getBlueChannel();
//        short[][][] temp = new short[][][]{tempRed, tempGreen, tempBlue};
//
//        ArrayList<int[]> centers = new ArrayList<>();
//
//        while (!isEmpty(temp)) {
//            int initialX = 0, initialY = 0;
//            while (isBlack(temp, initialY, initialX)) {
//                initialX = (int) (Math.random() * red[0].length);
//                initialY = (int) (Math.random() * red.length);
//            }
//            int left = initialX, right = initialX, top = initialY, bottom = initialY;
//            while (left >= 0 && isBlack(temp, initialY, left)) left--;
//            System.out.println("Left: " + left);
//            while (right < red[0].length && isBlack(temp, initialY, right)) right++;
//            System.out.println("Right: " + right);
//            int x = (left+right)/2;
//            while (top >= 0 && isBlack(temp, top, x)) top--;
//            System.out.println("Top: " + top);
//            while (bottom < red.length && isBlack(temp, bottom, x)) bottom++;
//            System.out.println("Bottom: " + bottom);
//            int y = (top+bottom)/2;
//            centers.add(new int[]{x, y});
//            for (short[][] color : temp) {
//                for (int r = top; r <= bottom; r++) {
//                    for (int c = left; c <= right; c++) {
//                        color[r][c] = 0;
//                    }
//                }
//            }
//        }
        short[][] grid = img.getBWPixelGrid();
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();
        ArrayList<int[]> centers = new ArrayList<>();

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] != 0) grid[r][c] = 255;
            }
        }

        while (!isEmpty(grid)) {
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[r].length; c++) {
                    if (grid[r][c] != 0) {
                        int left = c, right = c, top = r, bottom = r;
                        while (left >= 0 && grid[r][left] != 0) left--;
                        while (right < grid[r].length && grid[r][right] != 0) right++;
                        int x = (left+right)/2;
                        while (top >= 0 && grid[top][x] != 0) top--;
                        while (bottom < grid.length && grid[bottom][x] != 0) bottom++;
                        int y = (top+bottom)/2;
                        while (left >= 0 && grid[y][left] != 0) left--;
                        while (right < grid[y].length && grid[y][right] != 0) right++;
                        x = (left+right)/2;
                        while (top >= 0 && grid[top][x] != 0) top--;
                        while (bottom < grid.length && grid[bottom][x] != 0) bottom++;
                        y = (top+bottom)/2;
                        centers.add(new int[]{x, y});
//                        System.out.println("Center: " + x + ", " + y + ". left: " + left + ", right: " + right + ", top: " + top + ", bottom: " + bottom);
//                        try {
//                            String output = "Center: " + x + ", " + y + ". {";
//                            for (int r1 = 0; r1 < grid.length; r1++) {
//                                output += "{";
//                                for (int c2 = 0; c2 < grid[r].length; c2++) {
//                                    output += grid[r1][c2] + ", ";
//                                }
//                                output += "}, ";
//                            }
//                            output += "}";
//                            writeDataToFile("tmp/center" + centers.size() + ".txt", output);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        for (int r2 = Math.max(0, top-2); r2 <= Math.min(grid.length-1, bottom+2); r2++) {
                            for (int c2 = Math.max(0, left-2); c2 <= Math.min(grid[0].length-1, right+2); c2++) {
                                grid[r2][c2] = 0;
                            }
                        }
                        r = 0;
                        c = 0;
                    }
                }
            }
        }

        System.out.println("Found " + centers.size() + " centers");

        for (int[] center : centers) {
            for (int r = Math.max(center[1] - 2, 0); r < Math.min(center[1] + 3, img.getHeight()); r++) {
                for (int c = Math.max(center[0] - 2, 0); c < Math.min(center[0] + 3, img.getWidth()); c++) {
                    red[r][c] = 255;
                    green[r][c] = 0;
                    blue[r][c] = 0;
                }
            }
            red[center[1]][center[0]] = 0;
            green[center[1]][center[0]] = 255;
            blue[center[1]][center[0]] = 0;
        }

        img.setColorChannels(red, green, blue);
//        img.setColorChannels(tempRed, tempGreen, tempBlue);
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

    private boolean isEmpty(short[][] grid) {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] != 0) return false;
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

    public static void writeDataToFile(String filePath, String data) throws IOException {
        try (FileWriter f = new FileWriter(filePath);
             BufferedWriter b = new BufferedWriter(f);
             PrintWriter writer = new PrintWriter(b);) {


            writer.println(data);


        } catch (IOException error) {
            System.err.println("There was a problem writing to the file: " + filePath);
            error.printStackTrace();
        }
    }

    public static String readFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}
