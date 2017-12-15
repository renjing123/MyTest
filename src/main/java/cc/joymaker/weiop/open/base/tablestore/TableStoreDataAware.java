package cc.joymaker.weiop.open.base.tablestore;

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKeyColumn;

public interface TableStoreDataAware {

	void setPrimaryKeyColumns(PrimaryKeyColumn[] columns);
	
	void setColumns(Column[] columns);
}
