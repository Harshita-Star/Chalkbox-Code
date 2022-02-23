package reports_module;

import java.io.File;
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

import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import schooldata.AdmitCardInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="viewEditAdmitCardBean")
@ViewScoped

public class ViewEditAdmitCardBean implements Serializable
{
	ArrayList<SelectItem> subjectList;
	ArrayList<AdmitCardInfo> list = new ArrayList<>();
	ArrayList<AdmitCardInfo> subList = new ArrayList<>();
	AdmitCardInfo selected = new AdmitCardInfo();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String examName="",selectedImage;
	Boolean photoType,manualType;
	transient UploadedFile file;
	String schoolId,session,userType,username;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	ArrayList<SelectItem> classList = new ArrayList<SelectItem>();


	
	public ViewEditAdmitCardBean() 
	{	
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		session=dbm.selectedSessionDetails(schoolId, conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		data();
	}
	
	public void data()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		if(!userType.equalsIgnoreCase("Teacher"))
		{
			list = dbr.admitCardList(schoolId,conn);
		}
		else
		{
			String empid=dbj.employeeIdbyEmployeeName(username,schoolId,conn);
			classList=dbm.allClassListForClassTeacher(empid,schoolId,conn);
			ArrayList<AdmitCardInfo> tempList = new ArrayList<AdmitCardInfo>();
			
			list = dbr.admitCardList(schoolId,conn);
			
			for(AdmitCardInfo adm : list)
			{
				for(SelectItem si : classList)
				{
					if(adm.getClassId().equalsIgnoreCase(String.valueOf(si.getValue())))
					{
						ArrayList<SelectItem> sectionList = new ArrayList<SelectItem>();
						sectionList=dbm.allSectionListForClassTeacher(empid,adm.getClassId(),conn);
						
						for(SelectItem k : sectionList)
						{
							if(adm.getSectionId().equalsIgnoreCase(String.valueOf(k.getValue())))
							{
								tempList.add(adm);
							}
						}
						
						
						
					}
				}
			}
			list = new ArrayList<AdmitCardInfo>();
			list = tempList;
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		if(selected.getPhotname().equalsIgnoreCase("no"))
		{
		  
		  subList=dbm.admitCardInfoForStudent(schoolId,selected.getGroupId(),selected.getClassId(),selected.getSectionId(),conn);
		
		  manualType=true;
		  photoType = false;
		  
		 
	   }
		else
		{
			SchoolInfoList ls =dbm.fullSchoolInfo(conn);

			String folderName = ls.getDownloadpath();
			manualType = false;
			photoType = true;
			selectedImage = folderName+selected.getPhotname();
			
		
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
		for(AdmitCardInfo ll:subList)
		{
			if(ll.getsNo().equals(selectedRow))
			{
				subId=ll.getSubjectId();
				for(AdmitCardInfo info:subList)
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
	
	public void addMore()
	{
		int k = subList.size();
		for(int i=k+1;i==k+1;i++)
		{
			AdmitCardInfo ll=new AdmitCardInfo();
			ll.setsNo(String.valueOf(i));
			ll.setDescription("");
			if(selected.getType().equalsIgnoreCase("sports"))
			{
				ll.setSubjectId("N/A");
			}
			else
			{
				ll.setSubjectId("");
			}
			//// // System.out.println(i);
			subList.add(ll);
		}
	}
	
	public void editDetails()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		if(selected.getPhotname().equalsIgnoreCase("no"))
		{
		  subList=dbm.admitCardInfoForStudent(schoolId,selected.getGroupId(),selected.getClassId(),selected.getSectionId(),conn);
		  subjectList=dbm.allSubjectsOfClass(selected.getClassId(),conn);
		  examName=selected.getExamName();
		  
		  manualType = true;
		  photoType = false;
		  
		}
		else
		{
			  SchoolInfoList ls = dbm.fullSchoolInfo(conn);

			  String folderName = ls.getDownloadpath();
			  
			  examName=selected.getExamName();
			  manualType = false;
			  photoType = true;
			  selectedImage = folderName+selected.getPhotname();
		}
		
		
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void viewFile()
	{
		PrimeFaces.current().executeInitScript("window.open('"+selectedImage+"')");

	}
	
	
	public String update()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		if(selected.getPhotname().equalsIgnoreCase("no"))
		{
			/*int j = dbr.deleteAdmitCard(dbm.schoolId(),selected.getGroupId(),selected.getClassId(),selected.getSectionId(),conn);
			if(j>0)
			{*/
				int i=dbr.updateAdmitCard(subList,selected.getType(),examName,conn,selected.getInstructions());
				if(i>=1)
				{
					String refNo3;
					try {
						refNo3=addWorkLog3(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Admit Card Updated Successfully"));
					try {
						conn.close();
					} catch (Exception e) {
					    e.printStackTrace();
					}
					return "viewEditAdmitCard.xhtml";
					//data();
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
				}
			/*}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
			}*/
		
	   }
	   else
	   {
		   
			int checker =0;
			String photoname ="";
			String photoRowId ="";
			SchoolInfoList ls = dbm.fullSchoolInfo(schoolId, conn);
			String path = ls.getUploadpath();
		   
		   if (file != null && file.getSize() > 0) 
			{
				String fileNamePhoto=""; 
				

				String dt = new SimpleDateFormat("dd-MM-yyyy").format(new Date()); 
				int rendomNumber = (int) (Math.random() * 9000) + 1000;
				String filePath1 = file.getFileName();
				String exten[] = filePath1.split("\\.");
				dbm.makeProfileSchid(schoolId,file, "admitCard_" + String.valueOf(rendomNumber) + "_" + dt + "." + exten[exten.length-1],
				conn);
				
				fileNamePhoto =  "admitCard_" + String.valueOf(rendomNumber) + "_" + dt + "." + exten[exten.length-1];
		   
				int i=dbr.updateAdmitCardPhoto(fileNamePhoto,selected.getId(),examName,conn,selected.getInstructions());
			    if(i>=1)
			  {
			    	
			    	String refNo2;
					try {
						refNo2=addWorkLog2(conn,fileNamePhoto);
					} catch (Exception e) {
						e.printStackTrace();
					}
			    	 if(checker == 1)
					 {
						boolean statusChk = dbr.checkPhotoPrrsentForOtherSections(photoname,photoRowId,conn);
						
						if(statusChk== false){
							File file = new File(path+photoname);
							file.delete();
						}
					 }
			    	
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Admit Card Updated Successfully"));
				try {
					conn.close();
				} catch (Exception e) {
				    e.printStackTrace();
				}
				return "viewEditAdmitCard.xhtml";
			  }
			  else
			  {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
			}
			}
		   else
		   {
				int i=dbr.updateAdmitCardPhoto(selected.getPhotname(),selected.getId(),examName,conn,selected.getInstructions());
			    if(i>=1)
			  {
			    	
			    	String refNo2;
					try {
						refNo2=addWorkLog2(conn,selected.getPhotname());
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						conn.close();
					} catch (Exception e) {
					    e.printStackTrace();
					}
					return "viewEditAdmitCard.xhtml";

			  }		
			  // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select File in specified size limit"));
		   }
	   }
		
		
	
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	return "";	
	}
	
	
	public String addWorkLog2(Connection conn,String fileNamePhoto)
	{
	    String value = "";
		String language= "";
		
		value = "Id-"+selected.getId()+" -- Photo-"+fileNamePhoto;
		
		String refNo = workLg.saveWorkLogMehod(language,"Edit Admit Card Photo","WEB",value,conn);
		return refNo;
	}

	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    language = selected.getType();
		
	   if(selected.getType().toLowerCase().equalsIgnoreCase("academics"))
	   {  	
	    for(AdmitCardInfo ad:subList)
	    {
	    	 String dtt = "";
			  try {
				  dtt = sdf.format(ad.getExamDate());
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	value += "("+ad.getSubjectId()+" - "+dtt+" - "+ad.getTimeExam()+")";
	    }
	   } 
	   else
	   {
		   for(AdmitCardInfo ad:subList)
		    {
			   String dtt = "";
				  try {
					  dtt = sdf.format(ad.getExamDate());
				} catch (Exception e) {
					e.printStackTrace();
				}
		    	value += "("+ad.getDescription()+" - "+dtt+" - "+ad.getTimeExam()+")";
		    }  
	   }
	   
	   
		String refNo = workLg.saveWorkLogMehod(language,"Edit Admit Card Manual","WEB",value,conn);
		return refNo;
	}
	

	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		
		int checker =0;
		String photoname ="";
		String photoRowId ="";
		SchoolInfoList ls = dbm.fullSchoolInfo(schoolId, conn);
		String path = ls.getUploadpath();
		if(!selected.getPhotname().equalsIgnoreCase("no"))
		{
			checker =1;
			photoname = selected.getPhotname();
			photoRowId = selected.getId();
			
		}
		
		 int i = dbr.deleteAdmitCard(schoolId,selected.getGroupId(),selected.getClassId(),selected.getSectionId(),conn);
		 if(i>0)
		 {
			 
			 String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			 if(checker == 1)
			 {
				
				boolean statusChk = dbr.checkPhotoPrrsentForOtherSections(photoname,photoRowId,conn);
				
				if(statusChk== false){
					File file = new File(path+photoname);
					file.delete();
				}
			 }
			 
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Admit Card Deleted Successfully"));
			data();
		 }
		 else
		 {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
		 }
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		String className=dbm.classname(selected.getClassId(), schoolId, conn);	
		String sectionname = dbm.sectionNameByIdSchid(schoolId,selected.getSectionId(), conn);
		
		value = "Class -"+className+" -- Section-"+sectionname+" --GroupId-"+selected.getGroupId() ;
	
		String refNo = workLg.saveWorkLogMehod(language,"Delete Admit Card","WEB",value,conn);
		return refNo;
	}

	public ArrayList<AdmitCardInfo> getList() {
		return list;
	}

	public void setList(ArrayList<AdmitCardInfo> list) {
		this.list = list;
	}

	public ArrayList<AdmitCardInfo> getSubList() {
		return subList;
	}

	public void setSubList(ArrayList<AdmitCardInfo> subList) {
		this.subList = subList;
	}

	public AdmitCardInfo getSelected() {
		return selected;
	}

	public void setSelected(AdmitCardInfo selected) {
		this.selected = selected;
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

	public Boolean getPhotoType() {
		return photoType;
	}

	public void setPhotoType(Boolean photoType) {
		this.photoType = photoType;
	}

	public Boolean getManualType() {
		return manualType;
	}

	public void setManualType(Boolean manualType) {
		this.manualType = manualType;
	}

	public String getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(String selectedImage) {
		this.selectedImage = selectedImage;
	}
	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	
}
