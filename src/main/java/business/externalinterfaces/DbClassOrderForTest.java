package business.externalinterfaces;
import java.util.List;

import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DbClass;
public interface DbClassOrderForTest extends DbClass{
	List<Integer> getAllOrderIds(CustomerProfile custProfile) throws DatabaseException;
}
