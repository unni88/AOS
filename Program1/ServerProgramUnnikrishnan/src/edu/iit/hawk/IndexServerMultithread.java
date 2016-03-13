package edu.iit.hawk;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class IndexServerMultithread extends Thread{

	private Socket clientSocket = null;
	
	public IndexServerMultithread(Socket socketTemp){
		super("IndexServerMultithread");
		this.clientSocket = socketTemp;
	}
	
	/*This Method has the main thread for starting the Index Server and getting ready for receiving requests form server.
	 * */
	
	public void run(){	
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		try{
	//		TestCasesFile.appendToLogFile("Client with IP Address "+clientSocket.getLocalAddress().getHostAddress()+" connected");
			ois = new ObjectInputStream(clientSocket.getInputStream());
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			Map<String,String> readMap  = (Map<String, String>) ois.readObject();
			if(null != readMap && (!readMap.isEmpty())){				
				final String clientIp = readMap.get("ip");
				final String port = readMap.get("port");
				final String type = readMap.get("type");
				if(null != type){
					if(type.equalsIgnoreCase("data") &&( (null != clientIp) && (!clientIp.isEmpty()))  && ((null != port)  && (!port.isEmpty()))   ){
						for(Map.Entry<String, String> mapEntry:readMap.entrySet()){					
							if(null != mapEntry.getKey() && (!mapEntry.getKey().equalsIgnoreCase("type")) && 
									(!mapEntry.getKey().equalsIgnoreCase("ip"))
									&& (!mapEntry.getKey().equalsIgnoreCase("port"))){
								IndexServerSocket.populateIndexMap(mapEntry.getKey(), clientIp, port, mapEntry.getValue());
							}			
						}
					}else if(type.equalsIgnoreCase("search")){
						final String fileName = readMap.get("fileNameToSearch");
						final Map<String,ArrayList<String>> fileNamesWithDetails = IndexServerSocket.getDetsIndexingMap(fileName);
						oos.writeObject(fileNamesWithDetails);
					}else if(type.equalsIgnoreCase("remove")){
						IndexServerSocket.removeClientFromIndex(clientIp,Integer.parseInt(port));						
					}else if(type.equalsIgnoreCase("clean")){
						IndexServerSocket.cleanUpPerformanceEvalData();
					}else{
						System.out.println("Invalid Type :"+type);
					}
				}
			}		
	//		IndexServerSocket.getDetsIndexingMap(null);
		}catch(final Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(null != ois){
					ois.close();
				}
				if(null != oos){
					oos.close();
				}
				if(null != clientSocket){
					clientSocket.close();
				}
			}catch(final Exception e){
				e.printStackTrace();
			}
		}	
	}

	
}
