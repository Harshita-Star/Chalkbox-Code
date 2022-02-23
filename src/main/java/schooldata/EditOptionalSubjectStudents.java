package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.fileupload.RequestContext;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

import session_work.RegexPattern;

@ManagedBean(name = "editOptionalSubjectStudents")
@ViewScoped
public class EditOptionalSubjectStudents implements Serializable {
	String regex = RegexPattern.REGEX;
	ArrayList<Subjects> subjectList;
	ArrayList<SelectItem> sectionList;
	Subjects selectedSubject;
	String selectedSection;
	boolean show = false, showPicker = false;
	public DualListModel<String> studentList;
	List<String> studentListSource = new ArrayList<>();
	List<String> studentListTarget = new ArrayList<>();
	DatabaseMethods1 dmb = new DatabaseMethods1();
	String sessionValue, schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseValidator objValidate = new DataBaseValidator();

	public EditOptionalSubjectStudents() {
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = dmb.schoolId();
		sessionValue = dmb.selectedSessionDetails(schoolId, conn);
		 // System.out.println("constructor called");
		subjectList = dmb.optionalSubjectList(conn);

		for (Subjects cc : subjectList) {
			String checker = dmb.checkStudentAssignedInOptionalSubject(cc.getSubjectCode(), conn);

			if (checker == "no") {
				cc.setDisable(false);
			} else {
				cc.setDisable(true);
			}

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void allSection() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = dmb.allSection(selectedSubject.getClassid(), conn);
		show = true;
		showPicker = false;
		PrimeFaces.current().scrollTo("editForm:pnlgrid");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editStudentSubjectDetails() {
		Connection conn = DataBaseConnection.javaConnection();

		studentListSource = dmb.searchStudentListbySection(selectedSection, conn);
		studentListTarget = dmb.searchStudentListbySubject(selectedSection, selectedSubject.getSubjectCode(), conn);
		
	

		if (studentListTarget.size() > 0) {
			for (String ss : studentListTarget) {
				/*
				 * int i=0; for(String ff:studentListSource) { i++; if(ss.equals(ff)); { break;
				 * } }
				 * 
				 * studentListSource.remove(i-1);
				 */
				studentListSource.remove(ss);
			}
		}
		studentList = new DualListModel<>(studentListSource, studentListTarget);
	
		
		
		showPicker = true;
		PrimeFaces.current().scrollTo("editForm:pickList");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String submit() {
		int x = 0;
		Connection conn = DataBaseConnection.javaConnection();
		
		// if(studentList.getTarget().size()>0)
		// {
		x = dmb.updateDetail(studentList, conn, selectedSection, selectedSubject.getSubjectCode(),
				selectedSubject.getClassid());
		// }
		// else
		// {
		// FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Plz
		// select atleast one student"));
		// return "";
		// }
		if (x >= 1) {
			String refNo3;
			try {
				refNo3 = addWorkLog3(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			// new DatabaseMethods1().deleteSubject(selectedSubject.getSubjectCode());
			PrimeFaces.current().ajax().update("confirmForm");
			PrimeFaces.current().executeInitScript("PF('enqDlg').show()");

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error"));

		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	public void ok() {

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editOptionalSubjectStudents.xhtml");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void cancel() {
		selectedSection = "";
		show = false;
		showPicker = false;
	}

	public void update() {
		Connection conn = DataBaseConnection.javaConnection();

		if (selectedSubject.getSubjectName().trim().equalsIgnoreCase("")) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Enter Subject Name"));

		} else {
			int dupl = objValidate.duplicateSubjectEdit(String.valueOf(selectedSubject.getSubjectCode()), sessionValue,
					selectedSubject.getClassid(), selectedSubject.getSubjectName(), selectedSubject.getSubjectType(),
					schoolId, conn);

			if (dupl == 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Subject Found"));
				subjectList = dmb.optionalSubjectList(conn);

				for (Subjects cc : subjectList) {
					String checker = dmb.checkStudentAssignedInOptionalSubject(cc.getSubjectCode(), conn);

					if (checker == "no") {
						cc.setDisable(false);
					} else {
						cc.setDisable(true);
					}

				}
			} else {

				int status = dmb.editOptionalSubject(selectedSubject.getSubjectCode(), selectedSubject.getSubjectName(),
						selectedSubject.getAddInExam(), conn);
				if (status == 1) {
					String refNo2;
					try {
						refNo2 = addWorkLog2(conn);
					} catch (Exception e) {
						// TODO: handle exception
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Edited Sucessfuly "));

					subjectList = dmb.optionalSubjectList(conn);

					for (Subjects cc : subjectList) {
						String checker = dmb.checkStudentAssignedInOptionalSubject(cc.getSubjectCode(), conn);

						if (checker == "no") {
							cc.setDisable(false);
						} else {
							cc.setDisable(true);
						}

					}
					PrimeFaces current = PrimeFaces.current();
					current.executeScript("PF('editDialog').hide();");
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));

				}
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void delete() {
		Connection conn = DataBaseConnection.javaConnection();

		boolean checkAllocate = dmb.subjectallocatedOrNot(Integer.valueOf(selectedSubject.getSubjectCode()), conn);
		if (checkAllocate) {
			FacesContext fc2 = FacesContext.getCurrentInstance();
			fc2.addMessage(null, new FacesMessage(
					"Can't Delete Selected Subject as it is Allocated to a teacher,Please Deallocate it first"));
		} else {

			int status = dmb.deleteOptionalSubject(selectedSubject.getSubjectCode(), conn);
			if (status == 1) {
				String refNo;
				try {
					refNo = addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Deleted "));

				subjectList = dmb.optionalSubjectList(conn);

				for (Subjects cc : subjectList) {
					String checker = dmb.checkStudentAssignedInOptionalSubject(cc.getSubjectCode(), conn);

					if (checker == "no") {
						cc.setDisable(false);
					} else {
						cc.setDisable(true);
					}

				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));

			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// For Delete
	public String addWorkLog(Connection conn) {
		String value = "";
		String language = "";

		value = selectedSubject.getSubjectCode();

		String refNo = workLg.saveWorkLogMehod(language, "Delete Optional Subject ", "WEB", value, conn);
		return refNo;
	}

	// For EDIT
	public String addWorkLog2(Connection conn) {
		String value = "";
		String language = selectedSubject.getSubjectName();

		value = selectedSubject.getSubjectCode() + " --- " + selectedSubject.getSubjectName();

		String refNo = workLg.saveWorkLogMehod(language, "Edit Optional Subject ", "WEB", value, conn);
		return refNo;
	}

	// For Assign
	public String addWorkLog3(Connection conn) {
		String value = selectedSubject.getSubjectCode() + " ---- " + selectedSubject.getClassid() + " ---- ";
		String language = selectedSection;

		for (String cvb : studentList.getTarget()) {
			String temp[] = cvb.split("/");
			String addNumber = temp[0];

			value += addNumber + " - ";
		}
		if (studentList.getTarget().size() > 0) {
			value = value.substring(0, value.length() - 3);
		}

		String refNo = workLg.saveWorkLogMehod(language, "Assign Optional Subject ", "WEB", value, conn);
		return refNo;
	}

	public ArrayList<Subjects> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<Subjects> subjectList) {
		this.subjectList = subjectList;
	}

	public Subjects getSelectedSubject() {
		return selectedSubject;
	}

	public void setSelectedSubject(Subjects selectedSubject) {
		this.selectedSubject = selectedSubject;
	}

	public List<String> getStudentListSource() {
		return studentListSource;
	}

	public void setStudentListSource(List<String> studentListSource) {
		this.studentListSource = studentListSource;
	}

	public List<String> getStudentListTarget() {
		return studentListTarget;
	}

	public void setStudentListTarget(List<String> studentListTarget) {
		this.studentListTarget = studentListTarget;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public DualListModel<String> getStudentList() {
		return studentList;
	}

	public void setStudentList(DualListModel<String> studentList) {
		this.studentList = studentList;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public boolean isShowPicker() {
		return showPicker;
	}

	public void setShowPicker(boolean showPicker) {
		this.showPicker = showPicker;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

}
