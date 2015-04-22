package business.externalinterfaces;

import java.util.List;


public interface ShoppingCart {
    Address getShippingAddress();
    Address getBillingAddress();
    CreditCard getPaymentInfo();
    List<CartItem> getCartItems();
    void setCartItems(List<CartItem> cartItems);
    double getTotalPrice();
    boolean deleteCartItem(String name);
    boolean isEmpty();

    //setters for testing
    public void setShipAddress(Address address);
    public void setBillAddress(Address address);
    public void setPaymentInfo(CreditCard info);
    public String getCartId();
}
