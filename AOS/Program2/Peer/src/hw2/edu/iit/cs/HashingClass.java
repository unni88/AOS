package hw2.edu.iit.cs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HashingClass {
	
	
	public static int M = 8;    //Number of Modules or number of peers/clients running , the default value is set at 8.
	public static final int R = 31;   //RANDOM PRIME NUMBER IN THIS LOGIC WE ARE TAKING 31 as our prime number
	
	/*
	 * 
	 * 
	 * This is the main method which calculates the hashing value of a key.
	 * It involves multiplying the Integer value with a random prime number which is 
	 * 31 here.
	 * 
	 * */
	
	public static int hashingFnc(final String entryToHash){
		int hashValue  = 0 ;		
		if(null != entryToHash && !entryToHash.isEmpty()){
			for(int i=0;i<entryToHash.length();i++){
				hashValue = hashValue  + ((R*hashValue) + entryToHash.charAt(i)); 
			}			
			hashValue = hashValue % M;
		}
		if(hashValue < 0 ){
			hashValue = (-1 * hashValue);
		}else if(hashValue == 0){
			hashValue = hashValue + 1;					
		}
		
		hashValue = hashValue + 1;
		return hashValue;
	}
	
	/*
	 * 
	 * This Method writes to the Log File and shows output.
	 * 
	 * 
	 * 
	 * */
	public static void writeOutputFile(final String message,boolean showScreen){
		if(showScreen){
			System.out.println(message);   
		}
		BufferedWriter writer = null;
	        try {
	            File logFile = new File("Output.txt");
	            if(!logFile.exists()){
	            	logFile.createNewFile();
	            }

	            writer = new BufferedWriter(new FileWriter(logFile,true));
	            writer.newLine();
	            writer.write(message);
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                // Close the writer regardless of what happens...
	                writer.close();
	            } catch (Exception e) {
	            }
	        }
	    }
}
