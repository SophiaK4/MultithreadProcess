import java.io.FileInputStream;
import java.io.IOException;

import Controller.MainProcessesController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Sophia Kavousifard
 *	Where the application begins
 *	and calls appropriate view and
 *	controller.
 */
public class Main extends Application {

	public static void main(String[] args) {launch(args);}

	@Override
	/**
	 *	Method gets called automatically when
	 *	the application launches. It links the 
	 *	main view to it's respective controller.
	 */
	public void start(Stage primaryStage) {
		try {
			MainProcessesController mainController = new MainProcessesController();
			
			//Linking loader with main process view controller
			FXMLLoader loader = new FXMLLoader();
	        String fxmlDocPath = "src/View/MainProcessesView.fxml";
	        FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);
	        loader.setController(mainController);
			
	        // Creates the ScrollPane with all of the process details
	        Parent root = (Parent) loader.load(fxmlStream);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
