import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MyMultithreadProcess extends Application {
	private Stage mainWindow;
	private Scene processIdScene;
	private Scene individualScene;
	private ArrayList<String> arrayProcessId;
	private HBox[] horizontalBoxes;
	private ScrollPane scrollPane;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	//Displaying new scene window
	@Override
	public void start(Stage primaryStage) throws Exception {
		//finds information of all running processes
		arrayProcessId = displayProcessesSettup();
		horizontalBoxes = new HBox[arrayProcessId.size()];
		
		//creating stage and scene with process information
		mainWindow = primaryStage;
		mainWindow.setTitle("Multithread Processing");
		
		//adding all individual horizontal boxes
		addIndividualHorizontalBox();
		
		//adding all horizontal boxes to the main vertical one
		VBox verticalBox = addHorizontalBoxToVertical();

		//Setting the scene and showing the mainWindow
		setSceneShowWindow(verticalBox);
		
		//TODO
//		scrollPane = new ScrollPane();
//		scrollPane.setFitToHeight(true);
	}
	
	private ArrayList<String> displayProcessesSettup() {
		try {
			ArrayList<String> arrayProcessId = new ArrayList<String>();
		    String processLine;
		    Process process = Runtime.getRuntime().exec("ps -e");
		    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    
		    while ((processLine = input.readLine()) != null) {
		    	//extracting all process id
		    	String processId = displayProcessId(processLine);
		    	if(processId != null) {
		    		arrayProcessId.add(processId);
			    	System.out.println(processId);
			        System.out.println(processLine);
		    	}
		    }
		    input.close();
		    return arrayProcessId;
		} 
		catch (Exception err) {
		    err.printStackTrace();
		    return null;
		}
	}
	
	private static String displayProcessId(String processLine) {
		//regex to find positive process id number
		Pattern p = Pattern.compile("^\\d+");
        Matcher m = p.matcher(processLine);
        
        while(m.find()){
	        return m.group();
        }
        //extra information, not process id
		return null;
	}
	
	private void addIndividualHorizontalBox() {
		for(int i=0; i < horizontalBoxes.length; i++) {
			String processId = arrayProcessId.get(i);
			
			//Thread monitoring button
			Label labelId = new Label("Process ID " + processId);
			Button button = new Button();
			button.setText("Monitor process " + processId);
			button.setOnAction(e -> {
				//setting up individual process information scene
				createIndividualProcessScene(Integer.parseInt(processId));
				mainWindow.setScene(individualScene);
				});
			
			//adding individual button to the scene
			horizontalBoxes[i] = new HBox();
			horizontalBoxes[i].setSpacing(20);
			horizontalBoxes[i].setAlignment(Pos.CENTER);
			horizontalBoxes[i].getChildren().addAll(labelId, button);
		}
	}
	
	private VBox addHorizontalBoxToVertical() {
		VBox verticalBox = new VBox();
		verticalBox.setSpacing(20);
		verticalBox.getChildren().addAll(horizontalBoxes);
	    return verticalBox;
	}
	
	private void createIndividualProcessScene(int chosenProcessID) {
		//creating scene for individual process monitoring
		String cpuMemoInformation = displayCpuAndMemoryStorageUtilization(chosenProcessID);
		Label cpuMemoryLabel = new Label(cpuMemoInformation);
		Button button = new Button();
		button.setText("Stop monitoring process with ID " + chosenProcessID);
		button.setOnAction(e -> mainWindow.setScene(processIdScene));
		
		//creating horizontal box holding monitoring information
		final HBox horizontalBox = new HBox();
		horizontalBox.setSpacing(20);
		horizontalBox.setAlignment(Pos.CENTER);
		horizontalBox.getChildren().addAll(cpuMemoryLabel, button);
		
		individualScene = new Scene (horizontalBox, 500, 500);
	}
	
	private void setSceneShowWindow(VBox verticalBox) {
		processIdScene = new Scene (verticalBox, 500, 500);
		mainWindow.setScene(processIdScene);
		mainWindow.show();
	}
	
	private String displayCpuAndMemoryStorageUtilization(int processId) {
	    String processOutput = "";
		String processLine;
	    Process process;
	    
		try {
			process = Runtime.getRuntime().exec("ps -p " + processId + " -o %cpu,%mem");
		    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    
		    while ((processLine = input.readLine()) != null) {
		    	//display cpu and memory utilization
		    	processOutput += processLine + "\n";
		    	System.out.println(processLine);
		    }
		    input.close();
		    return processOutput;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
