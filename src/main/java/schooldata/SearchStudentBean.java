
package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.RequestContext;
import org.primefaces.PrimeFaces;

import reports_module.DataBaseMethodsReports;
import session_work.RegexPattern;

@ManagedBean(name="searchStudent")
@ViewScoped
public class SearchStudentBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	boolean byname=false;
	boolean byclass=false;
	boolean classtt=false;
	boolean afterbyname=false,performanceStatus=false,testPerformanceStatus=false;
	String selectedtype,clientType,boardType;
	ArrayList<SelectItem> classSection;
	ArrayList<ClassTest>classTest;
	ArrayList<SelectItem> sectionList,sectionList1;
	String selectedSection,selectedClassSection1,selectedClassSection2,selectedSection1;
	String sectionid;
	String currentClass,addNumber,Studentname,fatherName;
	StudentInfo selectedStudent;
	String name,srNo;
	ArrayList<StudentInfo> studentList;
	boolean show;
	String selectedtransferType;
	String selectedCLassSection;
	boolean classDisabled;
	SchoolInfoList info; 
	public  SearchStudentBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		info = new DatabaseMethods1().fullSchoolInfo(conn);
		try
		{
			classSection=DBM.allClass(conn);
			clientType=DBM.checkClientType(conn);
			boardType=DBM.checkBoardType(conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public String done()
	{
		int i=0;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		try
		{
			if(clientType.equalsIgnoreCase("school"))
			{
				if(selectedtransferType.equalsIgnoreCase("section"))
				{
					performanceStatus=false;
				}
				else
				{
					if(boardType.equalsIgnoreCase("cbse"))
					{
						performanceStatus=DBM.performanceStatusOfStudentCBSE(selectedSection, conn);
					}
					else
					{
						performanceStatus=DBM.performanceStatusOfStudentRBSE(selectedSection, conn);
					}

				}


				if(performanceStatus==true)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You Can't Transfer Student After Adding Performance","You Can't Transfer Student After Adding Performance"));
					return "";
				}
				else
				{
					for(StudentInfo tt: studentList)
					{
						if(tt.getSelectedsection1()!=null && tt.getSelectedclass()!=null )
						{
							boolean checkOptSubj = dbr.checkStudentOptionalSubject(tt.getAddNumber(),DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),DBM.schoolId(),conn);

							if(checkOptSubj)
							{
								FacesContext fc=FacesContext.getCurrentInstance();
								fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,tt.getFname() + " - You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer",tt.getFname() + " - You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer"));
							}
							else
							{
								i=DBM.updateClassStudent(tt.getAddNumber(),tt.getSelectedsection1(),conn);
								if(i>=1)
								{
									String refNo3;
									try{
										refNo3=addWorkLog2(conn,tt.getAddNumber(),tt.getSelectedsection1());
									} catch (Exception e) {
											// TODO: handle exception
									}
									DBM.updateAttendanceOnClassTransfer(tt.getAddNumber(),selectedSection,tt.getSelectedsection1(),conn);
									dbr.updateSectionInStudentPerformace(tt.getAddNumber(), selectedSection, tt.getSelectedsection1(), conn);
									dbr.updateSectionInRankTable(tt.getAddNumber(), selectedSection, tt.getSelectedsection1(), conn);
									dbr.updateSectionInExtraValue(tt.getAddNumber(), selectedSection, tt.getSelectedsection1(), conn);
									dbr.updateSectionInPromotionTable(tt.getAddNumber(), selectedSection, tt.getSelectedsection1(), conn);
									classTest=DBM.classTestListForClassTransfer(selectedSection, tt.getSelectedsection1(), conn);
									/*if(selectedtransferType.equalsIgnoreCase("section"))
									{
										new DatabaseMethods1().udpatesectionInTranferStudent(tt.getAddNumber(),tt.getSelectedsection1(),conn);
									}*/
									for(ClassTest ss:classTest)
									{
										testPerformanceStatus=DBM.classTestPerformanceStatus(ss.getId(),conn);
										if(testPerformanceStatus==true)
										{
											DBM.blockTestPerformanceOnClassTransfer(ss.getSectionid(),conn);
											String refNo9;
											try{
												refNo9=addWorkLog4(conn,ss.getSectionid());
											} catch (Exception e) {
													// TODO: handle exception
											}
										}

									}
								}
							}
						}

					}
					if(i==1)
					{
						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage("Student Transferred Successfully"));

						return "classTransfer.xhtml";
					}
					else
					{

						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Something Went Wrong. Please Try Again","Something Went Wrong. Please Try Again"));
						return "";
					}
				}
			}
			else
			{
				for(StudentInfo tt: studentList)
				{
					if(tt.getSelectedsection1()!=null && tt.getSelectedclass()!=null )
					{
						boolean checkOptSubj = dbr.checkStudentOptionalSubject(tt.getAddNumber(),DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),DBM.schoolId(),conn);

						if(checkOptSubj)
						{
							FacesContext fc=FacesContext.getCurrentInstance();
							fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,tt.getFname() + " - You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer",tt.getFname() + " - You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer"));
						}
						else
						{
							i=DBM.updateClassStudent(tt.getAddNumber(),tt.getSelectedsection1(),conn);
							if(i>=1)
							{
								String refNo4;
								try{
									refNo4=addWorkLog2(conn,tt.getAddNumber(),tt.getSelectedsection1());
								} catch (Exception e) {
										// TODO: handle exception
								}
								DBM.updateAttendanceOnClassTransfer(tt.getAddNumber(),selectedSection,tt.getSelectedsection1(),conn);
								dbr.updateSectionInStudentPerformace(tt.getAddNumber(), selectedSection, tt.getSelectedsection1(), conn);
								dbr.updateSectionInRankTable(tt.getAddNumber(), selectedSection, tt.getSelectedsection1(), conn);
								dbr.updateSectionInExtraValue(tt.getAddNumber(), selectedSection, tt.getSelectedsection1(), conn);
								dbr.updateSectionInPromotionTable(tt.getAddNumber(), selectedSection, tt.getSelectedsection1(), conn);
								classTest=DBM.classTestListForClassTransfer(selectedSection, tt.getSelectedsection1(), conn);
								for(ClassTest ss:classTest)
								{
									testPerformanceStatus=DBM.classTestPerformanceStatus(ss.getId(),conn);
									if(testPerformanceStatus==true)
									{
										DBM.blockTestPerformanceOnClassTransfer(ss.getSectionid(),conn);
										String refNo10;
										try{
											refNo10=addWorkLog4(conn,ss.getSectionid());
										} catch (Exception e) {
												// TODO: handle exception
										}
									}

								}
							}
						}

					}

				}
				if(i==1)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Student Transfered Successfully"));
					return "classTransfer.xhtml";
				}
				else
				{

					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Something Went Wrong. Please Try Again","Something Went Wrong. Please Try Again"));
					return "";
				}
			}
		}
		finally
		{
			try
			{
				conn.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	 public String addWorkLog2(Connection conn,String studentId,String selSectionid)
	{
		    String value = "Student Id-"+studentId+" --Section Id-"+selectedSection+" --Selected Section id-"+selSectionid;
			String language= "";
			
			String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Class transfer","WEB",value,conn);
			return refNo;
		}
	
	
	public String backToManageStudentDashboard()
	{

		show=false;
		name=null;
		selectedCLassSection=null;
		selectedStudent=null;
		return "manageStudentDashboard";
	}

	public void searchClassSection()
	{

		classtt=true;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		try
		{
			studentList=DBM.searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
			classSection=DBM.allClass(conn);
			if(selectedtransferType.equalsIgnoreCase("section"))
			{
				classDisabled=true;
				selectedClassSection1= selectedCLassSection;
				sectionid=selectedSection;
				allSectionforTransfer2();
			}
			else
			{
				selectedClassSection1="";
				classDisabled=false;
			}
			for(StudentInfo tt: studentList)
			{
				tt.setSelectedclass(selectedClassSection1);
				tt.setClassSection(DBM.allClass(conn));
				tt.setSectionList1(sectionList1);
			}



		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public void searchby()
	{
		if(selectedtype.equalsIgnoreCase("byName"))
		{
			byname=true;
			byclass=false;
			classtt=false;
		}
		if(selectedtype.equalsIgnoreCase("byClass"))
		{
			byclass=true;
			byname=false;
			afterbyname=false;
		}

	}

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSectionforTransfer2()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		for(StudentInfo tt: studentList)
		{
			tt.setSectionList1(sectionList1);
		}


		sectionList1=obj.allSection(selectedClassSection1,conn);


		int i=0;
		for(SelectItem ns:sectionList1)
		{
			if(ns.getValue().equals(sectionid))
			{
				sectionList1.remove(i);
				break;
			}
			i++;
		}
		/*	for(StudentInfo tt: studentList) {
			tt.setSectionList1(sectionList1);

			//sectionList1=obj.allSection(selectedClassSection1,conn);
		}
		 */



		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSectionforTransferRowWise()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		FacesContext context = FacesContext.getCurrentInstance();
		int k=(int)UIComponent.getCurrentComponent(context).getAttributes().get("auto1");

		String classid = "";
		ArrayList<SelectItem> secList = new ArrayList<>();

		lp:for(StudentInfo tt: studentList)
		{
			if(k==tt.getSno())
			{
				classid = tt.getSelectedclass();
				secList=obj.allSection(classid,conn);
				tt.setSectionList1(secList);

				break lp;
			}


			//sectionList1=obj.allSection(selectedClassSection1,conn);
		}

		/*sectionList1=obj.allSection(selectedClassSection1,conn);


		int i=0;
		for(SelectItem ns:sectionList1)
		{
			if(ns.getValue().equals(sectionid))
			{
				sectionList1.remove(i);
				break;
			}
			i++;
		}*/
		/*	for(StudentInfo tt: studentList) {
			tt.setSectionList1(sectionList1);

			//sectionList1=obj.allSection(selectedClassSection1,conn);
		}
		 */



		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSectionforTransfer()
	{
		classtt=false;
		Connection conn=DataBaseConnection.javaConnection();

		sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
		int i=0;
		for(SelectItem ns:sectionList)
		{
			if(ns.getValue().equals(sectionid))
			{
				sectionList.remove(i);
				break;
			}
			i++;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void deleteStudent()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			int i=new DatabaseMethods1().deleteStudent(selectedStudent.getAddNumber(),"INACTIVE","ACTIVE",conn);
			if(i==1)
			{
				name="";
				selectedCLassSection=null;
				selectedSection=null;
				sectionList=null;
				selectedStudent=null;
				studentList=null;
				show=false;

				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Student Inactivated Successfully"));
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


	}
	public String transferAll()
	{
		int i=0;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		try
		{
			if(clientType.equalsIgnoreCase("school"))
			{
				if(selectedtransferType.equalsIgnoreCase("section"))
				{
					performanceStatus=false;
				}
				else
				{
					if(boardType.equalsIgnoreCase("cbse"))
					{
						performanceStatus=DBM.performanceStatusOfStudentCBSE(selectedSection, conn);
					}
					else
					{
						performanceStatus=DBM.performanceStatusOfStudentRBSE(selectedSection, conn);
					}
				}
				//performanceStatus=new DatabaseMethods1().performanceStatusOfStudent(selectedSection, conn);
				if(performanceStatus==true)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You Can't Transfer Student After Adding Performance","You Can't Transfer Student After Adding Performance"));
					return "";
				}
				else
				{
					for(StudentInfo tt: studentList)
					{
						if(selectedSection1!=null && selectedClassSection1!=null )
						{
							boolean checkOptSubj = dbr.checkStudentOptionalSubject(tt.getAddNumber(),DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),DBM.schoolId(),conn);

							if(checkOptSubj)
							{
								FacesContext fc=FacesContext.getCurrentInstance();
								fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,tt.getFname() + " - You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer",tt.getFname() + " - You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer"));
							}
							else
							{
								i=DBM.updateClassStudent(tt.getAddNumber(),selectedSection1,conn);
								if(i>=1)
								{
									String refNo5;
									try{
										refNo5=addWorkLog3(conn,tt.getAddNumber());
									} catch (Exception e) {
											// TODO: handle exception
									}
									DBM.updateAttendanceOnClassTransfer(tt.getAddNumber(),selectedSection,selectedSection1,conn);
									dbr.updateSectionInStudentPerformace(tt.getAddNumber(),selectedSection,selectedSection1,conn);
									dbr.updateSectionInRankTable(tt.getAddNumber(),selectedSection,selectedSection1,conn);
									dbr.updateSectionInExtraValue(tt.getAddNumber(),selectedSection,selectedSection1,conn);
									dbr.updateSectionInPromotionTable(tt.getAddNumber(),selectedSection,selectedSection1,conn);
								}
							}

						}
					}
					if(i==1)
					{
						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage("Student Transfered Successfully"));
						classTest=DBM.classTestListForClassTransfer(selectedSection, selectedSection1, conn);
						for(ClassTest ss:classTest)
						{
							testPerformanceStatus=DBM.classTestPerformanceStatus(ss.getId(),conn);
							if(testPerformanceStatus==true)
							{
								DBM.blockTestPerformanceOnClassTransfer(ss.getSectionid(),conn);
								String refNo8;
								try{
									refNo8=addWorkLog4(conn,ss.getSectionid());
								} catch (Exception e) {
										// TODO: handle exception
								}
							}

						}
						return "classTransfer.xhtml";
					}
					else
					{

						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Something Went Wrong. Please Try Again","Something Went Wrong. Please Try Again"));
						return "";
					}
				}

			}
			else
			{
				for(StudentInfo tt: studentList)
				{
					if(selectedSection1!=null && selectedClassSection1!=null )
					{
						boolean checkOptSubj = dbr.checkStudentOptionalSubject(tt.getAddNumber(),DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),DBM.schoolId(),conn);

						if(checkOptSubj)
						{
							FacesContext fc=FacesContext.getCurrentInstance();
							fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,tt.getFname() + " - You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer",tt.getFname() + " - You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer"));
						}
						else
						{
							i=DBM.updateClassStudent(tt.getAddNumber(),selectedSection1,conn);
							if(i>=1)
							{
								String refNo6;
								try{
									refNo6=addWorkLog3(conn,tt.getAddNumber());
								} catch (Exception e) {
										// TODO: handle exception
								}
								DBM.updateAttendanceOnClassTransfer(tt.getAddNumber(),selectedSection,selectedSection1,conn);
								dbr.updateSectionInStudentPerformace(tt.getAddNumber(),selectedSection,selectedSection1,conn);
								dbr.updateSectionInRankTable(tt.getAddNumber(),selectedSection,selectedSection1,conn);
								dbr.updateSectionInExtraValue(tt.getAddNumber(),selectedSection,selectedSection1,conn);
								dbr.updateSectionInPromotionTable(tt.getAddNumber(),selectedSection,selectedSection1,conn);
							}
						}

					}
				}
				if(i==1)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Student Transfered Successfully"));
					classTest=DBM.classTestListForClassTransfer(selectedSection, selectedSection1, conn);
					for(ClassTest ss:classTest)
					{
						testPerformanceStatus=DBM.classTestPerformanceStatus(ss.getId(),conn);
						if(testPerformanceStatus==true)
						{
							DBM.blockTestPerformanceOnClassTransfer(ss.getSectionid(),conn);
							String refNo7;
							try{
								refNo7=addWorkLog4(conn,ss.getSectionid());
							} catch (Exception e) {
									// TODO: handle exception
							}
						}

					}
					return "classTransfer.xhtml";
				}
				else
				{

					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Something Went Wrong. Please Try Again","Something Went Wrong. Please Try Again"));
					return "";
				}
			}

		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	 public String addWorkLog3(Connection conn,String studentId)
		{
			    String value = "Student Id-"+studentId+" --Section Id-"+selectedSection+" --Selected Section id-"+selectedSection1;
				String language= "";
				
				String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Class transfer(Transfer All)","WEB",value,conn);
				return refNo;
		}
	 
	 public String addWorkLog4(Connection conn,String SectionId)
		{
			    String value = "Section Id-"+SectionId ;
				String language= "";
				
				String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Block Test Performance On Class Transfer","WEB",value,conn);
				return refNo;
		}

	public void searchClassTransfer()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		if(clientType.equalsIgnoreCase("school"))
		{
			afterbyname=true;
			int index=name.lastIndexOf(":")+1;
			String id=name.substring(index);
			if(index!=0)
			{
				loop:for(StudentInfo info : studentList)
				{
					if(String.valueOf(info.getAddNumber()).equals(id))
					{
						try
						{
							srNo=info.getSrNo();
							addNumber=info.getAddNumber();
							Studentname=info.getFname();
							currentClass=info.getClassName();
							sectionid=info.getSectionid();
							if(boardType.equalsIgnoreCase("cbse"))
							{
								performanceStatus=obj.performanceStatusOfStudentCBSEbyStudent(info.getAddNumber(),sectionid, conn);
							}
							else
							{
								performanceStatus=obj.performanceStatusOfStudentRBSEbyStudent(info.getAddNumber(),sectionid, conn);
							}
							//performanceStatus=new DatabaseMethods1().performanceStatusOfStudent(sectionid,conn);


							if(selectedtransferType.equalsIgnoreCase("section"))
							{
								classDisabled=true;
								selectedCLassSection=info.getClassId();
								allSectionforTransfer();
							}
							else
							{
								selectedCLassSection="";
								classDisabled=false;
							}

							
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
						
						break loop;
					}

				}
			}

		}
		else
		{
			afterbyname=true;
			int index=name.lastIndexOf(":")+1;
			String id=name.substring(index);
			if(index!=0)
			{
				loop:for(StudentInfo info : studentList)
				{
					if(String.valueOf(info.getAddNumber()).equals(id))
					{
						try
						{
							addNumber=info.getAddNumber();
							Studentname=info.getFname();
							currentClass=info.getClassName();
							sectionid=info.getSectionid();
							//performanceStatus=new DatabaseMethods1().performanceStatusOfStudent(addNumber,sectionid,conn);
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
						
						break loop;
					}

				}
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	public void searchStudentByName()
	{

		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentList=new ArrayList<>();
						studentList.add(info);

						show=true;
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("selectedStudent", studentList.get(0));

			show=false;
			name=null;
			selectedCLassSection=null;
			selectedStudent=null;

			ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
			try
			{
				cc.redirect("editStudentDetails.xhtml");
			}
			catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
	}
	
	

	public String updateclassTranfer()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		try
		{
			if(clientType.equalsIgnoreCase("school"))
			{

				if(selectedtransferType.equalsIgnoreCase("section"))
				{
					performanceStatus=false;
				}

				boolean checkOptSubj = dbr.checkStudentOptionalSubject(addNumber,DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),DBM.schoolId(),conn);

				if(checkOptSubj)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer","You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer"));
					return "";
				}

				if(performanceStatus==true)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You Can't Transfer Student After Adding Performance","You Can't Transfer Student After Adding Performance"));
					return "";
				}
				else
				{
					int i=DBM.updateClassStudent(addNumber,selectedSection,conn);
					if(i==1)
					{
						String refNo;
						try{
							refNo=addWorkLog(conn);
						} catch (Exception e) {
								// TODO: handle exception
						}
						
						if(DBM.schoolId().equals("302")||DBM.schoolId().equals("221")||DBM.schoolId().equals("216")) {
							notificationSend();
						}
						
						
						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage("Student Transferred Successfully"));
						DBM.updateAttendanceOnClassTransfer(addNumber,sectionid,selectedSection,conn);
						dbr.updateSectionInStudentPerformace(addNumber,sectionid,selectedSection,conn);
						dbr.updateSectionInRankTable(addNumber,sectionid,selectedSection,conn);
						dbr.updateSectionInExtraValue(addNumber,sectionid,selectedSection,conn);
						dbr.updateSectionInPromotionTable(addNumber,sectionid,selectedSection,conn);
						classTest=DBM.classTestListForClassTransfer(sectionid, selectedSection, conn);
						for(ClassTest ss:classTest)
						{
							/*
							if(selectedtransferType.equalsIgnoreCase("section"))
							{
								new DatabaseMethods1().udpatesectionInTranferStudent(addNumber,selectedSection,conn);
							}*/

							testPerformanceStatus=DBM.classTestPerformanceStatus(ss.getId(),conn);
							if(testPerformanceStatus==true)
							{
								DBM.blockTestPerformanceOnClassTransfer(ss.getSectionid(),conn);
								String refNo11;
								try{
									refNo11=addWorkLog4(conn,ss.getSectionid());
								} catch (Exception e) {
										// TODO: handle exception
								}
							}

						}
						return "classTransfer.xhtml";
					}
					else
					{

						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Something Went Wrong. Please Try Again","Something Went Wrong. Please Try Again"));
						return "";
					}
				}
			}
			else
			{

				boolean checkOptSubj = dbr.checkStudentOptionalSubject(addNumber,DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),DBM.schoolId(),conn);

				if(checkOptSubj)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer","You Can't Transfer Student Becuase an Optional Subject is Assigned to This Student. Please Deassign The Optional Subject to Transfer"));
					return "";
				}

				int i=DBM.updateClassStudent(addNumber,selectedSection,conn);
				if(i==1)
				{
					String refNo2;
					try{
						refNo2=addWorkLog(conn);
					} catch (Exception e) {
							// TODO: handle exception
					}
					
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Student Transfered Successfully"));
					DBM.updateAttendanceOnClassTransfer(addNumber,sectionid,selectedSection,conn);
					dbr.updateSectionInStudentPerformace(addNumber,sectionid,selectedSection,conn);
					dbr.updateSectionInRankTable(addNumber,sectionid,selectedSection,conn);
					dbr.updateSectionInExtraValue(addNumber,sectionid,selectedSection,conn);
					dbr.updateSectionInPromotionTable(addNumber,sectionid,selectedSection,conn);
					classTest=DBM.classTestListForClassTransfer(sectionid, selectedSection, conn);
					for(ClassTest ss:classTest)
					{
						testPerformanceStatus=DBM.classTestPerformanceStatus(ss.getId(),conn);
						if(testPerformanceStatus==true)
						{
							DBM.blockTestPerformanceOnClassTransfer(ss.getSectionid(),conn);
							String refNo12;
							try{
								refNo12=addWorkLog4(conn,ss.getSectionid());
							} catch (Exception e) {
									// TODO: handle exception
							}
						}

					}
					return "classTransfer.xhtml";
				}
				else
				{

					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Something Went Wrong. Please Try Again","Something Went Wrong. Please Try Again"));
					return "";
				}
			}


		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	
	 private void notificationSend() {
		 Connection conn = DataBaseConnection.javaConnection();
		 DatabaseMethods1 DBM = new DatabaseMethods1();
			StringBuilder stds = new StringBuilder();
			String ex1 = "";
			String ex2 = "";
			String ex3 = "";
			String ex4 = "";
		 ArrayList<EmployeeInfo> principleId = new DatabaseMethods1().getPrincipleId(DBM.schoolId(), conn);
			String nextTId = new DatabaseMethods1().getClassTeacherId(selectedCLassSection, selectedSection, DBM.schoolId(),DBM.selectedSessionDetails(DBM.schoolId(),conn) , conn);
			String nextTName = new DatabaseMethods1().employeeNameById(nextTId, conn);
			EmployeeInfo s2 = new EmployeeInfo();
			EmployeeInfo s1 = new EmployeeInfo();
			for(StudentInfo x : studentList) {
				String currTId = new DatabaseMethods1().getClassTeacherId(x.getClassId(), x.getSectionid(), DBM.schoolId(), DBM.selectedSessionDetails(DBM.schoolId(),conn), conn);
				String cuuTName = new DatabaseMethods1().employeeNameById(currTId, conn);
				s1.setName(cuuTName);
				s1.setCategory("Teacher");
				
				if(!currTId.equals("")) {
					s1.setId(Integer.valueOf(currTId));
				}else {
					s1.setId(0);
				}
				
				s1.setEmpId(currTId);
				stds.append("1. "+x.getFname());
				ex1 = "is";
				ex2 = "student";
				ex3 = "name";
				ex4 = "record";
				
			}
			
			String className1 = currentClass.split("-")[0];
			String sectionName1 =currentClass.split("-")[1];
			String className2 =new DatabaseMethods1().classNameFromidSchid(DBM.schoolId(), selectedCLassSection, DBM.selectedSessionDetails(DBM.schoolId(),conn), conn);
			String sectionName2 =DBM.sectionNameByIdSchidSession(DBM.schoolId(), selectedSection, DBM.selectedSessionDetails(DBM.schoolId(),conn), conn);
			
			s2.setName(nextTName);
			s2.setCategory("Next Teacher");
			s2.setEmpId(nextTId);
			if(!nextTId.equals("")) {
				s2.setId(Integer.valueOf(nextTId));
			}else {
				s2.setId(0);
			}
			principleId.add(s1);
			principleId.add(s2);
			
			for(EmployeeInfo empi : principleId) {
				String msg = "";
				if(empi.getCategory().equalsIgnoreCase("Principal")||empi.getCategory().equalsIgnoreCase("Vice Principal")) {
					msg = "Dear "+empi.getCategory()+",\r\n" + 
							"\r\n" + 
							"Following "+ex2+" of class "+className1+" - "+sectionName1+" "+ex1+" transfered  to class "+className2+" - "+sectionName2+".\r\n" + 
							stds.toString() +
							"\r\n" + 
							"\r\n" + 
							"Regards\r\n" + 
							"\r\n" + 
							info.getSmsSchoolName();
					
				}else if(empi.getCategory().equalsIgnoreCase("Next Teacher")) {
					msg = "Dear "+empi.getName()+",\r\n" + 
							"Following "+ex2+" "+ex1+" transfered to your class "+className2+" - "+sectionName2+".\r\n" + 
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
							"Following "+ex2+" of your class "+className1+" - "+sectionName1+" "+ex1+" Transfered to class "+className2+" - "+sectionName2+".\r\n" + 
							"\r\n" + 
							stds.toString()+
							"\r\n" + 
							"Please forward all relevent details to class teacher of class "+className2+" - "+sectionName2+".\r\n" + 
							"\r\n" + 
							"Regards\r\n" + 
							"\r\n" + 
							info.getSmsSchoolName();
				}
				
				new SendingNotifications().sendNotifi(msg, DBM.schoolId(), "", "", "", "Teacher", Integer.toString(empi.getId()), "Transfer");
			}
		
	}
	public String addWorkLog(Connection conn)
	{
		    String value = "Student Id-"+addNumber+" --Section Id-"+sectionid+" --Selected Section id-"+selectedSection;
			String language= "";
			
			String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Class transfer","WEB",value,conn);
			return refNo;
		}

	public void searchStudent()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			studentList=new DatabaseMethods1().searchStudentList(name,conn);
			if (studentList.size() > 0) {
				Collections.sort(studentList,new StudentComp());
			}
			// Collections.sort(studentList);
			show=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	class StudentComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{
			return e1.getFname().compareToIgnoreCase(e2.getFname());
		}
	}

	public void ss()
	{
		PrimeFaces.current().dialog().openDynamic("showExpense");
	}
	public void searchStudentByClassSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			studentList=new DatabaseMethods1().searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);

			PrimeFaces context = PrimeFaces.current();
			context.executeInitScript("PF('dlg2').hide();");

			show=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public void editNow() throws IOException
	{
		if(selectedStudent!= null)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("selectedStudent", selectedStudent);

			show=false;
			name=null;
			selectedCLassSection=null;
			selectedStudent=null;

			ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
			cc.redirect("editStudentDetails.xhtml");

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select a student from the list", "Validation error"));
		}
	}

	public void viewDetails() throws IOException
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedStudent", selectedStudent);

		ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
		cc.redirect("printStudentDetails.xhtml");
	}

	public String getCurrentClass() {
		return currentClass;
	}
	public void setCurrentClass(String currentClass) {
		this.currentClass = currentClass;
	}
	public String getAddNumber() {
		return addNumber;
	}
	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}
	public String getStudentname() {
		return Studentname;
	}
	public void setStudentname(String studentname) {
		Studentname = studentname;
	}
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	public String getSelectedtype() {
		return selectedtype;
	}
	public void setSelectedtype(String selectedtype) {
		this.selectedtype = selectedtype;
	}
	public boolean isByname() {
		return byname;
	}
	public void setByname(boolean byname) {
		this.byname = byname;
	}
	public boolean isByclass() {
		return byclass;
	}
	public void setByclass(boolean byclass) {
		this.byclass = byclass;
	}

	public boolean isClasstt() {
		return classtt;
	}
	public void setClasstt(boolean classtt) {
		this.classtt = classtt;
	}
	public String getSelectedClassSection1() {
		return selectedClassSection1;
	}
	public void setSelectedClassSection1(String selectedClassSection1) {
		this.selectedClassSection1 = selectedClassSection1;
	}
	public Boolean getAfterbyname() {
		return afterbyname;
	}
	public void setAfterbyname(Boolean afterbyname) {
		this.afterbyname = afterbyname;
	}
	public String getSelectedClassSection2() {
		return selectedClassSection2;
	}
	public void setSelectedClassSection2(String selectedClassSection2) {
		this.selectedClassSection2 = selectedClassSection2;
	}
	public String getSelectedSection1() {
		return selectedSection1;
	}
	public void setSelectedSection1(String selectedSection1) {
		this.selectedSection1 = selectedSection1;
	}
	public ArrayList<SelectItem> getSectionList1() {
		return sectionList1;
	}
	public void setSectionList1(ArrayList<SelectItem> sectionList1) {
		this.sectionList1 = sectionList1;
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
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}



	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public String getSelectedtransferType() {
		return selectedtransferType;
	}
	public void setSelectedtransferType(String selectedtransferType) {
		this.selectedtransferType = selectedtransferType;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getSrNo() {
		return srNo;
	}
	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}
	public boolean isClassDisabled() {
		return classDisabled;
	}
	public void setClassDisabled(boolean classDisabled) {
		this.classDisabled = classDisabled;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}





}
