
package business.externalinterfaces;

public interface Address {
    public String getStreet();
    public String getCity();
    public String getState();
    public String getZip();
    public void setStreet(String s);
    public void setCity(String s);
    public void setState(String s);
    public void setZip(String s);
    public boolean isShippingAddress();
	public boolean isBillingAddress();  
	
	// add more
	public void setIsShippingAddress(boolean isShippingAddress);
	public void setIsBillingAddress(boolean isBillingAddress);
	
	public String getAddress1();
	public String getAddress2();
	public void setAddress1(String address1);
	public void setAddress2(String address2);
}
