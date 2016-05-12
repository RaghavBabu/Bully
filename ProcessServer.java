import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class ProcessServer
 * Each Process receives event from other process and updates its vector clock.
 * @author Raghav Babu
 * Date : 04/18/2016
 */
public class ProcessServer extends Thread {

	private InetSocketAddress boundPort = null;
	private static int port;
	private ServerSocket serverSocket;
	Process process;

	public ProcessServer(Process process) {
		port = ProcessIPPortXmlParser.processIDToPortMap.get(Process.processId);
		this.process = process;
	}

	@Override
	public void run(){

		try {

			//init server socket.
			initServerSocket();

			while(true) {

				Socket connectionSocket;
				ObjectInputStream ois;
				InputStream inputStream;

				connectionSocket = serverSocket.accept();
				inputStream = connectionSocket.getInputStream();
				ois = new ObjectInputStream(inputStream);

				Event event = (Event) ois.readObject();	

				//on receiving election message, sending a answer response.
				if(event.eventType == EventType.ELECT){

					Event e;
					ProcessClient client;
					
					// if the leader is already selected.
					if(process.leaderSelected){
						
						e = new Event(EventType.ANSWER, Process.processId);
						client = new ProcessClient(e, event.processId);
						client.send();
						
						e = new Event(EventType.CO_ORDINATOR, Process.processId);
						client = new ProcessClient(e, event.processId);
						client.send();
					}
					//just send an answer message.
					else{
						e = new Event(EventType.ANSWER, Process.processId);
						client = new ProcessClient(e, event.processId);
						client.send();
					}

				}
				//On receiving answer reponse, start the co-ordinator timeout to get co-ordinator response.
				else if(event.eventType == EventType.ANSWER){
					System.out.println("Got Response from process with higher process ID : "+event.processId);
					process.answerResponses.add(true);
				}

				//if co-ordinator message received, set this process as new leader.
				else if(event.eventType == EventType.CO_ORDINATOR){
					Process.currentLeader = event.processId;
					process.coordinatorResponse = true;
					process.leaderSelected = true;
					System.out.println("***** Current Leader : "+Process.currentLeader+" *****");
				}

			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * method which initialized and bounds a server socket to a port.
	 * @return void.
	 */
	private void initServerSocket()
	{
		boundPort = new InetSocketAddress(port);
		try
		{
			serverSocket = new ServerSocket(port);

			if (serverSocket.isBound())
			{
				System.out.println("Server bound to data port " + serverSocket.getLocalPort() + " and is ready...");
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to initiate socket.");
		}

	}

}
