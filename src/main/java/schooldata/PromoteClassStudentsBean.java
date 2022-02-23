package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

@ManagedBean(name="promoteClassStudentBean")
@ViewScoped
public class PromoteClassStudentsBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<SelectItem> classList,sectionList;
	SchoolInfoList info;
	String selectedClass,selectedSection,selectedPromoteToClass,carryForward,session;
	ArrayList<SelectItem> sessionList,promotionClassList;
	boolean renderTable;
	ArrayList<StudentInfo> studentList;
	List<StudentInfo> selectedStudentList;
	boolean renderPromotePanel;
	Date postDate;

	ArrayList<SelectItem> sectionList2,classlist2;
	String selectedPromoteToSection;

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void selectAndShowPromotPanel()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		DatabaseMethods1 obj=new DatabaseMethods1();
		classlist2=obj.allClassBySession(session, conn);
		
		if(classlist2.size()>0)
		{
			if(selectedStudentList.size()>0)
			{
				renderPromotePanel=true;
				PrimeFaces.current().scrollTo("form:promoteTo");
			}
			else
			{
				renderPromotePanel=false;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast One Student to Demote."));
			}
			
		}
		else
		{
			renderPromotePanel=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Go To Change Session, And Make Session Setting For Classes Before Promotion"));
	
		}
		
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void promotion()
	{
		ArrayList<StudentInfo> errorStudents=new ArrayList<>();
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		DataBaseValidator DBV=new DataBaseValidator();
		String schoolid=DBM.schoolId();

		boolean check = false;
		if(selectedPromoteToClass.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select Promotion Class", "Validation Error"));
		}
		double dueFee=0;
		boolean classStatus=DBM.classEntryStatusForPromotion(session,conn);
		boolean transportStatus=DBM.transportEntryStatusForPromotion(session,conn);
		
		if(classStatus==true && transportStatus==true)
		{
			if(selectedStudentList!=null)
			{
				for(StudentInfo ll : selectedStudentList)
				{
					
					if(carryForward.equals("Yes"))
					{
						dueFee=DBM.totalDueAmountForStudent(ll.getAddNumber(),conn);
					}

					if(schoolid.equals("206"))
					{
						boolean stuExp=DBM.checkStudentExpense(ll.getAddNumber(),conn);
						if(stuExp==true)
						{
							int billGenerated=DBV.checkStudentBillGeneration(ll.getAddNumber(),conn);
							if(billGenerated==2)
							{
								check=DBM.promoteStudentsToClass(selectedPromoteToSection,selectedSection,ll,postDate,session,conn);
								if(check)
								{
									String refNo;
									try {
										refNo=addWorkLog(conn,ll);
									} catch (Exception e) {
										// TODO: handle exception
									}
									String classid = DBM.classSectionNameFromidSchidSession(DBM.schoolId(), selectedPromoteToClass, session, conn);
									DatabaseMethods1.transportDataEntryForPromptedStudent(String.valueOf(ll.getAddNumber()),session,ll.getRouteName(),classid,conn);
									String refNo2;
									try {
										refNo2=addWorkLog2(conn,ll);
									} catch (Exception e) {
										// TODO: handle exception
									}
									DBM.updatePreviousFees(ll.getAddNumber(), dueFee,session,conn);
									String refNo3;
									try {
										refNo3=addWorkLog3(conn,ll,dueFee);
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							}
							else
							{
								errorStudents.add(ll);
							}
						}
						else
						{
							check=DBM.promoteStudentsToClass(selectedPromoteToSection,selectedSection,ll,postDate,session,conn);
							if(check)
							{
								String refNo;
								try {
									refNo=addWorkLog(conn,ll);
								} catch (Exception e) {
									// TODO: handle exception
								}
								String classid = DBM.classSectionNameFromidSchidSession(DBM.schoolId(), selectedPromoteToClass, session, conn);
								DatabaseMethods1.transportDataEntryForPromptedStudent(String.valueOf(ll.getAddNumber()),session,ll.getRouteName(), classid,conn);
								String refNo2;
								try {
									refNo2=addWorkLog2(conn,ll);
								} catch (Exception e) {
									// TODO: handle exception
								}
								DBM.updatePreviousFees(ll.getAddNumber(), dueFee,session,conn);
								String refNo3;
								try {
									refNo3=addWorkLog3(conn,ll,dueFee);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}
					}
					else
					{
						 // System.out.println(selectedPromoteToSection+"...."+selectedSection+"......"+session);
						check=DBM.promoteStudentsToClass(selectedPromoteToSection,selectedSection,ll,postDate,session,conn);
						if(check)
						{
							String refNo;
							try {
								refNo=addWorkLog(conn,ll);
							} catch (Exception e) {
								// TODO: handle exception
							}
							
							String classid = DBM.classSectionNameFromidSchidSession(DBM.schoolId(), selectedPromoteToClass, session, conn);
							DatabaseMethods1.transportDataEntryForPromptedStudent(String.valueOf(ll.getAddNumber()),session,ll.getRouteName(),classid,conn);
							String refNo2;
							try {
								refNo2=addWorkLog2(conn,ll);
							} catch (Exception e) {
								// TODO: handle exception
							}
							DBM.updatePreviousFees(ll.getAddNumber(), dueFee,session,conn);
							String refNo3;
							try {
								refNo3=addWorkLog3(conn,ll,dueFee);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}

					}
				}
			}
			if(check)
			{
				if(DBM.schoolId().equals("216")||DBM.schoolId().equals("302")||DBM.schoolId().equals("221")) {
					
					ArrayList<EmployeeInfo> principleId = new DatabaseMethods1().getPrincipleId(DBM.schoolId(), conn);
					String currTId = new DatabaseMethods1().getClassTeacherId(selectedClass, selectedSection, DBM.schoolId(), DBM.selectedSessionDetails(DBM.schoolId(),conn), conn);
					String cuuTName = new DatabaseMethods1().employeeNameById(currTId, conn);
					String nextTId = new DatabaseMethods1().getClassTeacherId(selectedPromoteToClass, selectedPromoteToSection, DBM.schoolId(), session, conn);
					String nextTName = new DatabaseMethods1().employeeNameById(nextTId, conn);
					
					String className1 = DBM.classname(selectedClass, DBM.schoolId(), conn);
					String sectionName1 = DBM.sectionNameByIdSchid(DBM.schoolId(), selectedSection, conn);
					String className2 =new DatabaseMethods1().classNameFromidSchid(DBM.schoolId(), selectedPromoteToClass, session, conn);
					String sectionName2 =DBM.sectionNameByIdSchidSession(DBM.schoolId(), selectedPromoteToSection, session, conn);
					EmployeeInfo s2 = new EmployeeInfo();
					s2.setName(nextTName);
					s2.setCategory("Next Teacher");
					s2.setEmpId(nextTId);
					if(!nextTId.equals("")) {
						s2.setId(Integer.valueOf(nextTId));
					}else {
						s2.setId(0);
					}
					
					EmployeeInfo s1 = new EmployeeInfo();
					s1.setName(cuuTName);
					s1.setCategory("Teacher");
					if(!currTId.equals("")) {
						s1.setId(Integer.valueOf(currTId));
					}else {
						s1.setId(0);
					}
					s1.setEmpId(currTId);
					
					principleId.add(s1);
					principleId.add(s2);
					
					
					StringBuilder stds = new StringBuilder();
					String ex1 = "";
					String ex2 = "";
					String ex3 = "";
					String ex4 = "";
					if(selectedStudentList.size()==1) {
						stds.append("1. "+selectedStudentList.get(0).getFullName());
						ex1 = "is";
						ex2 = "student";
						ex3 = "name";
						ex4 = "record";
					}else {
						for(int i = 1;i<=selectedStudentList.size();i++) {
							stds.append(i+". "+selectedStudentList.get(i-1).getFullName()+"\n");
						}
						ex1 = "are";
						ex2 = "students";
						ex3 = "names";
						ex4 = "records";
					}
					
					for(EmployeeInfo empi : principleId) {
						String msg = "";
						if(empi.getCategory().equalsIgnoreCase("Principal")||empi.getCategory().equalsIgnoreCase("Vice Principal")) {
							msg = "Dear "+empi.getCategory()+",\r\n" + 
									"\r\n" + 
									"Following "+ex2+" of class "+className1+" - "+sectionName1+" "+ex1+" promoted  to class "+className2+" - "+sectionName2+".\r\n" + 
									stds.toString() +
									"\r\n" + 
									"\r\n" + 
									"Regards\r\n" + 
									"\r\n" + 
									info.getSmsSchoolName();
							
						}else if(empi.getCategory().equalsIgnoreCase("Next Teacher")) {
							msg = "Dear "+empi.getName()+",\r\n" + 
									"Following "+ex2+" "+ex1+" promoted to your class "+className2+" - "+sectionName2+".\r\n" + 
									"\r\n" + 
									stds.toString()+
									"\r\n" + 
									"Please enroll "+ex3+" in your  class "+ex4+"\r\n" + 
									"\r\n" + 
									"Regards\r\n" + 
									"\r\n" + 
									info.getSmsSchoolName();
							
						}else if(empi.getCategory().equals("Teacher")) {
							
							msg =  "Dear "+empi.getName()+",\r\n" + 
									"Following "+ex2+" of your class "+className1+" - "+sectionName1+" "+ex1+" Promoted to class "+className2+" - "+sectionName2+".\r\n" + 
									"\r\n" + 
									stds.toString()+
									"\r\n" + 
									"Please forward all relevent details to class teacher of class "+className2+" - "+sectionName2+".\r\n" + 
									"\r\n" + 
									"Regards\r\n" + 
									"\r\n" + 
									info.getSmsSchoolName();
						}
						
						new SendingNotifications().sendNotifi(msg, DBM.schoolId(), "", "", session, "Teacher", Integer.toString(empi.getId()), "Promotion");
					}
//					
				}
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, +selectedStudentList.size()+" Student Promotion was Successful", ""));
				studentList=new DatabaseMethods1().getAllStudentStrentgth(selectedSection, conn);
				renderPromotePanel=false;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Select atleast one student to promote", ""));
			}

			if(errorStudents.size()>0)
			{
				for(StudentInfo si : errorStudents)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bill(s) are not generated for "+si.getAddNumber()+"-"+si.getFullName()+". Please Generate Bill", ""));
				}
			}

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Go To Change Session, And Make Session Setting For Classes Before Promotion",""));

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String addWorkLog(Connection conn,StudentInfo ll)
	{
	    String value = "";
		String language= "";
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		String classNamePro=obj.classname(selectedPromoteToClass, schid, conn);
		String sectionnamePro =obj.sectionNameByIdSchid(schid,selectedPromoteToSection, conn);
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String admDate = "";
		try {
			admDate = formatter.format(postDate);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		language = "Class-"+className+" --Section-"+sectionname+" --promted Class-"+classNamePro+" --Section promoted-"+sectionnamePro+" --Session-"+session+" --Date-"+admDate+" --Carry Forward-"+carryForward;
		
	     value = "Student Id-"+ll.getAddNumber();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Student promotion","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn,StudentInfo ll)
	{
	    String value = "";
		String language= "";
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		String classNamePro=obj.classname(selectedPromoteToClass, schid, conn);
		String sectionnamePro =obj.sectionNameByIdSchid(schid,selectedPromoteToSection, conn);
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String admDate = "";
		try {
			admDate = formatter.format(postDate);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		language = "Class-"+className+" --Section-"+sectionname+" --promted Class-"+classNamePro+" --Section promoted-"+sectionnamePro+" --Session-"+session+" --Date-"+admDate+" --Carry Forward-"+carryForward;
		
	     value = "Student Id-"+ll.getAddNumber()+" --Route-"+ll.getRouteName();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Student promotion transport entry","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog3(Connection conn,StudentInfo ll,double dueFee)
	{
	    String value = "";
		String language= "";
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		String classNamePro=obj.classname(selectedPromoteToClass, schid, conn);
		String sectionnamePro =obj.sectionNameByIdSchid(schid,selectedPromoteToSection, conn);
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String admDate = "";
		try {
			admDate = formatter.format(postDate);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		language = "Class-"+className+" --Section-"+sectionname+" --promted Class-"+classNamePro+" --Section promoted-"+sectionnamePro+" --Session-"+session+" --Date-"+admDate+" --Carry Forward-"+carryForward;
		
	     value = "Student Id-"+ll.getAddNumber()+" --Due fee-"+dueFee;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Student promotion Update previous Fee","WEB",value,conn);
		return refNo;
	}
	

	public PromoteClassStudentsBean(){
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		classList=obj.allClass(conn);
		
		/*sessionList=new ArrayList<SelectItem>();
		for(int i=2011;i<=2021;i++){
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));
			sessionList.add(item);
		}*/

		session=DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
		String selectSession[]=session.split("-");
		session=selectSession[1]+"-"+(Integer.parseInt(selectSession[1])+1);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getStudentStrength()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		studentList=new DatabaseMethods1().getAllStudentStrentgth(selectedSection, conn);
		info = new DatabaseMethods1().fullSchoolInfo(conn);
		if(info.getBranch_id().equals("54"))
		{
			Collections.sort(studentList, new MySalaryComp());
		}
		if(studentList.isEmpty()){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record found", "Validation Error"));
			renderTable=false;
		}
		else{
			renderTable=true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections2()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList2=new DatabaseMethods1().allSectionbySession(session,selectedPromoteToClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isRenderPromotePanel(){
		return renderPromotePanel;
	}
	public void setRenderPromotePanel(boolean renderPromotePanel) {
		this.renderPromotePanel = renderPromotePanel;
	}
	public boolean isRenderTable() {
		return renderTable;
	}
	public void setRenderTable(boolean renderTable) {
		this.renderTable = renderTable;
	}
	public boolean isRenderTableb() {
		return renderTable;
	}
	public void setRenderTableb(boolean renderTableb) {
		this.renderTable = renderTableb;
	}
	public String getSelectedPromoteToClass() {
		return selectedPromoteToClass;
	}
	public void setSelectedPromoteToClass(String selectedPromoteToClass) {
		this.selectedPromoteToClass = selectedPromoteToClass;
	}
	public ArrayList<SelectItem> getPromotionClassList() {
		return promotionClassList;
	}
	public void setPromotionClassList(ArrayList<SelectItem> promotionClassList) {
		this.promotionClassList = promotionClassList;
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
	public boolean isB() {
		return renderTable;
	}
	public void setB(boolean b) {
		this.renderTable = b;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<SelectItem> getSectionList2() {
		return sectionList2;
	}
	public void setSectionList2(ArrayList<SelectItem> sectionList2) {
		this.sectionList2 = sectionList2;
	}

	public String getSelectedPromoteToSection() {
		return selectedPromoteToSection;
	}
	public void setSelectedPromoteToSection(String selectedPromoteToSection) {
		this.selectedPromoteToSection = selectedPromoteToSection;
	}



	public Date getPostDate() {
		return postDate;
	}
	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}
	public List<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}
	public void setSelectedStudentList(List<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}
	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}
	public String getCarryForward() {
		return carryForward;
	}
	public void setCarryForward(String carryForward) {
		this.carryForward = carryForward;
	}
	
	
	class MySalaryComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			String srno1 = e1.getSrNo().substring(4, e1.getSrNo().length());
			String srno2 = e2.getSrNo().substring(4, e2.getSrNo().length());

			int sr1 = Integer.parseInt(srno1);
			int sr2 = Integer.parseInt(srno2);

			if(sr1 >= sr2)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}


	public ArrayList<SelectItem> getClasslist2() {
		return classlist2;
	}

	public void setClasslist2(ArrayList<SelectItem> classlist2) {
		this.classlist2 = classlist2;
	}
}
