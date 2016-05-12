import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Class Process
 * Each Process running initiates all threads to choose the leader among all the running processes.
 * @author Raghav Babu
 * Date : 04/18/2016
 */
public class Process {

	static int processId;
	static int totalProcess;
	static int currentLeader = 0;
	
    volatile boolean leaderSelected = false;
    volatile boolean leaderPing = false;
    
	List<Boolean> answerResponses;
	boolean coordinatorResponse = false;

	public Process(){
		this.answerResponses = new ArrayList<Boolean>();
	}

	/*
	 * Main method.
	 */
	public static void main(String[] args) {

		//parse XML files to updates process running IP and ports map.
		ProcessIPPortXmlParser parser = new ProcessIPPortXmlParser();
		parser.parseXML();

		processId = Integer.parseInt(args[0]);
		totalProcess = Integer.parseInt(args[1]);

		Process process = new Process();

		//start process server to receive events from other processes.
		ProcessServer server = new ProcessServer(process);
		server.start();

		//starting the process thread.
		ProcessThread procThread = new ProcessThread(process);
		procThread.start();

		//starting failure detector thread to monitor leader process.
		LeaderFailureDetector detector = new LeaderFailureDetector(process);
		detector.start();
		
		//starting heartBeat server 
		HeartBeatServer hbServer = new HeartBeatServer(process);
		hbServer.start();


	}


}
