import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Main {
	private static int F = 100;
	
	private static final int[] ALLOCATION_RATE = {100, 200, 100, 300, 100, 100, 100, 200, 100, 200, 100, 300, 100, 100, 100, 200, 100, 200, 100, 300, 100, 100, 100, 200, 100, 200, 100, 300, 100, 100, 100, 200, 100, 200, 100, 300, 100, 100, 100, 200, 100, 200, 100, 300, 100, 100, 100, 200, 100, 200, 100, 300, 100, 100, 100, 200, 100, 200, 100, 300, 100, 100, 100, 200};
	private static final int[] ROUNDS = new int[ALLOCATION_RATE.length]; // = {F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F, F};
	public static int N_THREADS = 32;
	private static int F_THREADS;

	private static ArrayList<Worker> workers = new ArrayList<Worker>();
	
	public static ArrayList<List<Item>> blackLists = new ArrayList<List<Item>>();

	public static void main(String[] args) {
		if (args.length != 2) System.exit(1);
		N_THREADS = Integer.parseInt(args[0]);
		F_THREADS = N_THREADS;
		System.out.println("Number of threads: " + N_THREADS);
		F = Integer.parseInt(args[1]);
		for (int i = 0; i < ROUNDS.length; i++) {
			ROUNDS[i] = F;
		}
		System.out.println("Rounds: " + F);
		
		org.mmtk.plan.Plan.harnessWarmupFinished = true;
		org.mmtk.plan.Plan.startTiming();
		
		if (ALLOCATION_RATE.length < N_THREADS || ROUNDS.length < N_THREADS) System.exit(1);
		
		for (int i = 0; i < N_THREADS; i++) {
		  List<Item> blackList = Collections.synchronizedList(new ArrayList<Item>());
			workers.add(new Worker(i, ALLOCATION_RATE[i], ROUNDS[i], blackList));
		}
		
		for (int i = 0; i < N_THREADS; i++) {
			System.out.println("Starting " + i + " " + workers.get(i).toString());
			workers.get(i).start();
		}
		
		System.out.println("All threads spawned");
		
		int touched = 0;
		
		while (F_THREADS > 0) {
		  //;;
		  for (int i = 0; i < blackLists.size(); i++) {
			  List<Item> blackList = blackLists.get(i);
			  synchronized (blackList) {
		      Iterator<Item> it = blackList.iterator();
		      while (it.hasNext()) {
		        Item s = it.next();
            it.remove();
            s.setValue(""+i);
            touched++;
		      }
		    }
			}
		}
		
		System.out.println("PASSED " + touched);
		org.mmtk.plan.Plan.endTiming();
	}
	
	public static synchronized void threadFinished(int threadID) {
		F_THREADS--;
	}
}
