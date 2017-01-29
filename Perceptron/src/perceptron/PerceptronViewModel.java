/*
 * © 2017 TAKAHASHI,Toru
 */
package perceptron;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.XYChart;

/**
 * PercptronView パッケージにおけるViewModel
 * 
 * <li>入力データ（教師データ、評価データの生成）
 * 
 * @author toru
 */
public class PerceptronViewModel {
    private static final int NUM_TRAIN = 1000;
    private static final int NUM_TEST = 200;
    private static final int NUM_DIMENSION = 2;
    private static final int NUM_EPOCH = 2000;
    private static final double LEARNING_RATE = 1d;
    
    private final Random random;
    private double[][] trainData;
    private int[] trainLabels;
    private double[][] testData;
    private int[] testLabels;
    private final int[] predicted;
    
    private final Perceptron classifier;
    
    private List<XYChart.Series> trainDataSeries;
    private StringProperty accuracyProperty = new SimpleStringProperty();
    private StringProperty precisionProperty = new SimpleStringProperty();
    private StringProperty recallProperty = new SimpleStringProperty();
            
    public PerceptronViewModel() {
        random = new Random(1234);
        trainData = new double[NUM_TRAIN][NUM_DIMENSION];
        trainLabels = new int[NUM_TRAIN];
        testData = new double[NUM_TEST][NUM_DIMENSION];
        testLabels = new int[NUM_TEST];
        predicted = new int[NUM_TEST];    
        classifier = new Perceptron(NUM_DIMENSION);
        generateTrainData();
        generateTestData();
    }

    /**
     * 表示用トレーニングデータの取得
     * 
     * @return データ系列
     */
    List<XYChart.Series> getTrainData() {
        if (trainDataSeries != null) {
            return trainDataSeries;
        }
        trainDataSeries = new ArrayList<>();
        XYChart.Series trainDataSeries1 = new XYChart.Series();
        IntStream.range(0, NUM_TRAIN / 2)
                .mapToObj(i -> new XYChart.Data(trainData[i][0], trainData[i][1]))
                .forEach(data -> trainDataSeries1.getData().add(data));
        trainDataSeries.add(trainDataSeries1);
        
        XYChart.Series trainDataSeries2 = new XYChart.Series();
        IntStream.range(NUM_TRAIN / 2, NUM_TRAIN)
                .mapToObj(i -> new XYChart.Data(trainData[i][0], trainData[i][1]))
                .forEach(data -> trainDataSeries2.getData().add(data));
        trainDataSeries.add(trainDataSeries2);
        
        return trainDataSeries;
    }
    
    /**
     * 学習の実行
     */
    void train() {
        int epoch = 0;
        while (true) {
            int classified  = 0;
            for (int i = 0; i < NUM_TRAIN; i++) {
                classified += classifier.train(trainData[i], trainLabels[i], LEARNING_RATE);
            }
            if (classified == NUM_TRAIN) {
                break;
            }
            epoch++;
            if (epoch > NUM_EPOCH) {
                break;
            }
        }
    }

    /**
     * テストの実行
     */
    void test() {
        for (int i = 0; i < NUM_TEST; i++) {
            predicted[i] = classifier.predict(testData[i]);
        }
    }
    
    /**
     * 評価の実施
     */
    void evaluate() {
        int[][] confusionMatrix = new int[2][2];
        double accuracy = 0d;
        double precision = 0d;
        double recall = 0d;
        for (int i = 0; i < NUM_TEST; i++) {
            if (predicted[i] > 0) {
                if (testLabels[i] > 0) {
                    accuracy += 1d;
                    precision += 1d;
                    recall += 1d;
                    confusionMatrix[0][0] += 1;
                } else {
                    confusionMatrix[1][0] += 1;
                }
            } else {
                if (testLabels[i] > 0) {
                    confusionMatrix[0][1] += 1;
                } else {
                    accuracy += 1d;
                    confusionMatrix[1][1] += 1;
                }
            }
        }
        accuracy /= NUM_TEST;
        precision /= confusionMatrix[0][0] + confusionMatrix[1][0];
        recall /= confusionMatrix[0][0] + confusionMatrix[0][1];
        
        accuracyProperty.set(String.format("%.1f %%", accuracy * 100));
        precisionProperty.set(String.format("%.1f %%", precision * 100));
        recallProperty.set(String.format("%.1f %%", recall * 100));
    }
    
    public StringProperty accuracyProperty() {
        return accuracyProperty;
    }

    public StringProperty precisionProperty() {
        return precisionProperty;
    }

    public StringProperty recallProperty() {
        return recallProperty;
    }
    
    /**
     * トレーニングデータの生成
     */
    private void generateTrainData() {
        trainData = IntStream.range(0, NUM_TRAIN)
                .map(i -> (i < NUM_TRAIN / 2 ? 1 : -1))
                .mapToObj(this::generateDatum)
                .toArray(double[][]::new);

        trainLabels = IntStream.range(0, NUM_TRAIN)
                .map(i -> (i < NUM_TRAIN / 2 ? 1 : -1))
                .toArray();
    }

    private double[] generateDatum(int klass) {
        double x1, x2;
        if (klass == 1) {
            x1 = random.nextGaussian() - 2.0;
            x2 = random.nextGaussian() + 2.0;
        } else {
            x1 = random.nextGaussian() + 2.0;
            x2 = random.nextGaussian() - 2.0;
        }
        return new double[] { x1, x2 };
    }
    /**
     * 評価データの生成
     */
    private void generateTestData() {
        testData = IntStream.range(0, NUM_TEST)
                .map(i -> i < NUM_TEST / 2 ? 1 : -1)
                .mapToObj(this::generateDatum)
                .toArray(double[][]::new);
        testLabels = IntStream.range(0, NUM_TEST)
                .map(i -> i < NUM_TEST / 2 ? 1: -1)
                .toArray();
    }

}
