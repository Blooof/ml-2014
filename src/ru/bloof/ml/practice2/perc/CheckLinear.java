package ru.bloof.ml.practice2.perc;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Scanner;

public class CheckLinear {

    public static final String CHECK_IMGS = "practice_2/mnist/cimgs", CHECK_LABELS = "practice_2/mnist/clabels";

    public static void main(String[] args) throws Exception {
        DataInputStream imgs = new DataInputStream(new FileInputStream(
                CHECK_IMGS)), labels = new DataInputStream(new FileInputStream(
                CHECK_LABELS));
        Scanner sc = new Scanner(new File(PercGen.RESULT));
        imgs.skip(4);
        labels.skip(8);
        int count = imgs.readInt(), n = imgs.readInt(), m = imgs.readInt(), num, cur;
        double omega[][][] = new double[10][n][m], err = 0;
        for (int k = 0; k < 10; ++k) {
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < m; ++j) {
                    String s = sc.next();
                    omega[k][i][j] = Double.parseDouble(s);
                }
            }
        }
        double sum[] = new double[10];
        for (int c = 0; c < count; ++c) {
            num = labels.read();
            Arrays.fill(sum, 0);
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < m; ++j) {
                    cur = imgs.read();
                    for (int k = 0; k < 10; ++k) {
                        sum[k] += cur * omega[k][i][j];
                    }
                }
            }

            double max = sum[0];
            int maxk = 0;
            for (int k = 0; k < 10; ++k) {
                if (max < sum[k]) {
                    max = sum[k];
                    maxk = k;
                }
            }
            if (maxk != num) {
                ++err;
                System.out.println(c + " number = " + num + ", we think that " + maxk);
            }
            if (c % 1000 == 0) {
                System.out.println(c);
            }
        }
        System.out.println("Total errors: " + err + "/" + count + " ("
                + (1.0 * err / count) + ")");
    }
}
