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

@ManagedBean(name = "consolidatedClassStrenght")
@ViewScoped
public class ConsolidatedClassStrenghtBean implements Serializable {
	ArrayList<ClassInfo> strenghtList = new ArrayList<>();
	ArrayList<SelectItem> sectionList = new ArrayList<>();
	ArrayList<SelectItem> classlist = new ArrayList<>();
	ArrayList<studentCategoryInfo> finallist = new ArrayList<>();
	ArrayList<SelectItem>categorylist = new ArrayList<>();
	int total=0,general=0,obc=0,sc=0,st=0,sbc=0;
	public int getTotal() {
		return total;
	}



	public void setTotal(int total) {
		this.total = total;
	}
	DataBaseMethodsReports obj = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();

	public ConsolidatedClassStrenghtBean() {
		Connection conn = DataBaseConnection.javaConnection();
		categorylist=obj.selectValuesOfCategories(conn);
		classlist=dbm.allClass(conn);
		String classid="";
		total=0;
		int count=0;
		int sum=0;
		int CT = 0;
		for(SelectItem cls:classlist)
		{
			classid= String.valueOf(cls.getValue());

			sectionList=dbm.sectionNameAndIdListByid(classid,conn);
			for(SelectItem section:sectionList)
			{
				studentCategoryInfo info =new studentCategoryInfo();
				info.setClassid(classid);
				info.setClassName(cls.getLabel());
				info.setSection(section.getLabel());
				info.setSectionid(String.valueOf(section.getValue()));

				finallist.add(info);


			}
		}

		/////////////////////////////////////////////////////////////////////

		for(studentCategoryInfo sc : finallist)
		{
			CT=0;
			count=0;
			count = obj.fetchAllValuesFromregistration(sc.getSectionid(),"all", conn);

			Map<String, String> map = new HashMap<>();
			for(SelectItem ss : categorylist)
			{
				sum = obj.fetchAllValuesFromregistration(sc.getSectionid(), String.valueOf(ss.getValue()), conn);
				map.put(ss.getLabel(), String.valueOf(sum));
				// //// // System.out.println("val : "+map.get(String.valueOf(ss.getValue())));
				CT = CT+sum;
				sc.setCategMap(map);
			}

			total = total+count;
			sc.setUncaterised(count-CT);
			sc.setTotal(count);

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ClassInfo> getStrenghtList() {
		return strenghtList;
	}



	public void setStrenghtList(ArrayList<ClassInfo> strenghtList) {
		this.strenghtList = strenghtList;
	}



	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}



	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<studentCategoryInfo> getFinallist() {
		return finallist;
	}



	public void setFinallist(ArrayList<studentCategoryInfo> finallist) {
		this.finallist = finallist;
	}



	public ArrayList<SelectItem> getClasslist() {
		return classlist;
	}

	public void setClasslist(ArrayList<SelectItem> classlist) {
		this.classlist = classlist;
	}


	public ArrayList<SelectItem> getCategorylist() {
		return categorylist;
	}



	public void setCategorylist(ArrayList<SelectItem> categorylist) {
		this.categorylist = categorylist;
	}

}