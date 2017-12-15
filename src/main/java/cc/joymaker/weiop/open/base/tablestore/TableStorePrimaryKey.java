package cc.joymaker.weiop.open.base.tablestore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

public @interface TableStorePrimaryKey {

	String value();
	
	int index() default -1;
	
	boolean autoIncr() default false;
}
