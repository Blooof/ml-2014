package ru.bloof.ml.practice3.pca;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class Runner {
    public static final String VECTORS_DIR = "data/vectors/";
    public static final double EPS = 0.01;

    public static void main(String[] args) throws Exception {
        Path vectorsDir = Paths.get(VECTORS_DIR);
        Files.walk(vectorsDir).filter(file -> !Files.isDirectory(file)/* && file.endsWith("test")*/).forEach(file -> {
            List<double[]> vectors;
            try {
                vectors = Files.lines(file).map(line -> {
                    String[] s = line.split(" ");
                    double[] vector = new double[s.length];
                    for (int i = 0; i < s.length; i++) {
                        vector[i] = Double.parseDouble(s[i]);
                    }
                    return vector;
                }).collect(Collectors.<double[]>toList());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            RealMatrix fM = MatrixUtils.createRealMatrix(vectors.size(), vectors.get(0).length);
            for (int i = 0; i < vectors.size(); i++) {
                fM.setRow(i, vectors.get(i));
            }
            RealMatrix fM_T = fM.transpose();
            RealMatrix fM_T_fM = fM_T.multiply(fM);
            EigenDecomposition decomposition = new EigenDecomposition(fM_T_fM, 0);
            double[] realEigenvalues = decomposition.getRealEigenvalues();
            int columns = 0, rows = 0;
            double eigenvaluesSum = 0;
            for (int i = 0; i < realEigenvalues.length; ++i) {
                if (realEigenvalues[i] > EPS) {
                    columns++;
                    rows = decomposition.getEigenvector(i).getDimension();
                    eigenvaluesSum += realEigenvalues[i];
                }
            }
            RealMatrix u = MatrixUtils.createRealMatrix(rows, columns);
            int column = 0;
            for (int i = 0; i < realEigenvalues.length; i++) {
                System.out.print(realEigenvalues[i] + " ");
                if (realEigenvalues[i] > EPS) {
                    u.setColumn(column++, decomposition.getEigenvector(i).toArray());
                }
            }
            System.out.println();
            System.out.println("Eigenvalues sum=" + eigenvaluesSum);
            System.out.print(String.format("dim=%dx%d ", u.getRowDimension(), u.getColumnDimension()));
            System.out.println(u);
        });
    }
}