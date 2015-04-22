package integrationtests;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import dbsetup.DbQueries;
import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderSubsystem;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import business.shoppingcartsubsystem.CartItemImpl;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import business.externalinterfaces.CartItem;
import alltests.AllTests;
import junit.framework.TestCase;

public class CheckoutAndOrderTest extends TestCase {
	static String name = "Checkout And OrderTest Test";
	static Logger log = Logger.getLogger(CheckoutAndOrderTest.class.getName());
	
	static {
		AllTests.initializeProperties();
	}
	
	public CheckoutAndOrderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	public void testSubmitOrderAndOrderHistory(){
		Order newOrder = null;
		try {
			// submit new order
			CustomerSubsystem css = new CustomerSubsystemFacade();
			CustomerProfile  custProfile = css.getGenericCustomerProfile();
			css.initializeCustomer(custProfile.getCustId(), 1);

			// read order history before submmitting new order
			List<Order> beforeOrders =  css.getOrderHistory();
			
			ShoppingCartSubsystem scss =  ShoppingCartSubsystemFacade.INSTANCE;
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
			// verify credit cart
			scss.setLiveCart(shopCart);
			scss.updateShoppingCartItems(cartItems);
			css.checkCreditCard();
			
			// submit order
			OrderSubsystem orderSubsystem = new OrderSubsystemFacade(custProfile);
			orderSubsystem.submitOrder(shopCart);
			
			/* get order history, 
			make sure it contains the new one already included in order history
			*/
			css.refreshAfterSubmit();
			List<Order> afterOrders =  css.getOrderHistory();
			
			assertTrue(beforeOrders.size() == (afterOrders.size() - 1));
			newOrder = afterOrders.get(afterOrders.size()-1);
			assertTrue(newOrder.getOrderItems().size() == shopCart.getCartItems().size());
		
		} catch (BackendException e) {
			fail("fail: " + e.getMessage() );
			e.printStackTrace();
		} catch(BusinessException e){
			fail("fail: " + e.getMessage() );
			e.printStackTrace();
		}finally{
			// delete recently submitted order
			if(newOrder != null){
				log.info("Delete test order: " + newOrder.getOrderId());
				DbQueries.deleteOrder(newOrder.getOrderId());
			}
			
		}
		
	}
}
