package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;

@ManagedBean(name = "generateRbscTC")
@ViewScoped
public class GenerateRbscTC implements Serializable {
	String name;
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo> studentList;
	ArrayList<StudentInfo> list;
	boolean show;
	StudentInfo selectedStudent;
	String selectedClass;
	ArrayList<SelectItem> classSection;

	public GenerateRbscTC() {
		Connection conn = DataBaseConnection.javaConnection();
		classSection = new DatabaseMethods1().allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query) {
		Connection conn = DataBaseConnection.javaConnection();
		studentList = new DatabaseMethods1().rbscSearchStudentListForTCByName(query, conn);
		List<String> studentListt = new ArrayList<>();

		for (StudentInfo info : studentList) {
			studentListt.add(info.getFname() + " / " + info.getFathersName() + " / " + info.getSrNo() + "-"
					+ info.getClassName() + "-" + info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void searchStudentByName() {

		list = new ArrayList<>();
		int index = name.lastIndexOf("-") + 1;
		String id = name.substring(index);
		if (index != 0) {
			for (StudentInfo info : studentList) {
				if (String.valueOf(info.getAddNumber()).equals(id)) {
					try {
						studentList = new ArrayList<>();
						list.add(info);

						show = true;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		show = true;
	}

	public void editNow() throws Exception {
		Connection conn = DataBaseConnection.javaConnection();
		try {
			/*
			 * String status=new
			 * DatabaseMethods1().studentStatusRbscTC(selectedStudent.getAddNumber(),conn);
			 * if(status!=null) { FacesContext.getCurrentInstance().addMessage(null, new
			 * FacesMessage("Student Details Already Submitted")); } else {
			 */
			if (selectedStudent != null) {
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss.setAttribute("rbscTCDetail", selectedStudent);
				show = false;
				name = null;
				FacesContext.getCurrentInstance().getExternalContext().redirect("rbscTCDetail.xhtml");
			} else {

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Student From List"));
			}
			/* } */
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
