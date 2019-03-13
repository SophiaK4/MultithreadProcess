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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Sophia Kavousifard
 *
 */
public class MyMultithreadProcess extends Application {
	private Stage mainWindow;
	private Scene processIdScene;
	private Scene monitorScene;
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
		horizontalBoxes = new HBox[arrayProcessId.size() + 1];
		
		//creating stage and scene with process information
		mainWindow = primaryStage;
		mainWindow.setTitle("Multithread Processing");
		
		//adding all individual horizontal boxes
		addIndividualHorizontalBox();
		
		//adding all horizontal boxes to the main vertical one
		VBox verticalBox = addHorizontalBoxToVertical();

		//Setting the scene and showing the mainWindow
		setSceneShowWindow(verticalBox);
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
		Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(processLine);
        
        while(m.find()){
	        return m.group();
        }
        //extra information, not process id
		return null;
	}
	
	private void addIndividualHorizontalBox() {
		for(int i=0; i < horizontalBoxes.length - 1; i++) {
			String processId = arrayProcessId.get(i);
			
			//Thread monitoring button
			CheckBox checkBox = new CheckBox("Process ID " + processId);
			
			//adding individual button to the scene
			horizontalBoxes[i] = new HBox();
			horizontalBoxes[i].setSpacing(20);
			horizontalBoxes[i].setAlignment(Pos.CENTER);
			horizontalBoxes[i].getChildren().add(checkBox);
		}
		
		Button button = new Button();
		button.setText("Monitor Process(es)");
		button.setOnAction(e -> {
			//setting up monitor process information scene
			ArrayList<Integer> listSelectedProcess = new ArrayList<Integer>();
			listSelectedProcess = findSelectedProcessToMonitor(horizontalBoxes, listSelectedProcess);
			createMonitorProcessScene(listSelectedProcess);
			mainWindow.setScene(monitorScene);
			});
		horizontalBoxes[horizontalBoxes.length - 1]= new HBox();
		horizontalBoxes[horizontalBoxes.length - 1].getChildren().add(button);
	}
	
	private VBox addHorizontalBoxToVertical() {
		VBox verticalBox = new VBox();
		verticalBox.setSpacing(20);
		verticalBox.getChildren().addAll(horizontalBoxes);
	    return verticalBox;
	}
	
	private ArrayList<Integer> findSelectedProcessToMonitor(HBox[] horizontalBoxes, ArrayList<Integer> listSelectedProcess) {
		for(int i=0; i < horizontalBoxes.length-1; i++) {
			CheckBox checkBox = (CheckBox) horizontalBoxes[i].getChildren().get(0);
			if(checkBox.isSelected()) {
				String processID = displayProcessId(checkBox.getText());
				listSelectedProcess.add(Integer.parseInt(processID));
			}
		}
		return listSelectedProcess;
	}
	
	private void createMonitorProcessScene(ArrayList<Integer> listSelectedProcess) {
		//creating as many threads as there are selected processes
		String threadOutputs = createParallelThreads(listSelectedProcess);
		
		//creating scene for individual process monitoring
		Label cpuMemoryLabel = new Label(threadOutputs);
		Button button = new Button();
		button.setText("Stop monitoring and return");
		button.setOnAction(e -> mainWindow.setScene(processIdScene));
		
		//creating horizontal box holding monitoring information
		final HBox horizontalBox = new HBox();
		horizontalBox.setSpacing(20);
		horizontalBox.setAlignment(Pos.CENTER);
		horizontalBox.getChildren().addAll(cpuMemoryLabel, button);
		
		monitorScene = new Scene (horizontalBox, 500, 500);
	}
	
	private String createParallelThreads(ArrayList<Integer> listSelectedProcess) {
		String informationAllProcess = "";
		
		for(int i=0; i < listSelectedProcess.size(); i++) {
			ProcessThread thread = new ProcessThread(listSelectedProcess.get(i));
			thread.run();
			informationAllProcess += thread.toString() + "\n";
		}
		return informationAllProcess;
	}
	
	private void setSceneShowWindow(VBox verticalBox) {
		scrollPane = new ScrollPane();
		scrollPane.setContent(verticalBox);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setFitToHeight(true);
		processIdScene = new Scene (scrollPane, 500, 500);
		
		mainWindow.setScene(processIdScene);
		mainWindow.show();
	}
}
