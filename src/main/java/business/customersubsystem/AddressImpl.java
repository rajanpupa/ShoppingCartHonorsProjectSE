package business.customersubsystem;

import business.externalinterfaces.Address;

public class AddressImpl implements Address {
	private String street;
	private String city;
	private String state;
	private String zip;
	private boolean isShippingAddress = false;
	private boolean isBillingAddress = false;
	
	private String address1;
	private String address2;
	
	public AddressImpl(String str, String c, String state, String zip, 
			boolean isShip, boolean isBill) {
		street=str;
		city=c;
		this.state=state;
		this.zip=zip;
		isShippingAddress = isShip;
		isBillingAddress = isBill;
	}
	public AddressImpl() {}
	public boolean isShippingAddress() {
		return isShippingAddress;
	}
	
	public boolean isBillingAddress() {
		return isBillingAddress;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	@Override
	public void setIsShippingAddress(boolean isShippingAddress) {
		this.isShippingAddress = isShippingAddress;
		
	}
	@Override
	public void setIsBillingAddress(boolean isBillingAddress) {
		this.isBillingAddress = isBillingAddress;
	}
	@Override
	public String getAddress1() {
		return address1;
	}
	@Override
	public String getAddress2() {
		return address2;
	}
	@Override
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	@Override
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	@Override
	public boolean equals(Address addr) {
		if(this.state.equals(addr.getState())
				&& city.equals(addr.getCity() )
						&& street.equals(addr.getStreet())
						&& zip.equals(addr.getZip())
								){
					return true;
				}
			return false;
	}
	
}
