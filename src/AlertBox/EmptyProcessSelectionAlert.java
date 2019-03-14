package AlertBox;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *	@author Sophia Kavousifard
 *	Alert message box for empty selection
 */
public class EmptyProcessSelectionAlert {
	/**
	 *	Alert box notifies user that they
	 *	have not selected any processes to
	 *	monitor and that they must try again.
	 *	@param title title of the alert box
	 *	@param message message to user
	 */
	public static void displayAlert(String title, String message) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);
		
		Label label = new Label();
		label.setText(message);
		Button exitButton = new Button("Close");
		exitButton.setOnAction(e -> window.close());
		
		VBox verticalBox = new VBox();
		verticalBox.getChildren().addAll(label,exitButton);
		verticalBox.setAlignment(Pos.CENTER);
		
		Scene alertScene = new Scene(verticalBox);
		window.setScene(alertScene);
		window.showAndWait();
	}
}
