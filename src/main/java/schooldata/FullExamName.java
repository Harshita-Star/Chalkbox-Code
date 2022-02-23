package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import exam.DatabaseMethodExam;
import exam_module.ExamInfo;

@ManagedBean(name = "fullexamName")
@ViewScoped
public class FullExamName implements Serializable {

	ArrayList<ExamInfo> allExamNames = new ArrayList<>();
	ArrayList<ExamInfo> allFullNames = new ArrayList<>();
	ArrayList<SelectItem> allClass = new ArrayList<>();
	ArrayList<String> selectedclasses;
	String session, schid;
	boolean showTable = false;
	DatabaseMethodExam dbexam = new DatabaseMethodExam();

	public FullExamName() {
		Connection conn = DataBaseConnection.javaConnection();
		schid = new DatabaseMethods1().schoolId();
		session = new DatabaseMethods1().selectedSessionDetails(schid, conn);
		allClass = new DatabaseMethods1().allClass(conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showExams() {
		Connection conn = DataBaseConnection.javaConnection();
		StringBuilder classIds = new StringBuilder();
		int count = 0;
		StringBuilder alreadyCLass = new StringBuilder();

		ArrayList<ExamInfo> checkClasses = dbexam.getALlFullNames(schid, session, conn);

		if (checkClasses.size() > 0) {
			for (ExamInfo a : checkClasses) {
				String[] classes = a.getClassid().substring(1, a.getClassid().length() - 1).trim().split("-");
				ArrayList<SelectItem> forClass = new ArrayList<>();
				for (int i = 0; i < classes.length; i++) {
					for (String str : selectedclasses) {
						if (str.trim().equalsIgnoreCase(classes[i].trim())) {
							count = count + 1;
							alreadyCLass.append(new DatabaseMethods1().classname(str, schid, conn) + ",");
						}
					}

				}

			}
		}

		if (count == 0) {
			for (int i = 0; i < selectedclasses.size(); i++) {
				classIds.append(selectedclasses.get(i) + "','");
			}
			String classid = classIds.substring(0, classIds.length() - 3);
			allExamNames = dbexam.getPeridicExamDetails(classid, session, schid, conn);
			allExamNames = dbexam.getAllExamsDetails(classid, session, schid, conn, "other", allExamNames);
			if (allExamNames.size() > 0) {
				showTable = true;
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("There is No Exams Found"));
			}
		} else {
			String alcl = alreadyCLass.toString().substring(0, alreadyCLass.toString().length() - 1);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(alcl + "  classes have already set their exam full forms."));
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveNames() {
		Connection conn = DataBaseConnection.javaConnection();
		 // System.out.println("in save method");
		String fullNames = "";
		
		if (allExamNames.size() == 1) {
			fullNames = "{" + allExamNames.get(0).getExamName() + ":" + allExamNames.get(0).getFullName() + "}";
		} else {
			for (int j = 0; j < allExamNames.size(); j++) {
				if (j == 0) {
					fullNames = "{" + allExamNames.get(j).getExamName() + ":" + allExamNames.get(j).getFullName()
							+ " && ";
				} else if (j == allExamNames.size() - 1) {
					fullNames = fullNames + allExamNames.get(j).getExamName() + ":" + allExamNames.get(j).getFullName()
							+ "}";
				} else {
					fullNames = fullNames + allExamNames.get(j).getExamName() + ":" + allExamNames.get(j).getFullName()
							+ " && ";
				}
			}
		}
		String classes = "";
		if (selectedclasses.size() == 1) {
			classes = "{" + selectedclasses.get(0) + "}";
		} else {
			for (int k = 0; k < selectedclasses.size(); k++) {

				if (k == 0) {
					classes = "{" + selectedclasses.get(k) + "-";
				} else if (k == selectedclasses.size() - 1) {
					classes = classes + selectedclasses.get(k) + "}";
				} else {
					classes = classes + selectedclasses.get(k) + "-";
				}

			}
		}
		
		int status = dbexam.saveFullNames(fullNames, classes, schid, session, conn);
		if (status > 0) {
			 // System.out.println(status);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Names Saved"));
			showTable = false;
			selectedclasses = new ArrayList<>();
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ExamInfo> getAllExamNames() {
		return allExamNames;
	}

	public void setAllExamNames(ArrayList<ExamInfo> allExamNames) {
		this.allExamNames = allExamNames;
	}

	public ArrayList<SelectItem> getAllClass() {
		return allClass;
	}

	public void setAllClass(ArrayList<SelectItem> allClass) {
		this.allClass = allClass;
	}

	public ArrayList<String> getSelectedclasses() {
		return selectedclasses;
	}

	public void setSelectedclasses(ArrayList<String> selectedclasses) {
		this.selectedclasses = selectedclasses;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

}
