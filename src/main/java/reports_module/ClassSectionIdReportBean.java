package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="classSectionIdReport")
@ViewScoped

public class ClassSectionIdReportBean implements Serializable
{
	ArrayList<ClassInfo> classSectionList = new ArrayList<>();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	public ClassSectionIdReportBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		classSectionList=DBM.allSectionList(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ClassInfo> getClassSectionList() {
		return classSectionList;
	}

	public void setClassSectionList(ArrayList<ClassInfo> classSectionList) {
		this.classSectionList = classSectionList;
	}
	
	
}


