package business.shoppingcartsubsystem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import middleware.exceptions.DatabaseException;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.Rules;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.shoppingcartsubsystem.DbClassShoppingCart;

public enum ShoppingCartSubsystemFacade implements ShoppingCartSubsystem {
	INSTANCE;

	ShoppingCartImpl liveCart = new ShoppingCartImpl(new LinkedList<CartItem>());
	ShoppingCartImpl savedCart;
	Integer shopCartId;
	CustomerProfile customerProfile;
	Logger log = Logger.getLogger(this.getClass().getPackage().getName());

	// interface methods
	public void setCustomerProfile(CustomerProfile customerProfile) {
		this.customerProfile = customerProfile;
	}

	public void makeSavedCartLive() {
		liveCart = savedCart;
	}

	public ShoppingCart getLiveCart() {
		return liveCart;
	}

	public void retrieveSavedCart() throws BackendException {
		try {
			DbClassShoppingCart dbClass = new DbClassShoppingCart();
			ShoppingCartImpl cartFound = dbClass
					.retrieveSavedCart(customerProfile);
			if (cartFound == null) {
				savedCart = new ShoppingCartImpl(new ArrayList<CartItem>());
			} else {
				savedCart = cartFound;
			}
		} catch (DatabaseException e) {
			throw new BackendException(e);
		}

	}

	public static CartItem createCartItem(String productName, String quantity,
			String totalprice) {
		try {
			return new CartItemImpl(productName, quantity, totalprice);
		} catch (BackendException e) {
			throw new RuntimeException(
					"Can't create a cartitem because of productid lookup: "
							+ e.getMessage());
		}
	}

	public void updateShoppingCartItems(List<CartItem> list) {
		if(liveCart == null){
			liveCart = new ShoppingCartImpl(new LinkedList<CartItem>());
		}
		liveCart.setCartItems(list);
	}

	public List<CartItem> getCartItems() {
		return liveCart.getCartItems();
	}

	@Override
	public void clearLiveCart() {
		// clear all cart items
		liveCart.getCartItems().clear();
	}

	@Override
	public List<CartItem> getLiveCartItems() {
		// return all cart items in Live Shopping Cart
		return liveCart.getCartItems();
	}

	@Override
	public void setShippingAddress(Address addr) {
		// store shipping address in Shopping Cart
		liveCart.setShipAddress(addr);
	}

	@Override
	public void setBillingAddress(Address addr) {
		// store billing address in Shopping Cart
		liveCart.setBillAddress(addr);
	}

	@Override
	public void setPaymentInfo(CreditCard cc) {
		// store Payment info in Shopping Cart
		liveCart.setPaymentInfo(cc);
	}

	@Override
	public void saveLiveCart() throws BackendException {
		// Save live cart & cart items to database
		DbClassShoppingCart dbClass = new DbClassShoppingCart();
		try {
			dbClass.saveCart(customerProfile, liveCart);;
		} catch (DatabaseException e) {
			throw new BackendException(e);
		}

	}

	@Override
	public void runShoppingCartRules() throws RuleException, BusinessException {
		// rules concerning validity of shopping cart
		Rules shoppingCartRules = new ShoppingCartRules(liveCart);
		shoppingCartRules.runRules();
	}

	@Override
	public void runFinalOrderRules() throws RuleException, BusinessException {
		// rules are run to check validity of the order
		Rules finalOrderRules = new FinalOrderRules(liveCart);
		finalOrderRules.runRules();
	}

	// interface methods for testing

	public ShoppingCart getEmptyCartForTest() {
		return new ShoppingCartImpl();
	}

	public CartItem getEmptyCartItemForTest() {
		return new CartItemImpl();
	}
}
