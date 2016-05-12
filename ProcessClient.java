import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map.Entry;

/**
 * Class ProcessClient
 * Each Process sends event or state to another process running in different machine or same machine based 
 * on configurations in XML file on request from Process Thread.
 * @author Raghav Babu
 * Date : 04/15/2016
 */
public class ProcessClient{

	Event event;
	int toProcessId;
	String toIPAddress = null;
	int  toPort;

	public ProcessClient(Event event, int toProcessId) {

		if(event.eventType == EventType.HEART_BEAT){
			this.event = event;
			this.toProcessId = toProcessId;
			this.toIPAddress = ProcessIPPortXmlParser.processIDToIpMap.get(toProcessId);
			this.toPort = ProcessIPPortXmlParser.processIDToHBPortMap.get(toProcessId);
		}
		else{
			this.event = event;
			this.toProcessId = toProcessId;
			this.toIPAddress = ProcessIPPortXmlParser.processIDToIpMap.get(toProcessId);
			this.toPort = ProcessIPPortXmlParser.processIDToPortMap.get(toProcessId);
		}
	}


	public boolean send() {

		try {
			Socket socket = null;
			try {

				try {
					socket = new Socket(toIPAddress, toPort);
				} catch (Exception e) {
					return false;
				}

				//write msg object
				OutputStream os = socket.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);

				if(event.eventType == EventType.ELECT){
					//System.out.println("------------------------------------------");
					oos.writeObject(event);
					System.out.println(event);
					System.out.println("Sending election message to processId : "+toProcessId);
				}
				//sending co-ordinator msg to process which sent the election msg.
				else if(event.eventType == EventType.ANSWER){
					oos.writeObject(event);
					System.out.println(event);
				}
				//sending co-ordinator msg to all processes.
				else if(event.eventType == EventType.CO_ORDINATOR){
					oos.writeObject(event);
					System.out.println("Sending coordinator message to processId : "+toProcessId);
					System.out.println(event);
				}
	
				//to check if leader process is running. sending heartbeat every 3 seconds.
				else if(event.eventType == EventType.HEART_BEAT){
					oos.writeObject(event);
				}

			}catch (Exception e){
				System.out.println("Exception while passing event object to  "+toIPAddress);
				e.printStackTrace();
			}
			socket.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
}
