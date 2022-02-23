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

@ManagedBean(name = "searchAchievementRecord")
@ViewScoped
public class SearchTermAchievementRecord implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String name;
	ArrayList<StudentInfo> studentList;
	boolean show;
	ArrayList<SelectItem> classSection, sectionList;
	String selectedClassSection;
	StudentInfo selectedStudent;
	String selectedSection;

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = new DatabaseMethods1().allSection(selectedClassSection, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public SearchTermAchievementRecord() {
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
		studentList = new DatabaseMethods1().searchStudentList(query, conn);
		List<String> studentListt = new ArrayList<>();

		for (StudentInfo info : studentList) {
			studentListt.add(info.getFname() + " / " + info.getFathersName() + "-" + info.getClassName() + "-"
					+ info.getAddNumber());

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public void searchStudentByName() {
		int index = name.lastIndexOf("-") + 1;
		String id = name.substring(index);
		if (index != 0) {
			for (StudentInfo info : studentList) {

				if (String.valueOf(info.getAddNumber()).equals(id)) {
					try {
						studentList = new ArrayList<>();
						studentList.add(info);

						show = true;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}

	}

	public void searchStudentByClassSection() {
		Connection conn = DataBaseConnection.javaConnection();
		try {
			studentList = new DatabaseMethods1().searchStudentListByClassSection(selectedClassSection,selectedSection, conn);
			show = true;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String achievement() {

		if (selectedStudent != null) {
			HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("StudentDetail", selectedStudent);

			show = false;
			name = null;
			selectedClassSection = null;
			selectedStudent = null;

			return "AchievementRecord";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Please select atleast one student", "Validation error"));
		}
		return null;

	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
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

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public String getSelectedClassSection() {
		return selectedClassSection;
	}

	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}
}
