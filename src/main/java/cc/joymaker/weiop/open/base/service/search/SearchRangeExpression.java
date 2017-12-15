package cc.joymaker.weiop.open.base.service.search;

import java.util.HashMap;
import java.util.Map;

public class SearchRangeExpression extends SearchExpression {

	private final String propertyName;

	private final Map<String, Object> filters;

	public SearchRangeExpression(String propertyName, Map<String, Object> filters) {
		this.propertyName = propertyName;
		this.filters = filters;
	}

	public Map<String, Map<String, Object>> toFilter() {
		Map<String, Map<String, Object>> map = new HashMap<>();
		Map<String, Object> cond = new HashMap<>();
		cond.put(propertyName, filters);
		map.put("range", cond);
		return map;
	}

}
