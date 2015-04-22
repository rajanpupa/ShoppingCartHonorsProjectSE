package performancetests.finalorderstubs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.DynamicBean;
import business.externalinterfaces.Rules;
import business.externalinterfaces.RulesConfigKey;
import business.externalinterfaces.RulesConfigProperties;
import business.externalinterfaces.RulesSubsystem;
import business.externalinterfaces.ShoppingCart;
import business.rulesbeans.FinalOrderBean;
import business.rulesubsystem.RulesSubsystemFacade;

public class FinalOrderRules implements Rules {
	private HashMap<String,DynamicBean> table;
	private DynamicBean bean;	
	private RulesConfigProperties config = new RulesConfigProperties();
	
	public FinalOrderRules(ShoppingCart shoppingCart){
		bean = new FinalOrderBean(shoppingCart);
	}	
	@Override
	public String getModuleName() {
		// return Module name
		return config.getProperty(RulesConfigKey.FINAL_ORDER_MODULE.getVal());
	}

	@Override
	public String getRulesFile() {
		// return rule file
		return config.getProperty(RulesConfigKey.FINAL_ORDER_RULES_FILE.getVal());
	}

	@Override
	public void prepareData() {
		// prepate data for rules
		table = new HashMap<String,DynamicBean>();		
		String deftemplate = config.getProperty(RulesConfigKey.FINAL_ORDER_DEFTEMPLATE.getVal());
		table.put(deftemplate, bean);
	}

	@Override
	public HashMap<String, DynamicBean> getTable() {
		return table;
	}

	@Override
	public void runRules() throws BusinessException, RuleException {
		// perform Rules
		RulesSubsystem rulesSubsytem = new RulesSubsystemFacade();
		rulesSubsytem.runRules(this);
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
  