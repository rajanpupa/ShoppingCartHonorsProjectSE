package presentation.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.usecasecontrol.BrowseAndSelectController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import presentation.gui.GuiConstants;

public enum CheckoutData {
	INSTANCE;
	
	public Address createAddress(String street, String city, String state,
			String zip, boolean isShip, boolean isBill) {
		return CustomerSubsystemFacade.createAddress(street, city, state, zip, isShip, isBill);
	}
	
	public CreditCard createCreditCard(String nameOnCard,String expirationDate,
               String cardNum, String cardType) {
		return CustomerSubsystemFacade.createCreditCard(nameOnCard, expirationDate, 
				cardNum, cardType);
	}
	
	//Customer Ship Address Data
	private ObservableList<CustomerPres> shipAddresses = loadShipAddresses();
	
	//Customer Bill Address Data
	private ObservableList<CustomerPres> billAddresses = loadBillAddresses();
	
	private ObservableList<CustomerPres> loadShipAddresses() {	
		CustomerSubsystem customerSubsystem = DataUtil.readCustFromCache();
		if(customerSubsystem == null){
			return null;
		}
	    List<CustomerPres> list = null;
		try {
			list = customerSubsystem.getAllAddresses()
							   .stream()
							   .map(address -> new CustomerPres(customerSubsystem.getCustomerProfile(), address))
							   .filter(cust -> cust.getAddress().isShippingAddress())
							   .collect(Collectors.toList());
		} catch (BackendException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FXCollections.observableList(list);				   
										   
	}
	private ObservableList<CustomerPres> loadBillAddresses() {
		CustomerSubsystem customerSubsystem = DataUtil.readCustFromCache();
		if(customerSubsystem == null){
			return null;
		}
		List<CustomerPres> list = null;
		try {
			list = customerSubsystem.getAllAddresses()
					   .stream()
					   .map(address -> new CustomerPres(customerSubsystem.getCustomerProfile(), address))
					   .filter(cust -> cust.getAddress().isBillingAddress())
					   .collect(Collectors.toList());
		} catch (BackendException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FXCollections.observableList(list);
	}
	public ObservableList<CustomerPres> getCustomerShipAddresses() {
		if(shipAddresses == null){
			shipAddresses = loadShipAddresses();
		}
		return shipAddresses;
	}
	public ObservableList<CustomerPres> getCustomerBillAddresses() {
		return billAddresses;
	}
	public List<String> getDisplayAddressFields() {
		return GuiConstants.DISPLAY_ADDRESS_FIELDS;
	}
	public List<String> getDisplayCredCardFields() {
		return GuiConstants.DISPLAY_CREDIT_CARD_FIELDS;
	}
	public List<String> getCredCardTypes() {
		return GuiConstants.CREDIT_CARD_TYPES;
	}
	public Address getDefaultShippingData() {
		return DataUtil.readCustFromCache().getDefaultShippingAddress();
	}
	
	public Address getDefaultBillingData() {
		return DataUtil.readCustFromCache().getDefaultBillingAddress();
	}
	
	public CreditCard getDefaultPaymentInfo() {
		return DataUtil.readCustFromCache().getDefaultPaymentInfo();
	}
	
	
	public CustomerProfile getCustomerProfile() {
		return BrowseAndSelectController.INSTANCE.getCustomerProfile();
	}
	
		
	
	private class ShipAddressSynchronizer implements Synchronizer {
		public void refresh(ObservableList list) {
			shipAddresses = list;
		}
	}
	public ShipAddressSynchronizer getShipAddressSynchronizer() {
		return new ShipAddressSynchronizer();
	}
	
	private class BillAddressSynchronizer implements Synchronizer {
		public void refresh(ObservableList list) {
			billAddresses = list;
		}
	}
	public BillAddressSynchronizer getBillAddressSynchronizer() {
		return new BillAddressSynchronizer();
	}
	
	public static class ShipBill {
		public boolean isShipping;
		public String label;
		public Synchronizer synch;
		public ShipBill(boolean shipOrBill, String label, Synchronizer synch) {
			this.isShipping = shipOrBill;
			this.label = label;
			this.synch = synch;
		}
		
	}
	
}
