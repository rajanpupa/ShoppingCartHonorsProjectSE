package business.productsubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import presentation.gui.GuiUtils;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.util.TwoKeyHashMap;

public class DbClassCatalog implements DbClass {
	@SuppressWarnings("unused")
	private static final Logger LOG = 
		Logger.getLogger(DbClassCatalog.class.getPackage().getName());
	private DataAccessSubsystem dataAccessSS = 
    	new DataAccessSubsystemFacade();
	
	private static TwoKeyHashMap<Integer, String, Catalog> catalogTable;
	private List<Catalog> catalogList;
	private Catalog catalog;
	private int catalogId;
	private String catalogName;
	private String query;
    private String queryType;
    
    private final String LOAD_CAT_TABLE = "LoadCatTable";
    private final String READ_CAT_LIST = "ReadCatList";
    private final String READ_CATALOG = "ReadCatalog";
    private final String SAVE_NEW_CAT = "SaveNewCat";
    private final String DELETE_CAT = "DeleteCat";
    private final String UPDATE_CAT = "UpdateCat";
    
    
    public Integer saveNewCatalog(Catalog catalog) throws DatabaseException {
    	this.catalog = catalog;
    	queryType = SAVE_NEW_CAT;
    	return dataAccessSS.saveWithinTransaction(this);  	
    }
    
    /**
	 * Returns productTable if not null, else db call
	 * 
	 * @return TwoKeyHashMap<Integer, String, Product>
	 * @throws DatabaseException
	 */
	public TwoKeyHashMap<Integer, String, Catalog> readCatalogTable()
			throws DatabaseException {
		if (catalogTable != null) {
			return catalogTable.clone();
		}
		return refreshCatalogTable();
	}
	
	/**
	 * Force a database call
	 */
	public TwoKeyHashMap<Integer, String, Catalog> refreshCatalogTable()
			throws DatabaseException {
		queryType = LOAD_CAT_TABLE;
		dataAccessSS.atomicRead(this);
		
		// Return a clone since catalogTable must not be corrupted
		return catalogTable.clone();
	}
     
	public List<Catalog> readCatalogs() throws DatabaseException{
		queryType = READ_CAT_LIST;
		dataAccessSS.atomicRead(this);
		return catalogList;
	}
	
	/**
	 * Returns a Catalog with id = catalog.id
	 * @param catalog
	 * @return
	 * @throws DatabaseException 
	 */
	public Catalog readCatalog(Integer catalogId) throws DatabaseException{
		if (catalogTable != null && catalogTable.isAFirstKey(catalogId)) {
			return catalogTable.getValWithFirstKey(catalogId);
		}
		queryType = READ_CATALOG;
		this.catalogId = catalogId;
		dataAccessSS.atomicRead(this);
		return catalog;
	}
	
	public void deleteCatalog(Catalog catalog) throws DatabaseException{
		this.catalogId = catalog.getId();
		this.catalog = catalog;
		this.queryType = DELETE_CAT;
		dataAccessSS.deleteWithinTransaction(this);
		
	}
	
	public void updateCatalog(Catalog catalog2) throws DatabaseException {
		this.catalog = catalog2;
		this.queryType = UPDATE_CAT;
		dataAccessSS.saveWithinTransaction(this);
	}
	
	public void buildQuery() throws DatabaseException {
		if(queryType.equals(SAVE_NEW_CAT)) {
			buildSaveQuery();
		}else if(queryType.equals(READ_CAT_LIST)){
			//readCatalogs
			buildCatListQuery();
		}else if(queryType.equals(READ_CATALOG)){
			//readsinglecatalog
			buildReadCatalogQuery();
		}else if(queryType.equals(DELETE_CAT)){
			// delete single catalog
			buildDeleteCatalogQuery();
		}else if(queryType.equals(LOAD_CAT_TABLE)){
			// get the catalog table
			buildCatTableQuery();
		}else if(queryType.equals(UPDATE_CAT)){
			buildUpdateCatalogQuery();
		}
	}
	
	private void buildUpdateCatalogQuery() {
		this.query = String.format("update  catalogtype set catalogname='%s' where catalogid=%d", 
				this.catalog.getName(), this.catalog.getId());
	}

	private void buildDeleteCatalogQuery() {
		this.query = String.format("delete from catalogtype where catalogid = %d", this.catalogId);
	}

	private void buildCatListQuery() {
		this.query = "select catalogid, catalogname from catalogtype";
	}

	private void buildReadCatalogQuery() {
		this.query = String.format("select catalogid, catalogname from catalogtype where catalogid=%d", this.catalogId);
	}

	private void buildCatTableQuery() {
		this.query = "select catalogid, catalogName from catalogtype";
	}

	void buildSaveQuery() throws DatabaseException {
		query = "INSERT into CatalogType "+
				"(catalogid,catalogname) " +
				"VALUES(NULL,'"+catalog.getName()+"')"; 
	}

	public String getDbUrl() {
		DbConfigProperties props = new DbConfigProperties();	
    	return props.getProperty(DbConfigKey.PRODUCT_DB_URL.getVal());
	}

	public String getQuery() {
		return query;
	}

	public void populateEntity(ResultSet resultSet) throws DatabaseException {
		if (queryType.equals(LOAD_CAT_TABLE)) {
			populateCatTable(resultSet);
		} else if (queryType.equals(READ_CATALOG)) {
			populateCatalog(resultSet);
		} else if (queryType.equals(READ_CAT_LIST)) {
			populateCatList(resultSet);
		}
	}

	private void populateCatList(ResultSet rs) throws DatabaseException {
		catalogList = new LinkedList<Catalog>();
		try {
			Integer catalogId = null;
			String catalogName = null;
			while (rs.next()) {
				catalogId = rs.getInt("catalogid");
				catalogName = rs.getString("catalogname");
				Catalog catalog = new CatalogImpl(catalogId,catalogName);
				//Catalog c, Integer pi, String pn, int qa, 
				//double up, LocalDate md, String d
				
				catalogList.add(catalog);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	private void populateCatalog(ResultSet rs) throws DatabaseException {
		try {
			if (rs.next()) {
				CatalogImpl c = new CatalogImpl(rs.getInt("catalogid"), 
						rs.getString("catalogName"));
				
				this.catalog = c;
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * ensure that catalogTable is up to date
	 * @param resultSet
	 * @throws DatabaseException 
	 */
	private void populateCatTable(ResultSet rs) throws DatabaseException {
		catalogTable = new TwoKeyHashMap<Integer, String, Catalog>();
		try {
			Integer catalogId = null;
			String catalogName = null;
			while (rs.next()) {
				catalogName = rs.getString("catalogName");
				catalogId = rs.getInt("catalogid");
				CatalogImpl catalog = new CatalogImpl(catalogId,catalogName);
				catalogTable.put(catalogId, catalogName, catalog);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	
	
}
