package hw2ClientProgram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hw2.edu.iit.cs.HashingClass;
import hw2.edu.iit.cs.MenuAcceptInputThread;

public class PerfEValClass {

	static int hashingValue = 0;
	static BufferedReader inputBR= null;
	static PrintWriter out = null;	
	static Socket sock = null;
	static Map<Integer,ArrayList<String>> bucketDataMap = new HashMap<Integer,ArrayList<String>>();

	
	/*
	 * 
	 * Performance Evaluation happens here with accpeting the number of operations from the user
	 * 
	 * 
	 * 
	 * */
	public static void doPerfEvaluation(){
		HashingClass.writeOutputFile("EVALUATION Starting",true);		
		try{
			HashingClass.writeOutputFile("Enter the Number of Operations you wish to carry out",true);
			final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int n = Integer.parseInt(br.readLine());		
			String fileName  ="test";
			ArrayList<String>  strArr = getKeys(fileName,n);
			HashingClass.writeOutputFile("KEYS COUNT"+strArr.size(),true);
			Map<String,String> keyValuePair = getKeyValuePair(fileName,n);
			HashingClass.writeOutputFile("PUT EVALUATION",true);
			Long startTime = System.currentTimeMillis();
			doPutEvaluation(keyValuePair);
			Long endTime = System.currentTimeMillis();
			HashingClass.writeOutputFile("TIME (Milliseconds)"+ ((endTime - startTime)) ,true  );
			
			HashingClass.writeOutputFile("GET EVALUATION",true);
			startTime = System.currentTimeMillis();
			doGetEvaluation(strArr);
			endTime = System.currentTimeMillis();
			HashingClass.writeOutputFile("TIME (Milliseconds)"+ ((endTime - startTime)) ,true  );
			
			HashingClass.writeOutputFile("DELETE EVALUATION",true);
			startTime = System.currentTimeMillis();
			doDeleteEvaluation(strArr);
			endTime = System.currentTimeMillis();
			HashingClass.writeOutputFile("TIME (Milliseconds)"+ ((endTime - startTime)) ,true  );
			
			HashingClass.writeOutputFile("DATA DISTRIBUTION",true);
			if(null != bucketDataMap && !bucketDataMap.isEmpty()){
				for(Map.Entry<Integer, ArrayList<String>> buckDataMapENTRY:bucketDataMap.entrySet()){
					HashingClass.writeOutputFile("BUCKET :"+buckDataMapENTRY.getKey()+" COUNT:"+buckDataMapENTRY.getValue().size(),true);
				}
			}
			
			
		}catch(final Exception e){
			e.printStackTrace();
		}
		
	}
	/*
	 * 
	 * Put OPeration Performance Evaluation
	 * 
	 * 
	 * 
	 * */	

	public static void doPutEvaluation(Map<String,String> keyValueMap) throws Exception{
		for(Map.Entry<String, String>keyValueEntry:keyValueMap.entrySet()){
			hashingValue  = HashingClass.hashingFnc(keyValueEntry.getKey());
			ArrayList<String> valuesList = new ArrayList<String>();
			if(null != bucketDataMap && !bucketDataMap.isEmpty()  && null != bucketDataMap.get(hashingValue)){
				valuesList = bucketDataMap.get(hashingValue);
			}
			valuesList.add(keyValueEntry.getKey());
			bucketDataMap.put(hashingValue,valuesList);		
			sock = MenuAcceptInputThread.getSocket(hashingValue,true);			
			if(null != sock){
				out = new PrintWriter(sock.getOutputStream(),true);
				out.println("requestType:put;key:"+keyValueEntry.getKey()+";data:"+keyValueEntry.getValue());
				
			}else{
				HashingClass.writeOutputFile("Sock is null",true);
			}
		}
	}


	public static void doGetEvaluation(ArrayList<String> keys) throws Exception{
		for(String key : keys){
			hashingValue  = HashingClass.hashingFnc(key);
			sock = MenuAcceptInputThread.getSocket(hashingValue,true);
			if(null != sock){
				out = new PrintWriter(sock.getOutputStream(),true);
				out.println("requestType:get;key:"+key);
				
			}
		}
	}
	/*
	 * 
	 * Delete OPeration Performance Evaluation
	 * 
	 * 
	 * 
	 * */	
	
	public static void doDeleteEvaluation(ArrayList<String> keys) throws Exception{
		for(String key: keys){
			hashingValue  = HashingClass.hashingFnc(key);
			sock = MenuAcceptInputThread.getSocket(hashingValue,true);
			if(null != sock){
				out = new PrintWriter(sock.getOutputStream(),true);
				out.println("requestType:delete;key:"+key);	
			}
			break;
		}
	}
	/*
	 * 
	 * Get OPeration Performance Evaluation
	 * 
	 * 
	 * 
	 * */	

	public static Map<String,String>  getKeyValuePair(final String fileName,final int n) throws Exception{	
		Map<String,String> detMap = new HashMap<String,String>();
		final ArrayList<String> words  = getKeys(fileName,n);
		if(null != words && words.size() > 0){
			for(String word:words){
				detMap.put(word, word);
			}
		}
		return detMap;
	}
	/*
	 * 
	 * Get OPeration Performance Evaluation
	 * 
	 * 
	 * 
	 * */	
	public static ArrayList<String>  getKeys(final String fileName,final int n) throws Exception{	
		ArrayList<String> words = new ArrayList<String>();
		for(int  i =0;i<n;i++){
			words.add(i+"");
		}

		return words;
	}

}
