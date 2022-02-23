package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import exam.DatabaseMethodExam;
import exam_module.ExamInfo;
import exam_module.FullNameInfo;

@ManagedBean(name = "editfullexamName")
@ViewScoped
public class EditViewDeleteFullNames implements Serializable {

	ArrayList<ExamInfo> allExamNames = new ArrayList<>();
	ArrayList<ExamInfo> allFullNames = new ArrayList<>();
	ArrayList<ExamInfo> allExamNamesDlg = new ArrayList<>();
	ArrayList<SelectItem> allClass = new ArrayList<>();
	ArrayList<SelectItem> viewALlClass = new ArrayList<>();
	ArrayList<String> selectedcls = new ArrayList<>();
	ExamInfo selectedFullRow = new ExamInfo();
	ArrayList<String> selectedclasses;
	ArrayList<String> selectedClasses = new ArrayList<>();
	ExamInfo selectedRow = new ExamInfo();
	ExamInfo selectedRowName = new ExamInfo();
	ExamInfo selectedforAdd = new ExamInfo();
	String session, schid;
	boolean showTable = false, showPanel = false, showAddButt = true, showTableEdit = false;
	ArrayList<SelectItem> viewClass = new ArrayList<>();
	DatabaseMethodExam dbexam = new DatabaseMethodExam();

	public EditViewDeleteFullNames() {
		Connection conn = DataBaseConnection.javaConnection();
		schid = new DatabaseMethods1().schoolId();
		session = new DatabaseMethods1().selectedSessionDetails(schid, conn);

		allFullNameMethods(conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void allFullNameMethods(Connection conn) {
		allFullNames = dbexam.getALlFullNames(schid, session, conn);
		if (allFullNames.size() > 0) {
			for (ExamInfo a : allFullNames) {
				String[] classIds = a.getClassid().substring(1, a.getClassid().length() - 1).trim().split("-");
				ArrayList<SelectItem> forClass = new ArrayList<>();
				ArrayList<FullNameInfo> forFullName = new ArrayList<>();
				for (int i = 0; i < classIds.length; i++) {
					SelectItem fcls = new SelectItem();
					fcls.setValue(classIds[i].trim());
					fcls.setLabel(new DatabaseMethods1().classname(classIds[i].trim(), schid, conn));
					forClass.add(fcls);
				}
				String[] fname = a.getFullName().substring(1, a.getFullName().length() - 1).trim().split("&&");
				for (int j = 0; j < fname.length; j++) {
					FullNameInfo forName = new FullNameInfo();
					forName.setShortName(fname[j].split(":")[0].trim());
					forName.setFullName(fname[j].split(":")[1].trim());
					forFullName.add(forName);
				}
				a.setForClassNames(forClass);
				a.setFullExamNames(forFullName);
			}
			showTable = true;
			showPanel = false;
		} else {
			showTable = false;
			showPanel = true;
		}
	}

	public void loadClass() {
		selectedClasses = new ArrayList<>();
		viewClass = new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		String[] classIds = selectedRow.getClassid().substring(1, selectedRow.getClassid().length() - 1).trim()
				.split("-");
		for (int i = 0; i < classIds.length; i++) {
			SelectItem fcls = new SelectItem();
			fcls.setValue(classIds[i].trim());
			selectedClasses.add(classIds[i].trim());
			fcls.setLabel(new DatabaseMethods1().classname(classIds[i].trim(), schid, conn));
			viewClass.add(fcls);
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateClasses() {
		Connection conn = DataBaseConnection.javaConnection();

		String classes = "";
		if (selectedClasses.size() == 1) {
			classes = "{" + selectedClasses.get(0) + "}";
		} else {
			for (int k = 0; k < selectedClasses.size(); k++) {

				if (k == 0) {
					classes = "{" + selectedClasses.get(k) + "-";
				} else if (k == selectedClasses.size() - 1) {
					classes = classes + selectedClasses.get(k) + "}";
				} else {
					classes = classes + selectedClasses.get(k) + "-";
				}

			}
		}
		 // System.out.println(classes);

		int status = dbexam.updateClasses(selectedRow.getId(), classes, conn);
		if (status > 0) {
			 // System.out.println(status);
			PrimeFaces.current().executeInitScript("PF('clsDlg').hide();");
		}

		allFullNameMethods(conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadNames() {
		Connection conn = DataBaseConnection.javaConnection();
		allExamNamesDlg = new ArrayList<>();
		int i = 1;
		String[] fname = selectedRowName.getFullName().substring(1, selectedRowName.getFullName().length() - 1).trim()
				.split("&&");
		for (int j = 0; j < fname.length; j++) {
			ExamInfo forName = new ExamInfo();
			forName.setSno(i);
			forName.setExamNameUpperCase(fname[j].split(":")[0].trim());
			forName.setFullName(fname[j].split(":")[1].trim());

			if (forName.getExamNameUpperCase().equalsIgnoreCase(forName.getFullName())) {
				forName.setDisableEdit(true);
			}
			i++;
			allExamNamesDlg.add(forName);
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateNames() {
		Connection conn = DataBaseConnection.javaConnection();

		String fullNames = "";
		if (allExamNamesDlg.size() == 1) {
			fullNames = "{" + allExamNamesDlg.get(0).getExamNameUpperCase() + ":" + allExamNamesDlg.get(0).getFullName()
					+ "}";
		} else {
			for (int j = 0; j < allExamNamesDlg.size(); j++) {
				if (j == 0) {
					fullNames = "{" + allExamNamesDlg.get(j).getExamNameUpperCase() + ":"
							+ allExamNamesDlg.get(j).getFullName() + " && ";
				} else if (j == allExamNamesDlg.size() - 1) {
					fullNames = fullNames + allExamNamesDlg.get(j).getExamNameUpperCase() + ":"
							+ allExamNamesDlg.get(j).getFullName() + "}";
				} else {
					fullNames = fullNames + allExamNamesDlg.get(j).getExamNameUpperCase() + ":"
							+ allExamNamesDlg.get(j).getFullName() + " && ";
				}
			}
		}

		int status = dbexam.updateNames(selectedRowName.getId(), fullNames, conn);
		if (status > 0) {
			 // System.out.println(status);
			PrimeFaces.current().executeInitScript("PF('nameDlg').hide();");
		}

		allFullNameMethods(conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeNames() {
		Connection conn = DataBaseConnection.javaConnection();
		int status = dbexam.removeNames(selectedFullRow.getId(), "Deleted", conn);
		if (status > 0) {
			PrimeFaces.current().executeInitScript("PF('dlg1').hide();");
			allFullNameMethods(conn);

		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadFullRow() {
		Connection conn = DataBaseConnection.javaConnection();
		viewALlClass = new DatabaseMethods1().allClass(conn);
		allExamNamesDlg = new ArrayList<>();
		allExamNames = new ArrayList<>();
		String[] classIds = selectedforAdd.getClassid().substring(1, selectedforAdd.getClassid().length() - 1).trim()
				.split("-");
		for (int i = 0; i < classIds.length; i++) {
			selectedcls.add(classIds[i].trim());
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openNames() {
		Connection conn = DataBaseConnection.javaConnection();
		allExamNamesDlg = new ArrayList<>();
		String[] fname = selectedforAdd.getFullName().substring(1, selectedforAdd.getFullName().length() - 1).trim()
				.split("&&");
		for (int j = 0; j < fname.length; j++) {
			ExamInfo forName = new ExamInfo();
			forName.setExamName(fname[j].split(":")[0].trim());
			forName.setExamNameUpperCase(fname[j].split(":")[0].trim());
			forName.setFullName(fname[j].split(":")[1].trim());

			if (forName.getExamNameUpperCase().equalsIgnoreCase(forName.getFullName())) {
				forName.setDisableEdit(true);
			}

			allExamNamesDlg.add(forName);
		}

		allExamNames = new ArrayList<>();
		StringBuilder classIds = new StringBuilder();

		for (int i = 0; i < selectedcls.size(); i++) {
			classIds.append(selectedcls.get(i) + "','");
		}
		String classid = classIds.substring(0, classIds.length() - 3);
		allExamNames = dbexam.getPeridicExamDetails(classid, session, schid, conn);
		allExamNames = dbexam.getAllExamsDetails(classid, session, schid, conn, "other", allExamNames);

		int l = 1;
		for (int x = 0; x < allExamNames.size(); x++) {
			int cn = 0;
			for (int j = 0; j < allExamNamesDlg.size(); j++) {
				if (allExamNamesDlg.get(j).getExamNameUpperCase()
						.equalsIgnoreCase(allExamNames.get(x).getExamNameUpperCase())) {
					allExamNamesDlg.get(x).setSno(l);
					cn = cn + 1;
				}
			}
			if (cn == 0) {
				allExamNamesDlg.get(x).setSno(l);
				allExamNamesDlg.add(allExamNames.get(x));
			}
			l++;
		}

		showAddButt = false;
		showTableEdit = true;

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		Connection conn = DataBaseConnection.javaConnection();
		
		 // System.out.println("in save method");
		String fullNames = "";
		int count = 0;
		
		if (allExamNamesDlg.size() == 1) {
			fullNames = "{" + allExamNamesDlg.get(0).getExamName() + ":" + allExamNamesDlg.get(0).getFullName() + "}";
		} else {
			for (int j = 0; j < allExamNamesDlg.size(); j++) {
				if(allExamNamesDlg.get(j).getFullName().equals("")) {
					count = 1;
				}else {
					if (j == 0) {
						fullNames = "{" + allExamNamesDlg.get(j).getExamName() + ":" + allExamNamesDlg.get(j).getFullName()
								+ " && ";
					} else if (j == allExamNamesDlg.size() - 1) {
						fullNames = fullNames + allExamNamesDlg.get(j).getExamName() + ":" + allExamNamesDlg.get(j).getFullName()
								+ "}";
					} else {
						fullNames = fullNames + allExamNamesDlg.get(j).getExamName() + ":" + allExamNamesDlg.get(j).getFullName()
								+ " && ";
					}
				}
			}
		}
		String classes = "";
		if (selectedcls.size() == 1) {
			classes = "{" + selectedcls.get(0) + "}";
		} else {
			for (int k = 0; k < selectedcls.size(); k++) {

				if (k == 0) {
					classes = "{" + selectedcls.get(k) + "-";
				} else if (k == selectedcls.size() - 1) {
					classes = classes + selectedcls.get(k) + "}";
				} else {
					classes = classes + selectedcls.get(k) + "-";
				}

			}
		}
		
		if(count == 0) {
			int status = dbexam.updateFullNames(selectedforAdd.getId(),fullNames, classes, conn);
			if (status > 0) {
				// System.out.println(status);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated"));
				selectedcls = new ArrayList<>();
				allFullNameMethods(conn);
				PrimeFaces.current().executeInitScript("PF('addDlg').hide();");
				
			}
		}else {
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Write Full Name First", "Please Write Full Name First"));
		PrimeFaces.current().executeInitScript("PF('addDlg').hide();");
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

	public ArrayList<ExamInfo> getAllFullNames() {
		return allFullNames;
	}

	public void setAllFullNames(ArrayList<ExamInfo> allFullNames) {
		this.allFullNames = allFullNames;
	}

	public ExamInfo getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(ExamInfo selectedRow) {
		this.selectedRow = selectedRow;
	}

	public ArrayList<String> getSelectedClasses() {
		return selectedClasses;
	}

	public void setSelectedClasses(ArrayList<String> selectedClasses) {
		this.selectedClasses = selectedClasses;
	}

	public ArrayList<SelectItem> getViewClass() {
		return viewClass;
	}

	public void setViewClass(ArrayList<SelectItem> viewClass) {
		this.viewClass = viewClass;
	}

	public ArrayList<ExamInfo> getAllExamNamesDlg() {
		return allExamNamesDlg;
	}

	public void setAllExamNamesDlg(ArrayList<ExamInfo> allExamNamesDlg) {
		this.allExamNamesDlg = allExamNamesDlg;
	}

	public ExamInfo getSelectedRowName() {
		return selectedRowName;
	}

	public void setSelectedRowName(ExamInfo selectedRowName) {
		this.selectedRowName = selectedRowName;
	}

	public ExamInfo getSelectedFullRow() {
		return selectedFullRow;
	}

	public void setSelectedFullRow(ExamInfo selectedFullRow) {
		this.selectedFullRow = selectedFullRow;
	}

	public boolean isShowPanel() {
		return showPanel;
	}

	public void setShowPanel(boolean showPanel) {
		this.showPanel = showPanel;
	}

	public ArrayList<SelectItem> getViewALlClass() {
		return viewALlClass;
	}

	public void setViewALlClass(ArrayList<SelectItem> viewALlClass) {
		this.viewALlClass = viewALlClass;
	}

	public ArrayList<String> getSelectedcls() {
		return selectedcls;
	}

	public void setSelectedcls(ArrayList<String> selectedcls) {
		this.selectedcls = selectedcls;
	}

	public ExamInfo getSelectedforAdd() {
		return selectedforAdd;
	}

	public void setSelectedforAdd(ExamInfo selectedforAdd) {
		this.selectedforAdd = selectedforAdd;
	}

	public boolean isShowAddButt() {
		return showAddButt;
	}

	public void setShowAddButt(boolean showAddButt) {
		this.showAddButt = showAddButt;
	}

	public boolean isShowTableEdit() {
		return showTableEdit;
	}

	public void setShowTableEdit(boolean showTableEdit) {
		this.showTableEdit = showTableEdit;
	}

}
