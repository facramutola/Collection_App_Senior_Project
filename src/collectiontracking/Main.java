package collectiontracking;


import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;


public class Main extends Application {
	public static void main(String[] args) {
		Application.launch(Main.class, (java.lang.String[]) null);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			VBox page = (VBox) FXMLLoader.load(Main.class.getResource("collectiontracking.fxml"));
			Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Collection Tracking");
			primaryStage.show();
		}
		catch(Exception ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	}

