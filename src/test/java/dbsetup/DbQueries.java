package dbsetup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;







import performancetests.finalorderstubs.ShoppingCartImpl;
import business.customersubsystem.CustomerSubsystemFacade;
import business.externalinterfaces.Address;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Order;
import business.externalinterfaces.ShoppingCart;
import business.ordersubsystem.OrderImpl;
import middleware.exceptions.DatabaseException;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessUtil;
import middleware.externalinterfaces.DbConfigKey;
import alltests.*;
public class DbQueries {
	static {
		AllTests.initializeProperties();
	}
	static final DbConfigProperties PROPS = new DbConfigProperties();
	static Connection con = null;
	static Statement stmt = null;
	static final String USER = PROPS.getProperty(DbConfigKey.DB_USER.getVal()); 
    static final String PWD = PROPS.getProperty(DbConfigKey.DB_PASSWORD.getVal()); 
    static final String DRIVER = PROPS.getProperty(DbConfigKey.DRIVER.getVal());
    static final int MAX_CONN = Integer.parseInt(PROPS.getProperty(DbConfigKey.MAX_CONNECTIONS.getVal()));
    static final String PROD_DBURL = PROPS.getProperty(DbConfigKey.PRODUCT_DB_URL.getVal());
    static final String ACCT_DBURL = PROPS.getProperty(DbConfigKey.ACCOUNT_DB_URL.getVal());
	static Connection prodCon = null;
	static Connection acctCon = null;
    String insertStmt = "";
	String selectStmt = "";
	
	/* Connection setup */
	static {
		try {
			Class.forName(DRIVER);
		}
		catch(ClassNotFoundException e){
			//debug
			e.printStackTrace();
		}
		try {
			prodCon = DriverManager.getConnection(PROD_DBURL, USER, PWD);
			acctCon = DriverManager.getConnection(ACCT_DBURL, USER, PWD);
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	// just to test this class
	public static void testing() {
		try {
			stmt = prodCon.createStatement();
			stmt.executeQuery("SELECT * FROM Product");
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	//////////////// The public methods to be used in the unit tests ////////////
	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - product id
	 * 2 - product name
	 */
	public static String[] insertProductRow() {
		String[] vals = saveProductSql();
		String query = vals[0];
		try {
			stmt = prodCon.createStatement();
			
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				vals[1] = (new Integer(rs.getInt(1)).toString());
			}
			stmt.close();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return vals;
	}
	
	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - catalog id
	 * 2 - catalog name
	 */
	public static List<Address> readCustAddresses() {
		String query = readAddressesSql();
		List<Address> addressList = new LinkedList<Address>();
		try {
			stmt = acctCon.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			    
                while(rs.next()) {
                    
                    String street = rs.getString("street");
                    String city = rs.getString("city");
                    String state = rs.getString("state");
                    String zip = rs.getString("zip");
                    
             
                    Address addr 
                      = CustomerSubsystemFacade.createAddress(street,city,state,zip,true,true);
                   
                    addressList.add(addr);
                }  
                stmt.close();
                
                    
	            
		}
		catch(SQLException e) {
			e.printStackTrace();
			
		}
		return addressList;
		
	}
	
	public static List<Integer> readAllOrders(Integer custId)
	{
		String query = readOrdersSql(custId);
		List<Integer> orderList = new LinkedList<Integer>();
		try {
			stmt = acctCon.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			    
                while(rs.next()) {
                    
                    int orderId = rs.getInt("orderid");                  
                    orderList.add(orderId);
                }  
                stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
			
		}
		return orderList;
	}
	
	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - catalog id
	 * 2 - catalog name
	 */
	public static String[] insertCatalogRow() {
		String[] vals = saveCatalogSql();
		String query = vals[0];
		try {
			stmt = prodCon.createStatement();
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				vals[1] = (new Integer(rs.getInt(1)).toString());
			}
			stmt.close();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return vals;
	}
	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - customer id
	 * 2 - Street
	 * 3 - City
	 * 4 - State
	 * 5 - Zip
	 */
	public static String[] insertAddressRow() {
		String[] vals = saveAddressSql();
		String query = vals[0];
		try {
			stmt = prodCon.createStatement();
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				vals[1] = (new Integer(rs.getInt(1)).toString());
			}
			stmt.close();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return vals;
	}
	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - customer id
	 * 2 - cust fname
	 * 3 - cust lname
	 */
	public static String[] insertCustomerRow() {
		String[] vals = saveCustomerSql();
		String query = vals[0];
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				vals[1] = (new Integer(rs.getInt(1)).toString());
			}
			stmt.close();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return vals;
	}
	
	/**
	 * Returns a String[] with values:
	 * 0 - query
	 * 1 - order id
	 * 2 - cust id
	 */
	public static String[] insertOrderRow() {
		String[] vals = saveOrderSql();
		String query = vals[0];
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs.next()) {
				vals[1] = (new Integer(rs.getInt(1)).toString());
			}
			stmt.close();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return vals;
	}
	
	public static String[] insertOrderItem(Integer orderId)
	{
		String[] vals = saveOrderItemSql(orderId);
		String query = vals[0];
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
			stmt.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return vals;
	}
	
	public static void deleteOrderRow(Integer orderId)
	{
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(deleteOrderSql(orderId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteOrderItemRow(Integer orderId)
	{
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(deleteOrderItemSql(orderId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteCatalogRow(Integer catId) {
		try {
			stmt = prodCon.createStatement();
			stmt.executeUpdate(deleteCatalogSql(catId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public static void deleteAddressRow(Integer addrId) {
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(deleteAddressSql(addrId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public static void deleteProductRow(Integer prodId) {
		try {
			stmt = prodCon.createStatement();
			stmt.executeUpdate(deleteProductSql(prodId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public static void deleteCustomerRow(Integer custId) {
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(deleteCustomerSql(custId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	///queries
	public static String readAddressesSql() {
		return "SELECT * from altaddress WHERE custid = 1";
	}
	
	public static String readOrdersSql(Integer custId){
		return "SELECT * from ord WHERE custid ="+custId;
	}
	
	public static String[] saveCatalogSql() {
		String[] vals = new String[3];
		
		String name = "testcatalog";
		vals[0] =
		"INSERT into CatalogType "+
		"(catalogid,catalogname) " +
		"VALUES(NULL, '" + name+"')";	  
		vals[1] = null;
		vals[2] = name;
		return vals;
	}
	public static String[] saveAddressSql() {
		String[] vals = new String[6];
		String custid="1";
		String street="testStreet";
		String city="testCity";
		String state="testState";
		String zip="testZip";
		
		vals[0] =
		"INSERT into accountsdb.altaddress "+
		"(custid,street,city,state,zip) " +
		"VALUES('"+custid+"', '" + street+"','"+city+"','"+state+"','"+zip+"')";	  
		vals[1] = null;
		vals[2] = street;
		vals[3] = city;
		vals[4] = state;
		vals[5] = zip;
		return vals;
	}
	public static String[] saveProductSql() {
		String[] vals = new String[3];
		String name = "testprod";
		vals[0] =
		"INSERT into Product "+
		"(productid,productname,totalquantity,priceperunit,mfgdate,catalogid,description) " +
		"VALUES(NULL, '" +
				  name+"',1,1,'12/12/2014',1,'test')";				  
		vals[1] = null;
		vals[2] = name;
		return vals;
	}
	public static String[] saveCustomerSql() {
		String[] vals = new String[4];
		String fname = "testf";
		String lname = "testl";
		vals[0] =
		"INSERT into Customer "+
		"(custid,fname,lname) " +
		"VALUES(NULL,'" +
				  fname+"','"+ lname+"')";
				  
		vals[2] = fname;
		vals[3] = lname;
		return vals;
	}
	
	public static String[] saveOrderSql() {
		String[] vals = new String[4];
		String date = "12/12/3000"; 
		int custid = 1;
		
		vals[0] =
		"INSERT into ord "+
		"(orderid,custid,orderdate) " +
		"VALUES(NULL,"+custid+",'"+date+"')"; 
		vals[1] = null;
		vals[2] = date;
		return vals;
	}
	
	public static String[] saveOrderItemSql(Integer orderId){
		String[] vals = new String[2];
		vals[0] =
		"INSERT into orderitem "+
		"(orderitemid, orderid,productid,quantity,totalprice) " +
		"VALUES(NULL,"+orderId+",1,1,20)";
		return vals;
	}
	
	public static String deleteProductSql(Integer prodId) {
		return "DELETE FROM Product WHERE productid = "+prodId;
	}
	public static String deleteCatalogSql(Integer catId) {
		return "DELETE FROM CatalogType WHERE catalogid = "+catId;
	}
	public static String deleteAddressSql(Integer addrId) {
		return "DELETE FROM altaddress WHERE addressid = "+addrId;
	}
	public static String deleteCustomerSql(Integer custId) {
		return "DELETE FROM Customer WHERE custid = "+custId;
	}
	
	public static String deleteOrderItemSql(Integer orderId)
	{
		return "DELETE FROM orderitem WHERE orderid="+orderId;
	}
	
	public static String deleteOrderSql(Integer orderId)
	{
		return "DELETE FROM ord WHERE orderid = "+orderId;
	}
	
	
	public static void deleteOrder(Integer orderId) {
		try {
			stmt = acctCon.createStatement();
			stmt.executeUpdate(deleteOrderSql(orderId));
			stmt.executeUpdate(deleteOrderItemSql(orderId));
			stmt.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String readSavedCartID(Integer integer){
		String query = readSavedCartSQL(integer);
		String savedCartID = null;
		try {
			stmt = acctCon.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
                while(rs.next()) {
                	savedCartID =  rs.getString("shopcartid");
                }  
                stmt.close();
                
		}
		catch(SQLException e) {
			e.printStackTrace();
			
		}
		return savedCartID;
		
	}
	
	public static String readSavedCartSQL(Integer customerID){
		return "SELECT * from ShopCartTbl WHERE custid = "+customerID;
	}
	
	public static void main(String[] args) {
		readAddressesSql();
		//System.out.println(System.getProperty("user.dir"));
		/*
		String[] results = DbQueries.insertCustomerRow();
		System.out.println("id = " + results[1]);
		DbQueries.deleteCustomerRow(Integer.parseInt(results[1]));
		results = DbQueries.insertCatalogRow();
		System.out.println("id = " + Integer.parseInt(results[1]));
		DbQueries.deleteCatalogRow(Integer.parseInt(results[1]));*/
	}
}
