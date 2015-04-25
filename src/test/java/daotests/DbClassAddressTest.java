package daotests;

import integrationtests.BrowseAndSelectTest;

import java.util.List;
import java.util.logging.Logger;

import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.DbClassAddressForTest;
import dbsetup.DbQueries;
import junit.framework.TestCase;
import alltests.AllTests;

public class DbClassAddressTest extends TestCase {
	
	static String name = "Browse and Select Test";
	static Logger log = Logger.getLogger(BrowseAndSelectTest.class.getName());
	
	static {
		AllTests.initializeProperties();
	}
	
	
	public void testReadAllAddresses() throws BackendException {
		List<Address> expected = DbQueries.readCustAddresses();
		
		//test real dbclass address
		CustomerSubsystem css = new CustomerSubsystemFacade();
		DbClassAddressForTest dbclass = css.getGenericDbClassAddress();
		
		css.initializeCustomer(1, 1);
		CustomerProfile custProfile = css.getGenericCustomerProfile();
		try {
			dbclass.readAllAddresses(custProfile);
			List<Address> found = dbclass.getAddressList();
			assertTrue(expected.size() == found.size());
			//assertTrue(message, condition);
			
		} catch(Exception e) {
			fail("Address Lists don't match");
		}
		
		
	}

	/*
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - Address id
	 * 2 - Street name
	 * 3 - city
	 * 4 - state
	 * 5 - zip
	 */
	public void testSaveNewAddress() {
		
		String[] result = DbQueries.insertAddressRow();
		Address addr = CustomerSubsystemFacade.createAddress(result[2], result[3], result[4], result[5]);
		CustomerSubsystem css=new CustomerSubsystemFacade();
		DbClassAddressForTest dbclass = css.getGenericDbClassAddress();
		try {
			css.initializeCustomer(1, 0);
			CustomerProfile custProfile = css.getGenericCustomerProfile();
			dbclass.readAllAddresses(custProfile);
			
			List<Address> found = dbclass.getAddressList();
			boolean valfound = false;
			for(Address catData : found) {
				
					if(catData.equals(addr)){
						valfound = true;
						break;
					}
				
			}
			assertTrue(valfound);
			
		} catch(Exception e) {
			fail("Inserted value not found");
		} finally {
			DbQueries.deleteAddressRow(Integer.parseInt(result[1]));
		}
	
	}
}
