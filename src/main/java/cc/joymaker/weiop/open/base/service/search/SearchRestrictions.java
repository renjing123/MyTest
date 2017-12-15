package cc.joymaker.weiop.open.base.service.search;

import java.util.HashMap;
import java.util.Map;

public class SearchRestrictions {

	public static SearchExpression eq(String field, Object value) {
		return new SearchTermExpression(field, value);
	}
	
	public static SearchExpression gt(String field, Object value) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchExpression.GT, value);
		return new SearchRangeExpression(field, filters);
	}
	
	public static SearchExpression gte(String field, Object value) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchExpression.GTE, value);
		return new SearchRangeExpression(field, filters);
	}
	
	
	public static SearchExpression lte(String field, Object value) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchExpression.LTE, value);
		return new SearchRangeExpression(field, filters);
	}
	
	public static SearchExpression lt(String field, Object value) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchExpression.LT, value);
		return new SearchRangeExpression(field, filters);
	}
}
