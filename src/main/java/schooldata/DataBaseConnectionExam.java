package schooldata;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseConnectionExam {

	//final static private String myurl = "jdbc:mysql://103.93.17.133:3306/";!
	final static private String mydriver ="com.mysql.jdbc.Driver"; // driver
//
//	final static private String myurl = "jdbc:mysql://localhost:3306/"; // path
//	final static private String mydb = "cb_20_10";
//	final static private String pass = "root";
//	final static private String user = "root";

	final static private String myurl = "jdbc:mysql://103.120.178.153:3306/"; 
	final static private String mydb = "examchal_exam"; //chalkbox_newcb, chalkbox_db,wwweduca_db
	final static private String pass = "+6e*@eJ]I#j*";
	final static private String user = "examchal_exam";// new DB -> root	

	

	public static Connection javaConnection()
	{
		Connection conn = null;
		try
		{
			Class.forName(mydriver).newInstance();
			conn = DriverManager.getConnection(myurl + mydb+"?characterEncoding=UTF-8", user,pass);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


		return conn;
	}

}
