package hw2.edu.iit.cs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import hw2ClientProgram.PerfEValClass;

public class MenuAcceptInputThread extends Thread{

	/*
	 * 
	 * 
	 * This is the main run method of the thread.
	 * 
	 * 
	 * */
	public void run(){
		showMenu();
	}
	
	/*
	 * 
	 * 
	 * This Method shows the Menu to the User.
	 * 
	 * 
	 * */
	public void showMenu(){
		boolean exitCondition  = false;

		int hashingValue = 0;
		while(!exitCondition){		
			try{
				ClientMainClass.br = new BufferedReader(new InputStreamReader(System.in));
				BufferedReader inputBR= null;
				PrintWriter out = null;	
				String key = null;
				String value = null;
				Socket sock = null;
				
				
				
				
				HashingClass.writeOutputFile("-------------------------MENU------ENTER CHOICE-------------------------------",true);
				HashingClass.writeOutputFile("1 Get Entry",true);
				HashingClass.writeOutputFile("2 Put Entry",true);
				HashingClass.writeOutputFile("3 Delete Entry",true);
				HashingClass.writeOutputFile("4 Run Performance Evaluation Tests",true);
				HashingClass.writeOutputFile("------------------------------------------------------------------",true);				
				final int entry = Integer.parseInt(ClientMainClass.br.readLine());
				HashingClass.writeOutputFile(""+entry,false);
				switch(entry){
					case 1 :	//get value of a string key..
						HashingClass.writeOutputFile("Enter the Key",true);
						key = ClientMainClass.br.readLine();
						HashingClass.writeOutputFile(""+key,false);
						hashingValue  = HashingClass.hashingFnc(key);
						sock = getSocket(hashingValue,false);
						if(null != sock){
							out = new PrintWriter(sock.getOutputStream(),true);
							HashingClass.writeOutputFile("REQUESTING:"+key,true);
							out.println("requestType:get;key:"+key);
							inputBR  = new BufferedReader(new InputStreamReader(sock.getInputStream()));
							value = inputBR.readLine();
							if(null != value && (!value.isEmpty()) && (!value.equalsIgnoreCase("false"))){
								HashingClass.writeOutputFile("VALUE:"+value,true);
							}else if(value.equalsIgnoreCase("false")){
								HashingClass.writeOutputFile("DATA KEY/VALUE PAIR NOT FOUND",true);
							}
							
						}
						break;
					case 2:
						HashingClass.writeOutputFile("Enter the Key",true);
						key = ClientMainClass.br.readLine();
						HashingClass.writeOutputFile(""+key,false);
						HashingClass.writeOutputFile("Enter the Value",true);
						value = ClientMainClass.br.readLine();
						HashingClass.writeOutputFile(""+value,false);
						hashingValue  = HashingClass.hashingFnc(key);
						HashingClass.writeOutputFile("HASHING VALUE:"+hashingValue,true);
						sock = getSocket(hashingValue,false);
						if(null != sock){
							out = new PrintWriter(sock.getOutputStream(),true);
							out.println("requestType:put;key:"+key+";data:"+value);
							inputBR  = new BufferedReader(new InputStreamReader(sock.getInputStream()));
							value = inputBR.readLine();
							HashingClass.writeOutputFile(value,true);
						}
						break;
					case 3:
						//delete value of a string key..
						HashingClass.writeOutputFile("Enter the Key",true);
						key = ClientMainClass.br.readLine();
						HashingClass.writeOutputFile(""+key,false);
						hashingValue  = HashingClass.hashingFnc(key);
						sock = getSocket(hashingValue,false);
						if(null != sock){
							out = new PrintWriter(sock.getOutputStream(),true);
							out.println("requestType:delete;key:"+key);	
							HashingClass.writeOutputFile("Successfully Deleted",true);
						}
						break;
					case 4:
						PerfEValClass.doPerfEvaluation();
						break;
					default:
						exitCondition = false;
						break;				
				}
			}catch(final Exception e){
				e.printStackTrace();
				HashingClass.writeOutputFile("INVALID INPUT",true);
			}
			
		}
		
	}
	
	/*
	 * 
	 * 
	 * This Method returns the already connected sockets , if sockets where not already
	 * connected it makes a new connection to the Peer.
	 * 
	 * 
	 * */
	public static Socket getSocket(final int peerId,boolean flag){
		Socket sock = null;
		if(flag && null != ClientMainClass.socketMap && !ClientMainClass.socketMap.isEmpty() && null != ClientMainClass.socketMap.get(peerId)){
			sock = ClientMainClass.socketMap.get(peerId);
			
		}else{
			final String ipWithPort  = ClientMainClass.peersMap.get(peerId);
			final String ipAddress = ipWithPort.split(":")[0];
			final int port  = Integer.parseInt(ipWithPort.split(":")[1]);
			try{			
				sock = new Socket(ipAddress, port);
				ClientMainClass.socketMap.put(peerId, sock);
			}catch(final Exception e){
				HashingClass.writeOutputFile("SOCKET CONNECTION WITH IP:"+ipAddress+", AND PORT NUMBER "+port+" AND PEER ID "+peerId+" FAILED.PLEASE CHECK WHETHER THE CLIENT IS RUNNING!!",true);
				sock = null;
			}			
		}
		return sock;		
	}
}
