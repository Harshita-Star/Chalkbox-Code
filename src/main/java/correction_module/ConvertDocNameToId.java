package correction_module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

public class ConvertDocNameToId {

	public static void main(String[] args) {

		Connection conn=DataBaseConnection.javaConnection();
		Statement st=null;
		PreparedStatement pst=null;
		ResultSet rr=null;
		try
		{
			System.out.println("Started");
			st=conn.createStatement();
			String query="select * from doctype";
			rr=st.executeQuery(query);
			while(rr.next())
			{
				query="update student_documents set document_name=? where schid=? and document_name=?";
				pst=conn.prepareStatement(query);
				pst.setString(1, rr.getString("id"));
				pst.setString(2, rr.getString("schid"));
				pst.setString(3, rr.getString("name"));
				pst.executeUpdate();
			}
			System.out.println("Completed");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(st!=null)
					st.close();
				if(pst!=null)
					pst.close();
				if(rr!=null)
					rr.close();
					
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
}

