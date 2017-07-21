import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Worker extends Thread {

	//private ArrayList<Item> whiteItems = new ArrayList<Item>();
	//public ArrayList<Item> blackItems = new ArrayList<Item>();
	
	private int allocationRate;
	private int rounds;
	private int threadID;
	@SuppressWarnings("unused")
  private Random random = new Random();
	
	private List<Item> blackList;
	
	public Worker(int threadID, int allocationRate, int rounds, List<Item> blackList) {
		this.threadID = threadID;
		this.allocationRate = allocationRate;
		this.rounds = rounds;
		this.blackList = blackList;
	}
	
	public void run() {
		System.out.println("" + threadID + " starting...");
		int sum = 0;
		int touched = 0;
		while (rounds > 0) {
			rounds--;
			/*for (int i = 0; i < allocationRate; i++) {
				whiteItems.add(new Item(i));
			}*/
			
			for (int i = 0; i < allocationRate/10; i++) {
			  Item s = new Item(""+i);
			  s.setValue(s.getValue() + ".");
			  touched++;
			  blackList.add(s);
			}
			
			/*for (int i = 0; i < allocationRate-10; i++) {
				whiteItems.remove(random.nextInt(whiteItems.size()));
			}*/
			for (int i = 0; i < allocationRate; i++) {
			  sum += new Item("1").getValue().length();
			}
		}
		System.out.println("" + threadID + " finished with sum " + sum + " and touched " + touched);
		synchronized (blackList) {
      Iterator<Item> i = blackList.iterator();
      while (i.hasNext()) {
        i.remove();
      }
    }
		Main.threadFinished(threadID);
	}
}
