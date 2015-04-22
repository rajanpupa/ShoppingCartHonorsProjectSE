package daotests;

import integrationtests.BrowseAndSelectTest;

import java.util.List;
import java.util.logging.Logger;

import junit.framework.TestCase;
import performancetests.orderstubs.CustomerProfileImpl;
import alltests.AllTests;
import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.DbClassOrderForTest;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import dbsetup.DbQueries;

public class DbClassOrderTest extends TestCase{
	static String name = "Order Test";
	static Logger log = Logger.getLogger(DbClassOrderTest.class.getName());
	
	static {
		AllTests.initializeProperties();
	}
	
	
	public void testReadAllOrders() throws BackendException {
		List<Integer> expected = DbQueries.readAllOrders(1);
		CustomerSubsystem css = new CustomerSubsystemFacade();
		css.initializeCustomer(1, 0);
		CustomerProfile custProfile = css.getGenericCustomerProfile();
		
		OrderSubsystem oss = new OrderSubsystemFacade(custProfile);
		DbClassOrderForTest dbClass = oss.getGenericDbClassOrder();
		try {
			
			List<Integer> found = dbClass.getAllOrderIds(custProfile);
			assertTrue(expected.size() == found.size());
			
		} catch(Exception e) {
			fail("Order Lists don't match");
		}
		
	}
}
