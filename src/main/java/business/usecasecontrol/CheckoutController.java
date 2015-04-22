package business.usecasecontrol;

import java.util.logging.Logger;

import presentation.data.DataUtil;
import business.BusinessConstants;
import business.SessionCache;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;

public enum CheckoutController  {
	INSTANCE;
	
	private static final Logger LOG = Logger.getLogger(CheckoutController.class
			.getPackage().getName());
	
	
	public void runShoppingCartRules() throws RuleException, BusinessException {
		//implement
		ShoppingCartSubsystemFacade.INSTANCE.runShoppingCartRules();
	}
	
	public void runPaymentRules(Address addr, CreditCard cc) throws RuleException, BusinessException {
		//implement
		CustomerSubsystem cust = 
				DataUtil.readCustFromCache();
		cust.runPaymentRules(addr, cc);
	}
	
	public Address runAddressRules(Address addr) throws RuleException, BusinessException {
		CustomerSubsystem cust = 
				DataUtil.readCustFromCache();
		return cust.runAddressRules(addr);
	}
	
	/** Asks the ShoppingCart Subsystem to run final order rules */
	public void runFinalOrderRules() throws RuleException, BusinessException {
		//implement
		ShoppingCartSubsystemFacade.INSTANCE.runFinalOrderRules();
	}
	
	/** Asks Customer Subsystem to check credit card against 
	 *  Credit Verification System 
	 */
	public void verifyCreditCard() throws BusinessException {
		// implement
		CustomerSubsystem cust = 
				DataUtil.readCustFromCache();			
		cust.checkCreditCard();
	}
	
	public void saveNewAddress(Address addr) throws BackendException {
		CustomerSubsystem cust = 
				DataUtil.readCustFromCache();			
		cust.saveNewAddress(addr);
	}
	
	public void setShipAddressToShoppingCart(Address addr) throws BackendException {
		CustomerSubsystem cust = 
				(CustomerSubsystem)SessionCache.getInstance().get(BusinessConstants.CUSTOMER);			
			cust.setShippingAddressInCart(addr);
	}
	
	public void setBillAddressToShoppingCart(Address addr) throws BackendException {
		CustomerSubsystem cust = 
				(CustomerSubsystem)SessionCache.getInstance().get(BusinessConstants.CUSTOMER);			
			cust.setBillingAddressInCart(addr);
	}
	
	public void setPaymentInfoToShoppingCart(CreditCard cc)
	{
		CustomerSubsystem cust = 
				(CustomerSubsystem)SessionCache.getInstance().get(BusinessConstants.CUSTOMER);			
			cust.setPaymentInfoInCart(cc);
	}
	
	/** Asks Customer Subsystem to submit final order */
	public void submitFinalOrder() throws BackendException {
		//submit final order
		CustomerSubsystem cust = 
				DataUtil.readCustFromCache();;			
		cust.submitOrder();
	}


}
