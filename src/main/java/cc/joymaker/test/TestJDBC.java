package cc.joymaker.test;

import org.apache.commons.lang3.StringUtils;

import java.sql.*;

public class TestJDBC {

	/**
	 * @param args
	 */
	// 驱动程序就是之前在classpath中配置的JDBC的驱动程序的JAR 包中
	public static final String DBDRIVER = "com.mysql.jdbc.Driver";
	// 连接地址是由各个数据库生产商单独提供的，所以需要单独记住
//	public static final String DBURL = "jdbc:mysql://rm-2zeze8jt61iz8femeo.mysql.rds.aliyuncs.com:3306/ucenter_0?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
//	// 连接数据库的用户名
//	public static final String DBUSER = "saas_uc_4353";
//	// 连接数据库的密码
//	public static final String DBPASS = "tU6734rh4WTWy7";

	public static final String DBURL = "jdbc:mysql://rm-2zeze8jt61iz8femeo.mysql.rds.aliyuncs.com:3306/ucenter_0?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
	// 连接数据库的用户名
	public static final String DBUSER = "weiop_dxd5j";
	// 连接数据库的密码
	public static final String DBPASS = "eajHY2UUJsXAsKzd";

	public static void main(String[] args) throws Exception {
		queryUid();
	}

	public static void queryUid() throws ClassNotFoundException, SQLException {
		Connection con = null; // 表示数据库的连接对象
		Statement stmt = null; // 表示数据库的更新操作
		ResultSet result = null; // 表示接收数据库的查询结果

		Statement stmt2 = null;
		ResultSet result2 = null;

		Statement stmt3 = null; // insert
		ResultSet result3 = null;

		// Statement stmt3 = null; //insert
		// ResultSet result3 = null;

		Statement stmt4 = null; // update
		ResultSet result4 = null;

		Class.forName(DBDRIVER); // 1、使用CLASS 类加载驱动程序
		con = DriverManager.getConnection(DBURL, DBUSER, DBPASS); // 2、连接数据库
		System.out.println(con);
		stmt = con.createStatement(); // 3、Statement 接口需要通过Connection 接口进行实例化操作
		stmt2 = con.createStatement();
		stmt3 = con.createStatement();
		stmt4 = con.createStatement();

		int _128 = 128, _38 = 38, right = 0, wrong = 0, insertWrong = 0, updateWrong = 0;
		for (int i = 0; i <= 128; i++) {
			int curTableIndex = i;
			result = stmt.executeQuery("select * from t_user_score_record_" + curTableIndex+" r,t_tmp_openid o, t_tmp_code"); // 执行SQL
			// 语句，查询数据库
			while (result.next()) {
				int id = result.getInt("id");
				String uid = result.getString("uid");
				String productID = result.getString("product_id");
				String productName = result.getString("product_name");
				String productType = result.getString("product_type");
				int in = result.getInt("in_warehouse");
				int out = result.getInt("out_warehouse");
				String createTime = result.getString("create_time");
				String updateTime = result.getString("update_time");
				if (StringUtils.isBlank(updateTime)) {
					updateTime = "2017-06-02 00:00:00";
				}
				String memo = result.getString("memo");

				int actTableIndex = (Math.abs(uid.hashCode()) % _38);

				String insert = "insert into t_tmp_score_history" + "(uid,pdi,origin,dest) " + " values('" + uid + "','"
						+ productID + "'," + curTableIndex + "," + actTableIndex + ")"; // 执行SQL

				result2 = stmt2.executeQuery(
						"select * from t_tmp_table_hash where uid='" + uid + "' and pdi='" + productID + "'"); // 执行SQL
				if (!result2.next()) {
					stmt3.executeUpdate(insert);
					System.out.println(insert);
				}

				if (actTableIndex == curTableIndex) {
					// System.out.println("pass");
					right++;
					continue;
				}
				wrong++;

			}

		}
		System.out.println(right + "," + wrong + "," + insertWrong + "," + updateWrong);

		con.close(); // 4、关闭数据库
	}

	public static void updateUserInventory() throws ClassNotFoundException, SQLException {

		Connection con = null; // 表示数据库的连接对象
		Statement stmt = null; // 表示数据库的更新操作
		ResultSet result = null; // 表示接收数据库的查询结果

		Statement stmt2 = null;
		ResultSet result2 = null;

		Statement stmt3 = null; // insert
		ResultSet result3 = null;

		// Statement stmt3 = null; //insert
		// ResultSet result3 = null;

		Statement stmt4 = null; // update
		ResultSet result4 = null;

		Class.forName(DBDRIVER); // 1、使用CLASS 类加载驱动程序
		con = DriverManager.getConnection(DBURL, DBUSER, DBPASS); // 2、连接数据库
		System.out.println(con);
		stmt = con.createStatement(); // 3、Statement 接口需要通过Connection 接口进行实例化操作
		stmt2 = con.createStatement();
		stmt3 = con.createStatement();
		stmt4 = con.createStatement();

		int _128 = 128, _38 = 38, right = 0, wrong = 0, insertWrong = 0, updateWrong = 0;
		for (int i = 0; i <= 38; i++) {
			int curTableIndex = i;
			result = stmt.executeQuery("select * from t_user_inventory_record_" + curTableIndex); // 执行SQL
			// 语句，查询数据库
			while (result.next()) {
				int id = result.getInt("id");
				String uid = result.getString("uid");
				String productID = result.getString("product_id");
				String productName = result.getString("product_name");
				String productType = result.getString("product_type");
				int in = result.getInt("in_warehouse");
				int out = result.getInt("out_warehouse");
				String createTime = result.getString("create_time");
				String updateTime = result.getString("update_time");
				if (StringUtils.isBlank(updateTime)) {
					updateTime = "2017-06-02 00:00:00";
				}
				String memo = result.getString("memo");

				int actTableIndex = (Math.abs(uid.hashCode()) % _38);
				if (actTableIndex == curTableIndex) {
					// System.out.println("pass");
					right++;
					continue;
				}
				wrong++;
				result2 = stmt2.executeQuery("select * from t_user_inventory_record_" + actTableIndex + " where uid='"
						+ uid + "' and product_id='" + productID + "';"); // 执行SQL
				if (!result2.next()) {
					System.out.println("not found!");
					String insert = "insert into t_user_inventory_record_" + actTableIndex
							+ "(uid,product_id,product_name,in_warehouse,out_warehouse,product_type,create_time,update_time,memo) "
							+ " values('" + uid + "','" + productID + "','" + productName + "'," + in + "," + out + ","
							+ productType + ",'" + createTime + "','" + updateTime + "','dr-insert')";
					System.out.println(insert);
					stmt3.executeUpdate(insert); //

					String delete = "delete from t_user_inventory_record_" + curTableIndex + " where id=" + id + ";";
					System.out.println(delete);
					// stmt.executeUpdate(delete);
					insertWrong++;
				} else {
					result2.previous();
					while (result2.next()) {
						System.out.println(result2.getRow());
						String uid2 = result2.getString("uid");
						String productID2 = result2.getString("product_id");
						String productName2 = result2.getString("product_name");
						int in2 = result2.getInt("in_warehouse");
						int out2 = result2.getInt("out_warehouse");
						in2 += in;
						out2 += out;
						String update = "update t_user_inventory_record_" + actTableIndex + " set in_warehouse=" + in2
								+ ",out_warehouse=" + out2 + ",memo='dr-update' where uid='" + uid
								+ "' and product_id='" + productID + "'";
						System.out.println(update);
						stmt3.executeUpdate(update);

						String delete = "delete from t_user_inventory_record_" + curTableIndex + " where id=" + id
								+ ";";
						System.out.println(delete);
						// stmt.executeUpdate(delete);
						updateWrong++;
					}
				}
				// result2.close();
			}
			// result.close();
		}
		System.out.println(right + "," + wrong + "," + insertWrong + "," + updateWrong);

		con.close(); // 4、关闭数据库
	}

}
