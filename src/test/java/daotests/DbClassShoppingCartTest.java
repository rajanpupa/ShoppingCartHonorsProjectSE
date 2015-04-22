package daotests;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import middleware.exceptions.DatabaseException;
import dbsetup.DbQueries;
import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.shoppingcartsubsystem.CartItemImpl;
import business.shoppingcartsubsystem.DbClassShoppingCart;
import business.shoppingcartsubsystem.DbClassShoppingCartForTest;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import alltests.AllTests;
import junit.framework.TestCase;

public class DbClassShoppingCartTest extends TestCase {
	static String name = "DbClassShoppingCart Test";
	static Logger log = Logger.getLogger(DbClassShoppingCartTest.class.getName());
	
	static {
		AllTests.initializeProperties();
	}
	public DbClassShoppingCartTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSavingCart(){
		
		try {
			CustomerSubsystem css = new CustomerSubsystemFacade();
			CustomerProfile  custProfile = css.getGenericCustomerProfile();
		
			String savedCardID = DbQueries.readSavedCartID(custProfile.getCustId());
			
			css.initializeCustomer(custProfile.getCustId(), 1);
			ShoppingCartSubsystem scss =  ShoppingCartSubsystemFacade.INSTANCE;
	
			DbClassShoppingCartForTest dbClass = scss.getDBClassShoppingCartForTest();
			ShoppingCart originalSavedCart = dbClass
					.getSavedCartOfCustomer(css.getCustomerProfile());
			
			assertTrue(originalSavedCart.getCartId().equalsIgnoreCase(savedCardID));
			
		} catch (BackendException e) {
			fail("fail: " + e.getMessage() );
			e.printStackTrace();
		} catch (DatabaseException e) {
			fail("fail: " + e.getMessage() );
			e.printStackTrace();
		} finally{
		
		}
		
	}
}
