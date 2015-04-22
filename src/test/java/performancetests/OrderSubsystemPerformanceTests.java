package performancetests;

import java.util.logging.Logger;

import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.OrderSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import performancetests.orderstubs.CustomerProfileImpl;
import alltests.AllTests;
import junit.framework.TestCase;

public class OrderSubsystemPerformanceTests extends TestCase{
	static String name = "business.OrderSubsystemFacade";
	static Logger log = Logger.getLogger(RulesPerformanceTests.class.getName());
	
	static {
		AllTests.initializeProperties();
	}

	
	public OrderSubsystemPerformanceTests(String arg0) {
		super(arg0);
	}
	
	public void setUp() {
		
	}
	CustomerProfile cp;
	OrderSubsystem os;
	int i=0;
	public void testGetOrderHistoryRepeadly()
	{
		final int NUM_TRIALS = 10;
		final int EXPECTED_RUNNING_TIME = 140;
		long[] results = new long[NUM_TRIALS];
		long start = 0L;
		long finish = 0L;
		for(i = 1; i < NUM_TRIALS; ++i){
			prepareGetOrderHistory();
			start = System.currentTimeMillis();
			try {
				os.getOrderHistory();
			}
			catch(Exception e) {
				log.info(e.getMessage());
				fail("Rules engine encountered an exception.");
			}
			
			finish = System.currentTimeMillis();
			if(i>0){
				results[i]= finish-start;
			}
		}
		long accum = 0L;
		//count time elapsed starting at j=1 
		//because the 0th trial measures startup time too
		String output = "[";
		for(int j = 0; j < NUM_TRIALS; ++j){
			accum += results[j];
			output += (results[j]+", ");
		}
		output = output.substring(0,output.length()-2)+"]";
		
		long average = accum / (NUM_TRIALS - 1);
		log.info(output);
		log.info("average: "+average);
		assertTrue(average < EXPECTED_RUNNING_TIME);
	}
	
	private void prepareGetOrderHistory()
	{
		cp = new CustomerProfileImpl(1,"FirstName", "Lastname",true);
		os = new OrderSubsystemFacade(cp);
		
	}
}
