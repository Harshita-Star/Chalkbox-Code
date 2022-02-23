package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import reports_module.DataBaseMethodsReports;

@ManagedBean(name = "bulkTransporthBean")
@ViewScoped
public class BulkTransportBean implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> arrayList = new ArrayList<>();
	ArrayList<StudentInfo> studentList, multipleStudentList;
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass;
	String selectedSection;
	String className, section, total, userType, staff, schoolid;
	ArrayList<ClassInfo> list;
	boolean b;
	DatabaseMethods1 obj=new DatabaseMethods1();
	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = obj.allSection(selectedClass, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public BulkTransportBean() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ses.getAttribute("username");
		userType=(String)ses.getAttribute("type");
		schoolid=(String) ses.getAttribute("schoolid");
		classList=obj.allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getStudentStrength() {
		Connection conn = DataBaseConnection.javaConnection();
		if (selectedSection.equals("-1")) {
			studentList = obj.bulkTransportFeesForAllSection(selectedClass, conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else
				b = true;

			total = String.valueOf(studentList.size());
			className = "All";
			sectionName = "All";
		} else {

			studentList = obj.bulkTransportDetailByParticularSection(selectedSection, conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else
				b = true;

			total = String.valueOf(studentList.size());

		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void calculateTransportFee() {
		Connection conn = DataBaseConnection.javaConnection();


		FacesContext context = FacesContext.getCurrentInstance();
		int k = (int) UIComponent.getCurrentComponent(context).getAttributes().get("serialNo");
		for (StudentInfo tt : studentList) {

			if (k == tt.getSno()) {
				tt.setTranportFee(obj.transportFeeByGroupId(tt.getNewRoute(), conn));
				tt.setTotalFee(tt.getTranportFee() - tt.getDiscountFees());
			}

		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calculateDiscountFee() {
		Connection conn = DataBaseConnection.javaConnection();
		FacesContext context = FacesContext.getCurrentInstance();
		int k = (int) UIComponent.getCurrentComponent(context).getAttributes().get("serialNo");
		for (StudentInfo tt : studentList) {
			if (k == tt.getSno()) {
				if (tt.getTranportFee() >= tt.getDiscountFees()) {
					tt.setTotalFee(tt.getTranportFee() - tt.getDiscountFees());
				} else {
					tt.setDiscountFees(0);
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Discount should be less than or equal to Transport Fee !!"));
				}

			}
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addFees() {
		Connection conn = DataBaseConnection.javaConnection();
		ArrayList<Transport> transportDetails = new ArrayList<>();
		String session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
		for (StudentInfo tt : multipleStudentList) {
			transportDetails = obj.transportListDetails(obj.schoolId(),tt.getAddNumber(), session, conn);
			obj.updateTransportDetailInRegistration(tt.getAddNumber(), session, tt.getNewRoute(),
					tt.getDiscountFees(), conn);
			if (transportDetails.size() <= 0) {
				if (tt.getNewRoute().equals("")) {

				} else {
					obj.bulkTransportDataEntry(tt.getDate(),
							tt.getAddNumber(), tt.getNewRoute(), "Yes", tt.getClassId(), conn);

				}

			} else {

				new DataBaseMethodsReports().deleteStudentTransport(DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn), tt.getAddNumber(), conn);
				if(tt.getNewRoute().equals(""))
				{
					obj.transportDataEntry(obj.schoolId(),tt.getDate(),tt.getAddNumber(), tt.getNewRoute(), "No", tt.getClassId(),conn);
				}
				else
				{
					 // System.out.println("enter");
					
					obj.transportDataEntry(obj.schoolId(),tt.getDate(),tt.getAddNumber(), tt.getNewRoute(), "Yes", tt.getClassId(),conn);
				}

				/*if (tt.getNewRoute().equals("")) {
					updateTransportDetail = obj.updateTransport(tt.getAddNumber(), "No", "0", conn);
				} else {
					updateTransportDetail = obj.updateTransport(tt.getAddNumber(), "Yes",
							tt.getNewRoute(), conn);
				}*/
			}
		}
		if (multipleStudentList.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast one Row !! "));
		} else {
			//if (updateTransportDetail == true) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Transport Added Successfully "));
			b = false;
			/*} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("some Error Occurred !!"));
			}*/
		}
		
		try {
			conn.close();
		} catch (Exception e) {
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

	String sectionName;

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public ArrayList<StudentInfo> getMultipleStudentList() {
		return multipleStudentList;
	}

	public void setMultipleStudentList(ArrayList<StudentInfo> multipleStudentList) {
		this.multipleStudentList = multipleStudentList;
	}

}
