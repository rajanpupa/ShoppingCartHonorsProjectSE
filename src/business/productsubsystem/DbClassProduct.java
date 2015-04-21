package business.productsubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;
import presentation.gui.GuiUtils;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductFromGui;
import business.util.TwoKeyHashMap;

class DbClassProduct implements DbClass {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DbClassProduct.class
			.getPackage().getName());
	private DataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();

	/**
	 * The productTable matches product ID with Product object. It is static so
	 * that requests for "read product" based on product ID can be handled
	 * without extra db hits
	 */
	private static TwoKeyHashMap<Integer, String, Product> productTable;
	private String queryType;
	private String query;
	private Product product;
	private String description;
	private List<Product> productList;
	Catalog catalog;
	Integer productId;

	private final String LOAD_PROD_TABLE = "LoadProdTable";
	private final String READ_PRODUCT = "ReadProduct";
	private final String READ_PROD_LIST = "ReadProdList";
	private final String SAVE_NEW_PROD = "SaveNewProd";
	private final String DELETE_PROD = "DeleteProd";
	private final String UPDATE_PROD = "UpdateProd";

	public void buildQuery() {
		if (queryType.equals(LOAD_PROD_TABLE)) {
			buildProdTableQuery();
		} else if (queryType.equals(READ_PRODUCT)) {
			buildReadProductQuery();
		} else if (queryType.equals(READ_PROD_LIST)) {
			buildProdListQuery();
		} else if (queryType.equals(SAVE_NEW_PROD)) {
			buildSaveNewQuery();
		} else if(queryType.equals(DELETE_PROD)){
			buildDeleteProductQuery();
		} else if(queryType.equals(UPDATE_PROD)){
			buildUpdateProductQuery();
		}

	}

	private void buildUpdateProductQuery() {
		query = String.format(" update product set "
				+ "productName='%s', "
				+ "totalquantity=%d, "
				+ "priceperunit=%f,"
				+ "mfgdate='%s', "
				+ "description='%s' "
				+ "where productid=%d ", 
				this.product.getProductName(),
				this.product.getQuantityAvail(),
				this.product.getUnitPrice(),
				DateTimeFormatter.ofPattern("MM/dd/yyyy").format(this.product.getMfgDate()),
				this.product.getDescription(),
				this.product.getProductId());
	}

	private void buildDeleteProductQuery() {
		query = String.format(" delete from product where productid = '%d' ", this.productId);
	}

	private void buildProdTableQuery() {
		query = "SELECT * FROM product";
	}

	private void buildProdListQuery() {
		query = "SELECT * FROM Product WHERE catalogid = " + catalog.getId();
	}

	private void buildReadProductQuery() {
		query = "SELECT * FROM Product WHERE productid = " + productId;
	}

	private void buildSaveNewQuery() {
		int catalogId = product.getCatalog().getId();
		String name = product.getProductName();
		int totalQuantity = product.getQuantityAvail();
		double pricePerUnit = product.getUnitPrice();
		LocalDate date = product.getMfgDate();
		String description = product.getDescription();
		
		query = String.format(
					"insert into "
					+ " product (productid, catalogid, productname, totalquantity, priceperunit, mfgdate, description) "
					+ " values (null, %d, '%s', %d, %f, '%s', '%s') "
					 ,catalogId, name, totalQuantity, pricePerUnit, DateTimeFormatter.ofPattern("MM/dd/yyyy").format(date), description 
				 );
	}

	/**
	 * Returns productTable if not null, else db call
	 * 
	 * @return TwoKeyHashMap<Integer, String, Product>
	 * @throws DatabaseException
	 */
	public TwoKeyHashMap<Integer, String, Product> readProductTable()
			throws DatabaseException {
		if (productTable != null) {
			return productTable.clone();
		}
		return refreshProductTable();
	}

	/**
	 * Force a database call
	 */
	public TwoKeyHashMap<Integer, String, Product> refreshProductTable()
			throws DatabaseException {
		queryType = LOAD_PROD_TABLE;
		dataAccessSS.atomicRead(this);
		
		// Return a clone since productTable must not be corrupted
		return productTable.clone();
	}

	public List<Product> readProductList(Catalog cat)
			throws DatabaseException {
		if (productList == null) {
			return refreshProductList(cat);
		}
		return Collections.unmodifiableList(productList);
	}

	public List<Product> refreshProductList(Catalog cat)
			throws DatabaseException {
		this.catalog = cat;
		queryType = READ_PROD_LIST;
		dataAccessSS.atomicRead(this);
		return productList;
	}

	public Product readProduct(Integer productId)
			throws DatabaseException {
		if (productTable != null && productTable.isAFirstKey(productId)) {
			return productTable.getValWithFirstKey(productId);
		}
		queryType = READ_PRODUCT;
		this.productId = productId;
		dataAccessSS.atomicRead(this);
		return product;
	}
	

	/**
	 * Database columns: productid, productname, totalquantity, priceperunit,
	 * mfgdate, catalogid, description
	 */
	public void saveNewProduct(ProductFromGui product, Integer catalogid,
			String description) throws DatabaseException {
		//implement
	}
	
	public Integer saveNewProduct(Product product) throws DatabaseException {

		this.product = product;
		this.queryType = SAVE_NEW_PROD;
		return dataAccessSS.saveWithinTransaction(this);
		//dataAccessSS.commit();

	}
	
	public Integer deleteProduct(Product product) throws DatabaseException {
		this.productId = product.getProductId();
		this.product = product;
		this.queryType = DELETE_PROD;
		int rowcount = dataAccessSS.deleteWithinTransaction(this);

		System.out.println("Number of rows deleted = " + rowcount);
		return rowcount;
	}

	public void populateEntity(ResultSet resultSet) throws DatabaseException {
		if (queryType.equals(LOAD_PROD_TABLE)) {
			populateProdTable(resultSet);
		} else if (queryType.equals(READ_PRODUCT)) {
			populateProduct(resultSet);
		} else if (queryType.equals(READ_PROD_LIST)) {
			populateProdList(resultSet);
		}
	}
	
	

	private void populateProdList(ResultSet rs) throws DatabaseException {
		productList = new LinkedList<Product>();
		try {
			Product product = null;
			Integer prodId = null;
			String productName = null;
			Integer quantityAvail = null;
			Double unitPrice = null;
			String mfgDate = null;
			Integer catalogId = null;
			String description = null;
			while (rs.next()) {
				prodId = rs.getInt("productid");
				productName = rs.getString("productname");
				quantityAvail = rs.getInt("totalquantity");
				unitPrice =rs.getDouble("priceperunit");
				mfgDate = rs.getString("mfgdate");
				catalogId = rs.getInt("catalogid");
				description = rs.getString("description");
				CatalogImpl catalog = new CatalogImpl(catalogId, 
						(new CatalogTypesImpl()).getCatalogName(catalogId));
				//Catalog c, Integer pi, String pn, int qa, 
				//double up, LocalDate md, String d
				product = new ProductImpl(catalog, prodId, productName, quantityAvail,
						 unitPrice, GuiUtils.localDateForString(mfgDate),
					    description);
				productList.add(product);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Internal method to ensure that product table is up to date.
	 */
	private void populateProdTable(ResultSet rs) throws DatabaseException {
		productTable = new TwoKeyHashMap<Integer, String, Product>();
		try {
			Product product = null;
			Integer prodId = null;
			String productName = null;
			Integer quantityAvail = null;
			Double unitPrice = null;
			String mfgDate = null;
			Integer catalogId = null;
			String description = null;
			while (rs.next()) {
				prodId = rs.getInt("productid");
				productName = rs.getString("productname");
				quantityAvail = rs.getInt("totalquantity");
				unitPrice = rs.getDouble("priceperunit");
				mfgDate = rs.getString("mfgdate");
				catalogId = rs.getInt("catalogid");
				description = rs.getString("description");
				CatalogImpl catalog = new CatalogImpl(catalogId, 
						(new CatalogTypesImpl()).getCatalogName(catalogId));
				product = new ProductImpl(catalog, prodId, productName, quantityAvail,
						unitPrice, GuiUtils.localDateForString(mfgDate), description);
				productTable.put(prodId, productName, product);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	private void populateProduct(ResultSet rs) throws DatabaseException {
		try {
			if (rs.next()) {
				CatalogImpl catalog = new CatalogImpl(rs.getInt("catalogid"), 
						(new CatalogTypesImpl()).getCatalogName(rs.getInt("catalogid")));
				
				product = new ProductImpl(catalog, rs.getInt("productid"),
						rs.getString("productname"),
						rs.getInt("totalquantity"),
						rs.getDouble("priceperunit"),
						GuiUtils.localDateForString(rs.getString("mfgdate")),
						rs.getString("description"));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	public String getDbUrl() {
		DbConfigProperties props = new DbConfigProperties();
		return props.getProperty(DbConfigKey.PRODUCT_DB_URL.getVal());
	}

	public String getQuery() {
		return query;
	}
	
	public static void main(String[] args) {
		//ProductImpl(Catalog c, String name, LocalDate date, int numAvail, double price)
		Catalog c = new CatalogImpl(3, "abc");
		Product p = new ProductImpl(c, "acb Prod1", GuiUtils.localDateForString("07/20/1989"), 10, 10.50);
		p.setDescription("jeans for all");
		
		DbClassProduct d = new DbClassProduct();
		try {
			d.saveNewProduct(p);
			System.out.println(d.getQuery());//check this query
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//delete the product
		try{
			d.deleteProduct(p);
			System.out.println(d.getQuery());
		} catch(DatabaseException e){
			e.printStackTrace();
		}
	}

	public void updateProduct(Product product2) throws DatabaseException {
		this.product = product2;
		this.queryType = UPDATE_PROD;
		dataAccessSS.saveWithinTransaction(this);
	}
}
