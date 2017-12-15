package cc.joymaker.weiop.open.base.tablestore;

public class TableStoreException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3369236155374952392L;
	private int code;

	public int getCode() {
		return code;
	}
	
	public TableStoreException() {
		super();
	}
	
	public TableStoreException(int code , String msg) {
		this(code, msg, null);
	}
	
	public TableStoreException(int code, String msg, Throwable t) {
		super(msg, t);
		this.code = code;
	}
}
