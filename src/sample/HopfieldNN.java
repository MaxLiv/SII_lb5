package sample;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class HopfieldNN {
    int n = 400;
    int m = 4;
    int w[][] = new int[n][n];

    String root = "D:\\Desktop\\Универ\\ИИ\\lb5_sii\\SII_lb5\\src\\sample\\img\\";//for windows
//    String root = "/home/developer/Java/SII_lb5/src/sample/img/";//for linux

    public HopfieldNN() {
        String[] names = {"s", "c", "m", "h"};
        for (int i = 0; i < m; i++) {
            for (int j = 1; j <= 3; j++) {
                setW(names[i] + j);
            }
        }
    }

    public WritableImage search(String path) {
        int[] y = marchThroughImage(path);

        while (true) {
            int[] S = new int[n];
            for (int j = 0; j < n; j++) {
                int sum = 0;
                for (int i = 0; i < n; i++) {
                    sum += y[i] * w[i][j];
                }
                S[j] = sum;
            }

            int[] yNext = new int[n];

            for (int j = 0; j < n; j++) {
                if (S[j] < 0) {
                    yNext[j] = -1;
                } else {
                    yNext[j] = (S[j] > 0) ? 1 : y[j];
                }
            }

            if (Arrays.equals(y, yNext)) {
                break;
            } else {
                y = yNext;
            }
        }

        return printResult(y);
    }

    private WritableImage printResult(int[] array) {
        int width = 20;
        int height = 20;
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        System.out.println(" ***** RESULT *****");
        int x = 0;
        int y = 0;
        for (int i = 0; i < n; i++) {
            System.out.print((array[i] == -1) ? "  " : "XX");
            pixelWriter.setColor(x,y, (array[i] == -1) ? Color.WHITE : Color.BLACK);
            x++;
            if (x == width) {
                System.out.println();
                x = 0;
                y++;
            }
        }
        return writableImage;
    }

    private void setW(String name) {
        int[] x = marchThroughImage(root + name + ".bmp");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    w[i][j] += x[i] * x[j];
                }
            }
        }
    }

    private int[] marchThroughImage(String path) {
        BufferedImage image = null;
        File file = new File(path);
        try {
            System.out.println(path);
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(path);
        int w = image.getWidth();
        int h = image.getHeight();
        int[] array = new int[h * w];
        System.out.println("width, height: " + w + ", " + h);
        int k = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = image.getRGB(j, i);
//                System.out.print((pixel == -1) ? "  " : "XX");
                array[k] = (pixel == -1) ? -1 : 1;
//                System.out.print(array[k]+((array[k]==1)?" ":""));
                if (array[k] == 1) {
                    System.out.print("XX");
                } else {
                    if (array[k] == -1) {
                        System.out.print("  ");
                    } else {
                        System.out.print("??");
                    }
                }
                k++;

            }
            System.out.println();
        }
        return array;
    }

}
