package subsystemtests;

import java.util.List;
import java.util.stream.Collectors;

import junit.framework.TestCase;
import alltests.AllTests;
import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.Address;
import business.externalinterfaces.CustomerSubsystem;
import dbsetup.DbQueries;

public class CustomerSubsystemTest extends TestCase {
	
	static {
		AllTests.initializeProperties();
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
		CustomerSubsystem css=new CustomerSubsystemFacade();
		try {
			css.initializeCustomer(1, 0);
			Address addr = CustomerSubsystemFacade.createAddress(result[2], result[3], result[4], result[5]);
			List<Address> found = css.getAllAddresses();
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
