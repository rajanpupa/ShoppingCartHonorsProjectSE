package subsystemtests;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.TestCase;
import alltests.AllTests;
import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.shoppingcartsubsystem.CartItemImpl;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;

public class ShoppingCartSubsytemTest extends TestCase {

	static String name = "ShoppingCart Subsystem Test";
	static Logger log = Logger.getLogger(ShoppingCartSubsytemTest.class.getName());
	
	static {
		AllTests.initializeProperties();
	}
	
	public ShoppingCartSubsytemTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public void testSaveCart(){
		ShoppingCart originalSavedCart = null;
		ShoppingCartSubsystem scss = null;
		CustomerSubsystem css = null;
		try {
			// submit new order
			css = new CustomerSubsystemFacade();
			CustomerProfile  custProfile = css.getGenericCustomerProfile();
			css.initializeCustomer(custProfile.getCustId(), 1);

			
			scss =  ShoppingCartSubsystemFacade.INSTANCE;
			ShoppingCart shopCart = scss.getEmptyCartForTest();
			shopCart.setBillAddress(css.getDefaultBillingAddress());
			shopCart.setShipAddress(css.getDefaultShippingAddress());
			shopCart.setPaymentInfo(css.getDefaultPaymentInfo());
			List<CartItem> cartItems = new ArrayList<CartItem>();
			for (int i = 1 ; i < 3; i++){
				CartItem cartItem = new CartItemImpl(1, 1, 1, String.valueOf(i), String.valueOf(i*0.5), true);
						
				cartItem.setProductid(1);
				cartItems.add(cartItem);
			}
			
			shopCart.setCartItems(cartItems);
			// keep saved cart
			originalSavedCart = scss.getSavedCart();
			
			scss.setLiveCart(shopCart);
			scss.updateShoppingCartItems(cartItems);
			
			assertTrue("Live cart items passed", scss.getLiveCartItems().size() == shopCart.getCartItems().size());
			scss.saveLiveCart();
			
			// retrieve saved cart
			scss.retrieveSavedCart();
			ShoppingCart savedCart = scss.getSavedCart();
			assertTrue("Saved Cart successfully", shopCart.getCartItems().size() == savedCart.getCartItems().size());
			
		} catch (BackendException e) {
			fail("fail: " + e.getMessage() );
			e.printStackTrace();
		} finally{
			scss.updateShoppingCartItems(originalSavedCart.getCartItems());
			try {
				scss.saveLiveCart();
			} catch (BackendException e) {
				fail("fail: "+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
