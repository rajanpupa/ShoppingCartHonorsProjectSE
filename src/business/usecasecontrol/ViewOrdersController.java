package business.usecasecontrol;

import java.util.List;

import business.exceptions.BackendException;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;

/**
 * @author pcorazza
 */
public class ViewOrdersController {

	public List<Order> getOrderHistory(CustomerProfile custProfile)
			throws BackendException {
		OrderSubsystem oss = new OrderSubsystemFacade(custProfile);
		return oss.getOrderHistory();
	}
}
