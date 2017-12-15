package cc.joymaker.weiop.open.base.tablestore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)

public @interface TableStore {

	String value();
	
	boolean overwrite() default false;
	
}
