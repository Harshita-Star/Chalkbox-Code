package correction_module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import schooldata.DataBaseConnection;

public class ExamDataUpdate {
	public static void main(String [] args)
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			
			Statement st=conn.createStatement();
			Statement st1=conn.createStatement();
			Statement st2=conn.createStatement();
			ResultSet rr=null,rr1=null,rr2=null;
			
			String query="select * from exam_table_cbse where status='INACTIVE'";
			rr=st.executeQuery(query);
			while(rr.next())
			{
				query="delete from sub_exam_table_cbse where mainExamid='"+rr.getString("id")+"'";
				st1.executeUpdate(query);
				
				query="delete from student_performance_cbse where mainExamid='"+rr.getString("id")+"'";
				st1.executeUpdate(query);
			}
			 // System.out.println("Deletion Complted");
			
			
			query="select * from exam_table_cbse where examType='other' and status='ACTIVE' group by termId";
			rr=st.executeQuery(query);
			int i=0;
			while(rr.next())
			{
				 // System.out.println("in..."+(i++));
				query="update exam_table_cbse set examName='coschool' where id='"+rr.getString("id")+"'";
				st1.executeUpdate(query);
				
				query="select * from sub_exam_table_cbse where examType='other' and termId='"+rr.getString("termId")+"' ";
				rr1=st1.executeQuery(query);
				while(rr1.next())
				{
					query="update sub_exam_table_cbse set examName='coschool' , mainExamId='"+rr.getString("id")+"' where id='"+rr1.getString("id")+"'";
					st2.executeUpdate(query);
				}
				
				query="update student_performance_cbse set subExamName='coschool' , mainExamId='"+rr.getString("id")+"' where examType='other' and termId='"+rr.getString("termId")+"'";
				st1.executeUpdate(query);
			}
			
			query="select * from exam_table_cbse where examType='coscholastic' and status='ACTIVE' group by termId";
			rr=st.executeQuery(query);
			i=0;
			while(rr.next())
			{
				 // System.out.println("in..."+(i++));
				query="update exam_table_cbse set examName='coschool' where id='"+rr.getString("id")+"'";
				st1.executeUpdate(query);
				
				query="select * from sub_exam_table_cbse where examType='coscholastic' and termId='"+rr.getString("termId")+"' ";
				rr1=st1.executeQuery(query);
				while(rr1.next())
				{
					query="update sub_exam_table_cbse set examName='coschool' , mainExamId='"+rr.getString("id")+"' where id='"+rr1.getString("id")+"'";
					st2.executeUpdate(query);
				}
				
				query="update student_performance_cbse set subExamName='coschool' , mainExamId='"+rr.getString("id")+"' where examType='coscholastic' and termId='"+rr.getString("termId")+"'";
				st1.executeUpdate(query);
			}
			
			 // System.out.println("completed");
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
