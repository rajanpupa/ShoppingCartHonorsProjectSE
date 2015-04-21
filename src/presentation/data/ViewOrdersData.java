package presentation.data;

import java.util.List;

import business.exceptions.BackendException;
import business.usecasecontrol.BrowseAndSelectController;
import business.usecasecontrol.ViewOrdersController;

public enum ViewOrdersData {
	INSTANCE;
	private OrderPres selectedOrder;
	public OrderPres getSelectedOrder() {
		return selectedOrder;
	}
	public void setSelectedOrder(OrderPres so) {
		selectedOrder = so;
	}
	
	public List<OrderPres> getOrders() throws BackendException {
		
		ViewOrdersController vo=new ViewOrdersController();
		return business.Util.orderToOrderPres(vo.getOrderHistory(BrowseAndSelectController.INSTANCE.getCustomerProfile()));
		//return DefaultData.ALL_ORDERS;
	}
}
