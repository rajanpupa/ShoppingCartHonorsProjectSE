package business.customersubsystem;

import java.util.List;

import middleware.creditverifcation.CreditVerificationFacade;
import middleware.exceptions.DatabaseException;
import middleware.exceptions.MiddlewareException;
import middleware.externalinterfaces.CreditVerification;

import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.DbClassAddressForTest;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderSubsystem;
import business.externalinterfaces.Rules;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.ordersubsystem.OrderImpl;
import business.ordersubsystem.OrderSubsystemFacade;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;

public class CustomerSubsystemFacade implements CustomerSubsystem {
	ShoppingCartSubsystem shoppingCartSubsystem;
	OrderSubsystem orderSubsystem;
	List<Order> orderHistory;
	AddressImpl defaultShipAddress;
	AddressImpl defaultBillAddress;
	CreditCard defaultPaymentInfo;
	CustomerProfileImpl customerProfile;

	/**
	 * Use for loading order history, default addresses, default payment info,
	 * saved shopping cart,cust profile after login
	 */
	public void initializeCustomer(Integer id, int authorizationLevel)
			throws BackendException {
		boolean isAdmin = (authorizationLevel >= 1);
		loadCustomerProfile(id, isAdmin);
		loadDefaultShipAddress();
		loadDefaultBillAddress();
		loadDefaultPaymentInfo();
		shoppingCartSubsystem = ShoppingCartSubsystemFacade.INSTANCE;
		shoppingCartSubsystem.setCustomerProfile(customerProfile);
		shoppingCartSubsystem.retrieveSavedCart();
		loadOrderData();
	}

	void loadCustomerProfile(int id, boolean isAdmin) throws BackendException {
		try {
			DbClassCustomerProfile dbclass = new DbClassCustomerProfile();
			dbclass.readCustomerProfile(id);
			customerProfile = dbclass.getCustomerProfile();
			customerProfile.setIsAdmin(isAdmin);
		} catch (DatabaseException e) {
			throw new BackendException(e);
		}
	}

	void loadDefaultShipAddress() throws BackendException {
		DbClassAddress dbclass = new DbClassAddress();
		try {
			dbclass.readDefaultShipAddress(customerProfile);
			defaultShipAddress = dbclass.getDefaultShipAddress();
		} catch (DatabaseException e) {
			throw new BackendException(e);
		}
	}

	void loadDefaultBillAddress() throws BackendException {
		DbClassAddress dbclass = new DbClassAddress();
		try {
			dbclass.readDefaultBillAddress(customerProfile);
			defaultBillAddress = dbclass.getDefaultBillAddress();
		} catch (DatabaseException e) {
			throw new BackendException(e);
		}
	}

	void loadDefaultPaymentInfo() throws BackendException {
		// implement
		DbClassCreditCard dbclass = new DbClassCreditCard();
		try {
			dbclass.readDefaultPaymentInfo(customerProfile);
			defaultPaymentInfo = dbclass.getDefaultPaymentInfo();
		} catch (DatabaseException e) {
			throw new BackendException(e);
		}
	}

	void loadOrderData() throws BackendException {

		// retrieve the order history for the customer and store here
		orderSubsystem = new OrderSubsystemFacade(customerProfile);

		orderHistory = orderSubsystem.getOrderHistory();

	}

	/**
	 * Returns true if user has admin access
	 */
	public boolean isAdmin() {
		return customerProfile.isAdmin();
	}

	/**
	 * Use for saving an address created by user
	 */
	public void saveNewAddress(Address addr) throws BackendException {
		try {
			DbClassAddress dbClass = new DbClassAddress();
			dbClass.setAddress(addr);
			dbClass.saveAddress(customerProfile);
		} catch (DatabaseException e) {
			throw new BackendException(e);
		}
	}

	public CustomerProfile getCustomerProfile() {

		return customerProfile;
	}

	public Address getDefaultShippingAddress() {
		return defaultShipAddress;
	}

	public Address getDefaultBillingAddress() {
		return defaultBillAddress;
	}

	public CreditCard getDefaultPaymentInfo() {
		return defaultPaymentInfo;
	}

	/**
	 * Use to supply all stored addresses of a customer when he wishes to select
	 * an address in ship/bill window
	 */
	public List<Address> getAllAddresses() throws BackendException {
		DbClassAddress dbclass = new DbClassAddress();
		try {
			dbclass.readAllAddresses(customerProfile);
			return dbclass.getAddressList();
		} catch (DatabaseException e) {
			throw new BackendException(e);
		}

	}

	public Address runAddressRules(Address addr) throws RuleException,
			BusinessException {

		Rules transferObject = new RulesAddress(addr);
		transferObject.runRules();

		// updates are in the form of a List; 0th object is the necessary
		// Address
		AddressImpl update = (AddressImpl) transferObject.getUpdates().get(0);
		return update;
	}

	public void runPaymentRules(Address addr, CreditCard cc)
			throws RuleException, BusinessException {
		Rules transferObject = new RulesPayment(addr, cc);
		transferObject.runRules();
	}

	public static Address createAddress(String street, String city,
			String state, String zip, boolean isShip, boolean isBill) {
		return new AddressImpl(street, city, state, zip, isShip, isBill);
	}

	public static CustomerProfile createCustProfile(Integer custid,
			String firstName, String lastName, boolean isAdmin) {
		return new CustomerProfileImpl(custid, firstName, lastName, isAdmin);
	}

	public static CreditCard createCreditCard(String nameOnCard,
			String expirationDate, String cardNum, String cardType) {
		return new CreditCardImpl(nameOnCard, expirationDate, cardNum, cardType);
	}

	public List<Order> getOrderHistory() throws BackendException {
		return orderHistory;
	}

	@Override
	public void submitOrder() throws BackendException {

		OrderSubsystem os = new OrderSubsystemFacade(customerProfile);
		os.submitOrder(shoppingCartSubsystem.getLiveCart());
		// clear live cart
		shoppingCartSubsystem.clearLiveCart();
	}

	@Override
	public void setShippingAddressInCart(Address addr) {
		ShoppingCartSubsystemFacade.INSTANCE.setShippingAddress(addr);
	}

	@Override
	public void refreshAfterSubmit() throws BackendException {
		// loadDefaultShipAddress();
		// loadDefaultBillAddress();
		// loadDefaultPaymentInfo();
		shoppingCartSubsystem.retrieveSavedCart();
		loadOrderData();

	}

	@Override
	public ShoppingCartSubsystem getShoppingCart() {
		return ShoppingCartSubsystemFacade.INSTANCE;
	}

	@Override
	public void saveShoppingCart() throws BackendException {
		ShoppingCartSubsystemFacade scss = ShoppingCartSubsystemFacade.INSTANCE;
		scss.setBillingAddress(getDefaultBillingAddress());
		scss.setShippingAddress(getDefaultShippingAddress());
		scss.setPaymentInfo(getDefaultPaymentInfo());
		scss.saveLiveCart();

	}


	@Override
	public void checkCreditCard() throws BusinessException {

		List<CartItem> items = ShoppingCartSubsystemFacade.INSTANCE
				.getLiveCartItems();
		ShoppingCart theCart = ShoppingCartSubsystemFacade.INSTANCE
				.getLiveCart();
		Address billAddr = theCart.getBillingAddress();
		CreditCard cc = theCart.getPaymentInfo();
		String amount = quickComputeTotalPrice(items);
		CreditVerification creditVerif = new CreditVerificationFacade();
		try {
			creditVerif.checkCreditCard(customerProfile, billAddr, cc,
					new Double(amount));
		} catch (MiddlewareException e) {
			throw new BusinessException(e);
		}
	}
	
	private String quickComputeTotalPrice(List<CartItem> items){
		double totalPrice = 0;
		for(CartItem item : items){
			totalPrice += Double.parseDouble(item.getTotalprice());
		}
		return String.format("%.2f", totalPrice);
	}

	@Override
	public DbClassAddressForTest getGenericDbClassAddress() {
		 DbClassAddressForTest dbClassAddress = new DbClassAddress();
		  return dbClassAddress;
	}

	@Override
	public CustomerProfile getGenericCustomerProfile() {
		 CustomerProfile cust = new CustomerProfileImpl(1, "Test_Firs_Name",
				    "Test_Last_Name");
				  return cust;
	}

	@Override
	public void setBillingAddressInCart(Address addr) {
		ShoppingCartSubsystemFacade.INSTANCE.setBillingAddress(addr);
	}

	@Override
	public void setPaymentInfoInCart(CreditCard cc) {
		ShoppingCartSubsystemFacade.INSTANCE.setPaymentInfo(cc);

	}

}
