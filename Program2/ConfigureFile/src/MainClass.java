import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class MainClass {

	public static Map<Integer,String>peerIdIPMap = new HashMap<Integer,String>();
	
	public static void main(String args[]){
		BufferedReader br = null;				
		
 		try{			
			br = new BufferedReader(new InputStreamReader(System.in));				
			System.out.println("Enter the Number of Peers/Clients");
			int numberOfPeers = Integer.parseInt(br.readLine());					
			
			System.out.println("Choose how you want to create peers 1.Explicitly Create Peers 2.Create Duplicate Peers Automatically in the current Local System (same IP and different Ports)");
			int option = Integer.parseInt(br.readLine());
			if(option == 2){			
				try{
					createTestPeersConfig(numberOfPeers);
				}catch(final Exception e){
					e.printStackTrace();
				}  
			}else {
				for(int i = 1;i<=numberOfPeers;i++){
					System.out.println("Enter IP of Peer"+i);
					final String ip = br.readLine();
					System.out.println("Enter Port of Peer"+i);
					final int port = Integer.parseInt(br.readLine());
					peerIdIPMap.put(i, ip+":"+port);				
				}
			}
	/*		File testDir = new File("test");
			if(!testDir.exists()){
				testDir.mkdirs();
			}
			File parentFile = testDir.getParentFile();			
			File configDir = new File("./"+File.separator+"config");
			if(!configDir.exists()){
				configDir.mkdirs();
			}
			*/
			File configFile = new File("CONNECTEDPEERS.txt");
			configFile.createNewFile();
			FileWriter fw = new FileWriter(configFile,false);
			BufferedWriter bw = new BufferedWriter(fw);
			final StringBuffer sbw = new StringBuffer();
			if(null != peerIdIPMap && (!peerIdIPMap.isEmpty()) ){
				for(Map.Entry<Integer, String>peerIdMapEntry:peerIdIPMap.entrySet()){
					sbw.append(peerIdMapEntry.getKey()+":"+peerIdMapEntry.getValue()+";");				
				}	
			}			
			System.out.println("Configuration File Generated.You can overwrite over this by running this program again.");
			System.out.println("You can run the Client Program now after exiting this program..");
			
			bw.write(sbw.toString());
			bw.close();		
		}catch(final Exception e){
			e.printStackTrace();
		}
	}
	
	
	private static void createTestPeersConfig(final int numberOfPeers) throws Exception{
		final String ip = InetAddress.getLocalHost().getHostAddress();
		for(int i=1;i<=numberOfPeers;i++){
			ServerSocket s = new ServerSocket(0);
			final int port  = s.getLocalPort();
			peerIdIPMap.put(i, ip+":"+port);
			s.close();
		}
			
		
		
	}
	
}
