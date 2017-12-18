package sample;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class NeyronNetwork {

    final int defI = 50 * 50;
    final int defJ = 50 * 50 / 2;
    final int defK = 4;


    double[][] w = new double[defI][defJ];
    double[][] v = new double[defJ][defK];

    private void setRandomWeight() {
        for (int i = 0; i < w.length; i++) {
            for (int j = 0; j < w[i].length; j++) {
                w[i][j] = -0.29 + Math.random() * 0.59;
            }
        }

        for (int i = 0; i < v.length; i++) {
            for (int j = 0; j < v[i].length; j++) {
                v[i][j] = -0.3 + Math.random() * 0.6;
            }
        }
    }

    double[] sumW = new double[defJ];
    double[] yJ = new double[defJ];
    double[] sumV = new double[defK];
    double[] yK = new double[defK];
    double[] d;


    public Map<String, Double> findElement(String name) {
        double[] elem = marchThroughImage(name);
        Map<String, Double> map = new HashMap<>();
        w = loadArrayFromFile("w.txt");
        v = loadArrayFromFile("v.txt");

        for (int j = 0; j < defJ; j++) {
            for (int i = 0; i < defI; i++) {
                sumW[j] += w[i][j] * elem[i];
            }
        }

        for (int i = 0; i < yJ.length; i++) {
            yJ[i] = 1.0 / (1.0 + Math.exp(-sumW[i]));
        }

        for (int j = 0; j < defK; j++) {
            for (int i = 0; i < defJ; i++) {
                sumV[j] = v[i][j] * yJ[i];
            }
        }

        String[] names = {"ship", "car", "man", "house"};
        for (int i = 0; i < yK.length; i++) {
            yK[i] = 1.0 / (1.0 + Math.exp(-sumV[i]));
            System.out.print(names[i] + ": " + yK[i] * 100 + "%\n");
            map.put(names[i], yK[i] * 100);
        }
        System.out.println();
        return map;
    }


    private void cicle(double[] elem) {
        int l = 0;
        while (true) {

            System.out.println((l++));

            for (int j = 0; j < defJ; j++) {
                for (int i = 0; i < defI; i++) {
                    sumW[j] += w[i][j] * elem[i];
                }
            }

            for (int i = 0; i < yJ.length; i++) {
                yJ[i] = 1.0 / (1.0 + Math.exp(-sumW[i]));
            }


            for (int j = 0; j < defK; j++) {
                for (int i = 0; i < defJ; i++) {
                    sumV[j] = v[i][j] * yJ[i];
                }
            }

            for (int i = 0; i < yK.length; i++) {
                yK[i] = 1.0 / (1.0 + Math.exp(-sumV[i]));
            }


            double sumNaimKv = 0;

            for (int i = 0; i < defK; i++) {
                sumNaimKv += (yK[i] - d[i]) * (yK[i] - d[i]);
            }

            if (sumNaimKv < 0.1) {
                System.out.println("DA");
                return;
            } else {
                for (int i = 0; i < defJ; i++) {
                    for (int j = 0; j < defK; j++) {
                        v[i][j] = v[i][j] - 0.1 * yJ[i] * calcSigmaK(j);
                    }
                }

                for (int i = 0; i < defI; i++) {
                    for (int j = 0; j < defJ; j++) {
                        w[i][j] = w[i][j] - 0.1 * calcDEDW(i, j, elem);
                    }
                }
            }
        }
    }

    private double calcSigmaK(int k) {
        return (yK[k] - d[k]) * yK[k] * (1 - yK[k]);
    }

    private double calcDEDW(int i, int j, double[] elem) {
        double sum = 0;
        for (int k = 0; k < defK; k++) {
            sum += calcSigmaK(k) * v[j][k];
        }
        return sum * yJ[j] * (1 - yJ[j]) * elem[i];
    }

    private void saveArrayToFile() {
        try {
            File file = new File("w.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter("w.txt"));
            for (int i = 0; i < w.length; i++) {
                for (int j = 0; j < w[0].length; j++) {
                    bw.write(String.valueOf(w[i][j]));
                    bw.newLine();
                }
            }
            bw.flush();
            bw.close();

            File file2 = new File("v.txt");
            file2.createNewFile();
            bw = new BufferedWriter(new FileWriter("v.txt"));
            for (int i = 0; i < v.length; i++) {
                for (int j = 0; j < v[0].length; j++) {
                    bw.write(String.valueOf(v[i][j]));
                    bw.newLine();
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double[][] loadArrayFromFile(String filename) {
        double[][] arr = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            if (filename.equals("w.txt")) {
                arr = new double[defI][defJ];
            } else arr = new double[defJ][defK];
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = Double.parseDouble(br.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arr;
    }

    public double[] marchThroughImage(String path) {
        BufferedImage image = null;
        System.out.println(path);
        File file = new File(path);
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        int w = image.getWidth();
        int h = image.getHeight();

        double[] array = new double[h * w];

        System.out.println("width, height: " + w + ", " + h);

        int k = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = image.getRGB(j, i);

//                System.out.print((pixel == -1) ? "  " : "XX");

                array[k] = (pixel == -1) ? 0.1 : 0.9;
                k++;

            }
//            System.out.println();
        }
        return array;
    }


    // 1 - r
    // 2 - lr
    // 3 - l
    // 4 - d
    // 5 - e
    public void learning() throws IOException {

        setRandomWeight();

//        System.out.println("Ypa");

        Map<String, double[]> imgs = new HashMap<>();
        imgs.put("c", new double[]{0.9, 0.1, 0.1, 0.1, 0.1});
//        imgs.put("lr", new double[]{0.1, 0.9, 0.1, 0.1, 0.1});
//        imgs.put("l", new double[]{0.1, 0.1, 0.9, 0.1, 0.1});
//        imgs.put("d", new double[]{0.1, 0.1, 0.1, 0.9, 0.1});
//        imgs.put("e", new double[]{0.1, 0.1, 0.1, 0.1, 0.9});

        for (Map.Entry entry : imgs.entrySet()) {
            System.out.println(String.valueOf(entry.getKey()));
            d = (double[]) entry.getValue();
            for (int i = 1; i <= 4; i++) {
                cicle(marchThroughImage("/home/developer/Java/SII_lb5/src/sample/img/" +
                        String.valueOf(entry.getKey()) + i + ".bmp"));
            }
        }

        saveArrayToFile();
    }
}

