package cc.joymaker.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class TableName {
	public static void main(String[] args) {
		System.out.println(Math.abs("45e07bb342742288375969230c2c10d1".hashCode()) % 128);
	
		Set<String> ADMIN_PWD = new HashSet<String>(Arrays.asList("admin123", "admin368"));
	}
}
