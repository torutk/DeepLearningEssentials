/*
 * © 2017 TAKAHASHI,Toru
 */
package perceptron;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Label;

/**
 *
 * @author toru
 */
public class PerceptronViewController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private ScatterChart chart;
    @FXML
    private Label accuracyLabel;
    @FXML
    private Label precisionLabel;
    @FXML
    private Label recallLabel;
    
    private PerceptronViewModel model = new PerceptronViewModel();
    
    @FXML
    private void doLearning(ActionEvent event) {
        chart.getData().add(model.getTrainData());
        model.train();
        model.test();
        model.evaluate();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chart.setTitle("学習データの散布図");
        accuracyLabel.textProperty().bind(model.accuracyProperty());
        precisionLabel.textProperty().bind(model.precisionProperty());
        recallLabel.textProperty().bind(model.recallProperty());
    }    
    
}
