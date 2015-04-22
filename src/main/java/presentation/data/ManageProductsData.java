package presentation.data;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import launch.Start;
import presentation.gui.GuiUtils;
import business.exceptions.BackendException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.productsubsystem.ProductSubsystemFacade;
import business.usecasecontrol.ManageProductsController;

public enum ManageProductsData {
	INSTANCE;
	
	@Inject
	ProductSubsystem pss =(ProductSubsystem) Start.ctx.getBean("productsubsystem");
	
	//private ProductSubsystemFacade pss = new ProductSubsystemFacade();//dont use this directly here
	private ManageProductsController manageProductController = new ManageProductsController();
	
	private CatalogPres defaultCatalog = readDefaultCatalogFromDataSource();
	private CatalogPres readDefaultCatalogFromDataSource() {
		return DefaultData.CATALOG_LIST_DATA.get(0);
	}
	public CatalogPres getDefaultCatalog() {
		return defaultCatalog;
	}
	
	private CatalogPres selectedCatalog = defaultCatalog;
	public void setSelectedCatalog(CatalogPres selCatalog) {
		selectedCatalog = selCatalog;
	}
	public CatalogPres getSelectedCatalog() {
		return selectedCatalog;
	}
	//////////// Products List model
	private ObservableMap<CatalogPres, List<ProductPres>> productsMap
	   = readProductsFromDataSource();
	
	/** Initializes the productsMap 
	 * @throws BackendException */
	private ObservableMap<CatalogPres, List<ProductPres>> readProductsFromDataSource() {
		//return DefaultData.PRODUCT_LIST_DATA;
		ObservableMap<CatalogPres, List<ProductPres>> om= FXCollections.observableHashMap();
		try {
			for (Catalog catalog : manageProductController.getCatalogList()) {
				CatalogPres cp = new CatalogPres();
				cp.setCatalog(catalog);
				List<ProductPres> ppl = new ArrayList<ProductPres>();

				for (Product p : manageProductController.getProductList(catalog)) {
					ProductPres pp = new ProductPres();
					pp.setProduct(p);
					ppl.add(pp);
				}
				
				om.put(cp, ppl);
			}
		}
		catch(BackendException be){
			//log something here
			
		}
		
		return om;
	}
	
	/**
	 * forces database read of data
	 */
	public void refreshData(){
		readProductsFromDataSource();
	}
	/** Delivers the requested products list to the UI */
	public ObservableList<ProductPres> getProductsList(CatalogPres catPres) {
		return FXCollections.observableList(productsMap.get(catPres));
	}
	
	public ProductPres productPresFromData(Catalog c, String name, String date,  //MM/dd/yyyy 
			int numAvail, double price) {
		
		Product product = ProductSubsystemFacade.createProduct(c, name, 
				GuiUtils.localDateForString(date), numAvail, price);
		ProductPres prodPres = new ProductPres();
		prodPres.setProduct(product);
		return prodPres;
	}
	
	public void addToProdList(CatalogPres catPres, ProductPres prodPres) throws BackendException {
		ObservableList<ProductPres> newProducts = FXCollections
				.observableArrayList(prodPres);
		List<ProductPres> specifiedProds = productsMap.get(catPres);

		// Place the new item at the bottom of the list
		Product newProd = prodPres.getProduct();
		int newId = manageProductController.saveNewProduct(newProd);
		newProd.setProductId(newId);
		specifiedProds.addAll(newProducts);
	}
	
	/** This method looks for the 0th element of the toBeRemoved list 
	 *  and if found, removes it. In this app, removing more than one product at a time
	 *  is  not supported.
	 */
	public boolean removeFromProductList(CatalogPres cat, ObservableList<ProductPres> toBeRemoved) {
		if(toBeRemoved != null && !toBeRemoved.isEmpty()) {
			try {
				ProductPres productPres = toBeRemoved.get(0);
				Product toBeDeletedProduct = productPres.getProduct();
				boolean result = productsMap.get(cat)
						.remove(productPres);
				// delegate the product to the manageProductController, so it
				// could be deleted from db
				manageProductController.deleteProduct(toBeDeletedProduct);
				
				System.out.println("===========>product deleted is : " + toBeDeletedProduct.getProductId());
				return result;
			}catch(BackendException be){
				// Log something here
				System.out.println("=========================" + "could not remove the product from the list");
			}
		}
		return false;
	}
		
	//////// Catalogs List model
	//private ObservableList<CatalogPres> catalogList = readCatalogsFromDataSource();

	/** Initializes the catalogList 
	 * @throws BackendException */
	private ObservableList<CatalogPres> readCatalogsFromDataSource() throws BackendException {
		//return 
		//FXCollections.observableList(DefaultData.CATALOG_LIST_DATA);
		List<Catalog> catalogList = pss.getCatalogList();
		List<CatalogPres> catPresList = new ArrayList<CatalogPres>();
		for(Catalog c : catalogList){
			catPresList.add(new CatalogPres(c));
		}
		return FXCollections.observableList(catPresList);
	}

	/** Delivers the already-populated catalogList to the UI 
	 * @throws BackendException */
	public ObservableList<CatalogPres> getCatalogList() throws BackendException {
		return readCatalogsFromDataSource();
	}

	public CatalogPres catalogPresFromData(int id, String name) {
		Catalog cat = ProductSubsystemFacade.createCatalog(id, name);
		CatalogPres catPres = new CatalogPres();
		catPres.setCatalog(cat);
		return catPres;
	}

	public void addToCatalogList(CatalogPres catPres) throws BackendException {
		ObservableList<CatalogPres> newCatalogs = FXCollections
				.observableArrayList(catPres);

		// Place the new item at the bottom of the list
		// catalogList is guaranteed to be non-null
		ObservableList<CatalogPres> catalogList = readCatalogsFromDataSource();
		manageProductController.saveNewCatalog(catPres.getCatalog());
		boolean result = catalogList.addAll(newCatalogs);
		if(result) { //must make this catalog accessible in productsMap
			productsMap.put(catPres, FXCollections.observableList(new ArrayList<ProductPres>()));
		}
	}

	/**
	 * This method looks for the 0th element of the toBeRemoved list in
	 * catalogList and if found, removes it. In this app, removing more than one
	 * catalog at a time is not supported.
	 * 
	 * This method also updates the productList by removing the products that
	 * belong to the Catalog that is being removed.
	 * 
	 * Also: If the removed catalog was being stored as the selectedCatalog,
	 * the next item in the catalog list is set as "selected"
	 * @throws BackendException 
	 */
	public boolean removeFromCatalogList(ObservableList<CatalogPres> toBeRemoved) throws BackendException {
		boolean result = false;
		CatalogPres item = toBeRemoved.get(0);
		ObservableList<CatalogPres> catalogList = readCatalogsFromDataSource();
		if (toBeRemoved != null && !toBeRemoved.isEmpty()) {
			result = catalogList.remove(item);
			//delegate the catalog to be deleted to the manageProductController from here, from database
			manageProductController.deleteCatalog(item.getCatalog());
			
			System.out.println("===========>catalog deleted is : " + item.getCatalog().getId());
		}
		if(item.equals(selectedCatalog)) {
			if(!catalogList.isEmpty()) {
				selectedCatalog = catalogList.get(0);
			} else {
				selectedCatalog = null;
			}
		}
		if(result) {//update productsMap
			productsMap.remove(item);
		}
		return result;
	}
	
	//Synchronizers
	public class ManageProductsSynchronizer implements Synchronizer {
		@SuppressWarnings("rawtypes")
		@Override
		public void refresh(ObservableList list) {
			productsMap.put(selectedCatalog, list);
		}
	}
	public ManageProductsSynchronizer getManageProductsSynchronizer() {
		return new ManageProductsSynchronizer();
	}
	
	private class ManageCatalogsSynchronizer implements Synchronizer {
		@SuppressWarnings("rawtypes")
		@Override
		public void refresh(ObservableList list) throws BackendException {
			ObservableList<CatalogPres> catalogList = readCatalogsFromDataSource();
			catalogList = list;
		}
	}
	public ManageCatalogsSynchronizer getManageCatalogsSynchronizer() {
		return new ManageCatalogsSynchronizer();
	}
	public void editCatalog(CatalogPres instance2) throws BackendException {
		manageProductController.updateCatalog(instance2.getCatalog());
	}
	public void editProduct(ProductPres instance2) throws BackendException {
		manageProductController.updateProduct(instance2.getProduct());
	}
}
