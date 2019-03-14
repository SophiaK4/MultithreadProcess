package Thread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Sophia Kavousifard
 *
 */
public class ProcessThread implements Runnable {
	private int name;//name is the process ID
	private String processOutput;
	
	public ProcessThread(int name) {
		this.name = name;
	}
	
	//Getter
	public String getProcessOutput() {
		return processOutput;
	}
	//Setter
	public void setProcessOutput(String processOutput) {
		this.processOutput = processOutput;
	}


	@Override
	public void run() {
		String processLine;
	    Process process;
	    
		try {
			process = Runtime.getRuntime().exec("ps -p " + name + " -o %cpu,%mem");
		    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    
		    while ((processLine = input.readLine()) != null) {
		    	//display cpu and memory utilization
		    	processOutput += processLine + "\n";
		    	System.out.println(processLine);
		    }
		    input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Thread linked to Process ID " + name + ": \n" + processOutput;
	}
}
