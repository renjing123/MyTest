package cc.joymaker.weiop.open.base.service.search;

import java.util.Map;

public abstract class SearchExpression {

	public static final String EQ = "=";
	public static final String GT = "gt";
	public static final String LT = "lt";
	public static final String GTE = "gte";
	public static final String LTE = "lte";
	public static final String IN = "in";
	
	abstract public Map<String, Map<String, Object>> toFilter();
}
