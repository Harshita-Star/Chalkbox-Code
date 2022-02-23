package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name = "showClassWiseDocumentsDetails")
@ViewScoped
public class ShowClassWiseCompleteDocumentsBean implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	ArrayList<StudentInfo> arrayList = new ArrayList<>();
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass;
	String selectedSection;
	String selectedType;
	String className, section, total;
	ArrayList<ClassInfo> list;
	boolean b;
	ArrayList<StudentInfo> studentList;
	String sectionName;

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = obj1.allSection(selectedClass, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ShowClassWiseCompleteDocumentsBean() {
		Connection conn = DataBaseConnection.javaConnection();
		classList = obj1.allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getStudentStrength() {
		Connection conn = DataBaseConnection.javaConnection();
		String session = DatabaseMethods1.selectedSessionDetails(obj1.schoolId(),conn);
		studentList = obj1.studentListForCompleteIncomplete(selectedSection, selectedType, conn);
		if (studentList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", ""));
			b = false;
		} else
			b = true;

		total = String.valueOf(studentList.size());
		className = obj1.classNameFromidSchid(obj1.schoolId(),selectedClass, session, conn);
		sectionName = obj1.sectionNameByIdSchid(obj1.schoolId(),selectedSection, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<StudentInfo> getArrayList() {
		return arrayList;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public void setArrayList(ArrayList<StudentInfo> arrayList) {
		this.arrayList = arrayList;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

}
