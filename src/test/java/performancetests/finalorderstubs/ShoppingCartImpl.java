package performancetests.finalorderstubs;

import java.util.Iterator;
import java.util.List;

import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.ShoppingCart;

public class ShoppingCartImpl implements ShoppingCart{
	private List<CartItem> cartItems;
    private Address shipAddress;
    private Address billAddress;
    private CreditCard creditCard;
    @SuppressWarnings("unused")
	private String cartId;
    
    public ShoppingCartImpl(List<CartItem>cartItems, Address shipAddr,Address billAddr, CreditCard cc )
    {
    	this.cartItems = cartItems;
    	this.shipAddress = shipAddr;
    	this.billAddress = billAddr;
    	this.creditCard = cc;
    }
    
	@Override
	public Address getShippingAddress() {
		// TODO Auto-generated method stub
		return shipAddress;
	}

	@Override
	public Address getBillingAddress() {
		return billAddress;
	}

	@Override
	public CreditCard getPaymentInfo() {
		return creditCard;
	}

	@Override
	public List<CartItem> getCartItems() {
		return cartItems;
	}

	@Override
	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	@Override
	public double getTotalPrice() {
		double sum= 0.00;
    	
    	Iterator<CartItem> itr = cartItems.iterator();
        while(itr.hasNext()){
        	CartItem item =itr.next();
        	sum += Double.parseDouble(item.getTotalprice());
        }
    	return sum;
	}

	@Override
	public boolean deleteCartItem(String name) {
		// TODO Auto-generated method stub
		CartItem  itemSought = null;
    	for(CartItem item : cartItems) {
    		if(item.getProductName().equals(name))
    			itemSought = item;
    	}
    	Object ob = cartItems.remove(itemSought);
    	return (ob != null);
	}

	@Override
	public boolean isEmpty() {
		if (cartItems == null){
			return true;
		}
		return cartItems.size() == 0;
	}

	@Override
	public void setShipAddress(Address address) {
		this.shipAddress = address;
	}

	@Override
	public void setBillAddress(Address address) {
		this.billAddress = address;
	}

	@Override
	public void setPaymentInfo(CreditCard info) {
		this.creditCard = info;
	}

	@Override
	public String getCartId() {
		return cartId;
	}
}
  