package presentation.control;

import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import presentation.data.BrowseSelectData;
import presentation.data.CheckoutData;
import presentation.data.DataUtil;
import presentation.data.ErrorMessages;
import presentation.gui.CatalogListWindow;
import presentation.gui.FinalOrderWindow;
import presentation.gui.OrderCompleteWindow;
import presentation.gui.PaymentWindow;
import presentation.gui.ShippingBillingWindow;
import presentation.gui.ShoppingCartWindow;
import presentation.gui.TermsWindow;
import sun.util.logging.PlatformLogger.Level;
//import rulesengine.OperatingException;
//import rulesengine.ReteWrapper;
//import rulesengine.ValidationException;
//import system.rulescore.rulesengine.*;
//import system.rulescore.rulesupport.*;
//import system.rulescore.util.*;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.usecasecontrol.CheckoutController;

public enum CheckoutUIControl {
	INSTANCE;

	private static final Logger LOG = Logger.getLogger(CheckoutUIControl.class
			.getPackage().getName());
	// Windows managed by CheckoutUIControl
	ShippingBillingWindow shippingBillingWindow;
	PaymentWindow paymentWindow;
	TermsWindow termsWindow;
	FinalOrderWindow finalOrderWindow;
	OrderCompleteWindow orderCompleteWindow;

	public ShippingBillingWindow getShippingBillingWindow() {
		return shippingBillingWindow;
	}

	// handler for ShoppingCartWindow proceeding to checkout
	private class ProceedFromCartToShipBill implements
			EventHandler<ActionEvent>, Callback {
		
		/**
		 * This method is called after a successful login.
		 */
		public void doUpdate() {
			CheckoutData data = CheckoutData.INSTANCE;
			shippingBillingWindow = new ShippingBillingWindow();
			CustomerProfile custProfile = data.getCustomerProfile();
			Address defaultShipAddress = data.getDefaultShippingData();
			Address defaultBillAddress = data.getDefaultBillingData();
			
			shippingBillingWindow.setShippingAddress(custProfile.getFirstName()
					+ " " + custProfile.getLastName(),
					defaultShipAddress.getStreet(),
					defaultShipAddress.getCity(),
					defaultShipAddress.getState(), defaultShipAddress.getZip());
			shippingBillingWindow.setBillingAddress(custProfile.getFirstName()
					+ " " + custProfile.getLastName(),
					defaultBillAddress.getStreet(),
					defaultBillAddress.getCity(),
					defaultBillAddress.getState(), defaultBillAddress.getZip());
			shippingBillingWindow.show();
		}
		@Override
		public void handle(ActionEvent evt) {
			
			
			boolean rulesOk = true;
			/* check that cart is not empty before going to next screen */	
			
			try {
				CheckoutController.INSTANCE.runShoppingCartRules();
			} catch (RuleException e) {
				rulesOk =  false;
				//handle
				ShoppingCartWindow.INSTANCE
				.displayError("Shopping Cart error: "
						+ e.getMessage());
			} catch (BusinessException e) {
				//handle
				rulesOk = false;
				ShoppingCartWindow.INSTANCE
				.displayError("Shopping Cart error: "
						+ e.getMessage());
			}
	
			if (rulesOk) {
				ShoppingCartWindow.INSTANCE.clearMessages();
				ShoppingCartWindow.INSTANCE.setTableAccessByRow();
				ShoppingCartWindow.INSTANCE.hide();
				
				LOG.info("Shopping Cart Rules passed!");
				boolean isLoggedIn = DataUtil.isLoggedIn();
				
				if (!isLoggedIn) {
					LoginUIControl loginControl 
					  = new LoginUIControl(shippingBillingWindow, ShoppingCartWindow.INSTANCE, this);
					loginControl.startLogin();
				} else {
					doUpdate();
				}			
			}

		}
		
		@Override
		public Text getMessageBar() {
			return ShoppingCartWindow.INSTANCE.getMessageBar();
		}
	}
	
	

	
	public ProceedFromCartToShipBill getProceedFromCartToShipBill() {
		return new ProceedFromCartToShipBill();
	}

	// handlers for ShippingBillingWindow
	private class BackToShoppingCartHandler implements
			EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			ShoppingCartWindow.INSTANCE.show();
			shippingBillingWindow.clearMessages();
			shippingBillingWindow.hide();
		}
	}

	public BackToShoppingCartHandler getBackToShoppingCartHandler() {
		return new BackToShoppingCartHandler();
	}

	private class ProceedToPaymentHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			shippingBillingWindow.clearMessages();
			boolean rulesOk = true;
			Address cleansedShipAddress = null;
			Address cleansedBillAddress = null;
			try {
				cleansedShipAddress = CheckoutController.INSTANCE.runAddressRules(shippingBillingWindow
						.getShippingAddress());
			} catch (RuleException e) {
				rulesOk = false;
				shippingBillingWindow
						.displayError("Shipping address error: "
								+ e.getMessage());
			} catch (BusinessException e) {
				rulesOk = false;
				shippingBillingWindow.displayError(
						ErrorMessages.GENERAL_ERR_MSG + ": Message: " + e.getMessage());
			}
			
			try {
				cleansedBillAddress = CheckoutController.INSTANCE.runAddressRules(shippingBillingWindow
						.getBillingAddress());
			} catch (RuleException e) {
				rulesOk = false;
				shippingBillingWindow
						.displayError("Billing address error: "
								+ e.getMessage());
			} catch (BusinessException e) {
				rulesOk = false;
				shippingBillingWindow.displayError(
						ErrorMessages.GENERAL_ERR_MSG + ": Message: " + e.getMessage());
			}
			
			if (rulesOk) {
				LOG.info("Address Rules passed!");
				if (cleansedShipAddress != null && shippingBillingWindow.getSaveShipAddr()) {
					try {
						CheckoutController.INSTANCE.saveNewAddress(cleansedShipAddress);
						// set new shipping address for live cart
						CustomerSubsystem customerSubsystem = DataUtil.readCustFromCache();
						customerSubsystem.setShippingAddressInCart(cleansedShipAddress);
					} catch(BackendException e) {
						shippingBillingWindow.displayError("New shipping address not saved. Message: " 
							+ e.getMessage());
					}
				}
				
				if (cleansedBillAddress != null && shippingBillingWindow.getSaveBillAddr()) {		
					try {
						CheckoutController.INSTANCE.saveNewAddress(cleansedBillAddress);
						// set new billing address for live cart
						CustomerSubsystem customerSubsystem = DataUtil.readCustFromCache();
						customerSubsystem.setBillingAddressInCart(cleansedBillAddress);
					} catch(BackendException e) {
						shippingBillingWindow.displayError("New billing address not saved. Message: " 
							+ e.getMessage());
					}
				}
				
				boolean addressAssginedToCart = true;
				if(cleansedShipAddress != null)
				{
					try {
						CheckoutController.INSTANCE.setShipAddressToShoppingCart(cleansedShipAddress);
					} catch (BackendException e) {
						addressAssginedToCart = false;
						shippingBillingWindow.displayError("Shipping Address not assigned to shopping cart. Message: " 
								+ e.getMessage());
					}
				}
				if(cleansedBillAddress != null)
				{
					try{
						CheckoutController.INSTANCE.setBillAddressToShoppingCart(cleansedBillAddress);
					}catch(BackendException e){
						addressAssginedToCart = false;
						shippingBillingWindow.displayError("Billing Address not assigned to shopping cart. Message: " 
								+ e.getMessage());
					}
				}
				if(addressAssginedToCart)
				{
					paymentWindow = new PaymentWindow();
					paymentWindow.show();
					shippingBillingWindow.hide();
				}
			}
		}
	}
	

	public ProceedToPaymentHandler getProceedToPaymentHandler() {
		return new ProceedToPaymentHandler();
	}

	public class SaveShipChangeListener implements ChangeListener<Boolean> {
		@Override
		public void changed(ObservableValue<? extends Boolean> observed,
				Boolean oldval, Boolean newval) {
			shippingBillingWindow.displayInfo("");
		}
	}

	public class SaveBillChangeListener implements ChangeListener<Boolean> {
		@Override
		public void changed(ObservableValue<? extends Boolean> observed,
				Boolean oldval, Boolean newval) {
			shippingBillingWindow.displayInfo("");

		}
	}

	public SaveShipChangeListener getSaveShipChangeListener() {
		return new SaveShipChangeListener();
	}

	public SaveBillChangeListener getSaveBillChangeListener() {
		return new SaveBillChangeListener();
	}

	// handlers for PaymentWindow
	private class BackToShipBillWindow implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			paymentWindow.clearMessages();
			shippingBillingWindow.show();
			paymentWindow.hide();
		}
	}

	public BackToShipBillWindow getBackToShipBillWindow() {
		return new BackToShipBillWindow();
	}

	private class BackToCartHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			paymentWindow.clearMessages();
			ShoppingCartWindow.INSTANCE.show();
			paymentWindow.hide();
		}
	}

	public BackToCartHandler getBackToCartHandler() {
		return new BackToCartHandler();
	}

	private class ProceedToTermsHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			try {
				CheckoutController.INSTANCE.runPaymentRules(shippingBillingWindow.getBillingAddress(),
					paymentWindow.getCreditCardFromWindow());
				CheckoutController.INSTANCE.setPaymentInfoToShoppingCart(paymentWindow.getCreditCardFromWindow());
				paymentWindow.clearMessages();
				paymentWindow.hide();
				termsWindow = new TermsWindow();
				termsWindow.show();
				
				// set payment info for live card
				CustomerSubsystem customerSubsystem = DataUtil.readCustFromCache();
				customerSubsystem.setPaymentInfoInCart(paymentWindow.getCreditCardFromWindow());
				
				// verify credit cart
				CheckoutController.INSTANCE.verifyCreditCard();
				
			} catch(RuleException e) {
				paymentWindow.displayError(e.getMessage());
			} catch(BusinessException e) {
				paymentWindow.displayError(ErrorMessages.DATABASE_ERROR);
			}
		}
	}

	public ProceedToTermsHandler getProceedToTermsHandler() {
		return new ProceedToTermsHandler();
	}


	// handlers for TermsWindow

	private class ToCartFromTermsHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			termsWindow.hide();
			ShoppingCartWindow.INSTANCE.show();
		}
	}

	public ToCartFromTermsHandler getToCartFromTermsHandler() {
		return new ToCartFromTermsHandler();
	}

	private class AcceptTermsHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			finalOrderWindow = new FinalOrderWindow();
			finalOrderWindow.setData(BrowseSelectData.INSTANCE.getCartData());
			finalOrderWindow.show();
			termsWindow.hide();
		}
	}

	public AcceptTermsHandler getAcceptTermsHandler() {
		return new AcceptTermsHandler();
	}

	// handlers for FinalOrderWindow
	private class SubmitHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			
			try {
				CheckoutController.INSTANCE.runFinalOrderRules();
				LOG.info("Final Order rules passed!");
				CheckoutController.INSTANCE.submitFinalOrder();
				orderCompleteWindow = new OrderCompleteWindow();
				orderCompleteWindow.show();
				finalOrderWindow.clearMessages();
				finalOrderWindow.hide();
			} catch(RuleException ex){
				finalOrderWindow.displayError(ex.getMessage());
			}catch (BusinessException e) {
				finalOrderWindow.displayError(e.getMessage());
			}
			
		}

	}

	public SubmitHandler getSubmitHandler() {
		return new SubmitHandler();
	}

	private class CancelOrderHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			finalOrderWindow.displayInfo("Order Cancelled");
		}
	}

	public CancelOrderHandler getCancelOrderHandler() {
		return new CancelOrderHandler();
	}

	private class ToShoppingCartFromFinalOrderHandler implements
			EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			ShoppingCartWindow.INSTANCE.show();
			finalOrderWindow.hide();
			finalOrderWindow.clearMessages();
		}
	}

	public ToShoppingCartFromFinalOrderHandler getToShoppingCartFromFinalOrderHandler() {
		return new ToShoppingCartFromFinalOrderHandler();
	}

	// handlers for OrderCompleteWindow

	private class ContinueFromOrderCompleteHandler implements
			EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			CatalogListWindow.getInstance().show();
			orderCompleteWindow.hide();
		}
	}

	public ContinueFromOrderCompleteHandler getContinueFromOrderCompleteHandler() {
		return new ContinueFromOrderCompleteHandler();
	}
}
