package correction_module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import schooldata.DataBaseConnection;

public class MakeMissingExam {

	public static void main(String[] args) {
		Connection conn=DataBaseConnection.javaConnection();
		String schid="298",session="2019-2020";
		try
		{
			Statement st=conn.createStatement();
			Statement st1=conn.createStatement();
			Statement st2=conn.createStatement();
			
			 // System.out.println("start");
			String query="select id from exam_table_cbse where schid='"+schid+"' and examtype='coscholastic' and session='"+session+"' and classid='7' and termId='5306'";
			ResultSet rr=st.executeQuery(query);
			while(rr.next())
			{
				 // System.out.println("main Id.."+rr.getString("id"));
				query="select * from sub_exam_table_cbse where mainExamId='"+rr.getString("id")+"' and schid='"+schid+"' and examtype='coscholastic' and session='"+session+"'";
				ResultSet rr1=st1.executeQuery(query);
				while(rr1.next())
				{
					query="select * from demo_performance where schid='"+schid+"' and termId='"+rr1.getString("termId")+"' and session='"+session+"' and subjectId='"+rr1.getString("subjectId")+"'";
					//st2.executeUpdate(query);
					ResultSet rr2=st2.executeQuery(query);
					 // System.out.println(query);
					while(rr2.next())
					{
						query="insert into student_performance_cbse (studentId, sectionId, subjectId,maxMarks, marksObtained,subExamName,"
								+ " mainExamId,examType,session,termId,schId) values(?,?,?,?,?,?,?,?,?,?,?) ";
						PreparedStatement pst=conn.prepareStatement(query);
						pst.setString(1,rr2.getString("studentId"));
						pst.setString(2,rr2.getString("sectionId"));
						pst.setString(3,rr1.getString("subjectId"));
						pst.setString(4,"0");
						pst.setString(5,rr2.getString("marksObtained"));
						pst.setString(6,"coschool");
						pst.setString(7,rr.getString("id"));
						pst.setString(8,"coscholastic");
						pst.setString(9,session);
						pst.setString(10, rr1.getString("termId"));
						pst.setString(11, schid);
						pst.executeUpdate();
					}
				}
			}
			 // System.out.println("end");
			
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
