package edu.iit.hawk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientSocket {

	public static ServerSocket serverSocket  = null;
	public static String listening = "true";

	
	/* Main Method for this class.
	 * */

	public static void main(String args[]){
		final ClientSocket cl = new ClientSocket();
		try {
			cl.intializeClient();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/* This Method intializes and prepares the client for accepting request from other peers and also show the menu to the user.
	 * */
	
	public void intializeClient() throws IOException{
		try{
			serverSocket = new ServerSocket(0); //creating a Server Socket for Sending Files...	    	
			TestCasesFile.appendToLogFile("Client Listening For File Download at:"+serverSocket.getLocalPort());
			new Thread(new ClientAcceptInputThread()).start();
			while( (null != listening) && (listening.equalsIgnoreCase("true")) ){
				new Thread(new ClientServerMultithread(serverSocket.accept())).start();				
				Thread.sleep(10);			
			}
		}catch(final Exception e){
			e.printStackTrace();
		}finally {    		
			TestCasesFile.appendToLogFile("CLOSING......");
			serverSocket.close();
		}
	}
/*This method is called in response to quit issued by the user.
 * */
	public void exitAndCloseClient() throws IOException{
		if(null != serverSocket){
			serverSocket.close();  //close the Server Socket that serves the Files..
		} 	
		System.exit(0);
	}

}
