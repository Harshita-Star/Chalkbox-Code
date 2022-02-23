package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean(name = "bulkExamPdfUpload")
@ViewScoped
public class BulkExamPdfUploadBean implements Serializable {

	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass;
	String selectedSection;
	String className, section;
	ArrayList<ClassInfo> list;
	ArrayList<String> examList;
	boolean b;
	String sectionName, examName;
	ArrayList<StudentInfo> studentList;
	DatabaseMethods1 dbm = new DatabaseMethods1();

	public BulkExamPdfUploadBean() {
		Connection conn = DataBaseConnection.javaConnection();
		classList = dbm.allClass(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = new DatabaseMethods1().allSection(selectedClass, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allExams() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		examList = new DatabaseMethods1().checkListOfExams(selectedClass, selectedSection, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void studentInClass() {

		Connection conn = DataBaseConnection.javaConnection();

		if (examName.trim().equalsIgnoreCase("")) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Empty Exam Name"));

		}

		else {
			studentList = dbm.findClassStudentList(selectedSection, conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else {
				b = true;
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String addNow() {

		Connection conn = DataBaseConnection.javaConnection();

		int size = studentList.size();
		int counter = 0, typeCheck = 0;
		for (StudentInfo in : studentList) {

			if (in.getFile() == null) {
				counter++;
			}
		}

		for (StudentInfo in : studentList) {

			if (in.getFile() != null && in.getFile().getSize() > 0) {
				String fileCheck = in.getFile().getFileName();
				String sub = fileCheck.substring(fileCheck.length() - 4, fileCheck.length());
				// //// // System.out.println(sub);
				if (!sub.equalsIgnoreCase(".pdf")) {
					typeCheck++;
				}
			}

		}

		if (counter == size) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Please Choose any File Before Submitting"));
			return "";
		}

		else {

			if (typeCheck > 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Choose Pdf File Only"));
				return "bulkExamPdfUpload.xhtml";
			} else {

				int i = dbm.addExamPdfMarksheet(studentList, examName.trim(), conn);
				if (i > 0) {
					FacesContext context1 = FacesContext.getCurrentInstance();
					context1.addMessage(null, new FacesMessage("Marksheets Uploaded Successfully"));

					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "bulkExamPdfUpload.xhtml";
				} else {
					FacesContext context1 = FacesContext.getCurrentInstance();
					context1.addMessage(null, new FacesMessage("Some error Occur please try Again"));
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "";
				}
			}
		}
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

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<String> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<String> examList) {
		this.examList = examList;
	}

}
