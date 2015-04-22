package business.productsubsystem;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;



import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import middleware.exceptions.DatabaseException;
import business.exceptions.BackendException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.util.TwoKeyHashMap;

@Service("productsubsystem")
public class ProductSubsystemFacade implements ProductSubsystem {
	private static final Logger LOG = Logger.getLogger(ProductSubsystemFacade.class
			.getPackage().getName());
	
	public static Catalog createCatalog(int id, String name) {
		return new CatalogImpl(id, name);
	}
	public static Product createProduct(Catalog c, String name, 
			LocalDate date, int numAvail, double price) {
		return new ProductImpl(c, name, date, numAvail, price);
	}
	public static Product createProduct(Catalog catalog, Integer productId, String productName, int quantityAvailable, 
			double unitPrice, LocalDate mfgDate, String desc) {
		return new ProductImpl(catalog, productId, productName, quantityAvailable, unitPrice, mfgDate, desc);
	}
	
	/** obtains product for a given product name */
    public Product getProductFromName(String prodName) throws BackendException {
    	try {
			DbClassProduct dbclass = new DbClassProduct();
			Product p = dbclass.readProduct(getProductIdFromName(prodName));
			LOG.log(Level.INFO, "product %s being returned from ProductName %s ", new String[]{p.toString(), prodName});
			return p;
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Database exception occured while trying to fetch product by productName %s", prodName);
			throw new BackendException(e);
		}	
    }
    
    /**
     *  Reads the products table, returns id of product
     */
    public Integer getProductIdFromName(String prodName) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			TwoKeyHashMap<Integer,String,Product> table = dbclass.readProductTable();
			Integer id = table.getFirstKey(prodName);
			LOG.log(Level.FINE, "id %s being returned for productName %s", new String[]{id.toString(), prodName});
			return id;
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Database exception occured while trying to fetch productId by productName %s", prodName);
			throw new BackendException(e);
		}
		
	}
    
    /**
     * Return the product with the productId=prodId, from database
     */
    public Product getProductFromId(Integer prodId) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			Product prod = dbclass.readProduct(prodId);
			if(prod != null){
				LOG.log(Level.FINE, "Product %s being returned for productId %s", new String[]{prod.toString(), prodId.toString()});
			}
			return prod;
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Database exception occured while trying to fetch product by id %d", prodId);
			throw new BackendException(e);
		}
	}
    
    /**
     * Reads the list of Catalogs from db
     */
    public List<Catalog> getCatalogList() throws BackendException {
    	try {
			DbClassCatalogTypes dbClass = new DbClassCatalogTypes();
			LOG.log(Level.FINE, "Returning List<Catalog>");
			return dbClass.getCatalogTypes().getCatalogs();
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Exception while trying to fetch catalogList");
			throw new BackendException(e);
		}
		
    }
    
    /**
     * Reads the list of products of belonging to the catalog from db
     */
    public List<Product> getProductList(Catalog catalog) throws BackendException {
    	try {
    		DbClassProduct dbclass = new DbClassProduct();
    		LOG.log(Level.INFO, "Returning List<Product> from database");
    		return dbclass.readProductList(catalog);
    	} catch(DatabaseException e) {
    		LOG.log(Level.SEVERE, "Could not return the product list. " + e.getMessage());
    		throw new BackendException(e);
    	}
    }
	
	public int readQuantityAvailable(Product product) throws BackendException {
		//IMPLEMENT
		Product prod = getProductFromId(product.getProductId());
		return prod.getQuantityAvail();
		//return 5;
	}

	@Override
	public Integer saveNewProduct(Product product) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			Integer prodId = dbclass.saveNewProduct(product);
			LOG.log(Level.FINE,
					"saved product %s with id generated %s in db",
					new String[] { product.getProductName(), prodId.toString() });
			return prodId;
		} catch (DatabaseException e) {
			LOG.log(Level.SEVERE, "Exception occured while saving the product "
					+ product);
			throw new BackendException(e);
		}
	}
	@Override
	public void deleteProduct(Product product) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			dbclass.deleteProduct(product);
			LOG.log(Level.FINE, "Deleted product %s from database.", product);
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Could not delete the product : %s. database exception", product);
			throw new BackendException(e);
		}
	}
	@Override
	public Catalog getCatalogFromName(String catName) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			Catalog cat = dbclass.readCatalog(getCatalogIdFromName(catName));
			LOG.log(Level.FINE, "Returning Catalog %s from name %s", new String[]{cat.toString(), catName});
			return cat;
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Error trying to search for catalog %s", catName);
			throw new BackendException(e);
		}
	}
	@Override
	public Catalog getCatalogFromId(Integer catId) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			Catalog cat = dbclass.readCatalog(catId);
			LOG.log(Level.FINE, "returning catalog %s from catId %s", new String[]{cat.toString(), catId.toString()});
			return cat;
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Could not find Catalog from catId %d", catId );
			throw new BackendException(e);
		}
	}
	@Override
	public Integer getCatalogIdFromName(String catName) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			TwoKeyHashMap<Integer, String, Catalog> table = dbclass.readCatalogTable();
			return table.getFirstKey(catName);
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Error trying to get catalogId from catName %s", catName);
			throw new BackendException(e);
		}
	}
	@Override
	public Integer saveNewCatalog(Catalog catalog) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			Integer id=dbclass.saveNewCatalog(catalog);
			LOG.log(Level.FINE, "catalog %s saved successfully", catalog);
			return id;
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Error trying to save catalog %s", catalog);
			throw new BackendException(e);
		}
	}
	@Override
	public void deleteCatalog(Catalog catalog) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			dbclass.deleteCatalog(catalog);
			LOG.log(Level.FINE, "Catalog %s deleted", catalog);
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Error trying to save catalog %s", catalog);
			throw new BackendException(e);
		}
	}
	@Override
	public void updateProduct(Product product) throws BackendException {
		try {
			DbClassProduct dbclass = new DbClassProduct();
			dbclass.updateProduct(product);
			LOG.log(Level.FINE, "Product %s updated", product);
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Error trying to update product %s", product);
			throw new BackendException(e);
		}
	}
	@Override
	public void updateCatalog(Catalog catalog) throws BackendException {
		try {
			DbClassCatalog dbclass = new DbClassCatalog();
			dbclass.updateCatalog(catalog);
			LOG.log(Level.FINE, "Catalog %s updated", catalog);
		} catch(DatabaseException e) {
			LOG.log(Level.SEVERE, "Error trying to update catalog %s", catalog);
			throw new BackendException(e);
		}
	}
}
