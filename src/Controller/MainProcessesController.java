package Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import AlertBox.EmptyProcessSelectionAlert;
import Thread.ProcessThread;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainProcessesController implements Initializable{
	@FXML private ArrayList<String> arrayProcessId;
	@FXML private HBox[] horizontalBoxes;
	@FXML private Stage mainWindow;
	@FXML private Scene processIdScene;
	@FXML private Scene monitorScene;
	@FXML private ScrollPane scrollPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//finds information of all running processes
		arrayProcessId = displayProcessesSettup();
		horizontalBoxes = new HBox[arrayProcessId.size() + 1];
		
		//creating stage and scene with process information
		mainWindow = new Stage();
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
			
			//no selected process causes alert
			if(listSelectedProcess.size() == 0) {
				EmptyProcessSelectionAlert.displayAlert("No Selected Process",
						"Please select a minimum of one process to monitor.");
			}
			//minimum of 1 selected process
			else {
				createMonitorProcessScene(listSelectedProcess);
				mainWindow.setScene(monitorScene);
			}
		});
		horizontalBoxes[horizontalBoxes.length - 1]= new HBox();
		horizontalBoxes[horizontalBoxes.length - 1].getChildren().add(button);
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
		//creating horizontal box holding monitoring information
		final VBox verticalBox = new VBox();
		verticalBox.setSpacing(20);
		verticalBox.setAlignment(Pos.CENTER);
				
		//creating as many threads as there are selected processes
		String[] threadOutputs = createParallelThreads(listSelectedProcess);
		
		//creating scene for parallel process monitoring
		for(int i=0; i < threadOutputs.length; i++) {
			verticalBox.getChildren().add(new Label(threadOutputs[i]));
		}

		Button button = new Button();
		button.setText("Stop monitoring and return");
		button.setOnAction(e -> mainWindow.setScene(processIdScene));
		verticalBox.getChildren().add(button);
		
		scrollPane = settingScrollPane(verticalBox);
		monitorScene = new Scene (scrollPane, 500, 500);
	}
	
	private String[] createParallelThreads(ArrayList<Integer> listSelectedProcess) {
		String[] informationAllProcess = new String[listSelectedProcess.size()];
		
		for(int i=0; i < listSelectedProcess.size(); i++) {
			ProcessThread thread = new ProcessThread(listSelectedProcess.get(i));
			thread.run();
			informationAllProcess[i] = thread.toString() + "\n";
		}
		return informationAllProcess;
	}
	
	private ScrollPane settingScrollPane(Pane box) {
		scrollPane = new ScrollPane();
		scrollPane.setContent(box);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setFitToHeight(true);
		
		return scrollPane;
	}
	
	private VBox addHorizontalBoxToVertical() {
		VBox verticalBox = new VBox();
		verticalBox.setSpacing(20);
		verticalBox.getChildren().addAll(horizontalBoxes);
	    return verticalBox;
	}
	
	private void setSceneShowWindow(VBox verticalBox) {
		scrollPane = settingScrollPane(verticalBox);
		processIdScene = new Scene (scrollPane, 500, 500);
		
		mainWindow.setScene(processIdScene);
		mainWindow.show();
	}
}
