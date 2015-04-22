package business.customersubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

public class DbClassCreditCard implements DbClass {
	private static final Logger LOG = Logger.getLogger(DbClassAddress.class
			.getPackage().getName());
	
	private final String READ_DEFAULT_PAYMENT_INFOR = "READ_DEFAULT_PAYMENT_INFOR";
	
	private DataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();
	private CustomerProfile custProfile;
	private String queryType;
	private String query;
	
	private CreditCard defaultPaymentInfo;
	
	
	public CreditCard getDefaultPaymentInfo() {
		return defaultPaymentInfo;
	}

	@Override
	public void buildQuery() throws DatabaseException {
		
		LOG.info("Query  for " + queryType + ": " + query);
		if (queryType.equals(READ_DEFAULT_PAYMENT_INFOR)) {
			buildReadDefaultPaymentInfo();
		} 
	}

	@Override
	public void populateEntity(ResultSet resultSet) throws DatabaseException {
		if (queryType.equals(READ_DEFAULT_PAYMENT_INFOR)) {
			populateDefaultPaymentInfo(resultSet);
		}
		
	}

	@Override
	public String getDbUrl() {
		DbConfigProperties props = new DbConfigProperties();
		return props.getProperty(DbConfigKey.ACCOUNT_DB_URL.getVal());
	}

	@Override
	public String getQuery() {
		return query;
	}
	
	
	void readDefaultPaymentInfo(CustomerProfile custProfile)
			throws DatabaseException {
		this.custProfile = custProfile;
		queryType = READ_DEFAULT_PAYMENT_INFOR;
		dataAccessSS.atomicRead(this);
	}
	
	
	void buildReadDefaultPaymentInfo() {
		query = "SELECT nameoncard, expdate, cardtype, cardnum "
				+ "FROM Customer "
				+ "WHERE custid = "
				+ custProfile.getCustId();
	}
	
	void populateDefaultPaymentInfo(ResultSet rs) throws DatabaseException{
		try {
			if (rs != null) {
				rs.next();
				if(rs != null){
					defaultPaymentInfo = 
							new CreditCardImpl(rs.getString("nameoncard"), rs.getString("expdate"), rs.getString("cardnum"), rs.getString("cardtype"));
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

}
