import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class HeartBeatServer
 * This process will be running in leader process and receives heartbeat messages from other processes.
 * @author Raghav Babu
 * Date:04/18/2016
 */
public class HeartBeatServer extends Thread {

	private InetSocketAddress boundPort = null;
	private static int port;
	private ServerSocket serverSocket;
	Process process;

	public HeartBeatServer(Process process) {
		port = ProcessIPPortXmlParser.processIDToHBPortMap.get(Process.processId);
		this.process = process;
	}

	@Override
	public void run(){

		try {

			initServerSocket();

			while(true) {

				if(Process.currentLeader == Process.processId){

					Socket connectionSocket;
					ObjectInputStream ois;
					InputStream inputStream;

					connectionSocket = serverSocket.accept();
					inputStream = connectionSocket.getInputStream();
					ois = new ObjectInputStream(inputStream);

					Event event = (Event) ois.readObject();	

					//for every heart beat msg, send a heartbeat response to all other processes.
					if(event.eventType == EventType.HEART_BEAT){
						//System.out.println("Receiving heart beat msgs from all other processes");

					}
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
