package edu.iit.hawk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientServerMultithread extends Thread{

	private Socket clientSocket = null;
	
	public ClientServerMultithread(Socket socketTemp){
		clientSocket = socketTemp;
	}
			
/*This is the main run method for this thread.
 * */
	
	public void run(){
		try{			
			serveFileForDownload();
		}catch(final Exception e){
			e.printStackTrace();
		}		
	}
	
/*This method serves files for download..
 * */	

	private void serveFileForDownload() throws IOException{
		//TestCasesFile.appendToLogFile("Client with IP Address "+clientSocket.getLocalAddress().getHostAddress()+" connected");
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;				
		BufferedReader br= null;
		try{
			br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));			
			final String entireFilePath  = (String) br.readLine();			
			if(null != entireFilePath){
				File myFile = new File(entireFilePath);
				byte[] myByteArray = new byte[(int)myFile.length()];
				fis = new FileInputStream(myFile);
				bis = new BufferedInputStream(fis);
				bis.read(myByteArray, 0, myByteArray.length);
				os= clientSocket.getOutputStream();
				os.write(myByteArray);				
				os.flush();
				
			}		    	
		}catch(final Exception e){
			e.printStackTrace();
		}finally{
			if(null != fis){
				fis.close();
			}
			if(null != bis){
				bis.close();
			}
			if(null != os){
				os.close();
			}
			if(null != br){
				br.close();
			}		    	    	
		}	    	
	}

}
