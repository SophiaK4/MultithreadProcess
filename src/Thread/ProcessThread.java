package Thread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Sophia Kavousifard
 * Thread class that holds thread ID as name
 * and when run, will execute linux command to
 * display CPU and Memory information
 */
public class ProcessThread implements Runnable {
	private int name;//name is the process ID
	private String processOutput;
	
	/**
	 * Non default constructor
	 * @param name ID of process
	 */
	public ProcessThread(int name) {
		this.name = name;
	}
	
	/**
	 * Getter for CPU and Memory information
	 * @return string holding these informations
	 */
	public String getProcessOutput() {
		return processOutput;
	}
	/**
	 * Setter for CPU and Memory information
	 * @param processOutput new information to be stored
	 */
	public void setProcessOutput(String processOutput) {
		this.processOutput = processOutput;
	}


	@Override
	/**
	 * Linux command to be performed
	 *  when thread is run
	 */
	public void run() {
		String processLine;
	    Process process;
	    
		try {
			//linux command
			process = Runtime.getRuntime().exec("ps -p " + name + " -o %cpu,%mem");
		    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    
		    while ((processLine = input.readLine()) != null) {
		    	//display cpu and memory utilization
		    	processOutput += processLine + "\n";
		    	System.out.println(processLine);
		    }
		    
		    //removing word null form output
		    if(processOutput.contains("null")) {
		    	processOutput = processOutput.replaceAll("null", "");
		    }
		    
		    input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * Thread information to be displayed
	 */
	public String toString() {
		return "Thread linked to Process ID " + name + ": \n" + processOutput;
	}
}
