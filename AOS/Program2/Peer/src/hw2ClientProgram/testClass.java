package hw2ClientProgram;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import hw2.edu.iit.cs.HashingClass;

public class testClass {
/*
	public static void main(String args[]){	
		Map<String,String> detMap = new HashMap<String,String>();
		detMap.put("1", "1");
		detMap.put("2", "1");
		HashingClass.writeOutputFile(detMap.size()+"");
		
		checkPortAvailabillity();
		
			
		
		
		
		HashingClass.writeOutputFile("Enter a String");
		
		final Map<Integer,ArrayList<String>> hashkeyMap = new HashMap<Integer,ArrayList<String>>();
		final int M = 4;
		final int R = 31;
		
		try{			
		
	
		File f = new File("testDir");
		if(!f.exists()){
			f.mkdirs();
		}
		FileReader readFile = new FileReader("testDir"+File.separator+"textFile.txt");	
		BufferedReader br = new BufferedReader(readFile);
		final String line  = br.readLine();
		String[] words = line.split(" ");
		int hash  = 0;
		for(String word:words){
			hash  = 0;
			final String str = word;
			for(int i=0;i<str.length();i++){
				hash = hash  + ((R*hash) + str.charAt(i)); 
			}			
			hash = hash % M;
			
			
			ArrayList<String> list = new ArrayList<String>();
			if(null != hashkeyMap.get(hash) && !hashkeyMap.get(hash).isEmpty()){
				list = hashkeyMap.get(hash);
			}
			list.add(str);
			
			hashkeyMap.put(hash, list);
		}
		if(null != hashkeyMap && !hashkeyMap.isEmpty()){
			for(Integer key:hashkeyMap.keySet()){
				ArrayList<String> values = hashkeyMap.get(key);
				System.out.println(key+":"+values.size());			
			}
		}
		
			

			
			
			
			
			
			System.out.println(hash+"");  
		}catch(final Exception e){
			e.printStackTrace();
		}
		
	}*/
	
	
	public void initalizeClient(){
		
	}
	
	public static void checkPortAvailabillity(){
		try{
			ServerSocket s = new ServerSocket(51693);
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			System.out.println("listening on port: " + s.getLocalPort());
			s.close();
		}catch(final Exception e){
			e.printStackTrace();
		}
	}
	
}
