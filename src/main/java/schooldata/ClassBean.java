package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import session_work.RegexPattern;

@ManagedBean(name = "classBean")
@ViewScoped
public class ClassBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String regex = RegexPattern.REGEX;
	int addmissionFees = 2700, examFee = 1000, activityFee, termFee, artFee, fee, annualfee = 500;
	ArrayList<FeeInfo> list;
	String name, description = "", schid, session;
	String cunt;
	ArrayList<ConnsessionList> connList;
	ArrayList<Category> sectionlist = new ArrayList<>();
	ArrayList<Category> newsectionlist = new ArrayList<>();
	Category selectedList;
	ArrayList<ClassInfo> classList;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseValidator validator = new DataBaseValidator();
	
	int j = 1;

	public ClassBean() {
		Connection conn = DataBaseConnection.javaConnection();

		schid = DBM.schoolId();
		session = DBM.selectedSessionDetails(schid, conn);
		cunt = DBM.allStudentcount(schid, "", "", session, "", conn);
		list = DBM.viewFeeListForCLassAdd(conn);
		list.remove(list.size() - 1);
		list.remove(list.size() - 1);
		list.remove(list.size() - 1);

		classList = new ArrayList<>();
		connList = DBM.allCategoryList(conn);

		for (FeeInfo lst : list) {

			Map<String, String> map = new HashMap<>();
			Map<String, String> map1 = new HashMap<>();

			for (ConnsessionList ls : connList) {
				map.put(ls.getId(), "0");
				map1.put(ls.getId(), "0");

			}

			lst.setNewfeeAmountmap(map);
			lst.setOlfFeeAmountmap(map1);

		}

		for (int iii = 0; iii < 5; iii++) {
			if (iii == 0) {
				j = 1;
				Category ls = new Category();
				ls.setSerialNumber("1");
				ls.setCategory("A");
				ls.setCheck(true);
				ls.setRemove(true);
				sectionlist.add(ls);
				j++;

			} else {
				addmore();
			}
		}

		for (int jj = 0; jj < 5; jj++) {
			Category lsttt = new Category();
			lsttt.setSerialNumber(String.valueOf(jj + 1));
			if (jj == 0) {
				lsttt.setRemove(true);
				lsttt.setCheck(true);
				lsttt.setCategory("A");

			} else if (jj == 1) {
				lsttt.setRemove(false);
				lsttt.setCheck(false);
				lsttt.setCategory("B");

			} else if (jj == 2) {
				lsttt.setRemove(false);
				lsttt.setCheck(false);
				lsttt.setCategory("C");

			} else if (jj == 3) {
				lsttt.setRemove(false);
				lsttt.setCheck(false);
				lsttt.setCategory("D");

			} else if (jj == 4) {
				lsttt.setRemove(false);
				lsttt.setCheck(false);
				lsttt.setCategory("E");

			}

			newsectionlist.add(lsttt);

		}

		for (int ii = 0; ii < 15; ii++) {
			ClassInfo lstt = new ClassInfo();
			lstt.setSno(ii + 1);
			lstt.setCategoryList(newsectionlist);
			classList.add(lstt);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void addmore() {

		Category ls = new Category();
		ls.setSerialNumber(String.valueOf(j));
		ls.setCategory("");
		ls.setCheck(true);
		ls.setRemove(false);
		sectionlist.add(ls);
		j++;

	}

	public void remove() {

		sectionlist.remove(Integer.parseInt(selectedList.getSerialNumber()) - 1);
		int i = 1;
		for (Category ls : sectionlist) {
			if (i == 1) {
				ls.setRemove(true);

			} else {
				ls.setRemove(false);

			}
			ls.setSerialNumber(String.valueOf(i));
			i++;
		}
		j = i;
	}

	// public void check()
	// {
	// FacesContext context = FacesContext.getCurrentInstance();
	// int index = (int)
	// UIComponent.getCurrentComponent(context).getAttributes().get("check");
	//
	// //// // System.out.println(index);
	//
	// for(ConnsessionList lst:connList)
	// {
	//
	// }
	//
	//
	// }

	public String addNewClass() {
		Connection conn = DataBaseConnection.javaConnection();
		try {
			// one section value is must so we are check on 0 index because we hard coded
			// that there must be some value on 0 index
			 // System.out.println(sectionlist.get(0).getCategory());
			if (sectionlist.get(0).getCategory().trim().isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Section name can not be empty",
						"Validation error"));
			}
			else 
			{
				if (validator.duplicateClass(name, conn)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Duplicate entry found,choose a different class name", "Validation error"));
				} else {
					int status = DBM.newClassAdd(name, description, list, connList, sectionlist, conn);
					if (status == 1) {
						// dont know why we are using this variable
						String refNo = addWorkLog(conn);
						
						
						
						
						
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Class added successfully"));
						return "addClass.xhtml";
					} else {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"An error occurred, try again ", "Validation error"));
					}
				}
				
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	public String addWorkLog(Connection conn) {
		String value = "";
		String language = name;

		for (Category cat : sectionlist) {
			value += "(" + cat.getCategory() + ")";
		}

		return workLg.saveWorkLogMehod(language, "Add Class", "WEB", value, conn);
	}

	public String addNewClassType() {
		Connection conn = DataBaseConnection.javaConnection();
		try {
			int flag = 0;
			/*
			 * if (flag == 0) { int status = new DataBaseValidator().duplicateClass(name,
			 * conn); if (status == 0) { flag++;
			 * FacesContext.getCurrentInstance().addMessage(null, new
			 * FacesMessage(FacesMessage.SEVERITY_ERROR,
			 * "Duplicate entry found,choose a different class name", "Validation error"));
			 * } }
			 */

			if (flag == 0) {
				try {
					for (ClassInfo info : classList) {
						name = info.getClassName().trim();
						if (!name.equalsIgnoreCase("")) {
							DBM.newClassAdd(name, description, list, connList, info.getCategoryList(), conn);

						}

					}
					/*
					 * if (status == 1) { FacesContext fc = FacesContext.getCurrentInstance();
					 * fc.addMessage(null, new FacesMessage("Class added successfully")); //return
					 * "addClass.xhtml"; } else { FacesContext.getCurrentInstance().addMessage(null,
					 * new FacesMessage(FacesMessage.SEVERITY_ERROR,
					 * "An error occurred, try again ", "Validation error")); }
					 */
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	
	public String addClass() {
		Connection conn = DataBaseConnection.javaConnection();
		try {
			// one section value is must so we are check on 0 index because we hard coded
			// that there must be some value on 0 index
			 // System.out.println(sectionlist.get(0).getCategory());
			if (sectionlist.get(0).getCategory().trim().isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Section name can not be empty",
						"Validation error"));
			}
			else 
			{
				if (validator.duplicateClass(name, conn)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Duplicate entry found,choose a different class name", "Validation error"));
				} else {
					int status = DBM.newClassAdd(name, description, list, connList, sectionlist, conn);
					if (status == 1) {
						// dont know why we are using this variable
						String refNo = addWorkLog(conn);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Class added successfully"));
						return "addClass.xhtml";
					} else {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"An error occurred, try again ", "Validation error"));
					}
				}
				
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public void sectionpage() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("addSection.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public void editSection() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editSection.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public void editClass() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editClass.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> sessionDetail() {
		ArrayList<SelectItem> list = new ArrayList<>();
		try {
			for (int i = 1990; i <= 2050; i++) {
				String temp = String.valueOf(i) + "-" + String.valueOf(i + 1);
				SelectItem temp1 = new SelectItem();
				temp1.setLabel(temp);
				temp1.setValue(temp);

				list.add(temp1);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public ArrayList<FeeInfo> getList() {
		return list;
	}

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAnnualfee() {
		return annualfee;
	}

	public void setAnnualfee(int annualfee) {
		this.annualfee = annualfee;
	}

	public void setList(ArrayList<FeeInfo> list) {
		this.list = list;
	}

	public int getActivityFee() {
		return activityFee;
	}

	public void setActivityFee(int activityFee) {
		this.activityFee = activityFee;
	}

	public int getTermFee() {
		return termFee;
	}

	public void setTermFee(int termFee) {
		this.termFee = termFee;
	}

	public int getArtFee() {
		return artFee;
	}

	public void setArtFee(int artFee) {
		this.artFee = artFee;
	}

	public int getExamFee() {
		return examFee;
	}

	public void setExamFee(int examFee) {
		this.examFee = examFee;
	}

	public int getAddmissionFees() {
		return addmissionFees;
	}

	public void setAddmissionFees(int addmissionFees) {
		this.addmissionFees = addmissionFees;
	}

	public String getCunt() {
		return cunt;
	}

	public void setCunt(String cunt) {
		this.cunt = cunt;
	}

	public ArrayList<ConnsessionList> getConnList() {
		return connList;
	}

	public void setConnList(ArrayList<ConnsessionList> connList) {
		this.connList = connList;
	}

	public ArrayList<Category> getSectionlist() {
		return sectionlist;
	}

	public void setSectionlist(ArrayList<Category> sectionlist) {
		this.sectionlist = sectionlist;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public void copy() {

		FacesContext fc = FacesContext.getCurrentInstance();
		int i = (int) UIComponent.getCurrentComponent(fc).getAttributes().get("abc");

		for (FeeInfo bb : list) {
			if (i == bb.getSno()) {
				bb.setOldfeeAmount(bb.getFeeAmount());

			}
		}
	}

	public Category getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(Category selectedList) {
		this.selectedList = selectedList;
	}

	public ArrayList<ClassInfo> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<ClassInfo> classList) {
		this.classList = classList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

}
/*
 * public String addNewClass() { Connection conn =
 * DataBaseConnection.javaConnection();
 * 
 * try {
 * 
 * int flag = 0; if (flag == 0) { int status = validator.duplicateClass(name,
 * conn); if (status == 0) { flag++;
 * FacesContext.getCurrentInstance().addMessage(null, new
 * FacesMessage(FacesMessage.SEVERITY_ERROR,
 * "Duplicate entry found,choose a different class name", "Validation error"));
 * } }
 * 
 * if (flag == 0) { try { int status = DBM.newClassAdd(name, description, list,
 * connList, sectionlist, conn); if (status == 1) {
 * 
 * String refNo; try { refNo=addWorkLog(conn); } catch (Exception e) { // TODO:
 * handle exception } FacesContext fc = FacesContext.getCurrentInstance();
 * fc.addMessage(null, new FacesMessage("Class added successfully")); return
 * "addClass.xhtml"; } else { FacesContext.getCurrentInstance().addMessage(null,
 * new FacesMessage(FacesMessage.SEVERITY_ERROR,
 * "An error occurred, try again ", "Validation error")); } } catch (Exception
 * ex) { ex.printStackTrace(); } } } finally { try { conn.close(); } catch
 * (SQLException e) { e.printStackTrace(); } }
 * 
 * return null;
 * 
 * }
 * 
 * public String addClass() { Connection conn =
 * DataBaseConnection.javaConnection(); try { int flag = 0; if (flag == 0) { int
 * status = validator.duplicateClass(name, conn); if (status == 0) { flag++;
 * FacesContext.getCurrentInstance().addMessage(null, new
 * FacesMessage(FacesMessage.SEVERITY_ERROR,
 * "Duplicate entry found,choose a different class name", "Validation error"));
 * } }
 * 
 * if (flag == 0) { try { int status = 0; if (status == 1) { FacesContext fc =
 * FacesContext.getCurrentInstance(); fc.addMessage(null, new
 * FacesMessage("Class added successfully"));
 * 
 * return "addClass.xhtml"; } else {
 * FacesContext.getCurrentInstance().addMessage(null, new
 * FacesMessage(FacesMessage.SEVERITY_ERROR, "An error occurred, try again ",
 * "Validation error")); } } catch (Exception ex) { ex.printStackTrace(); } } }
 * finally { try { conn.close(); } catch (SQLException e) { e.printStackTrace();
 * } } return null; }
 * 
 */