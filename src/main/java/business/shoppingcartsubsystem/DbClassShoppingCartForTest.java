package business.shoppingcartsubsystem;

import middleware.exceptions.DatabaseException;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.ShoppingCart;

public interface DbClassShoppingCartForTest {

	public ShoppingCart getSavedCartOfCustomer(CustomerProfile cust) throws DatabaseException;
}
