package business.shoppingcartsubsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.DynamicBean;
import business.externalinterfaces.Rules;
import business.externalinterfaces.RulesConfigKey;
import business.externalinterfaces.RulesConfigProperties;
import business.externalinterfaces.RulesSubsystem;
import business.externalinterfaces.ShoppingCart;
import business.rulesbeans.PaymentBean;
import business.rulesbeans.ShopCartBean;
import business.rulesubsystem.RulesSubsystemFacade;

public class ShoppingCartRules implements Rules {
	
	private HashMap<String,DynamicBean> table;
	private DynamicBean bean;	
	private RulesConfigProperties config = new RulesConfigProperties();
	
	public ShoppingCartRules(ShoppingCart shoppingCart){
		bean = new ShopCartBean(shoppingCart);
	}	
	
	@Override
	public String getModuleName() {
		// return mmodule name
		return config.getProperty(RulesConfigKey.SHOPCART_MODULE.getVal());
	}

	@Override
	public String getRulesFile() {
		// get rule file
		return config.getProperty(RulesConfigKey.SHOPCART_RULES_FILE.getVal());
	}

	@Override
	public void prepareData() {
		table = new HashMap<String,DynamicBean>();		
		String deftemplate = config.getProperty(RulesConfigKey.SHOPCART_DEFTEMPLATE.getVal());
		table.put(deftemplate, bean);
	}

	@Override
	public HashMap<String, DynamicBean> getTable() {
		return table;
	}

	@Override
	public void runRules() throws BusinessException, RuleException {
		RulesSubsystem rules = new RulesSubsystemFacade();
    	rules.runRules(this);
	}

	@Override
	public void populateEntities(List<String> updates) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<?> getUpdates() {
		// TODO Auto-generated method stub
		return new ArrayList<Object>();
	}

}
