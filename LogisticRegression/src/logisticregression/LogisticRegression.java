/*
 * © 2017 TAKAHASHI,Toru
 */
package logisticregression;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 *
 * @author toru
 */
public class LogisticRegression {

    private int inputDimension;
    private int outputDimension;
    private double[][] weights;
    private double[] biases;

    public LogisticRegression(int nIn, int nOut) {
        inputDimension = nIn;
        outputDimension = nOut;
        weights = new double[outputDimension][inputDimension];
        biases = new double[outputDimension];
    }
    
    public double[][] train(double[][] data, int[][] teacher, int minibatchSize, double learningRate) {
        double[][] gradientWeights = new double[outputDimension][inputDimension];
        double[] gradientBiases = new double[outputDimension];
        
        double[][] deltaY = new double[minibatchSize][outputDimension];

        for (int n = 0; n < minibatchSize; n++) {
            double[] predictedY = output(data[n]);
            for (int j = 0; j < outputDimension; j++) {
                deltaY[n][j] = predictedY[j] - teacher[n][j];
                for (int i = 0; i < inputDimension; i++) {
                    gradientWeights[j][i] += deltaY[n][j] * data[n][i];
                }
                gradientBiases[j] += deltaY[n][j];
            }            
        }
        
        for (int j = 0; j < outputDimension; j++) {
            for (int i = 0; i < inputDimension; i++) {
                weights[j][i] -= learningRate * gradientWeights[j][i] / minibatchSize;
            }
            biases[j] -= learningRate * gradientBiases[j] / minibatchSize;
        }
        return deltaY;
    }
    
    public double[] output(double[] data) {
        double[] preActivation = new double[outputDimension];
        for (int j = 0; j < outputDimension; j++) {
            for (int i = 0; i < inputDimension; i++) {
                preActivation[j] += weights[j][i] * data[i];
            }
            preActivation[j] += biases[j];
        }
        return softmax(preActivation, outputDimension);
    }
    
    public Integer[] predict(double[] x) {
        double[] y = output(x);
        double max = Arrays.stream(y).max().orElse(0);
        double maxIndex = IntStream.range(0, y.length)
                .filter(i -> y[i] == max)
                .findFirst().orElse(-1);
        Integer[] teacher = IntStream.range(0, outputDimension)
                .mapToObj(i -> i == maxIndex ? 1 : 0)
                .toArray(Integer[]::new);
        return teacher;
    }
    
    public static double[] softmax(double[] x, int n) {
        double max = Arrays.stream(x).max().orElse(0);
        double[] y = Arrays.stream(x)
                .map(xi -> Math.exp(xi - max))
                .toArray();
        double sum = Arrays.stream(y).sum();
        return Arrays.stream(y).map(yi -> yi / sum).toArray();
    }
}
