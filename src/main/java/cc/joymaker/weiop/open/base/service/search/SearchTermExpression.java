package cc.joymaker.weiop.open.base.service.search;

import java.util.HashMap;
import java.util.Map;

public class SearchTermExpression extends SearchExpression {

	private final String propertyName;

	private final Object value;

	public SearchTermExpression(String propertyName, Object value) {
		this.propertyName = propertyName;
		this.value = value;
	}

	public Map<String, Map<String, Object>> toFilter() {
		Map<String, Map<String, Object>> map = new HashMap<>();
		Map<String, Object> cond = new HashMap<>();
		cond.put(propertyName, value);
		map.put("term", cond);
		return map;
	}

}
