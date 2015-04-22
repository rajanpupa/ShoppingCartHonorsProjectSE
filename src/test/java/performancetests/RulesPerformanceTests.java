package performancetests;



import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.TestCase;
import performancetests.finalorderstubs.CartItemImpl;
import performancetests.finalorderstubs.ShoppingCartImpl;
import performancetests.rulesstubs.AddressImpl;
import performancetests.rulesstubs.RulesAddress;
import alltests.AllTests;
import business.customersubsystem.CreditCardImpl;
import business.exceptions.BackendException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.DynamicBean;
import business.externalinterfaces.Rules;
import business.externalinterfaces.RulesSubsystem;
import business.rulesubsystem.RulesSubsystemFacade;
import business.shoppingcartsubsystem.FinalOrderRules;


public class RulesPerformanceTests extends TestCase {

	static String name = "business.RulesSubsystemFacade";
	static Logger log = Logger.getLogger(RulesPerformanceTests.class.getName());
	
	static {
		AllTests.initializeProperties();
	}

	
	public RulesPerformanceTests(String arg0) {
		super(arg0);
	}
	
	public void setUp() {
		
	}
	RulesSubsystem rules; 
	Rules rulesAddress;
	DynamicBean bean;
	Address addr;
	int i = 0;
	public void testAddressRulesRepeatedly(){
		final int NUM_TRIALS = 10;
		final int EXPECTED_RUNNING_TIME = 140;
		long[] results = new long[NUM_TRIALS];
		long start = 0L;
		long finish = 0L;
		for(i = 1; i < NUM_TRIALS; ++i){
			addressRulesSetup(i);
			start = System.currentTimeMillis();
			try {
				rules.runRules(rulesAddress);
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

	public void addressRulesSetup(int i){		
		String[] addrFields = {"10"+i+" N. 6th","Fairfield","IA","5255"+i};
		addr = new AddressImpl(addrFields);
		
		rulesAddress = new RulesAddress(addr);
		rules = new RulesSubsystemFacade();	
	}
	
	ShoppingCartImpl sci;
	Rules orderRules;
	public void testFinalOrderRulesRepeatedly()
	{
		final int NUM_TRIALS = 10;
		final int EXPECTED_RUNNING_TIME = 160;
		long[] results = new long[NUM_TRIALS];
		long start = 0L;
		long finish = 0L;
		for(i = 1; i < NUM_TRIALS; ++i){
			finalOrderRulesSetup(i);
			start = System.currentTimeMillis();
			try {
				rules.runRules(orderRules);
			}
			catch(Exception e) {
				log.info(e.getMessage());
				e.printStackTrace();
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
	
	public void finalOrderRulesSetup(int i)
	{
		Address billAddress;
		Address shipAddress;
		CreditCard cc;
		cc = new CreditCardImpl("Name"+i, "01/01/2015", "1234"+i, "credit");
		String[] addrFields = {"10"+i+" N. 6th","Fairfield","IA","5255"+i};
		billAddress = new AddressImpl(addrFields);
		shipAddress = new AddressImpl(addrFields);
		List<CartItem> cartList = new ArrayList<CartItem>();
		cartList.add(getCartitem());
		sci = new ShoppingCartImpl(cartList,shipAddress,billAddress,cc); 
		
		orderRules = new FinalOrderRules(sci);
		rules = new RulesSubsystemFacade();	
	}
	
	public CartItem getCartitem()
	{
		CartItem ci = null;
		try{
			ci = new CartItemImpl("Gone With The Wind","20","23");
		}catch(BackendException e)
		{
			fail("Error generating cartitem");
		}
		return ci;
	}
}
