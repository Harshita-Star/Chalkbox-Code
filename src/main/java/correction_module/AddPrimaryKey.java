package correction_module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import schooldata.DataBaseConnection;

public class AddPrimaryKey {

	public static void main(String[] args) {
		Connection conn=DataBaseConnection.javaConnection();
		int i = 0;
		
		//for primary key
		try
		{
			Statement st=conn.createStatement();
			Statement st1 = conn.createStatement();
			Statement st2 = conn.createStatement();
			Statement st3 = conn.createStatement();
			ResultSet rr=null,rs=null,rr3=null;
			String query="SELECT table_name FROM information_schema.tables WHERE table_schema = 'cb_31_12'";
			rr=st.executeQuery(query);
			while(rr.next()) {
				query = "SELECT column_name FROM information_schema.columns WHERE table_name='"+rr.getString("table_name")+"'";
				rs = st1.executeQuery(query);
				while(rs.next()) {
					query= "select column_name from information_schema.columns where  table_schema ='cb_31_12' and `table_name` = '"+rr.getString("table_name")+"' and column_key = 'PRI'";
					rr3 = st2.executeQuery(query);
					if(rr3.next()) {
							System.out.println("column...."+rs.getString("column_name")+"...table..."+rr.getString("table_name"));
							System.out.println(i++);
						
					}else {
//						if(!rr.getString("table_name").equals("student_performance_cbse")&&!rr.getString("table_name").equals("student_fee_table")&&!rr.getString("table_name").equals("student_extra_value")
//								&&!rr.getString("table_name").equals("student_expense_category")
//								&&!rr.getString("table_name").equals("student_expense")&&!rr.getString("table_name").equals("student_documents")
//								&&!rr.getString("table_name").equals("attlog")&&!rr.getString("table_name").equals("events")
//								&&!rr.getString("table_name").equals("previousfees")&&!rr.getString("table_name").equals("previosfees")
//								&&!rr.getString("table_name").equals("student_admin_chat")) {
							System.out.println("from else......column...."+rs.getString("column_name")+"...table..."+rr.getString("table_name"));
							query = "alter table "+rr.getString("table_name")+" add primary key ("+rs.getString("column_name")+")";
							i = st3.executeUpdate(query);
//						}
					}
						break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//for auto_increment
		
		try
		{
			Statement st=conn.createStatement();
			Statement st1 = conn.createStatement();
			Statement st2 = conn.createStatement();
			Statement st3 = conn.createStatement();
			ResultSet rr=null,rs=null,rr3=null;
			String query="SELECT table_name FROM information_schema.tables WHERE table_schema = 'cb_20_20'";
			rr=st.executeQuery(query);
			while(rr.next()) {
				query = "SELECT column_name FROM information_schema.columns WHERE table_name='"+rr.getString("table_name")+"'";
				rs = st1.executeQuery(query);
				while(rs.next()) {
					//query= "select column_name from information_schema.columns where  table_schema ='cb_20_20' and `table_name` = '"+rr.getString("table_name")+"' and column_key = 'AI'";
					query="  select column_name from information_schema.columns where TABLE_SCHEMA='cb_20_20' and TABLE_NAME='"+rr.getString("table_name")+"' and EXTRA like '%auto_increment%'";
					rr3 = st2.executeQuery(query);
					if(rr3.next()) {
							System.out.println(rs.getString("column_name")+"<-c name"+rr.getString("table_name")+"<-table_name");
							System.out.println(i++);
						
					}else {
						if(!rr.getString("table_name").equals("student_performance_cbse")&&!rr.getString("table_name").equals("student_fee_table")&&!rr.getString("table_name").equals("student_extra_value")
								&&!rr.getString("table_name").equals("student_expense_category")
								&&!rr.getString("table_name").equals("student_expense")&&!rr.getString("table_name").equals("student_documents")
								&&!rr.getString("table_name").equals("attlog")&&!rr.getString("table_name").equals("events")
								&&!rr.getString("table_name").equals("previousfees")&&!rr.getString("table_name").equals("previosfees")
								&&!rr.getString("table_name").equals("student_admin_chat")) {
							System.out.println("table Name-->"+rr.getString("table_name"));
							System.out.println("column Name-->"+rs.getString("column_name"));
							query = "ALTER TABLE "+rr.getString("table_name")+" " + 
									"MODIFY "+rs.getString("column_name")+" INT NOT NULL AUTO_INCREMENT" + 
									"";
							i = st3.executeUpdate(query);
						}
					}
						break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
