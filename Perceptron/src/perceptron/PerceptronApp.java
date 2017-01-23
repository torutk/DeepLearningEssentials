/*
 * © 2017 TAKAHASHI,Toru
 */
package perceptron;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author toru
 */
public class PerceptronApp extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("PerceptronView.fxml"));
        
        Scene scene = new Scene(root);
        stage.setTitle("機械学習 - Perceptron");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
