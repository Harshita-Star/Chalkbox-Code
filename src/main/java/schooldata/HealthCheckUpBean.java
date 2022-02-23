package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;

@ManagedBean (name="healthCheckUp")
@ViewScoped
public class HealthCheckUpBean implements Serializable{

	String regex=RegexPattern.REGEX;
	private String checkUp="",height="",weight="",bloodPressure="",rbc="",wbc="",hemoglobin="",totalCholesterol="",hdlCholesterol=""
			,ldlCholesterol="",disease="",treatment="",remarks="",addDiseaseName="",selectedStudent="";
	private Date checkUpDate=new Date();
	private ArrayList<SelectItem> diseaseList;
	ArrayList<StudentInfo> studentList;
	StudentInfo selectedInfo = new StudentInfo();
	String  timevalue;
	String bloodgroup;
	private boolean showRoutineCheckUp,showDiseaseCheckUp,showDetails;
	DataBaseMethodsHostelModule obj=new DataBaseMethodsHostelModule();

	public HealthCheckUpBean() {
		Connection con=DataBaseConnection.javaConnection();
		checkUp="routineCheckUp";
		diseaseList=obj.viewAllDisease(con);

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateDetails() {
		Connection conn=DataBaseConnection.javaConnection();
		showDetails=true;
		if(checkUp.equals("routineCheckUp"))
		{
			showRoutineCheckUp=true;
			showDiseaseCheckUp=false;
		}
		else {
			showRoutineCheckUp=false;
			showDiseaseCheckUp=true;
		}
		String studentId=selectedStudent.substring(selectedStudent.lastIndexOf("-")+1);
		StudentInfo studentlist=new DatabaseMethods1().studentDetailslistByAddNo(new DatabaseMethods1().schoolId(),studentId,conn);

		height=studentlist.getHeight();
		weight=studentlist.getWeight();
		bloodgroup=studentlist.getBloodGroup();


		timevalue=new SimpleDateFormat("hh:mm a").format(new Date());
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<String> autoCompleteStudent(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	public void addDiseaseNameMethod() {

		Connection conn=DataBaseConnection.javaConnection();
		if(addDiseaseName.trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Disease Name Cannot be blank"));
		}
		else
		{
			boolean duplicateDiseaseName=obj.checkForDuplicateDiseaseName(conn,addDiseaseName);
			if(duplicateDiseaseName==false)
			{
				int i=obj.addDiseaseName(addDiseaseName,conn);
				if(i>0)
				{
					String refNo;
					try {
						refNo=addWorkLog(conn);
					} catch (Exception e) {
						// TODO: handle exception
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Disease Added Sucessfully."));
					diseaseList=obj.viewAllDisease(conn);
					addDiseaseName="";
					PrimeFaces.current().executeInitScript("PF('diseaseName').hide()");
					PrimeFaces.current().ajax().update("form");
					PrimeFaces.current().ajax().update("form2");
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
				}}
			else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Disease Name Found"));
			}
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= addDiseaseName ;
		
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Disease","WEB",value,conn);
		return refNo;
	}

	public String submit() {
		String sts = "no";
		Connection conn=DataBaseConnection.javaConnection();
		String studentId=selectedStudent.substring(selectedStudent.lastIndexOf(":")+1);
		for(StudentInfo info : studentList)
		{
			if(String.valueOf(info.getAddNumber()).equals(studentId))
			{
				try
				{
					selectedInfo=info;
					sts="yes";
					break;
					//return "studentFeeCollection";
					//show=true;
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}

		if(sts.equalsIgnoreCase("yes"))
		{
			int i=obj.addCheckUp(checkUp,height,weight,bloodPressure,rbc,wbc,hemoglobin,totalCholesterol,hdlCholesterol,ldlCholesterol,disease,treatment,remarks,studentId,checkUpDate,timevalue,conn);
			if(i>0)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Check Up Added Sucessfully."));
				new DatabaseMethods1().notification(new DatabaseMethods1().schoolId(),"Wellness","Health check up detail of "+selectedInfo.getFname()+" is added.", selectedInfo.getFathersPhone()+"-"+selectedInfo.getAddNumber()+"-"+new DatabaseMethods1().schoolId(),conn);
				new DatabaseMethods1().notification(new DatabaseMethods1().schoolId(),"Wellness","Health check up detail of "+selectedInfo.getFname()+" is added.", selectedInfo.getMothersPhone()+"-"+selectedInfo.getAddNumber()+"-"+new DatabaseMethods1().schoolId(),conn);
				return "healthCheckUp.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast One Student!"));
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
	
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		String studentId=selectedStudent.substring(selectedStudent.lastIndexOf(":")+1);
		
	    value = "Checkup-"+checkUp+" -- Height-"+height+" -- Weight-"+weight+" -- BP-"+bloodPressure+" -- Rbc-"+rbc+" -- Wbc-"+wbc+" -- Hemoglobin-"+hemoglobin+" -- TotalCholesterol"+totalCholesterol+" -- HdlCholesterol-"+hdlCholesterol+" -- ldlCholesterol-"+ldlCholesterol+" -- Disease-"+disease+" -- Treatment-"+treatment+" -- Remarks-"+remarks+" -- Student-"+studentId+" -- Date-"+checkUpDate+" -- Time-"+timevalue;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Checkup","WEB",value,conn);
		return refNo;
	}


	public String getCheckUp() {
		return checkUp;
	}

	public void setCheckUp(String checkUp) {
		this.checkUp = checkUp;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getBloodPressure() {
		return bloodPressure;
	}

	public void setBloodPressure(String bloodPressure) {
		this.bloodPressure = bloodPressure;
	}

	public String getRbc() {
		return rbc;
	}

	public void setRbc(String rbc) {
		this.rbc = rbc;
	}

	public String getWbc() {
		return wbc;
	}

	public void setWbc(String wbc) {
		this.wbc = wbc;
	}

	public String getHemoglobin() {
		return hemoglobin;
	}

	public void setHemoglobin(String hemoglobin) {
		this.hemoglobin = hemoglobin;
	}

	public String getTotalCholesterol() {
		return totalCholesterol;
	}

	public void setTotalCholesterol(String totalCholesterol) {
		this.totalCholesterol = totalCholesterol;
	}

	public String getHdlCholesterol() {
		return hdlCholesterol;
	}

	public void setHdlCholesterol(String hdlCholesterol) {
		this.hdlCholesterol = hdlCholesterol;
	}

	public String getLdlCholesterol() {
		return ldlCholesterol;
	}

	public void setLdlCholesterol(String ldlCholesterol) {
		this.ldlCholesterol = ldlCholesterol;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAddDiseaseName() {
		return addDiseaseName;
	}

	public void setAddDiseaseName(String addDiseaseName) {
		this.addDiseaseName = addDiseaseName;
	}

	public Date getCheckUpDate() {
		return checkUpDate;
	}

	public void setCheckUpDate(Date checkUpDate) {
		this.checkUpDate = checkUpDate;
	}

	public ArrayList<SelectItem> getDiseaseList() {
		return diseaseList;
	}

	public void setDiseaseList(ArrayList<SelectItem> diseaseList) {
		this.diseaseList = diseaseList;
	}

	public boolean isShowRoutineCheckUp() {
		return showRoutineCheckUp;
	}

	public void setShowRoutineCheckUp(boolean showRoutineCheckUp) {
		this.showRoutineCheckUp = showRoutineCheckUp;
	}

	public boolean isShowDiseaseCheckUp() {
		return showDiseaseCheckUp;
	}

	public void setShowDiseaseCheckUp(boolean showDiseaseCheckUp) {
		this.showDiseaseCheckUp = showDiseaseCheckUp;
	}

	public boolean isShowDetails() {
		return showDetails;
	}

	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	public String getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(String selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public String getTimevalue() {
		return timevalue;
	}

	public void setTimevalue(String timevalue) {
		this.timevalue = timevalue;
	}

	public String getBloodgroup() {
		return bloodgroup;
	}

	public void setBloodgroup(String bloodgroup) {
		this.bloodgroup = bloodgroup;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}


}
