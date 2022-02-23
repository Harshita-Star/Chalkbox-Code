package correction_module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import schooldata.DataBaseConnection;

public class ExamSettingCopy {
	
	public void copySetting(String schid)
	//public static void main(String [] args)
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			
			PreparedStatement ps=null;
			Statement st=conn.createStatement();
			ResultSet rr=null;
			
			String query="select * from exam_setting where schid='"+schid+"' and session='2020-2021'";
			rr=st.executeQuery(query);
			while(rr.next())
			{
				query=" insert into exam_setting (classId, schid, examName, no_of_PT, actualMark, reflectMark, include_PT, header1, "
				+ "header2, header3, finalMarks, maxMark_ml, maxMark_ab, rank_base, round_off, round_off_type, marksFormat, extraField,"
				+ " otherField, extraFieldPlace, extraFormat, extraValues, otherValues, termTotal, termGrade, finalTotal, finalGrade,"
				+ " rowTotal, rowPercent, sign1, sign2, sign3, sign4, showGradeScale, std_image, show_header, coschol_sub,"
				+ " coschol_header, coschol_term, term_name_coschol, add_sub, add_header, other_sub, other_header, other_term,"
				+ " term_name_other, seperate_disci, disci_sub, disci_header, disci_term, term_name_disci, finalPercent, examMarks,"
				+ " rowGrade, round_off_percent, marks_grade, session, grade_scale_format, mandatory_sub, optional_sub,"
				+ " show_grade_scale_coschol) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				ps=conn.prepareStatement(query);
				ps.setString(1,rr.getString("classId"));
				ps.setString(2,rr.getString("schid"));
				ps.setString(3,rr.getString("examName"));
				ps.setString(4,rr.getString("no_of_PT"));
				ps.setString(5,rr.getString("actualMark"));
				ps.setString(6,rr.getString("reflectMark"));
				ps.setString(7,rr.getString("include_PT"));
				ps.setString(8,rr.getString("header1"));
				ps.setString(9,rr.getString("header2"));
				ps.setString(10,rr.getString("header3"));
				ps.setString(11,rr.getString("finalMarks"));
				ps.setString(12,rr.getString("maxMark_ml"));
				ps.setString(13,rr.getString("maxMark_ab"));
				ps.setString(14,rr.getString("rank_base"));
				ps.setString(15,rr.getString("round_off"));
				ps.setString(16,rr.getString("round_off_type"));
				ps.setString(17,rr.getString("marksFormat"));
				ps.setString(18,rr.getString("extraField"));
				ps.setString(19,rr.getString("otherField"));
				ps.setString(20,rr.getString("extraFieldPlace"));
				ps.setString(21,rr.getString("extraFormat"));
				ps.setString(22,rr.getString("extraValues"));
				ps.setString(23,rr.getString("otherValues"));
				ps.setString(24,rr.getString("termTotal"));
				ps.setString(25,rr.getString("termGrade"));
				ps.setString(26,rr.getString("finalTotal"));
				ps.setString(27,rr.getString("finalGrade"));
				ps.setString(28,rr.getString("rowTotal"));
				ps.setString(29,rr.getString("rowPercent"));
				ps.setString(30,rr.getString("sign1"));
				ps.setString(31,rr.getString("sign2"));
				ps.setString(32,rr.getString("sign3"));
				ps.setString(33,rr.getString("sign4"));
				ps.setString(34,rr.getString("showGradeScale"));
				ps.setString(35,rr.getString("std_image"));
				ps.setString(36,rr.getString("show_header"));
				ps.setString(37,rr.getString("coschol_sub"));
				ps.setString(38,rr.getString("coschol_header"));
				ps.setString(39,rr.getString("coschol_term"));
				ps.setString(40,rr.getString("term_name_coschol"));
				ps.setString(41,rr.getString("add_sub"));
				ps.setString(42,rr.getString("add_header"));
				ps.setString(43,rr.getString("other_sub"));
				ps.setString(44,rr.getString("other_header"));
				ps.setString(45,rr.getString("other_term"));
				ps.setString(46,rr.getString("term_name_other"));
				ps.setString(47,rr.getString("seperate_disci"));
				ps.setString(48,rr.getString("disci_sub"));
				ps.setString(49,rr.getString("disci_header"));
				ps.setString(50,rr.getString("disci_term"));
				ps.setString(51,rr.getString("term_name_disci"));
				ps.setString(52,rr.getString("finalPercent"));
				ps.setString(53,rr.getString("examMarks"));
				ps.setString(54,rr.getString("rowGrade"));
				ps.setString(55,rr.getString("round_off_percent"));
				ps.setString(56,rr.getString("marks_grade"));
				ps.setString(57,"2021-2022");
				ps.setString(58,rr.getString("grade_scale_format"));
				ps.setString(59,rr.getString("mandatory_sub"));
				ps.setString(60,rr.getString("optional_sub"));
				ps.setString(61,rr.getString("show_grade_scale_coschol"));
				ps.executeUpdate();
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
