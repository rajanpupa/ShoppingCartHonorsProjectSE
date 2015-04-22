
package performancetests.rulesstubs;


import business.externalinterfaces.Address;


/**
 * @author pcorazza
 * @since Nov 4, 2004
 * Class Description:
 * 
 * 
 */
public class AddressImpl implements Address{
    
    public AddressImpl(String street, String city, 
    		String state, String zip, boolean isShippingAddress, boolean isBillingAddress){
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.isShippingAddress = isShippingAddress;
        this.isBillingAddress = isBillingAddress;
    }
    public AddressImpl(String[] fields) {
    	this.street=fields[0];
    	this.city = fields[1];
    	this.state = fields[2];
    	this.zip = fields[3];
    	isShippingAddress = isBillingAddress = true;
    }
    private String street;   
    private String city;
    private String state;
    private String zip;
    private boolean isShippingAddress;
    private boolean isBillingAddress;
    
    private String address1;
	private String address2;
    
 
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
 
    public String toString() {
        String n = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("Street: "+street+n);        
        sb.append("City: "+city+n);
        sb.append("State: "+state+n);
        sb.append("Zip: "+zip+n);
        return sb.toString();
    }

	

	
	
	@Override
	public String getStreet() {
		return street;
	}
	@Override
	public void setStreet(String s) {
		street = s;
		
	}
	@Override
	public boolean isShippingAddress() {
		return isShippingAddress;
	}
	@Override
	public boolean isBillingAddress() {
		
		return isBillingAddress;
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
	
	public boolean equals(Address addr) {
		// TODO Auto-generated method stub
		return false;
	}
}
