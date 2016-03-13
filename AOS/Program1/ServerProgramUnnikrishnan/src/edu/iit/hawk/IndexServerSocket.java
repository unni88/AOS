package edu.iit.hawk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IndexServerSocket {
	
	private static Map<String,ArrayList<String>> indexMap = new ConcurrentHashMap<String,ArrayList<String>>();

	private static volatile String continueListening  = "true";
	
	private static String perfEvalPrefix  = "perfevaldummy";
	
	/*
	 * This Method is responsible for populating the index Map
	 * */
	
	public static int populateIndexMap(final String fileName,final String clientip,final String port,final String filePath){
		ArrayList<String> indexLocations = new ArrayList<String>();		
		int result = 0;
		try{
			if(null != indexMap && (!indexMap.isEmpty()) && (null != indexMap.get(fileName)) && (!indexMap.get(fileName).isEmpty())  ){
				indexLocations = indexMap.get(fileName);
			}
			indexLocations.add(clientip+":"+port+":"+filePath);		
			indexMap.put(fileName, indexLocations);
		/*
			for(Map.Entry<String, ArrayList<String>>entry :indexMap.entrySet()){
				String key = entry.getKey();
				ArrayList<String>values = entry.getValue();
				for(String value:values){
					TestCasesFile.appendToLogFile(key+":"+value);
				}
			} */
		}catch(final Exception e){
			e.printStackTrace();
			result = 1;
		}
		return result;
	}
/*
 * This Method retrieved details from the Index Map.
 * */
	
	public static Map<String,ArrayList<String>> getDetsIndexingMap(final String fileName){
		Map<String,ArrayList<String>> details =new HashMap<String,ArrayList<String>>();
		if(null != indexMap && null != fileName && null != indexMap.get(fileName)){
			details.put(fileName,indexMap.get(fileName));
		}else if(null != fileName){
			details = null;
		}else if(null ==  fileName){
			details = indexMap;
		}
		return details;
	}
	
	/*This Method tries to cleanup the Index Map after exitting from the Performance Evaluation Mode
	 * */
	
	public static void cleanUpPerformanceEvalData(){
		final ArrayList<String> indexMapEntryKeysTORemove  =  new ArrayList<String>();
		if(null != indexMap && (!indexMap.isEmpty())){
			for(Map.Entry<String, ArrayList<String>> indexMapEntry : indexMap.entrySet()){
				final String key = indexMapEntry.getKey();
				if(null != key && !key.isEmpty()){
					if(key.split("_")[0].equalsIgnoreCase(perfEvalPrefix)){
						indexMapEntryKeysTORemove.add(key);
					}
				}
			}
			if(null != indexMapEntryKeysTORemove && !indexMapEntryKeysTORemove.isEmpty()){
				for(String indexMapEntryKey:indexMapEntryKeysTORemove){
					indexMap.remove(indexMapEntryKey);
				}
			}
		}
	}
	
	/*This Method removes Client From Index if the Client is not Serving any request. 
	 * */
	
	public static void removeClientFromIndex(final String clientIp,final int clientPortNumber){
		if(null != indexMap &&(!indexMap.isEmpty()) ){
			for(Map.Entry<String, ArrayList<String>> indexMapEntry:indexMap.entrySet()){
				final String fileName  = indexMapEntry.getKey();
				final ArrayList<String>ipDetails = indexMapEntry.getValue();
				final ArrayList<String> ipDetailsToAdd = new ArrayList<String>();
				if(null != ipDetails && !ipDetails.isEmpty()){					
					for(String ipDet : ipDetails){
						final String[] arr =  ipDet.split(":");
						if(arr.length >= 3){						
							final String ip = ipDet.split(":")[0];
							final int portNumber = Integer.parseInt(ipDet.split(":")[1]);
							//final String filePath = ipDet.split(":")[2];						
							if(ip.equalsIgnoreCase(clientIp) &&(portNumber == clientPortNumber) ){														
							}else{
								ipDetailsToAdd.add(ip);
							}
						}
					}			
				}
				if(null != ipDetailsToAdd && !ipDetailsToAdd.isEmpty()){
					indexMap.put(fileName, ipDetailsToAdd);
				}else{
					indexMap.remove(fileName);
				}	
			}
		}
	}
	
	/*Main Method for the index Server is present here.
	 * */
	
	public static void main(String args[]){
		final IndexServerSocket is = new IndexServerSocket();
		try {
			is.initializeServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*The Main Initialization Method is present here it accepts port number from user and initiates the Thread that waits for request from clients.
	 * */
	public void initializeServer() throws IOException{
		System.out.println("Starting Index Server");
		ServerSocket serverSocket = null;
		int portNumber = 0;
		final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try{			
			System.out.println("Enter the port Number to host Index Server in (Entering 0 will select an Available Free Port Automatically)");			
			String portString = br.readLine();
			if(null != portString && !portString.isEmpty()){
				try{
					portNumber = Integer.parseInt(portString);
				}catch(final Exception e){
					e.printStackTrace();
					portNumber = 0;
				}
			}			
			try{
				serverSocket = new ServerSocket(portNumber);						
			}catch(final Exception e){
				e.printStackTrace();
				serverSocket = new ServerSocket(0);					
			}						
			new UserInputClass().start(); //Listening for User Input			
			System.out.println("Server Listening at port :"+serverSocket.getLocalPort()+",IP Address:"+InetAddress.getLocalHost().getHostAddress());										
			try{
				new UserInputClass().start();
				while( (null != continueListening) && (continueListening.equalsIgnoreCase("true"))){
					new IndexServerMultithread(serverSocket.accept()).start();
				}
				
			}catch(final Exception e){
				e.printStackTrace();
			}
		
		}catch(final Exception e){
			e.printStackTrace();
		}finally{
			br.close();
			serverSocket.close();
		}		
	}
	
	class UserInputClass extends Thread{
		public void run(){
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				br.readLine();
				IndexServerSocket.continueListening = "false";
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
}
