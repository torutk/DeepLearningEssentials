/*
 * © 2017 TAKAHASHI,Toru
 */
package logisticregression;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author toru
 */
public class LogisticRegressionViewController implements Initializable {
    
    @FXML
    private ScatterChart chart;
    @FXML
    private TableView<LogisticRegressionViewModel.EvaluateItem> table;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn valueColumn;
    @FXML
    private Button executeButton;

    private LogisticRegressionViewModel model = new LogisticRegressionViewModel();
    
    @FXML
    private void doLearning(ActionEvent event) {
        model.getTrainData().stream()
                .forEach(chart.getData()::add);
        model.train();
        model.test();
        table.setItems(model.evaluate());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("valueText"));
    }    
    
}
