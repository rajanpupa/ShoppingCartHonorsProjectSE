
package business.usecasecontrol;

import java.util.List;
import java.util.logging.Logger;

import middleware.exceptions.DatabaseException;
import business.exceptions.BackendException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.productsubsystem.ProductSubsystemFacade;


public class ManageProductsController   {
    
    private static final Logger LOG = 
    	Logger.getLogger(ManageProductsController.class.getName());
    private ProductSubsystem productSubSystem = new ProductSubsystemFacade();
    
    public List<Product> getProductsList(String catalog) throws BackendException {
    	ProductSubsystem pss = new ProductSubsystemFacade();    	
    	//return pss.getProductList(catalog);
    	return null;
    }
    
    
    public void deleteProduct(Product product) throws BackendException {
    	//implement
    	productSubSystem.deleteProduct(product);
    }
    
    public Integer saveNewProduct(Product product) throws BackendException {
		// TODO Auto-generated method stub
		return productSubSystem.saveNewProduct(product);
	}


	public Integer saveNewCatalog(Catalog catalog) throws BackendException {
		// TODO Auto-generated method stub
		return productSubSystem.saveNewCatalog(catalog);
	}


	public void deleteCatalog(Catalog catalog) throws BackendException{
		// TODO Auto-generated method stub
		productSubSystem.deleteCatalog(catalog);
	}


	public List<Catalog> getCatalogList() throws BackendException{
		// TODO Auto-generated method stub
		return productSubSystem.getCatalogList();
	}


	public List<Product> getProductList(Catalog catalog) throws BackendException {
		// TODO Auto-generated method stub
		return productSubSystem.getProductList(catalog);
	}


	public void updateCatalog(Catalog catalog) throws BackendException {
		productSubSystem.updateCatalog(catalog);
		
	}


	public void updateProduct(Product product) throws BackendException {
		productSubSystem.updateProduct(product);
	}


	public Catalog getCatalogFromName(String text) throws BackendException {
		// TODO Auto-generated method stub
		return productSubSystem.getCatalogFromName(text);
	}
    
    
}
