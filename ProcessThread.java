import java.util.List;


/**
 * Class ProcessThread
 * Each ProcessThread running initiates leader election process.
 * @author Raghav Babu
 * Date : 04/16/2016
 */
public class ProcessThread extends Thread {

	Process process;

	public ProcessThread(Process process) {
		this.process = process;
	}

	@Override
	public void run() {

		while(true) {			
			
			// if leader not selected or process couldnt ping to the current leader process..re-initiate election.
			if(!process.leaderSelected || !process.leaderPing){
				process.answerResponses.clear();
				process.coordinatorResponse = false;

				System.out.println("--------Initiating Leader Election----------");
				startElection();
				System.out.println("--------Election Successfully Completed!!!----------");
				System.out.println("***** Current Leader : "+Process.currentLeader+" *****");
			}
		}
	}

	private void startElection() {
		
		//sending election message to all process with higher IDs.
		System.out.println("Sending election message to all higher identifier processes");
		Event event = new Event(EventType.ELECT, Process.processId);
		ProcessClient client;
		List<Integer> processIDs = Utils.getHigherIdentifiers(Process.processId);
		
		for(int id : processIDs){
			client = new ProcessClient(event, id);
			client.send();
		}
	
		//waiting for answer response.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//if received answer from one process then wait for coordinator message.
		if(process.answerResponses.contains(true)){
			
			//waiting for co-ordinator response.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Waiting to receive co-ordinator response.");
			
			//no coordinator response, make itself coordinator and send msg to all lower process IDs.
			if(!process.coordinatorResponse){
				System.out.println("No coordinator message received, so making itself leader and sending co-ordinator message to"
						+ " lower identifier processes");
				 event = new Event(EventType.CO_ORDINATOR, Process.processId);
				 Process.currentLeader = Process.processId;
				 process.leaderSelected = true;
				 processIDs = Utils.getLowerIdentifiers(Process.processId);
				
				for(int id : processIDs){
					client = new ProcessClient(event, id);
					client.send();
				}		
			}		
			else{
				//do nothing.
			}
			
		}
		//no answer from any of the higher identifier processes.
		else{
			System.out.println("No answer message received, so making itself leader and sending co-ordinator message to"
					+ " lower identifier processes");
			 event = new Event(EventType.CO_ORDINATOR, Process.processId);
			 Process.currentLeader = Process.processId;
			 process.leaderSelected = true;
			 processIDs = Utils.getLowerIdentifiers(Process.processId);
			
			for(int id : processIDs){
				client = new ProcessClient(event, id);
				client.send();
			}
		}
		
	}
	
}
