package presentation.control;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import presentation.data.CatalogPres;
import presentation.data.DataUtil;
import presentation.data.ManageProductsData;
import presentation.data.ProductPres;
import presentation.gui.AddCatalogPopup;
import presentation.gui.MaintainCatalogsWindow;
import presentation.gui.MaintainProductsWindow;
import business.BusinessConstants;
import business.SessionCache;
import business.exceptions.BackendException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Product;
import business.usecasecontrol.ManageProductsController;

public enum ManageProductsUIControl {
	INSTANCE;

	private final String DATABASE_CONNECTION_ERROR_MESSAGE = "Something is wrong with your database connection";
	private Stage primaryStage;
	private Callback startScreenCallback;
	// private ProductSubsystem prodSubSystem = new ProductSubsystemFacade();
	private ManageProductsController manageProductController = new ManageProductsController();

	// GUIs
	private AddCatalogPopup addCatalogPopup;
	// windows managed by this class
	MaintainCatalogsWindow maintainCatalogsWindow;
	MaintainProductsWindow maintainProductsWindow;

	public void setPrimaryStage(Stage ps, Callback returnMessage) {
		primaryStage = ps;
		startScreenCallback = returnMessage;
	}

	// Manage catalogs
	private class MaintainCatalogsHandler implements EventHandler<ActionEvent>,
			Callback {
		@Override
		public void handle(ActionEvent e) {
			//
			boolean isLoggedIn = DataUtil.isLoggedIn();
			maintainCatalogsWindow = new MaintainCatalogsWindow(primaryStage);
			if (!isLoggedIn) {
				LoginUIControl loginControl = new LoginUIControl(
						maintainCatalogsWindow, primaryStage, this);
				loginControl.startLogin();
			} else {
				doUpdate();
			}
		}

		@Override
		public Text getMessageBar() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void doUpdate() {
			if (isAdmin()) {
				try {
					// TODO Auto-generated method stub
					maintainCatalogsWindow = new MaintainCatalogsWindow(
							primaryStage);
					ObservableList<CatalogPres> list = ManageProductsData.INSTANCE
							.getCatalogList();
					maintainCatalogsWindow.setData(list);
					maintainCatalogsWindow.show();
					primaryStage.hide();
				} catch (BackendException e) {
					//
				}
			} else {
				primaryStage.show();
				startScreenCallback
						.displayError("Logged in User is not authorized to Maintain Catalogs. "
								+ "If you are an administrator, close this window and log in with admin account.");
			}
		}
	}

	private boolean isAdmin() {
		// TODO Auto-generated method stub
		CustomerSubsystem customer = (CustomerSubsystem) SessionCache
				.getInstance().get(BusinessConstants.CUSTOMER);
		return customer.isAdmin();
	}

	public MaintainCatalogsHandler getMaintainCatalogsHandler() {
		return new MaintainCatalogsHandler();
	}

	private class MaintainProductsHandler implements EventHandler<ActionEvent>,
			Callback {
		@Override
		public void handle(ActionEvent e) {
			//
			boolean isLoggedIn = DataUtil.isLoggedIn();
			maintainProductsWindow = new MaintainProductsWindow(primaryStage);
			if (!isLoggedIn) {
				LoginUIControl loginControl = new LoginUIControl(
						maintainProductsWindow, primaryStage, this);
				loginControl.startLogin();
			} else {
				doUpdate();
			}
		}

		@Override
		public Text getMessageBar() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void doUpdate() {
			System.out.println("----------hello ---------------");
			if (isAdmin()) {
				maintainProductsWindow = new MaintainProductsWindow(
						primaryStage);
				CatalogPres selectedCatalog = ManageProductsData.INSTANCE
						.getSelectedCatalog();
				if (selectedCatalog != null) {
					try {
						ObservableList<ProductPres> list = ManageProductsData.INSTANCE
								.getProductsList(selectedCatalog);
						maintainProductsWindow.setData(
								ManageProductsData.INSTANCE.getCatalogList(),
								list);
					} catch (BackendException e) {
						//
					}
				}
				maintainProductsWindow.show();
				primaryStage.hide();

			} else {
				// not authorized
				primaryStage.show();
				startScreenCallback
						.displayError("Logged in User is not authorized to Maintain Products. "
								+ "If you are an administrator, close this window and log in with admin account.");
			}
		}
	}

	public MaintainProductsHandler getMaintainProductsHandler() {
		return new MaintainProductsHandler();
	}

	private class BackButtonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {
			maintainCatalogsWindow.clearMessages();
			maintainCatalogsWindow.hide();
			startScreenCallback.clearMessages();
			primaryStage.show();
		}

	}

	public BackButtonHandler getBackButtonHandler() {
		return new BackButtonHandler();
	}

	private class BackFromProdsButtonHandler implements
			EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {
			maintainProductsWindow.clearMessages();
			maintainProductsWindow.hide();
			startScreenCallback.clearMessages();
			primaryStage.show();
		}

	}

	public BackFromProdsButtonHandler getBackFromProdsButtonHandler() {
		return new BackFromProdsButtonHandler();
	}

	/*
	 * private MenuItem maintainCatalogs() { MenuItem retval = new
	 * MenuItem("Maintain Catalogs"); retval.setOnAction(evt -> {
	 * MaintainCatalogsWindow maintain = new
	 * MaintainCatalogsWindow(primaryStage); ObservableList<CatalogPres> list =
	 * FXCollections.observableList( DefaultData.CATALOG_LIST_DATA);
	 * maintain.setData(list); maintain.show(); primaryStage.hide();
	 * 
	 * }); return retval; } private MenuItem maintainProducts() { MenuItem
	 * retval = new MenuItem("Maintain Products"); retval.setOnAction(evt -> {
	 * MaintainProductsWindow maintain = new
	 * MaintainProductsWindow(primaryStage); ObservableList<Product> list =
	 * FXCollections.observableList(
	 * DefaultData.PRODUCT_LIST_DATA.get(DefaultData.BOOKS_CATALOG));
	 * maintain.setData(DefaultData.CATALOG_LIST_DATA, list); maintain.show();
	 * primaryStage.hide();
	 * 
	 * }); return retval; }
	 */

	private class saveNewCatalogHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent evt) {
			// not yet implemented and used
		}
	}

	public void saveNewCatalog(Catalog catalog) throws BackendException {

		// prodSubSystem.saveNewCatalog(catalog);
		manageProductController.saveNewCatalog(catalog);

	}

	public void deleteCatalog(Catalog catalog) throws BackendException {
		// prodSubSystem.deleteCatalog(catId);
		manageProductController.deleteCatalog(catalog);
	}

	public void deleteProduct(Product product) throws BackendException {
		// prodSubSystem.deleteProduct(prodId);
		manageProductController.deleteProduct(product);
	}
}
