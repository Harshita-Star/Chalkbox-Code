package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import reports_module.DataBaseMethodsReports;

@ManagedBean(name = "consolidatedConcessionReport")
@ViewScoped
public class ConsolidatedConcessionReportBean implements Serializable {
	ArrayList<SelectItem> ClassList = new ArrayList<>();
	ArrayList<SelectItem> sectionList = new ArrayList<>();
	ArrayList<studentCategoryInfo> FinalList = new ArrayList<>();
	ArrayList<SelectItem> concessionList = new ArrayList<>();
	DatabaseMethods1 obj = new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	int total=0;

	public ConsolidatedConcessionReportBean ()
	{
		Connection conn = DataBaseConnection.javaConnection();
		ClassList=obj.allClass(conn);
		concessionList=dbr.selectAllValuesOfConcession(conn);
		String classId="";
		for(SelectItem cl:ClassList)
		{
			classId= String.valueOf(cl.getValue());
			sectionList=obj.sectionNameAndIdListByid(classId,conn);
			for(SelectItem sec:sectionList)
			{
				studentCategoryInfo sc = new studentCategoryInfo();
				sc.setClassid(classId);
				sc.setClassName(cl.getLabel());
				sc.setSection(sec.getLabel());
				sc.setSectionid(String.valueOf(sec.getValue()));
				FinalList.add(sc);
			}
		}
		total=0;
		int count=0,sum=0;
		for(studentCategoryInfo info:FinalList)
		{
			Map<String,String>map = new HashMap<>();
			count = dbr.fetchAllValuesFromregistration(info.getSectionid(),"all", conn);
			for(SelectItem con:concessionList)
			{
				sum =Integer.parseInt(obj.allStudentcount(obj.schoolId(), "category", info.getSectionid(),obj.selectedSessionDetails(obj.schoolId(), conn),String.valueOf(con.getValue()),conn));
				map.put(con.getLabel(), String.valueOf(sum));
				info.setConcMap(map);
			}
			total=total+count;
			info.setTotal(count);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getClassList() {
		return ClassList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		ClassList = classList;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<studentCategoryInfo> getFinalList() {
		return FinalList;
	}

	public void setFinalList(ArrayList<studentCategoryInfo> finalList) {
		FinalList = finalList;
	}

	public ArrayList<SelectItem> getConcessionList() {
		return concessionList;
	}

	public void setConcessionList(ArrayList<SelectItem> concessionList) {
		this.concessionList = concessionList;
	}

	public DatabaseMethods1 getObj() {
		return obj;
	}

	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}

