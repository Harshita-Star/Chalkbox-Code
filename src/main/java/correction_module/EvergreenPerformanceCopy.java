package correction_module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import schooldata.DataBaseConnection;

public class EvergreenPerformanceCopy 
{
	public static void main(String[] args) 
	{
		Connection conn=DataBaseConnection.javaConnection();
		Statement st=null;
		ResultSet rr=null;
		String sourceMainExam="5936",sourceTermId="5652",schId="216",session="2019-2020";
		String targetMainExam="7461",targetTermId="5993",subId="'13423','13421','13385'",sectionId="2037";
		try
		{
			String query="select * from student_performance_cbse where schid='"+schId+"' and sectionId='"+sectionId+"' and session='"+session+"' and mainExamId='"+sourceMainExam+"' and termId='"+sourceTermId+"' and subjectId in ("+subId+")";
			st=conn.createStatement();
			rr=st.executeQuery(query);
			while(rr.next())
			{
				query="insert into student_performance_cbse (studentId, sectionId, subjectId,maxMarks, marksObtained,subExamName,"
						+ " mainExamId,examType,session,termId,schId) values(?,?,?,?,?,?,?,?,?,?,?) ";
				PreparedStatement pst=conn.prepareStatement(query);
				pst.setString(1,rr.getString("studentId"));
				pst.setString(2,rr.getString("sectionId"));
				pst.setString(3,rr.getString("subjectId"));
				pst.setString(4,rr.getString("maxMarks"));
				pst.setString(5,rr.getString("marksObtained"));
				pst.setString(6,rr.getString("subExamName"));
				pst.setString(7,targetMainExam);
				pst.setString(8,rr.getString("examType"));
				pst.setString(9,session);
				pst.setString(10, targetTermId);
				pst.setString(11, schId);
				pst.executeUpdate();
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
