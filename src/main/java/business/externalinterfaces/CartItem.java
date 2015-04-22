package business.externalinterfaces;

public interface CartItem {
	public boolean isAlreadySaved();
	public Integer getCartid();
	public Integer getLineitemid();
	public Integer getProductid();
	public String getProductName();
	public String getQuantity();
	public String getTotalprice();
	
	// setter for testing
	public void setCartId(int id);
	public void setCartLineitemid(int i);
	public void setProductid(int i);
	public void setProductName(String name);
	public void setQuantity(String s);
	public void setTotalPrice(String s);
}
