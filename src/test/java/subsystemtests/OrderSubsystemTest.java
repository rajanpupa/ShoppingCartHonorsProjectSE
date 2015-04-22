package subsystemtests;

import integrationtests.BrowseAndSelectTest;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import junit.framework.TestCase;
import performancetests.orderstubs.CustomerProfileImpl;
import presentation.gui.GuiUtils;
import alltests.AllTests;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.OrderSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import dbsetup.DbQueries;

public class OrderSubsystemTest extends TestCase{
	static String name = "Order Subsystem Test";
	static Logger log = Logger.getLogger(BrowseAndSelectTest.class.getName());
	
	static {
		AllTests.initializeProperties();
	}
	
	public void testGetOrderHistoryNames() {
		//setup
		/*
		 * Returns a String[] with values:
		 * 0 - query
		 * 1 - order id
		 * 2 - order date
		 */
		String[] insertResult = DbQueries.insertOrderRow();
		String expected = insertResult[2];
		String[] insertOrderItemResult = DbQueries.insertOrderItem(Integer.parseInt(insertResult[1])); 
		CustomerProfile cp = new CustomerProfileImpl(1, "abc", "def", true);
		OrderSubsystem oss = new OrderSubsystemFacade(cp);
		
		try {
			List<String> found = oss.getOrderHistory()
				      .stream()
				      .map(ord -> GuiUtils.localDateAsString(ord.getOrderDate()))
				      .collect(Collectors.toList());
			boolean valfound = false;
			for(String ordDate : found) {
				
					if(ordDate.equals(expected)) valfound = true;
				
			}
			assertTrue(valfound);
			
		} catch(Exception e) {
			fail("Inserted value not found");
		} finally {
			DbQueries.deleteOrderItemRow(Integer.parseInt(insertResult[1]));
			DbQueries.deleteOrderRow(Integer.parseInt(insertResult[1]));
		}
	
	}
}
