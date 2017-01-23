/*
 * © 2017 TAKAHASHI,Toru
 */
package logisticregression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

/**
 * PercptronView パッケージにおけるViewModel
 * 
 * <li>入力データ（教師データ、評価データの生成）
 * 
 * @author toru
 */
public class LogisticRegressionViewModel {
    private static final int NUM_PATTERN = 3;
    private static final int NUM_TRAIN = 400 * NUM_PATTERN;
    private static final int NUM_TEST = 60 * NUM_PATTERN;
    private static final int NUM_INPUT_DIMENSION = 2;
    private static final int NUM_OUTPUT_DIMENSION = NUM_PATTERN;
    private static final int NUM_EPOCH = 2000;
    private static final int SIZE_MINIBATCH = 50;
    private static final int NUM_MINIBATCH = NUM_TRAIN / SIZE_MINIBATCH;
//    private static final double LEARNING_RATE = 1d;
    
    private final Random random;
    private final double[][] trainData;
    private final int[][] trainTeacher;
    private final double[][][] trainDataMinibatch;
    private final int[][][] trainTeacherMinibatch;
    private final List<Integer> minibatchIndex;
    private final double[][] testData;
    private final Integer[][] testTeacher;
    private final Integer[][] predicted;
    
    private final LogisticRegression classifier;
    
    private List<XYChart.Series> trainDataSeries;
            
    public static class EvaluateItem {
        private final StringProperty name;
        private final DoubleProperty value;

        public EvaluateItem(String name, double value) {
            this.name = new SimpleStringProperty(name);
            this.value = new SimpleDoubleProperty(value);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public StringProperty nameProperty() {
            return name;
        }
        
        public double getValue() {
            return value.get();
        }
        
        public void setValue(double v) {
            value.set(v);
        }
        
        public String getValueText() {
            return String.format("%.1f%%", value.get() * 100);
        }
        
    }
    
    public LogisticRegressionViewModel() {
        random = new Random(1234);
        
        trainData = new double[NUM_TRAIN][NUM_INPUT_DIMENSION];
        trainTeacher = new int[NUM_TRAIN][NUM_OUTPUT_DIMENSION];
        
        trainDataMinibatch = new double[NUM_MINIBATCH][SIZE_MINIBATCH][NUM_INPUT_DIMENSION];
        trainTeacherMinibatch = new int[NUM_MINIBATCH][SIZE_MINIBATCH][NUM_OUTPUT_DIMENSION];
        minibatchIndex = IntStream.range(0, NUM_TRAIN).boxed().collect(Collectors.toList());
        Collections.shuffle(minibatchIndex, random);
        
        testData = new double[NUM_TEST][NUM_INPUT_DIMENSION];
        testTeacher = new Integer[NUM_TEST][NUM_OUTPUT_DIMENSION];
        
        predicted = new Integer[NUM_TEST][NUM_OUTPUT_DIMENSION];
        classifier = new LogisticRegression(NUM_INPUT_DIMENSION, NUM_OUTPUT_DIMENSION);
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
        IntStream.range(0, NUM_TRAIN / NUM_PATTERN)
                .mapToObj(i -> new XYChart.Data(trainData[i][0], trainData[i][1]))
                .forEach(data -> trainDataSeries1.getData().add(data));
        trainDataSeries.add(trainDataSeries1);
        
        XYChart.Series trainDataSeries2 = new XYChart.Series();
        IntStream.range(NUM_TRAIN / NUM_PATTERN, NUM_TRAIN / NUM_PATTERN * 2)
                .mapToObj(i -> new XYChart.Data(trainData[i][0], trainData[i][1]))
                .forEach(data -> trainDataSeries2.getData().add(data));
        trainDataSeries.add(trainDataSeries2);
        
        XYChart.Series trainDataSeries3 = new XYChart.Series();
        IntStream.range(NUM_TRAIN / NUM_PATTERN * 2, NUM_TRAIN)
                .mapToObj(i -> new XYChart.Data(trainData[i][0], trainData[i][1]))
                .forEach(data -> trainDataSeries3.getData().add(data));
        trainDataSeries.add(trainDataSeries3);
        
        
        return trainDataSeries;
    }
    
    /**
     * 学習の実行
     */
    void train() {
        double learningRate = 0.2;
        for (int epoch = 0; epoch < NUM_EPOCH; epoch++) {
            for (int batch = 0; batch < NUM_MINIBATCH; batch++) {
                classifier.train(trainDataMinibatch[batch], trainTeacherMinibatch[batch], SIZE_MINIBATCH, learningRate);
                
            }
            learningRate *= 0.95;
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
    ObservableList<EvaluateItem> evaluate() {
        int[][] confusionMatrix = new int[NUM_PATTERN][NUM_PATTERN];
        double accuracy = 0d;
        double[] precision = new double[NUM_PATTERN];
        double[] recall = new double[NUM_PATTERN];
        
        for (int i = 0; i < NUM_TEST; i++) {
            int predict = Arrays.asList(predicted[i]).indexOf(1);
            int actual = Arrays.asList(testTeacher[i]).indexOf(1);
            confusionMatrix[actual][predict] += 1;
        }
        for (int i = 0; i < NUM_PATTERN; i++) {
            double col = 0;
            double row = 0;
            for (int j = 0; j < NUM_PATTERN; j++) {
                if (i == j) {
                    accuracy += confusionMatrix[i][j];
                    precision[i] += confusionMatrix[j][i];
                    recall[i] += confusionMatrix[i][j];
                }
                col += confusionMatrix[j][i];
                row += confusionMatrix[i][j];
            }
            precision[i] /= col;
            recall[i] /= row;
        }
        accuracy /= NUM_TEST;

        ObservableList<EvaluateItem> list = FXCollections.observableArrayList();
        list.add(new EvaluateItem("正解率", accuracy));
        for (int i = 0; i < NUM_PATTERN; i++) {
            list.add(new EvaluateItem("精度#" + (i + 1), precision[i]));
        }
        for (int i = 0; i < NUM_PATTERN; i++) {
            list.add(new EvaluateItem("再現率#" + (i + 1), recall[i]));
        }
        return list;
    }
    
    /**
     * トレーニングデータの生成
     */
    private void generateTrainData() {
        IntStream.range(0, NUM_TRAIN / NUM_PATTERN)
                .forEach(i -> {
                    trainData[i][0] = random.nextGaussian() - 2.0;
                    trainData[i][1] = random.nextGaussian() + 2.0;
                    trainTeacher[i] = new int[]{1, 0, 0};
                });
        IntStream.range(NUM_TRAIN / NUM_PATTERN, NUM_TRAIN / NUM_PATTERN * 2)
                .forEach(i -> {
                    trainData[i][0] = random.nextGaussian() + 2.0;
                    trainData[i][1] = random.nextGaussian() - 2.0;
                    trainTeacher[i] = new int[]{0, 1, 0};
                });
        IntStream.range(NUM_TRAIN / NUM_PATTERN * 2, NUM_TRAIN)
                .forEach(i -> {
                    trainData[i][0] = random.nextGaussian();
                    trainData[i][1] = random.nextGaussian();
                    trainTeacher[i] = new int[]{0, 0, 1};
                });
        IntStream.range(0, NUM_MINIBATCH)
                .forEach(i -> IntStream.range(0, SIZE_MINIBATCH)
                        .forEach(j -> {
                            trainDataMinibatch[i][j] = trainData[minibatchIndex.get(i * SIZE_MINIBATCH + j)];
                            trainTeacherMinibatch[i][j] = trainTeacher[minibatchIndex.get(i * SIZE_MINIBATCH + j)];
                        })
                );
    }

    /**
     * 評価データの生成
     */
    private void generateTestData() {
        IntStream.range(0, NUM_TEST / NUM_PATTERN)
                .forEach(i -> {
                    testData[i][0] = random.nextGaussian() - 2.0;
                    testData[i][1] = random.nextGaussian() + 2.0;
                    testTeacher[i] = new Integer[]{1, 0, 0};
                });
        IntStream.range(NUM_TEST / NUM_PATTERN, NUM_TEST / NUM_PATTERN * 2)
                .forEach(i -> {
                    testData[i][0] = random.nextGaussian() + 2.0;
                    testData[i][1] = random.nextGaussian() - 2.0;
                    testTeacher[i] = new Integer[]{0, 1, 0};
                });
        IntStream.range(NUM_TEST / NUM_PATTERN * 2, NUM_TEST)
                .forEach(i -> {
                    testData[i][0] = random.nextGaussian();
                    testData[i][1] = random.nextGaussian();
                    testTeacher[i] = new Integer[]{0, 0, 1};
                });
        
    }

}
