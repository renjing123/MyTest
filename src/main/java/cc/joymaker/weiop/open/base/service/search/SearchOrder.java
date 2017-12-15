package cc.joymaker.weiop.open.base.service.search;

public class SearchOrder {
	
	private static final String ASC = "asc";
	private static final String DESC = "desc";

	public String propertyName;
	
	private String order;
	
	private SearchOrder(String propertyName, String order) {
		this.propertyName = propertyName;
		this.order = order;
	}
	
	public static SearchOrder asc(String propertyName) {
		return new SearchOrder(propertyName, ASC);
	}
	
	public static SearchOrder desc(String propertyName) {
		return new SearchOrder(propertyName, DESC);
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public String getOrder() {
		return order;
	}
}
