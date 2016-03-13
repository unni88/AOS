package edu.iit.hawk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestCasesFile {


	/* This method is only called during performance evaluation and creates 1000 copies of a file from sharedfiles directory.
	 * */

	public static String LogFileDir = "log";
	
	public static String LogFile = "LogFile.txt";
	
	public static void createThousandCopies() throws IOException{
		File source = new File("unnikrishnan/sharedfiles/dummy.pdf");		
		InputStream is = null;
	    OutputStream os = null;
	    
	    try {
	    	File f1 = new File(ClientAcceptInputThread.fileSharePathPerf);
	    	if(!f1.exists()){
	    		f1.mkdirs();
	    	}	 
	    	if(!source.exists()){
	    		source.mkdirs();	    		
	    	}
	    	
	    	
	    	File[] fList = f1.listFiles();
	    	if(null != fList){
	    		for(File file:fList){
	    			file.delete();
	    		} 			    		
	    	}
	    	System.out.println("Enter a Unique Peer Prefix For File Creation");
	    	ClientAcceptInputThread.br = new BufferedReader(new InputStreamReader(System.in));
	    	String uniquePrefix  = ClientAcceptInputThread.br.readLine(); 
	    	if(null == uniquePrefix){
	    		uniquePrefix ="";
	    	}
	    	for(int  i= 0 ; i<ClientAcceptInputThread.fileCopiesToBeMade;i++){
		        	is = new FileInputStream(source);
		        	String destination  = ClientAcceptInputThread.fileSharePathPerf;		    		
		        	destination  = destination+"/perfevaldummy_"+uniquePrefix+""+i+".pdf";
		        	File dest = new File(destination);
		        	os = new FileOutputStream(dest);
			        final byte[] buffer = new byte[1024];
			        int length;
			        while ((length = is.read(buffer)) > 0) {
			            os.write(buffer, 0, length);
			        }  
			        os.flush();	        
		   } 	    
	    }catch(final Exception e){
	    	e.printStackTrace();
	    }finally {
	        is.close();
	        os.close();
	       
	    }
	}
	
	/*This Method is responsible for creating the Log File and appening to it.
	 * */
	
	public static void appendToLogFile(final String text){		
		PrintWriter out = null;
		try{			

			File f = new File(LogFileDir +File.separator+LogFile);
			if(!f.exists()){
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
			
			
				out = new PrintWriter(new BufferedWriter(new FileWriter(f,true)));
				out.println(text);
			}catch(final Exception e){
			
			}finally{
				if(null != out){
					out.close();
				}
			}
		
		System.out.println(text);
	}
}
