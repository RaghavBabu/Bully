/**
 * Class LeaderFailureDetector
 * If the process couldnt connect to the leader, it initiates the election again.
 * @author Raghav Babu
 * Date:04/18/2016
 */
public class LeaderFailureDetector extends Thread {

	Process process;

	public LeaderFailureDetector(Process process){
		this.process = process;
	}

	@Override
	public void run(){

		while(true){

			if(process.leaderSelected){

				Event eve = new Event(EventType.HEART_BEAT, Process.processId);

				ProcessClient leaderPing = new ProcessClient(eve, Process.currentLeader);
				process.leaderPing = leaderPing.send() ? true : false;

				if(!process.leaderPing){
					process.leaderSelected = false;
				}

				//sending heartbeat to leader process for every 3 seconds.
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
