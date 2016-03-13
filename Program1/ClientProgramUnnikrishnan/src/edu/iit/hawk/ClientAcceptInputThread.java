package edu.iit.hawk;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientAcceptInputThread extends Thread{
	private static Map<Integer, String> menuMap = new HashMap<Integer,String>(); //Menu Details are stored Here
	public static String indexServerIP = null; //The Ip Address of the Index Server
	public static int indexServerPort  = 0;	 //The port Address of the Index Server
	public static String fileSharePath = "unnikrishnan/sharedfiles";  //Path where Files Shared will be shown..
	public static String fileDownloadPath = "unnikrishnan/downloadedfiles"; //Path where Downloaded Files will be present..
	public Socket clientConnectSocket = null; //Socket used to Connect for Client

	
	public static boolean perfEvalMode = false;  //Whether System is running in Evaluation Mode or not.

	public static boolean isQuit = false; //Whether User has entered q for Quitting the System.

	public static String fileSharePathPerf = "unnikrishnan/perfSharedFiles"; //Files for Performance Evaluation uplaod are present here..
	public static String fileDownloadPathPerf  = "unnikrishnan/perfdownloadedfiles"; //Files for Performance Evaluation Download
	
	public static Long seqLookup = 0l;  //Time for Lookup
	public static Long seqDownload = 0l;//Time for Download
	
	public static BufferedReader br = null;
	
	public static Integer fileCopiesToBeMade = 1000;
	static{
		menuMap.put(1, "Enter a Index Server IP Address (eg: 192.168.1.1).Enter q to Quit the Program");
		menuMap.put(2, "Enter the Port Address of Index Server.Enter q to Quit the Program");
		menuMap.put(3, "Enter Choice : 1.Show Config Details of Client,2.Sync Files List With Index Server Again,3.Search Files in Index Server,4.Enter Performance Evaluation Mode,q.Quit and Close the Client");		
        menuMap.put(4, "Enter Choice : 1:Sequential Perf Evalu , 2:Concurrent Perf Eval"); 
	}

/* This Method contains the Thread Logic for displaying the Menu and Activities associated with it.
 * like Synchronizing and connecting to INdex Server and request file for download 
 * 
 * */	
	
	public void run(){
		try {
			indexServerIP = showMenu(1,null);     //Get Index Server IP.
			indexServerPort  = Integer.parseInt(showMenu(2,null)); //Get Index Server Port
			Map<String,String> resultMap  = getFileNamestoSync();  	//get All File Names in Client to Sync	
			Map<String,String> dataMap  = (Map<String, String>) connectToIndexServer(resultMap);
			while(null == dataMap){
				TestCasesFile.appendToLogFile("Index Server Connection Failed . Please Renter Valid Details");
				indexServerIP = showMenu(1,null);     //Get Index Server IP.
				indexServerPort  = Integer.parseInt(showMenu(2,null)); //Get Index Server Port
				resultMap  = getFileNamestoSync();  	//get All File Names in Client to Sync
				dataMap  = (Map<String, String>) connectToIndexServer(resultMap);
			}
			
			showMenu(3,dataMap);
		} catch (IOException e) {
			e.printStackTrace();
		} //Connect To Index Server to Send the Sync Data.	

	}
	
	/* This Method  Shows the Actual Menu For Displaying to User depending on what stage user is in.
	 * 
	 * */	

	
	private String showMenu(final int menuId,Map<String,String> dataMap) throws IOException{
		String input = "";
		boolean validatedInput = false;
		br = new BufferedReader(new InputStreamReader(System.in));
		if(null ==  dataMap){
			dataMap = new HashMap<String,String>();
		}
		
		try{    		
			while(( (!validatedInput) && null != menuMap.get(menuId)) ){
				TestCasesFile.appendToLogFile(menuMap.get(menuId)+".");
				input = br.readLine();
				validatedInput = validateInput(menuId, input);
				if(!validatedInput){
					TestCasesFile.appendToLogFile("Invalid Entry.");
				}
				else if( (validatedInput) && (input.equalsIgnoreCase("q")) ){
					br.close();
					isQuit = true;
					dataMap.put("type", "remove");
					connectToIndexServer(dataMap);
				}else if(validatedInput){
					processInput(menuId,input,dataMap);
				}

			}
		}catch(final Exception e){
			e.printStackTrace();
			br.close();
			//exitAndCloseClient(); TODO bring it later
		}
		return input;
	}
	
	/* This Method processes the valid Input given by the user in response to the Menu 
	 * */

	private void processInput(final int menuId,final String input,Map<String,String> dataMap) throws IOException{
		if(menuId == 3){
			if(input.equals("1")){
				if(perfEvalMode){
					perfEvalMode = false;
				}				
				TestCasesFile.appendToLogFile("-------------------Client Details----------------");
				TestCasesFile.appendToLogFile("IP Address:"+dataMap.get("ip"));
				TestCasesFile.appendToLogFile("Synced Files :");
				if(null != dataMap && !dataMap.isEmpty()){
					for(Map.Entry<String, String> entry:dataMap.entrySet()){
						if( (!entry.getKey().equalsIgnoreCase("ip"))  && (!entry.getKey().equalsIgnoreCase("port")) && (!entry.getKey().equalsIgnoreCase("type")) )
							TestCasesFile.appendToLogFile("#"+entry.getKey()+"#");
					}
				}else{
					TestCasesFile.appendToLogFile("No Details to Show");
				}
				TestCasesFile.appendToLogFile("File Downloads Served on Port:"+dataMap.get("port"));
				TestCasesFile.appendToLogFile("-------------------------------------------------");
				showMenu(3,dataMap);
			}else if(input.equals("2")){
				if(perfEvalMode){
					perfEvalMode = false;
				}
				try{
					Map<String,String> resultMap  = getFileNamestoSync();  		
					dataMap  = (Map<String, String>) connectToIndexServer(resultMap);
					showMenu(3,dataMap);
				}catch(final Exception e){
					e.printStackTrace();
				}
			}else if(input.equals("3")){  
				if(perfEvalMode){
					perfEvalMode = false;
				}
				br = new BufferedReader(new InputStreamReader(System.in));	    			
				try{
					final Map<String,String> resultMap = new HashMap<String,String>();
					resultMap.put("type","search");
					resultMap.put("fileNameToSearch",null);
					Map<String,ArrayList<String>>fileDetails = (Map<String,ArrayList<String>>) connectToIndexServer(resultMap); 					
					if(fileDetails == null){
						TestCasesFile.appendToLogFile("No Details Found in Index Server.Please Try Again..");
						showMenu(3, dataMap);
					}else{
						showAllFilesAvailableForDownload(fileDetails);
						TestCasesFile.appendToLogFile("Enter any File Name From above Details to Download");
						final String fileName  = br.readLine();
						if(null != fileName && (!fileName.isEmpty()) && (null !=fileDetails.get(fileName) && (!fileDetails.get(fileName).isEmpty()))){
							requestFileForDownload(fileDetails.get(fileName), fileName);
						}else{
							TestCasesFile.appendToLogFile("INVALID ENTRY.Please Try Again..");
						}
					}
				}catch(final Exception e){
					e.printStackTrace();
				}finally{
					//br.close();
				}			
			}else if(input.equals("4")){
				perfEvalMode = true;
				try{
					br = new BufferedReader(new InputStreamReader(System.in));
					TestCasesFile.appendToLogFile("Enter the Number of Copies to be Made for Performance Evaluation");
					final String cpy = br.readLine();
					fileCopiesToBeMade = Integer.parseInt(cpy);




					TestCasesFile.createThousandCopies();
					Map<String,String> fileNamesToSync = getFileNamestoSync();
					dataMap  = (Map<String, String>) connectToIndexServer(fileNamesToSync);
					Map<String,ArrayList<String>> detsMap = new HashMap<String,ArrayList<String>>();
					seqLookup = 0l;
					int i = 0;
					TestCasesFile.appendToLogFile("Calculating Lookup Time Please Wait ..");
					if( (null != fileNamesToSync) && (!fileNamesToSync.isEmpty())){
						for(Map.Entry<String, String> fileNameToSync:fileNamesToSync.entrySet()){
							if(i < fileCopiesToBeMade){

								final String key = fileNameToSync.getKey();
								if(null != key && (!key.equalsIgnoreCase("type"))  
										&& (!key.equalsIgnoreCase("fileNameToSearch"))
										&& (!key.equalsIgnoreCase("ip"))
										&& (!key.equalsIgnoreCase("port"))
										&& (!key.isEmpty())								
										){				
									dataMap.put("type","search");
									dataMap.put("fileNameToSearch",fileNameToSync.getKey());
									Map<String,ArrayList<String>>fileDetails = (Map<String,ArrayList<String>>) connectToIndexServer(dataMap); 					
									if(null != fileDetails && !fileDetails.isEmpty() && null != fileDetails.get(fileNameToSync.getKey())){
										detsMap.put(fileNameToSync.getKey(), fileDetails.get(fileNameToSync.getKey()));
									}			
								}
								i = i + 1;
							}
						}			
					}


					TestCasesFile.appendToLogFile("Total Time To Lookup"+seqLookup);

					seqDownload = 0l;
					i = 0;
					if(null != detsMap && !detsMap.isEmpty()){
						if(i < fileCopiesToBeMade){

							for(Map.Entry<String, ArrayList<String>> detMap:detsMap.entrySet()){
								requestFileForDownload(detMap.getValue(), detMap.getKey());
							}	
							i = i + 1;
						}
					}
					TestCasesFile.appendToLogFile("Total Time For Download"+seqDownload);	
				}catch(final Exception e){
					
				}finally{
					
				}

				showMenu(3,dataMap);
			}			
		}						
	}

/*
 * This Method is for cleaning up the Index in the Index Server after performance evaluation is completed 
 * */	
	private void cleanUpIndexServer(){
		try{
			final Map<String,String> resultMap = new HashMap<String,String>();
			resultMap.put("type","clean");
			resultMap.put("fileNameToSearch",null);
			connectToIndexServer(resultMap); 				
		}catch(final Exception e){
			e.printStackTrace();
		}
	}
/* This Method is executed in response to search request.It Shows all files available for download from the Index Server.
 * */	
	
	private void showAllFilesAvailableForDownload(final Map<String,ArrayList<String>> allFileDetails){
		if(null != allFileDetails && !allFileDetails.isEmpty()){
			TestCasesFile.appendToLogFile("-----All Synced Files Details------");			
			for (Map.Entry<String, ArrayList<String>> allFileDetail : allFileDetails.entrySet()) {			
				final String fileName  = allFileDetail.getKey();				
				final ArrayList<String>IPDetails  = allFileDetail.getValue();
				StringBuffer ips = new StringBuffer();		
				if(null != IPDetails && !IPDetails.isEmpty()){
					for(String ipDet : IPDetails){					
						final String[] arr = ipDet.split(":"); 
						if(arr.length >= 3){						
							final String clientIp = ipDet.split(":")[0];						
							final int portNumber = Integer.parseInt(ipDet.split(":")[1]);
							final String filePath = ipDet.split(":")[2];
							ips.append(clientIp+",");					
						}
					}
					TestCasesFile.appendToLogFile("FILE:"+fileName);
					TestCasesFile.appendToLogFile("LOCATIONS:"+ips);					
				}				
			}
			TestCasesFile.appendToLogFile("----------------------------------");		
		}else{
			TestCasesFile.appendToLogFile("No Details Found on Index Server");
		}
	}
	

/*This Method validates the Input given by the User in response to the menu.
 * It Checks if the User gave a valid input.
 * */

	private  boolean validateInput(final int menuId,final String input){
		boolean validated = true;
		if( (null == input) ||(input.trim().length() == 0) ){
			validated = false;
		}
		if(menuId == 3 && validated){
			if( !( (input.equalsIgnoreCase("1")) || (input.equalsIgnoreCase("2")) || (input.equalsIgnoreCase("3")) || (input.equalsIgnoreCase("4")) || (input.equalsIgnoreCase("q")))  ){
				validated = false;
			}	    		
		}
		return validated; 
	}
/*
 * This Method is used to connect to the Index Server.
 * */	
	
	public Object connectToIndexServer(Map<String,String> dataMap) throws IOException{    			
		Object result = new Object();
		Map<String,String> resultMap = new HashMap<String,String>();
		Map<String,ArrayList<String>> resultMapList = new HashMap<String,ArrayList<String>>();
		Long beforeTime = 0l;
		Long afterTime  = 0l;
		
		try{
			beforeTime = System.currentTimeMillis();
			
	//		if(null == clientConnectSocket){
				clientConnectSocket  = new Socket(indexServerIP, indexServerPort);
	//		}	
		}catch(final Exception e){
			result= null;
			clientConnectSocket = null;
			e.printStackTrace();
			TestCasesFile.appendToLogFile("INDEX SERVER CONNECTION FAILED.Check Connection.");
		}
		if(null != clientConnectSocket){
			final ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientConnectSocket.getOutputStream());
			final ObjectInputStream objectInputStream = new ObjectInputStream(clientConnectSocket.getInputStream());
			try{
				if(null != ClientSocket.serverSocket){	   			
					dataMap.put("ip",InetAddress.getLocalHost().getHostAddress());
					dataMap.put("port", ClientSocket.serverSocket.getLocalPort()+"");    			    		
					if(!perfEvalMode){
						TestCasesFile.appendToLogFile("Connecting With Index Server....");
					}
					objectOutputStream.writeObject(dataMap);   		  		
					if(null != dataMap && null != dataMap.get("type") && dataMap.get("type").equalsIgnoreCase("search")){
						resultMapList = (Map<String,ArrayList<String>>) objectInputStream.readObject();					
						afterTime  = System.currentTimeMillis();
						
						result = resultMapList;
					}else if(dataMap.get("type").equalsIgnoreCase("search")){
						resultMapList= null;
						result = resultMapList;	
					}else{
						resultMap.put("ip",InetAddress.getLocalHost().getHostAddress());
						resultMap.put("port",ClientSocket.serverSocket.getLocalPort()+"");
						result = resultMap;
					}		
				}else{
					TestCasesFile.appendToLogFile("Unable to open Socket for hosting hence Sync Failed.Please Try Running the Programs again.");
				}	 
				
				
				if(perfEvalMode && null != dataMap.get("type") && dataMap.get("type").equalsIgnoreCase("search")){
					Long timeDiff  = afterTime - beforeTime;
					//result = timeDiff;
					seqLookup = seqLookup + timeDiff;
				}
			}catch(final Exception e){
				e.printStackTrace();
			}finally{
				objectInputStream.close();
				objectOutputStream.close();		
				if(isQuit){
					exitAndCloseConnection();
				}
				
			}
		}
		return result;
	}

	
/*This Method requests the File for download from other peer or client.
 * */	

	private void requestFileForDownload(ArrayList<String> fileHostingServDetails,final String requestedFileName) throws IOException{
		if(null != fileHostingServDetails){
			int bytesRead;
			int current = 0;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			Socket sock = null;
			InputStream is = null;
			OutputStream os = null;
			PrintWriter pw = null;
			int FILE_SIZE = 6022386;
			String receivedFileName = fileDownloadPath;
			if(perfEvalMode){
				receivedFileName = fileDownloadPathPerf;			
			}
			
			
			byte[] byteArray = new byte[FILE_SIZE];
			Long beforeTime = 0l;
			Long afterTime = 0l;
			
			try{
				File dirFile =  new File(receivedFileName);
				if(!dirFile.exists()){
					try{dirFile.mkdirs();}catch(final SecurityException e){
						System.exit(0);
						e.printStackTrace();
						TestCasesFile.appendToLogFile("Couldn't Create Directory unnikrishnan/downloadedfiles.Please Check your permission.");
					} 		
				}					
				for(String details :fileHostingServDetails){								

					final String[] arr = details.split(":");
					if(arr.length >= 3)
					{
						final String clientIp = arr[0];
						final int portNumber = Integer.parseInt(arr[1]);
						final String filePath = arr[2];
						try{
							beforeTime = System.currentTimeMillis();
							sock = new Socket(clientIp, portNumber);
						}catch(final Exception e){
							Map mapToSend = new HashMap();
							mapToSend.put("type", "remove");
							connectToIndexServer(mapToSend);						
							TestCasesFile.appendToLogFile("ERROR Retrieving File From Client :"+clientIp);
					/*		e.printStackTrace();*/
							continue;
						}
						os = sock.getOutputStream();
						is = sock.getInputStream();
						pw = new PrintWriter(os, true);   
						pw.println(filePath);									
						final String completePath = receivedFileName +"/" +requestedFileName;
						fos = new FileOutputStream(completePath);
						bos = new BufferedOutputStream(fos);
						bytesRead = is.read(byteArray, 0, byteArray.length);
						current = bytesRead;
						do {
							bytesRead = is.read(byteArray, current, (byteArray.length - current));
							if (bytesRead >= 0)
								current += bytesRead;
						} while (bytesRead > -1);
						if(current > -1){
							bos.write(byteArray, 0, current);
						}
						bos.flush();
						afterTime = System.currentTimeMillis();
						
						
						
					}


				}

				if(perfEvalMode){
					 seqDownload = seqDownload + (afterTime - beforeTime);
				}
				

			}catch(final Exception e){
				e.printStackTrace();
			}finally{
				if(null != sock){	    				
					sock.close();
				}
				if(null != is){
					is.close();
				}
				if(null != os){
					os.close();
				}
				if(null != pw){
					pw.close();
				}
				if(null != fos){
					fos.close();
				}
				if(null != bos){
					bos.close();
				}
				if(!perfEvalMode){
					showMenu(3, null);
				}
			}

		}

	}
	
	/*This Method retrieves all the Files that are available for Sync from local directory
	 * */
	
	public Map<String,String> getFileNamestoSync(){  	    	
		File f = new File(fileSharePath);    
		if(perfEvalMode){
			f = new File(fileSharePathPerf); 
		}
		
		
		Map<String, String> fileDetsMap = new HashMap<String,String>();    	   	
		try{
			if(!f.exists()){
				try{f.mkdirs();}catch(final SecurityException e){
					System.exit(0);
					e.printStackTrace();
					TestCasesFile.appendToLogFile("Couldn't Create Directory unnikrishnan/sharedfiles.Please Check your permission.");
				} 		
			}	   
			TestCasesFile.appendToLogFile("Getting Files to Sync With Index Server from "+f.getCanonicalPath());
			File fList[] = f.listFiles();
			if(null == fList  || fList.length == 0){
				File dummyFile  = new File(f.getCanonicalPath()+"/dummyFile.txt");
				dummyFile.createNewFile();
				fList = f.listFiles();
			}
			if(null != fList && fList.length > 0){
				for(File fL:fList){
					fileDetsMap.put(fL.getName(), fL.getCanonicalPath());
					
				}
			}
		}catch(final Exception e){
			e.printStackTrace();
		}
		fileDetsMap.put("type", "data");
		return fileDetsMap;
	}
	
	/*This method is called in response to User quitting and closes any open sockets.
	 * */
	
	private void exitAndCloseConnection(){
		try{
			if(null != clientConnectSocket){
				clientConnectSocket.close();
				ClientSocket.listening = "false";
			}
		}catch(final Exception e){
			e.printStackTrace();
		}
	}
	
}
