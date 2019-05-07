package coursework3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Coursework3 extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Coursework3.class.getResource("Style.css").toExternalForm());
        

        
        stage.setScene(scene);
        stage.setTitle("Vehicle Sales");
        stage.show();
    }

   
    public static void main(String[] args) {
        launch(args);
    }
    
}
