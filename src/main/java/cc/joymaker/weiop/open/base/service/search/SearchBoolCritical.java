package cc.joymaker.weiop.open.base.service.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import cc.joymaker.weiop.open.base.utils.JsonUtils;

public class SearchBoolCritical {

	private int start = 0;
	private int size = 100;

	private List<SearchExpression> must = new ArrayList<>();

	private List<SearchExpression> mustNot = new ArrayList<>();

	private List<SearchExpression> should = new ArrayList<>();
	
	private List<SearchExpression> filters = new ArrayList<>();

	private List<SearchOrder> order = new ArrayList<>();

	public void setStart(int start) {
		this.start = start;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void add(SearchExpression ex) {
		filters.add(ex);
	}
	
	public void addMust(SearchExpression ex) {
		must.add(ex);
	}

	public void addMustNot(SearchExpression ex) {
		mustNot.add(ex);
	}

	public void addShould(SearchExpression ex) {
		should.add(ex);
	}
	
	public void addOrder(SearchOrder od) {
		order.add(od);
	}

	public String toQueryString() {

		List<Object> mustGroup = getCondition("must", must);
		List<Object> mustNotGroup = getCondition("must_not", mustNot);
		List<Object> shouldGroup = getCondition("should", should);
		Map<String, Object> bool = new LinkedHashMap<>();
		if (mustGroup.size() > 0) {
			bool.put("must", mustGroup);
		} else {
			Map<String, Map<String, String>> matchAll = new HashMap<>();
			matchAll.put("match_all", new HashMap<String, String>());
			bool.put("must", matchAll);
		}
		if (mustNotGroup.size() > 0)
			bool.put("must_not", mustNotGroup);
		if (shouldGroup.size() > 0)
			bool.put("should", shouldGroup);
		
		if (filters.size() > 0) {
			bool.put("filter", getCondition("filter", filters));
		}
		
		Map<String, Object> root = new HashMap<>();
		Map<String, Object> node = new HashMap<>();
		Map<String, Object> query = new HashMap<>();
		root.put("query", query);
		query.put("bool", bool);
		
		root.put("from", start);
		root.put("size", size);
		if (!order.isEmpty()) {
			Map<String, Map<String, String>> orders = new LinkedHashMap<>();
			for (SearchOrder s : order) {
				// 处理排序
				Map<String, String> orderProperty= new LinkedHashMap<>();
				orderProperty.put("order", s.getOrder());
				orders.put(s.getPropertyName(), orderProperty);
			}
			node.put("sort", orders);
		}
		return JsonUtils.formBean(root);
	}

	private List<Object> getCondition(String conn, List<SearchExpression> expressions) {
		List<Object> conditions = new ArrayList<>();
		for (SearchExpression ex : expressions) {
			conditions.add(ex.toFilter());
		}
		return conditions;
	}
	
	public static void main(String[] args) {
		SearchBoolCritical c = new SearchBoolCritical();
		c.add(SearchRestrictions.eq("orderid", "TZFPW8SE061256232494"));
		c.add(SearchRestrictions.lte("createTime", new Date()));
		c.addOrder(SearchOrder.desc("createTime"));
		System.out.println(c.toQueryString());
	}
}
