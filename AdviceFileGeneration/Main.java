import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	
	public static ArrayList<String> sharedTypes = new ArrayList<String>();

	public static void main(String[] argv) {
		BufferedReader br;
	
		try {
			br = new BufferedReader(new FileReader(argv[0]));
	        String line = br.readLine();
	        while (line != null) {
	            sharedTypes.add(line);
	            line = br.readLine();
	        }
	        br.close();
	        
	        br = new BufferedReader(new FileReader(argv[1]));
	        line = br.readLine();
	        while (line != null) {
	        	//System.err.println("# "+line);
	            new ClassHandler(line).methodsHandler();
	            line = br.readLine();
	        }
	        br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.err.println("Finished");
	}
}
