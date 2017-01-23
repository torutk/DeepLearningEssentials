/*
 * © 2017 TAKAHASHI,Toru
 */
package perceptron;

/**
 *
 * @author toru
 */
public class Perceptron {
    private final int dimensions;
    private final double[] weights;

    public Perceptron(int dimensions) {
        this.dimensions = dimensions;
        weights = new double[dimensions];
    }

    Perceptron() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int train(double[] data, int label, double learningRate) {
        int classified = 0;
        double c = 0;
        for (int i = 0; i < dimensions; i++) {
            c += weights[i] * data[i] * label;
        }
        if ( c >0 ) {
            classified = 1;
        } else {
            for (int i = 0; i < dimensions; i++) {
                weights[i] += learningRate * data[i] * label;
            }
        }
        return classified;
    }
    
    public int predict(double[] data) {
        double preActivation = 0;
        for (int i = 0; i < dimensions; i++) {
            preActivation += weights[i] * data[i];
        }
        return preActivation >= 0 ? 1 : -1;
    }
    
}
