package student_module;

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
import javax.servlet.http.HttpSession;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="onlineRegae")
@ViewScoped

public class OnlineRegAEBean implements Serializable
{
	ArrayList<SelectItem> classList,religionList,sessionList,nationalityList;
	boolean showParentNew,showParentEx,disableRel;
	transient UploadedFile fatherImage,motherImage,studentImage;
	OnlineAdmInfo info = new OnlineAdmInfo();
	LoginInfo linfo = new LoginInfo();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseOnlineAdm DBO = new DataBaseOnlineAdm();
	ArrayList<SiblingAEInfo> completeList=new ArrayList<>();
	SchoolInfoList ls=new SchoolInfoList();
	String index = "0";
	String session,schoolId;
	
	public OnlineRegAEBean()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		linfo=(LoginInfo) ss.getAttribute("signInfo");

		info.setLogin_id(linfo.getUnm());

		disableRel=true;
		showParentNew=true;
		showParentEx=false;
		Connection conn=DataBaseConnection.javaConnection();
		ls=DBM.fullSchoolInfo(conn);

		classList=DBM.allClass(conn);
		religionList=DBM.allReligionList(conn);
		nationalityList = DBM.allNationalityList(conn);

		completeList=new ArrayList<>();
		for(int k=1;k<=5;k++)
		{
			SiblingAEInfo ll=new SiblingAEInfo();
			ll.setSno(k);
			ll.setName("");
			ll.setClass_name("");
			ll.setClass_id("");
			completeList.add(ll);
		}
        schoolId = DBM.schoolId(); 
		session = DBM.selectedSessionDetails(ls.getSchid(),conn);
		int start = Integer.valueOf(session.split("-")[0]);
		int end = Integer.valueOf(session.split("-")[1]);

		sessionList=new ArrayList<>();
		for(int i=start;i<=end;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));

			sessionList.add(item);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void changeIndex(String idx)
	{
		index=idx;
	}


	public void checkParent()
	{
		info.setParent_id("");
		info.setParent_pwd("");
		info.setApplicant_name("");
		disableRel=true;
		info.setOther_rel("");
		info.setF_name("");
		info.setF_mob("");
		info.setF_email("");
		info.setF_emid("");
		info.setM_name("");
		info.setM_mob("");
		info.setM_email("");
		info.setM_emid("");

		fatherImage=null;
		motherImage=null;

		if(info.getParent_type().equalsIgnoreCase("New Parents"))
		{
			showParentNew=true;
			showParentEx=false;
			info.setApplicant_relation("Father");
		}
		else
		{
			showParentNew=false;
			showParentEx=true;
			info.setApplicant_relation("");
		}
	}

	public void checkRel()
	{
		info.setOther_rel("");
		if(info.getApplicant_relation().equalsIgnoreCase("Other"))
		{
			disableRel=false;
		}
		else
		{
			disableRel=true;

		}
	}

	public void submit()
	{
		Connection conn=DataBaseConnection.javaConnection();
		FacesContext fc=FacesContext.getCurrentInstance();
		try
		{
			if(info.getParent_type().equalsIgnoreCase("Existing Parents"))
			{
				DataBaseOnlineAdm.parentDetailByParentId(info, conn);
				if(info.getOnline_admcol().equalsIgnoreCase("found"))
				{
					reg();
				}
				else
				{
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Sorry! No such Parent ID Exists. Please Try Again With Correct Details.","Sorry! No such Parent ID Exists. Please Try Again With Correct Details."));
				}
			}
			else
			{
				reg();
			}

		}
		catch (Exception e)
		{
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



	}

	public String reg()
	{
		Connection conn=DataBaseConnection.javaConnection();
		FacesContext fc=FacesContext.getCurrentInstance();
		//		String dd="",fnm="";
		//		int rendomNumber =0 ;
		ls=DBM.fullSchoolInfo(conn);

		if (info.getSt_image() == null || info.getSt_image().equalsIgnoreCase(""))
		{
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please Upload Child's Photo.","Please Upload Child's Photo."));
			return "";
		}

		/*if (fatherImage != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=fatherImage.getFileName().split("\\.");
			fnm = "new_father_profile_"+dd+"."+exten[exten.length-1];
			info.setF_image(ls.getDownloadpath()+fnm);
			DBM.makeProfile(fatherImage, fnm, conn);
		}

		if (motherImage != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=motherImage.getFileName().split("\\.");
			fnm = "new_mother_profile_"+dd+"."+exten[exten.length-1];
			info.setM_image(ls.getDownloadpath()+fnm);
			DBM.makeProfile(motherImage, fnm, conn);
		}*/

		int enq_id = DBO.insertAdmInfo(info, "pending", conn);
		if(enq_id>=1)
		{
			DBO.insertDocInfo(info.getDocinfo(),info.getLogin_id(), enq_id, info.getStudent_id(),conn);
			DBO.insertMedicalInfo(info.getMedinfo(),info.getLogin_id(), enq_id, info.getStudent_id(),conn);
			DBO.insertVaccineInfo(info.getVacinfo(),info.getLogin_id(), enq_id, info.getStudent_id(), conn);
			DBO.insertSiblingInfo(completeList,String.valueOf(enq_id),info.getLogin_id(),info.getStudent_id(), conn);

			disableRel=true;
			showParentNew=true;
			showParentEx=false;

			completeList=new ArrayList<>();
			for(int k=1;k<=5;k++)
			{
				SiblingAEInfo ll=new SiblingAEInfo();
				ll.setSno(k);
				ll.setName("");
				ll.setClass_name("");
				ll.setClass_id("");
				completeList.add(ll);
			}

			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Your Application Sent Successfully!","Your Application Sent Successfully!"));

			LoginInfo ll= DBO.signUpInfo(info.getLogin_id(), conn);
			String schnm = DBM.schoolNameById(schoolId, conn);
			String subject = schnm+" Registration Application Submitted!";
			String heading = "<center>Hello "+info.getSt_name()+",</center>" + "<center class=\"red\">Congratulations, your registration application in  "+schnm+" has been submitted successfully!<center>";
			String msg = "<center><strong>Please login to your account, go to 'My Applications' under 'For Applicant' section, to check your registration application and its status.</strong><br></br>"+"<a href=\"http://chalkboxerp.in/DM/onlineAdmLogin.xhtml\"> <img style=\"height: 44px;\" src=\"http://chalkboxerp.in/loginNowButton.png\"> </a> <br></br></center>";

			Runnable r = new Runnable()
			{
				public void run()
				{

					new DataBaseOnlineAdm().doMail(ll.getEmail(), subject, heading, msg);
				}

			};
			new Thread(r).start();


			info = new OnlineAdmInfo();
			info.setLogin_id(linfo.getUnm());

		}
		else
		{

			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Something Went Wrong. Please Try Again With Correct Details!","Something Went Wrong. Please Try Again With Correct Details!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void fatherUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_father_profile_"+dd+"."+exten[exten.length-1];
		info.setF_image(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void motherUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_mother_profile_"+dd+"."+exten[exten.length-1];
		info.setM_image(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void studentUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_student_profile_"+dd+"."+exten[exten.length-1];
		info.setSt_image(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getReligionList() {
		return religionList;
	}

	public void setReligionList(ArrayList<SelectItem> religionList) {
		this.religionList = religionList;
	}

	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}

	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}

	public ArrayList<SelectItem> getNationalityList() {
		return nationalityList;
	}

	public void setNationalityList(ArrayList<SelectItem> nationalityList) {
		this.nationalityList = nationalityList;
	}

	public OnlineAdmInfo getInfo() {
		return info;
	}

	public void setInfo(OnlineAdmInfo info) {
		this.info = info;
	}

	public boolean isShowParentNew() {
		return showParentNew;
	}

	public void setShowParentNew(boolean showParentNew) {
		this.showParentNew = showParentNew;
	}

	public boolean isShowParentEx() {
		return showParentEx;
	}

	public void setShowParentEx(boolean showParentEx) {
		this.showParentEx = showParentEx;
	}

	public boolean isDisableRel() {
		return disableRel;
	}

	public void setDisableRel(boolean disableRel) {
		this.disableRel = disableRel;
	}

	public ArrayList<SiblingAEInfo> getCompleteList() {
		return completeList;
	}

	public void setCompleteList(ArrayList<SiblingAEInfo> completeList) {
		this.completeList = completeList;
	}

	public UploadedFile getFatherImage() {
		return fatherImage;
	}

	public void setFatherImage(UploadedFile fatherImage) {
		this.fatherImage = fatherImage;
	}

	public UploadedFile getMotherImage() {
		return motherImage;
	}

	public void setMotherImage(UploadedFile motherImage) {
		this.motherImage = motherImage;
	}

	public UploadedFile getStudentImage() {
		return studentImage;
	}

	public void setStudentImage(UploadedFile studentImage) {
		this.studentImage = studentImage;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public SchoolInfoList getLs() {
		return ls;
	}

	public void setLs(SchoolInfoList ls) {
		this.ls = ls;
	}


}
