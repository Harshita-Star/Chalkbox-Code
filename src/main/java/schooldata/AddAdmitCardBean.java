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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;

@ManagedBean(name="addAdmitCard")
@ViewScoped
public class AddAdmitCardBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classList,sectionList,subjectList;
	String examName,selectedClass,selectedSection="",type,examLabel,uploadType,userType,username,schoolid,instructions,refNo;
	ArrayList<AdmitCardInfo> admitInfo;
	boolean showAcademic,showSports,typePhoto,typeManual;
	transient UploadedFile file;
	int j=0;
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	DatabaseMethods1 obj=new DatabaseMethods1();

	
	public AddAdmitCardBean()
	{
		showAcademic=true;
		showSports=false;
		examLabel="Exam Name";

		type="academics";
		instructionMethod();
		Connection conn = DataBaseConnection.javaConnection();
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=new DatabaseMethods1().allClass(conn);
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=dbj.employeeIdbyEmployeeName(username,schoolid,conn);
			classList = obj.cordinatorClassList(empid, schoolid, conn);
		}
		else
		{
			String empid=dbj.employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.allClassListForClassTeacher(empid,schoolid,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkType()
	{
		j=0;
		admitInfo=new ArrayList<>();
		examName=selectedClass="";
		selectedSection="";
		if(type.equalsIgnoreCase("academics"))
		{
			showSports=false;
			showAcademic=true;
			examLabel="Exam Name";

		}
		else
		{
			showSports=true;
			showAcademic=false;
			examLabel="Event Name";
			for(int i=1;i<=5;i++)
			{
				AdmitCardInfo ll=new AdmitCardInfo();
				ll.setsNo(String.valueOf(i));
				ll.setDescription("");
				ll.setSubjectId("N/A");
				admitInfo.add(ll);

				j=i;
			}
		}
	}

	public void addMore()
	{
		//System.out.println(admitInfo.size());
		//int k=j;
		int k = admitInfo.size();
		for(int i=k+1;i==k+1;i++)
		{
			AdmitCardInfo ll=new AdmitCardInfo();
			ll.setsNo(String.valueOf(i));
			ll.setDescription("");
			ll.setSubjectId("N/A");
			admitInfo.add(ll);

			j=i;
		}
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		if(!userType.equalsIgnoreCase("Teacher"))
		{
		  sectionList=DBM.allSectionWithAllOption(selectedClass,conn);
		}
		else
		{	
		  String empid=dbj.employeeIdbyEmployeeName(username,schoolid,conn);
		  sectionList=obj.allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		subjectList=DBM.allSubjectsOfClass(selectedClass,conn);
		admitInfo=new ArrayList<>();
		for(int i=1;i<=subjectList.size();i++)
		{
			AdmitCardInfo ll=new AdmitCardInfo();
			ll.setsNo(String.valueOf(i));
			ll.setSubjectId("");
			ll.setDescription("N/A");
			admitInfo.add(ll);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkSubject()
	{
		FacesContext context=FacesContext.getCurrentInstance();
		String subId="";int flag=0;
		String selectedRow= (String) UIComponent.getCurrentComponent(context).getAttributes().get("sNo");
		for(AdmitCardInfo ll:admitInfo)
		{
			if(ll.getsNo().equals(selectedRow))
			{
				subId=ll.getSubjectId();
				for(AdmitCardInfo info:admitInfo)
				{
					if(!info.getsNo().equals(selectedRow) && (info.getSubjectId()!=null && !info.getSubjectId().equals("")))
					{
						if(info.getSubjectId().equals(subId))
						{
							flag=1;
							break;
						}
					}
				}
				if(flag==1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Subject Found"));
					ll.setSubjectId("");
				}
			}
		}
	}

	public void checkTime()
	{
		FacesContext context=FacesContext.getCurrentInstance();
		Date date=null,time=null;int flag=0;
		String selectedRow= (String) UIComponent.getCurrentComponent(context).getAttributes().get("sNo");
		for(AdmitCardInfo ll:admitInfo)
		{
			if(ll.getsNo().equals(selectedRow))
			{
				date=ll.getExamDate();
				time=ll.getExamTime();
				for(AdmitCardInfo info:admitInfo)
				{
					if(!info.getsNo().equals(selectedRow) && (info.getSubjectId()!=null && !info.getSubjectId().equals("")))
					{
						if(info.getExamDate().equals(date) && info.getExamTime().equals(time))
						{
							flag=1;
							break;
						}
					}
				}
				if(flag==1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Same Timing On Same Date Found"));
					ll.setExamTime(null);
				}
			}
		}
	}

	public String submitAdmitCard()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			if(uploadType.equalsIgnoreCase("photo"))
			{
				String fileNamePhoto = "";
				
			
				
				if (file != null && file.getSize() > 0) 
				{
					
					

					String dt = new SimpleDateFormat("dd-MM-yyyy").format(new Date()); 
					int rendomNumber = (int) (Math.random() * 9000) + 1000;
					String filePath1 = file.getFileName();
					if(filePath1.contains(".pdf") || filePath1.contains(".jpg") || filePath1.contains(".jpeg") || filePath1.contains(".png"))
					{
						String exten[] = filePath1.split("\\.");
						obj.makeProfileSchid(obj.schoolId(),file, "admitCard_" + String.valueOf(rendomNumber) + "_" + dt + "." + exten[exten.length-1],
						conn);
						fileNamePhoto =  "admitCard_" + String.valueOf(rendomNumber) + "_" + dt + "." + exten[exten.length-1];
						int i=new DatabaseMethods1().addAdmitCardPhoto(examName,selectedClass,selectedSection,conn,type,fileNamePhoto,instructions);
						if(i>=1)
						  {
					    	refNo=addWorkLog(conn,fileNamePhoto);
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Admit Card Added Successfully"));
							if(selectedSection.equalsIgnoreCase("All"))
							{
                                dbj.notification("Date Sheet","Datesheet for "+examName+" is added for your class.",selectedClass+"-"+obj.schoolId(),obj.schoolId(),"",conn);
							}
							else
							{
								dbj.notification("Date Sheet","Datesheet for "+examName+" is added for your class.",selectedSection+"-"+selectedClass+"-"+obj.schoolId(),obj.schoolId(),"",conn);
							}
							examName=selectedClass="";selectedSection="";admitInfo=new ArrayList<>();sectionList=new ArrayList<>();
							subjectList=new ArrayList<>();
			                uploadType ="";
							type="academics";
							typePhoto = false;
							checkType();
							instructionMethod();
							return "addAdmitCard.xhtml";
						  }
						  else
						  {
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
						  }
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select Correct File Format"));
					}
				 }
				else
				{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select File in specified size limit"));
				}	
			}
			else 
			{
		
				int i=obj.addAdmitCard(examName,selectedClass,selectedSection,admitInfo,conn,type,instructions);
				if(i>=1)
				{
					refNo=addWorkLog2(conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Admit Card Added Successfully"));
					if(selectedSection.equalsIgnoreCase("All"))
					{
						dbj.notification("Date Sheet","Datesheet for "+examName+" is added for your class.",selectedClass+"-"+obj.schoolId(),obj.schoolId(),"",conn);
					}
					else
					{
						dbj.notification("Date Sheet","Datesheet for "+examName+" is added for your class.",selectedSection+"-"+selectedClass+"-"+obj.schoolId(),obj.schoolId(),"",conn);
					}
					examName=selectedClass="";selectedSection="";admitInfo=new ArrayList<>();sectionList=new ArrayList<>();
					subjectList=new ArrayList<>();
	                uploadType ="";
					type="academics";
					checkType();
					instructionMethod();
					return "addAdmitCard.xhtml";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured..!"));
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	
	public void instructionMethod() {
		
		instructions = "IMPORTANT INSTRUCTIONS TO BE FOLLOWED BY CANDIDATES WHILE APPEARING IN PRE BOARD EXAMINATIONS. \n" + 
				"\n\n" + 
				"1.Report in the exam centre latest by 9:45 am. Entry is not permitted after 9:45 am. No candidate will be allowed to leave the exam centre before the exam  is over. \n" + 
				"\n" + 
				"2. Time of commencement of examination is 10:00 am. To confirm time and date(s) of examination, please check date sheet. \n" + 
				"\n" + 
				"3. Follow the instructions given by invigilators especially for writing Roll number in your answer book. \n" + 
				"\n" + 
				"4. Carry only Blue/ Royal Blue ball point/Gel/ Fountain pen, pencil, eraser, scale, sharpener, Geometry instruments, colours & Admit card in a transparent pouch.\n" + 
				"\n" + 
				"5. MOBILE & OTHER ELECTRONIC ITEMS ARE NOT PERMITTED INSIDE EXAMINATION CENTRE. \n" + 
				"\n" + 
				"6. All the candidates will carry their own hand sanitizer in transparent bottle. \n" + 
				"\n" + 
				"7. All the candidates will cover their nose and mouth with mask. \n" + 
				"\n" + 
				"8. All candidates will follow strictly 6 feet physical distance norms.\n" + 
				"\n" + 
				"9. Parents will guide their ward(s)about precautions to be taken by them to avoid spread of COVID-19.\n" + 
				"\n" + 
				"10. Candidates will follow all instructions given in the admit card. ";
	}
	
	public String addWorkLog(Connection conn,String fileNamePhoto)
	{
	    String value = "";
		String language= "";
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass, schid, conn);
		
		String sectionname ="All";
		if(!selectedSection.equalsIgnoreCase("All"))
		{
			sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		}
		  
		
	     language = "Type-"+type+" -- Exam Name-"+examName+" -- Class-"+className+" -- Section-"+sectionname+" --UploadType-Photo";
		value = examName+" -- "+className+" -- "+sectionname+" -- "+type+" -- "+fileNamePhoto;
	     
		return new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Admit Card Photo","WEB",value,conn);
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass, schid, conn);
		
		String sectionname ="All";
		if(!selectedSection.equalsIgnoreCase("All"))
		{
			sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		}
		
	     language = "Type-"+type+" -- Exam Name-"+examName+" -- Class-"+className+" -- Section-"+sectionname+" --UploadType-manual";
		value = examName+" -- "+className+" -- "+sectionname+" -- "+type+" -- ";
		
	  if(type.equalsIgnoreCase("academics"))
	  {  
		for(AdmitCardInfo adm: admitInfo)
		{
			 String dt = "";
			  try {
				  if(adm.getExamDate()!=null)
				  {	  
				    dt = sdf.format(adm.getExamDate());
				  }  
			} catch (Exception e) {
				e.printStackTrace();
			}
			value += "("+adm.getSubjectId()+" -"+dt+" - "+adm.getTimeExam()+")"; 
		}
	  }
	  else
	  {
		  for(AdmitCardInfo adm: admitInfo)
			{
			  String dt = "";
			  try {
				  dt = sdf.format(adm.getExamDate());
			} catch (Exception e) {
				e.printStackTrace();
			}
				
				value += "("+adm.getDescription()+" -"+dt+" - "+adm.getTimeExam()+")"; 
			}
		  
	  }
		return  new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Admit Card Manual","WEB",value,conn);
	}
	
	
	public void checkRenderCondition()
	{
		if(uploadType.equalsIgnoreCase("photo"))
		{
			typePhoto = true;
			typeManual = false;
		}
		else
		{
			typePhoto = false;
			typeManual = true;
			
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
	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
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
	public ArrayList<AdmitCardInfo> getAdmitInfo() {
		return admitInfo;
	}
	public void setAdmitInfo(ArrayList<AdmitCardInfo> admitInfo) {
		this.admitInfo = admitInfo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isShowAcademic() {
		return showAcademic;
	}

	public void setShowAcademic(boolean showAcademic) {
		this.showAcademic = showAcademic;
	}

	public boolean isShowSports() {
		return showSports;
	}

	public void setShowSports(boolean showSports) {
		this.showSports = showSports;
	}

	public String getExamLabel() {
		return examLabel;
	}

	public void setExamLabel(String examLabel) {
		this.examLabel = examLabel;
	}

	public String getUploadType() {
		return uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	public boolean isTypePhoto() {
		return typePhoto;
	}

	public void setTypePhoto(boolean typePhoto) {
		this.typePhoto = typePhoto;
	}

	public boolean isTypeManual() {
		return typeManual;
	}

	public void setTypeManual(boolean typeManual) {
		this.typeManual = typeManual;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	
	
	
}
