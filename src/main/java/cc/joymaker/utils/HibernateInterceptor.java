package cc.joymaker.utils;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import cc.joymaker.weiop.open.base.Sys;
import cc.joymaker.weiop.open.base.model.Org;
import cc.joymaker.weiop.open.base.model.Partitionable;

public class HibernateInterceptor extends EmptyInterceptor {

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof Org) {
			((Org) entity).setOrgId((String) Sys.get("orgid"));
		}
		if (entity instanceof Partitionable) {
			((Partitionable) entity).calcPartition();
		}
		return super.onSave(entity, id, state, propertyNames, types);
	}
	
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof Org) {
			String orgid = ((Org) entity).getOrgId();
			if (!orgid.equals((String) Sys.get("orgid"))) {
				throw new SecurityException("非法删除请求");
			}
		}
		super.onDelete(entity, id, state, propertyNames, types);
	}
	
	@Override
	public Object getEntity(String entityName, Serializable id) {
		
		Object o = super.getEntity(entityName, id);
		if (o instanceof Org) {
			String orgid = ((Org) o).getOrgId();
			if (!orgid.equals((String) Sys.get("orgid"))) {
				throw new SecurityException("您无权访问该数据");
			}
		}
		return o;
	}
	
	@Override
	public String onPrepareStatement(String sql) {
		return super.onPrepareStatement(sql);
	}
	
}
