package presentation.control;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import presentation.data.BrowseSelectData;
import presentation.data.CartItemPres;
import presentation.data.CatalogPres;
import presentation.data.CheckoutData;
import presentation.data.DataUtil;
import presentation.data.ProductPres;
import presentation.gui.CatalogListWindow;
import presentation.gui.OrdersWindow;
import presentation.gui.ProductDetailsWindow;
import presentation.gui.ProductListWindow;
import presentation.gui.ShoppingCartWindow;
import presentation.gui.TableUtil;
import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
//import rulesengine.OperatingException;
//import rulesengine.ReteWrapper;
//import rulesengine.ValidationException;
//import system.rulescore.rulesengine.*;
//import system.rulescore.rulesupport.*;
//import system.rulescore.util.*;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.exceptions.UnauthorizedException;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Product;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import business.usecasecontrol.BrowseAndSelectController;

public enum BrowseSelectUIControl {
	// Singleton
	INSTANCE;

	// Windows that this controller manages
	// difficult to manage CatalogListWindow, so instead
	// we access CatalogListWindow statically
	// private CatalogListWindow catalogListWindow;
	private ProductListWindow productListWindow;
	private ProductDetailsWindow productDetailsWindow;
	private ShoppingCartWindow shoppingCartWindow;
	private OrdersWindow ordersWindow;
	private Stage primaryStage;
	private Callback startScreenCallback;

	public void setPrimaryStage(Stage ps, Callback callback) {
		primaryStage = ps;
		startScreenCallback = callback;
	}

	// // Handlers for browse and select portion of Start page
	private class OnlinePurchaseHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent evt) {
			try {
				CatalogListWindow catList = CatalogListWindow.getInstance(
						primaryStage, FXCollections
								.observableList(BrowseSelectData.INSTANCE
										.getCatalogList()));
				catList.show();
				primaryStage.hide();
			} catch (BackendException e) {
				startScreenCallback.displayError("Database error. Message: "
						+ e.getMessage());
				primaryStage.show();
			}
		}
	}

	private class RetrieveSavedCartHandler implements
			EventHandler<ActionEvent>, Callback {
		public void doUpdate() {
			try {
				Authorization.checkAuthorization(shoppingCartWindow,
						DataUtil.custIsAdmin());
			} catch (UnauthorizedException e) {
				displayError(e.getMessage());
				return;
			}
			BrowseAndSelectController.INSTANCE.retrieveSavedCart();
			BrowseSelectData.INSTANCE.updateCartData();
			primaryStage.hide();
			shoppingCartWindow.show();
		}

		public Text getMessageBar() {
			return startScreenCallback.getMessageBar();
		}

		@Override
		public void handle(ActionEvent evt) {
			shoppingCartWindow = ShoppingCartWindow.INSTANCE;
			boolean isLoggedIn = DataUtil.isLoggedIn();
			if (!isLoggedIn) {
				LoginUIControl loginControl = new LoginUIControl(
						shoppingCartWindow, primaryStage, this);
				loginControl.startLogin();
			} else {
				doUpdate();
			}
		}

	}

	public OnlinePurchaseHandler getOnlinePurchaseHandler() {
		return new OnlinePurchaseHandler();
	}

	public RetrieveSavedCartHandler getRetrieveSavedCartHandler() {
		return new RetrieveSavedCartHandler();
	}

	public void updateCart() {
		BrowseSelectData.INSTANCE.updateShoppingCart();
	}

	// ////////Handlers for CatalogListWindow
	private class ViewProductsHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent evt) {

			TableView<CatalogPres> table = CatalogListWindow.getInstance()
					.getTable();
			CatalogPres cat = table.getSelectionModel().getSelectedItem();
			if (cat == null) {
				CatalogListWindow.getInstance().displayError(
						"Please select a row.");
			} else {
				try {
					BrowseSelectData.INSTANCE.setSelectedCatalog(cat);
					CatalogListWindow.getInstance().clearMessages();
					productListWindow = new ProductListWindow(cat);
					List<ProductPres> prods = BrowseSelectData.INSTANCE
							.getProductList(cat);
					productListWindow.setData(FXCollections
							.observableList(prods));
					CatalogListWindow.getInstance().hide();
					productListWindow.show();
				} catch (BackendException e) {
					CatalogListWindow.getInstance().displayError(
							"Unable to display list of products: "
									+ e.getMessage());
				}
			}
		}
	}

	private class BackToPrimaryHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {
			startScreenCallback.clearMessages();
			primaryStage.show();
			CatalogListWindow.getInstance().hide();
		}
	}

	private class CatalogsToCartHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {
			ShoppingCartWindow.INSTANCE.show();
			CatalogListWindow.getInstance().hide();
		}
	}

	public ViewProductsHandler getViewProductsHandler() {
		return new ViewProductsHandler();
	}

	public BackToPrimaryHandler getBackToPrimaryHandler() {
		return new BackToPrimaryHandler();
	}

	public CatalogsToCartHandler getCatalogsToCartHandler() {
		return new CatalogsToCartHandler();
	}

	// /////// Handlers for Product List Window
	private class BackToCatalogListHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {
			CatalogListWindow.getInstance().show();
			productListWindow.hide();
		}
	}

	private class ViewProductDetailsHandler implements
			EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {
			TableView<ProductPres> table = productListWindow.getTable();
			ProductPres prod = table.getSelectionModel().getSelectedItem();
			if (prod == null) {
				productListWindow.displayError("Please select a row.");
			} else {
				BrowseSelectData.INSTANCE.setSelectedProduct(prod);
				productListWindow.clearMessages();
				productDetailsWindow = new ProductDetailsWindow(prod);
				productListWindow.hide();
				productDetailsWindow.show();
			}
		}
	}

	public BackToCatalogListHandler getBackToCatalogListHandler() {
		return new BackToCatalogListHandler();
	}

	public ViewProductDetailsHandler getViewProductDetailsHandler() {
		return new ViewProductDetailsHandler();
	}

	// ////////// ProductDetailsWindow handlers
	private class BackToProductListHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {
			productListWindow.show();
			productDetailsWindow.hide();
		}
	}

	private class AddToCartHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {
			int quant = 1;
			double unitPrice = Double.parseDouble(BrowseSelectData.INSTANCE
					.getSelectedProduct().unitPriceProperty().get());

			// Make saved cart live
			BrowseAndSelectController.INSTANCE.retrieveSavedCart();

			String name = BrowseSelectData.INSTANCE.getSelectedProduct()
					.nameProperty().get();
			CartItemPres cartPres = BrowseSelectData.INSTANCE
					.cartItemPresFromData(name, unitPrice, quant);
			BrowseSelectData.INSTANCE.addToCart(cartPres);

			shoppingCartWindow = ShoppingCartWindow.INSTANCE;
			shoppingCartWindow.setData(BrowseSelectData.INSTANCE.getCartData());
			shoppingCartWindow.setPrimaryStage(primaryStage);
			shoppingCartWindow.show();
			productDetailsWindow.hide();
		}
	}

	public void runShoppingCartRules() {

	}

	public void runQuantityRules(Product product, String quantityRequested)
			throws RuleException, BusinessException {
		BrowseAndSelectController.INSTANCE.runQuantityRules(product,
				quantityRequested);
	}

	public BackToProductListHandler getBackToProductListHandler() {
		return new BackToProductListHandler();
	}

	public AddToCartHandler getAddToCartHandler() {
		return new AddToCartHandler();
	}

	// ////////// Handlers for ShoppingCartWindow

	private class CartContinueHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			try {
				CatalogListWindow window = CatalogListWindow.getInstance(
						primaryStage, FXCollections
								.observableList(BrowseSelectData.INSTANCE
										.getCatalogList()));
				window.clearMessages();
				shoppingCartWindow.hide();
				window.setTableAccessByRow();
				window.show();
			} catch (BackendException e) {
				shoppingCartWindow
						.displayError("Database is unavailable. Please try again later.");
			}
		}
	}

	public CartContinueHandler getCartContinueHandler() {
		return new CartContinueHandler();
	}

	private class SaveCartHandler implements EventHandler<ActionEvent>, Callback {
		@Override
		public void doUpdate() {

			try {

				/*
				 * save live cart with default shipping address, default billing
				 * address, default payment info
				 */
				CustomerSubsystem customerSubsystem = DataUtil
						.readCustFromCache();
				customerSubsystem.saveShoppingCart();
				shoppingCartWindow.show();
				shoppingCartWindow.displayInfo("Saved Cart successfully");

			} catch (BackendException e) {
				shoppingCartWindow.displayInfo("Saving Cart error: "
						+ e.getMessage());
			}
		}

		@Override
		public void handle(ActionEvent evt) {
			boolean isLoggedIn = DataUtil.isLoggedIn();

			if (!isLoggedIn) {
				LoginUIControl loginControl = new LoginUIControl(
						shoppingCartWindow, primaryStage, this);
				loginControl.startLogin();
				shoppingCartWindow.hide();
			} else {
				doUpdate();
			}

		}

		@Override
		public Text getMessageBar() {
			return null;
		}
	}

	public SaveCartHandler getSaveCartHandler() {
		return new SaveCartHandler();
	}

	public void updateCartItems(ObservableList<CartItemPres> items) {
		if (shoppingCartWindow != null) {
			shoppingCartWindow.setData(items);
			TableUtil.refreshTable(shoppingCartWindow.getTable(),
					BrowseSelectData.INSTANCE.getShoppingCartSynchronizer());
		}
	}

}
