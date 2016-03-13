package hw2.edu.iit.cs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;



public class ClientMainClass {
	public static final int R = 31;  //random Prime Number
	
	
	public static Map<Integer,String> peersMap = new HashMap<Integer,String>();
	
	public static Map<Integer,Socket> socketMap = new HashMap<Integer,Socket>();
	
	public static int peerId = 0;
	
	public static Map<String,String> dataMap = new HashMap<String,String>();
	
	
	public static ServerSocket serverSocket  = null;
	
	public static boolean perfEvalMode = false;
	
	public static BufferedReader br = null;
	
	public static FileReader fr = null;
	
	/*
	 * 
	 * 
	 * Main Method called when peer/client is started
	 * 
	 * 
	 * */
	public static void main(String[] args) {
		ClientMainClass clMainClass = new ClientMainClass();
		try{
			clMainClass.initalizeClient();
		}catch(final Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	 * 
	 * 
	 * Initilization of Client with accepting Peer id from user and starting the relevant threads takes place here.
	 * 
	 * 
	 * */
	
	public void initalizeClient() throws IOException{		
		try{		
			/*GET ALL THE PEERS OR CLIENTS FROM CONFIG FILE*/
			HashingClass.writeOutputFile("initializing the client...",true);
			File configFile = new File("CONNECTEDPEERS.txt");
			if(configFile.exists()){
				fr = new FileReader(configFile);
				br = new BufferedReader(fr);
				final String line = br.readLine();
				if(null != line && !line.isEmpty()){
					String ipWithPort[] = line.split(";");
					for(String ipWithPortString : ipWithPort){
						final int peerId = Integer.parseInt(ipWithPortString.split(":")[0]);
						String ipWithP = ipWithPortString.split(":")[1];
						ipWithP = ipWithP +":" +ipWithPortString.split(":")[2];
						peersMap.put(peerId, ipWithP);						
					}
					
		/*READING FROM CONFIG FILE ENDS HERE*/				
					HashingClass.M = peersMap.size();			/*calculating Number of Peers Present*/		
					boolean inputAccept = true;
					while(inputAccept){
						br = new BufferedReader(new InputStreamReader(System.in));
						
		/*TODO change it to accept ip and port*/				
						HashingClass.writeOutputFile("Enter the Peer Id of the currently running Client from the Config File (Refer the Config File.)",true);					
						peerId = Integer.parseInt(br.readLine());
						HashingClass.writeOutputFile(""+peerId,false);
						if(null != peersMap && null != peersMap.get(peerId)){
							try{
								hostServer();     //Host the Server to Accept Input Here --- Thread 1..
								if(serverSocket == null){
									inputAccept = false;
								}								
							}catch(final Exception e){
								HashingClass.writeOutputFile("ERROR IN HOSTING .....",true);
								e.printStackTrace();
								inputAccept = false;
							}		
						}else{
							inputAccept = false;
						}
						if(!inputAccept){
							HashingClass.writeOutputFile("Peer not found in the Configuration File.Please Enter a valid Peer Id.If Configuration File is Updated please restart the Application.",true);
						}else{
							runThreads();
						}						
					}
				}else{
					HashingClass.writeOutputFile("CONFIGURATION FILE EMPTY PLEASE MAKE SURE YOU RUN THE INITIALIZE PROGRAM FIRST TO CREATE THE CONFIGURATION FILE.Run this program again after you run the initialize program",true);
				}
			}else{
				HashingClass.writeOutputFile("CONFIGURATION FILE NOT FOUND PLEASE MAKE SURE YOU RUN THE INITIALIZE PROGRAM FIRST TO CREATE THE CONFIGURATION FILE.Run this program again after you run the initialize program",true);
			}		
		}catch(final Exception e){
			e.printStackTrace();
		}finally{
			if(null != br){
				br.close();
			}
		}
	}
	/*
	 * 
	 * This Method hosts the Server in Client which waits for Operation requests from
	 * other Peers.
	 * 
	 * 
	 * 
	 * */
	public void hostServer() throws Exception{
		final String ipWithPort  = peersMap.get(peerId);
		final String ipAddress  = ipWithPort.split(":")[0];			
		final int portAddress  = Integer.parseInt(ipWithPort.split(":")[1]);						
		serverSocket = new ServerSocket(portAddress);
		HashingClass.writeOutputFile("Server Running ..",true);
	}
	/*
	 * 
	 * 
	 * This Methods runs all the relevany threads.
	 * 
	 * 
	 * */
	
	public static void runThreads() throws IOException, InterruptedException{
		/*Show Menu to the User Thread 2 starts here..*/		
		new Thread(new MenuAcceptInputThread()).start();
		while(true){
			new Thread(new ClientListenThread(serverSocket.accept())).start();				
						
		}		
	}
	

	
	

	
}
