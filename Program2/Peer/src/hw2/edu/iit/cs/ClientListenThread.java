package hw2.edu.iit.cs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import hw2ClientProgram.PerfEValClass;

public class ClientListenThread extends Thread{

	private Socket clientSocket = null;
	
	
	public final static Map<String,String> dataMap = new HashMap<String,String>();
	
	public final static Map<String,String> requestMap = new HashMap<String,String>();
	
	static {
		requestMap.put("requestType",null);
		requestMap.put("data", null);
		requestMap.put("key", null);
	}
	
	/*
	 * 
	 * 
	 * Main Constructor of the class
	 * 
	 * 
	 * */
	public ClientListenThread(Socket socketTemp){
		clientSocket = socketTemp;
	}
			
	/*
	 * 
	 * 
	 * The Run() Method of the Thread.
	 * 
	 * 
	 * */
	public void run(){
		try{			
			serveRequestsFromPeers();
		}catch(final Exception e){
			e.printStackTrace();
		}		
	}
	/*
	 * This is the method that listens for request from other Peers for either get. delete or Put operation.
	 * It is constantly listening for requests.
	 * 
	 * 
	 * 
	 * */

	private void serveRequestsFromPeers() throws IOException{
		BufferedReader inputBR= null;
		PrintWriter out = null;
		try{
			out = new PrintWriter(clientSocket.getOutputStream(),true);
			inputBR  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			final String requestString = inputBR.readLine();
			if(null != requestString && !requestString.isEmpty()){
				final String entriesWithKeys[] = requestString.split(";");
				for(String entryWithKey:entriesWithKeys){	
					requestMap.put(entryWithKey.split(":")[0],entryWithKey.split(":")[1]);				
				}
			}			
			if(null != requestMap && !requestMap.isEmpty() && null != requestMap.get("requestType")){
				String requestType = requestMap.get("requestType");
				String response = null;				
				if(requestType.equalsIgnoreCase("get") && (null != requestMap.get("key")) && (!requestMap.get("key").isEmpty())){
				//	HashingClass.writeOutputFile("REQUESTING:"+requestMap.get("key"));
					response  = getDataFromMap(requestMap.get("key"));
				}else if(requestType.equalsIgnoreCase("put") && (null != requestMap.get("key")) && (null != requestMap.get("data")) ){
					response = putDataintoMap(requestMap.get("key"), requestMap.get("data"));
				}else if(requestType.equalsIgnoreCase("delete") && (null != requestMap.get("key"))){
					deleteDataFromMap(requestMap.get("key"));
					response = "true";
				}
		//		showCurrentContentsDataMap();
				if(null != response){
					out.println(response);
				}
			}
		}catch(final Exception e){
			e.printStackTrace();
		}finally{
		    	    	
		}	    	
	}
	/*
	 * 
	 * Method called while Put operation request is made.
	 * 
	 * 
	 * 
	 * */
	private String putDataintoMap(final String key,final String value){
		String result = "true";
		try{
			dataMap.put(key, value);
		}catch(final Exception e){
			result = "false";
			e.printStackTrace();
		}
		return result;
	}
	/*
	 * 
	 * 
	 * 
	 * Method called while get operation request is made.
	 * 
	 * */
	private String getDataFromMap(final String key){
		String result = dataMap.get(key);
		if(null == dataMap.get(key) || dataMap.get(key).isEmpty()){
			result = "false";
		}
		return result;
	}
	/*
	 * 
	 * 
	 * Method called while  delete operation request is made
	 * 
	 * 
	 * */
	private void deleteDataFromMap(final String key){
		dataMap.remove(key);		
	}

	private void showCurrentContentsDataMap(){
		if(null != dataMap && !dataMap.isEmpty()){
			for(Map.Entry<String, String> dataMapEntry :dataMap.entrySet()){
				HashingClass.writeOutputFile(dataMapEntry.getKey()+":"+dataMapEntry.getValue(),true);
			}
		}
	}
}
