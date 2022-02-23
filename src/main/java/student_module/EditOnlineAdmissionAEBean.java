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

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="editOnlineAdmAe")
@ViewScoped

public class EditOnlineAdmissionAEBean implements Serializable
{
	ArrayList<SelectItem> classList,religionList,sessionList,nationalityList;
	ArrayList<String> documentsSubmitted = new ArrayList<>();
	boolean gdocReq,mReq,fdocReq,gReq,famBook,gimg,disableSrNo;
	boolean showParentNew,showParentEx,disableRel,showSen;
	boolean gReqPpt,gReqEmid,fReqVisa,mReqVisa,stReqVisa;
	transient UploadedFile fatherImage,motherImage,studentImage,grImage,senImage;
	transient UploadedFile f_ppt,f_emid,f_visa,m_ppt,m_emid,m_visa,st_ppt,st_emid,st_visa,gr_ppt,gr_emid,gr_visa,birth_cert,vac_report,fam_book;
	OnlineAdmInfo info = new OnlineAdmInfo();
	OnlineAdmInfo selectedInfo = new OnlineAdmInfo();
	LoginInfo linfo = new LoginInfo();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseOnlineAdm DBO = new DataBaseOnlineAdm();
	ArrayList<SiblingAEInfo> completeList=new ArrayList<>();
	SchoolInfoList ls=new SchoolInfoList();
	String enqid = "",addNumber="",srno;
	Date addmissionDate=new Date();
	String schoolId;

	public EditOnlineAdmissionAEBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//linfo=(LoginInfo) ss.getAttribute("signInfo");
		enqid = (String) ss.getAttribute("enq_id");
		StudentInfo studentList=(StudentInfo) ss.getAttribute("selectedStudent");
		srno = studentList.getSrNo();
		info = DBO.onlineAdmInfoById(enqid, "id", conn);
		//info.setLogin_id(linfo.getUnm());

		disableRel=true;
		showParentNew=true;
		showParentEx=false;


		classList=DBM.allClass(conn);
		religionList=DBM.allReligionList(conn);
		nationalityList = DBM.allNationalityList(conn);

		completeList=new ArrayList<>();
		completeList=DBO.siblingListByEnqId(info.getId(),conn);
		int st = completeList.size()+1;
		for(int k=st;k<=5;k++)
		{
			SiblingAEInfo ll=new SiblingAEInfo();
			ll.setSno(k);
			ll.setName("");
			ll.setClass_name("");
			completeList.add(ll);
		}

		ls=DBM.fullSchoolInfo(conn);

		if(ls.getSrnoType().equalsIgnoreCase("manual"))
		{
			disableSrNo = false;
		}
		else
		{
			disableSrNo = true;
		}

		schoolId = DBM.schoolId();
		String session = DBM.selectedSessionDetails(schoolId,conn);
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

		if(info.getNationality().equalsIgnoreCase("UNITED ARAB EMIRATES"))
		{
			fdocReq=false;
			if(info.getDocinfo().getFamily_book().equals(""))
			{
				famBook=true;
			}
			else
			{
				famBook=false;
			}

		}
		else
		{
			fdocReq=true;
			famBook=false;
			if(info.getDocinfo().getParent_visa().equals(""))
			{
				fReqVisa=true;
			}
			else
			{
				fReqVisa=false;
			}

			if(info.getDocinfo().getMother_visa().equals(""))
			{
				mReqVisa=true;
			}
			else
			{
				mReqVisa=false;
			}

			if(info.getDocinfo().getStudent_visa().equals(""))
			{
				stReqVisa=true;
			}
			else
			{
				stReqVisa=false;
			}
		}

		if(info.getApplicant_relation().equalsIgnoreCase("Legal Guardian"))
		{
			gReq=true;
			if(info.getG_image().equals(""))
			{
				gimg=true;
			}
			else
			{
				gimg=false;
			}

			if(info.getDocinfo().getG_ppt().equals(""))
			{
				gReqPpt=true;
			}
			else
			{
				gReqPpt=false;
			}

			if(info.getDocinfo().getG_emid().equals(""))
			{
				gReqEmid=true;
			}
			else
			{
				gReqEmid=false;
			}
		}
		else
		{
			gReq=false;
			gimg=false;
		}

		checkParent();
		checkRel();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkGnation()
	{
		if(info.getG_nation().equalsIgnoreCase("UNITED ARAB EMIRATES"))
		{
			gdocReq=false;
		}
		else
		{
			if(!info.getG_name().equals("") || !info.getG_address().equals("") || !info.getG_email().equals("") ||
					!info.getG_emid().equals("") || !info.getG_employer().equals("") || !info.getG_mob().equals("") ||
					!info.getG_nation().equals("") || !info.getG_occupation().equals("") || !info.getG_rel().equals("") ||
					!info.getG_work().equals(""))
			{
				if(info.getDocinfo().getG_visa().equals(""))
				{
					gdocReq=true;
				}
				else
				{
					gdocReq=false;
				}
			}
			else
			{
				gdocReq=false;
			}
			//gdocReq=true;

		}
	}

	public void checkSen()
	{
		if(info.getSen().equalsIgnoreCase("Yes"))
		{
			showSen=true;
		}
		else
		{
			showSen=false;
		}
	}

	public void checkParent()
	{
		if(info.getParent_type().equalsIgnoreCase("New Parents"))
		{
			showParentNew=true;
			showParentEx=false;
		}
		else
		{
			showParentNew=false;
			showParentEx=true;
		}
	}

	public void checkRel()
	{
		if(info.getApplicant_relation().equalsIgnoreCase("Other"))
		{
			disableRel=false;
		}
		else
		{
			disableRel=true;

		}
	}

	public void viewFile(String link)
	{
		PrimeFaces.current().executeInitScript("window.open('"+link+"')");
	}

	public void submit()
	{
		if (info.getSt_image() == null || info.getSt_image().equalsIgnoreCase(""))
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please Upload Child's Photo.","Please Upload Child's Photo."));
		}
		else
		{
			Connection conn = DataBaseConnection.javaConnection();
			HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			//linfo=(LoginInfo) ss.getAttribute("signInfo");
			enqid = (String) ss.getAttribute("enq_id");

			//uploadDoc();
			int i = DBO.updateAdmInfo(info, conn);
			if(i>=1)
			{
				//DBO.updateAdmStatus("Admission Form Filled", "Admission Form Filled", info.getId(), conn);
				DBO.updateDocInfo(info.getDocinfo(), info.getLogin_id(), enqid, conn);
				DBO.updateMedicalInfo(info.getMedinfo(), info.getLogin_id(), enqid, conn);
				DBO.updateVaccineInfo(info.getVacinfo(), info.getLogin_id(), enqid, conn);
				DBO.deleteSiblingAe(enqid, conn);
				DBO.insertSiblingInfo(completeList, enqid, info.getLogin_id(), info.getStudent_id(), conn);
				//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Updated Successfully!"));
				info = DBO.onlineAdmInfoById(enqid, "id", conn);
				selectedInfo = info;

				updateHere();
			}

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/*public void uploadDoc()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		ls=DBM.fullSchoolInfo(conn);

		if (studentImage != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=studentImage.getFileName().split("\\.");
			fnm = "new_student_profile_"+dd+"."+exten[exten.length-1];
			info.setSt_image(ls.getDownloadpath()+fnm);
			DBM.makeProfile(studentImage, fnm, conn);
		}

		if (fatherImage != null)
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
		}

		if (grImage != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=grImage.getFileName().split("\\.");
			fnm = "new_guardian_profile_"+dd+"."+exten[exten.length-1];
			info.setG_image(ls.getDownloadpath()+fnm);
			DBM.makeProfile(grImage, fnm, conn);
		}

		if (senImage != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=senImage.getFileName().split("\\.");
			fnm = "new_sen_profile_"+dd+"."+exten[exten.length-1];
			info.setSen_file(ls.getDownloadpath()+fnm);
			DBM.makeProfile(senImage, fnm, conn);
		}
		//////
		if (f_ppt != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=f_ppt.getFileName().split("\\.");
			fnm = "new_father_ppt_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setParent_ppt(ls.getDownloadpath()+fnm);
			DBM.makeProfile(f_ppt, fnm, conn);
		}

		if (f_emid != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=f_emid.getFileName().split("\\.");
			fnm = "new_father_emid_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setParent_emid(ls.getDownloadpath()+fnm);
			DBM.makeProfile(f_emid, fnm, conn);
		}

		if (f_visa != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=f_visa.getFileName().split("\\.");
			fnm = "new_father_visa_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setParent_visa(ls.getDownloadpath()+fnm);
			DBM.makeProfile(f_visa, fnm, conn);
		}
	//////
		if (m_ppt != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=m_ppt.getFileName().split("\\.");
			fnm = "new_mother_ppt_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setMother_ppt(ls.getDownloadpath()+fnm);
			DBM.makeProfile(m_ppt, fnm, conn);
		}

		if (m_emid != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=m_emid.getFileName().split("\\.");
			fnm = "new_mother_emid_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setMother_emid(ls.getDownloadpath()+fnm);
			DBM.makeProfile(m_emid, fnm, conn);
		}

		if (m_visa != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=m_visa.getFileName().split("\\.");
			fnm = "new_mother_visa_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setMother_visa(ls.getDownloadpath()+fnm);
			DBM.makeProfile(m_visa, fnm, conn);
		}
		//////
		if (st_ppt != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=st_ppt.getFileName().split("\\.");
			fnm = "new_student_ppt_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setStudent_ppt(ls.getDownloadpath()+fnm);
			DBM.makeProfile(st_ppt, fnm, conn);
		}

		if (st_emid != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=st_emid.getFileName().split("\\.");
			fnm = "new_student_emid_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setStudent_emid(ls.getDownloadpath()+fnm);
			DBM.makeProfile(st_emid, fnm, conn);
		}

		if (st_visa != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=st_visa.getFileName().split("\\.");
			fnm = "new_student_visa_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setStudent_visa(ls.getDownloadpath()+fnm);
			DBM.makeProfile(st_visa, fnm, conn);
		}
	//////
		if (gr_ppt != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=gr_ppt.getFileName().split("\\.");
			fnm = "new_guardian_ppt_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setG_ppt(ls.getDownloadpath()+fnm);
			DBM.makeProfile(gr_ppt, fnm, conn);
		}

		if (gr_emid != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=gr_emid.getFileName().split("\\.");
			fnm = "new_guardian_emid_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setG_emid(ls.getDownloadpath()+fnm);
			DBM.makeProfile(gr_emid, fnm, conn);
		}

		if (gr_visa != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=gr_visa.getFileName().split("\\.");
			fnm = "new_guardian_visa_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setG_visa(ls.getDownloadpath()+fnm);
			DBM.makeProfile(gr_visa, fnm, conn);
		}
	//////
		if (birth_cert != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=birth_cert.getFileName().split("\\.");
			fnm = "new_st_birthCert_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setBirth_cert(ls.getDownloadpath()+fnm);
			DBM.makeProfile(birth_cert, fnm, conn);
		}

		if (vac_report != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=vac_report.getFileName().split("\\.");
			fnm = "new_st_vaccReport_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setVaccination(ls.getDownloadpath()+fnm);
			DBM.makeProfile(vac_report, fnm, conn);
		}

		if (fam_book != null)
		{
			dd = new SimpleDateFormat("yMdHms").format(new Date());
			rendomNumber=(int)(Math.random()*9000)+1000;
			dd = dd+rendomNumber;
			String exten[]=fam_book.getFileName().split("\\.");
			fnm = "new_st_familyBook_profile_"+dd+"."+exten[exten.length-1];
			info.getDocinfo().setFamily_book(ls.getDownloadpath()+fnm);
			DBM.makeProfile(fam_book, fnm, conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

	public void updateHere()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		StudentInfo studentList=(StudentInfo) ss.getAttribute("selectedStudent");

		addNumber = studentList.getAddNumber();
		addmissionDate=studentList.getStartingDate();

		Connection conn = DataBaseConnection.javaConnection();
		
		//String session = DatabaseMethods1.selectedSessionDetails();
		//transportDetails=DBM.transportListDetails(addNumber,session,conn);
		//SchoolInfoList sinfo=DBM.fullSchoolInfo(conn);

		/*if(transportDetails.size()<=0)
		{
			DBM.transportDataEntry(addmissionDate,addNumber, routeName1, "Yes",conn);
		}
		else
		{
			if(routeName1.equals(""))
			{
				DBM.updateTransport(addNumber,"No", "0",conn);
			}
			else
			{
				if(!routeName1.equalsIgnoreCase(oldroutename))
                {
                    DBM.updateTransport(addNumber,"Yes",routeName1,conn);
                }
			}
		}

		 */

		String studentPhone = studentList.getStudentPhone();
		if(studentPhone.equals(""))
		{
			studentPhone="0";
		}

		/*new DatabaseMethods1().updateStudentAttendance(addNumber, sectionName);
		new DatabaseMethods1().updateStudentPerformance(addNumber, sectionName);*/


		/*
		if(concession.equals(oldconcessionCtegory))
		{
			concessionStatus=oldconcessionStatus;
		}
		else
		{
			conceesionCategory=DBM.concessionCategoryNameById(concession, conn);
			 if(conceesionCategory.equalsIgnoreCase("General"))
	         {
	             concessionStatus="accepted";
	         }
	         else
	         {
	        	 	if(info.getConcessionRequest().equals("no"))
         		{
         			concessionStatus="accepted";
				}
         		else
         		{
         			concessionStatus="pending";
         		}
	         }
		}

		////// // System.out.println(concessionStatus+".........."+oldconcessionStatus);

		if(discountFee==null || discountFee.equals(""))
		{
			discountFee = "0";
		}*/
		String discountFee="0";
		String routeFees="0";
		String routeName1=studentList.getRouteStop();
		if(routeName1==null||routeName1.equals(""))
		{
			//routeName1
			routeFees="0";
			discountFee="0";
		}
		else
		{
			routeFees=DBM.routeStopListAllAmount(DBM.schoolId(),routeName1,conn);
			discountFee=studentList.getDiscountfee();
			String.valueOf(Integer.valueOf(routeFees)-Integer.valueOf(discountFee));

		}

		int index1=selectedInfo.getSt_image().lastIndexOf("/")+1;
		String studentImage=selectedInfo.getSt_image().substring(index1);

		int index2=selectedInfo.getF_image().lastIndexOf("/")+1;
		String fatherImage=selectedInfo.getF_image().substring(index2);

		int index3=selectedInfo.getM_image().lastIndexOf("/")+1;
		String motherImage=selectedInfo.getM_image().substring(index3);

		int index4=selectedInfo.getG_image().lastIndexOf("/")+1;
		String g1Image=selectedInfo.getG_image().substring(index4);

		String g2Image="";

		int i=DBO.updateStudent(studentList.getId(),srno,selectedInfo.getSt_name(),selectedInfo.getF_name(),
				selectedInfo.getM_name(), addmissionDate, selectedInfo.getDob(), studentList.getSectionid(),selectedInfo.getGender(),
				selectedInfo.getNationality(), selectedInfo.getReligion(), selectedInfo.getAddress(), selectedInfo.getAddress(), 0,
				selectedInfo.getNationality(), Long.valueOf(selectedInfo.getF_mob()), Long.valueOf(selectedInfo.getM_mob()),"", selectedInfo.getF_occupation(),
				studentList.getResidenceMobile(),studentImage,studentList.getBpl(),studentList.getBplCardNo(),studentList.getConcession(),
				studentList.getCaste(),studentList.getSingleChild(),selectedInfo.getMedinfo().getBlood_group(),selectedInfo.getEmid(),
				studentList.getSingleParent(),studentList.getLivingWith(),selectedInfo.getF_email(),selectedInfo.getM_email(),
				fatherImage,motherImage,g1Image, g2Image,/*sendMessageMobileNo,*/studentList.getRouteStop(),discountFee,
				studentList.getRollNo(),studentList.getDisability(),studentList.getHandicap(),conn,selectedInfo.getF_emid(),
				selectedInfo.getM_emid(),selectedInfo.getLast_school(), studentList.getPassedClass(),studentList.getMedium(),
				studentList.getpResult(),studentList.getBoard(),studentList.getP_percent(),studentList.getpReason(),studentList.getTcDate(),
				studentList.getHeight(),studentList.getWeight(),studentList.getEyes(),selectedInfo.getG_name(),selectedInfo.getG_rel(),
				selectedInfo.getG_occupation(),selectedInfo.getG_mob(),selectedInfo.getG_address(),"","","","","",
				studentList.getFatherQualification(),selectedInfo.getF_occupation(),studentList.getFatherOrganization(),
				studentList.getFatherOfficeAdd(),studentList.getFatherSchoolEmp(),studentList.getMotherQualification(),
				selectedInfo.getM_occupation(),studentList.getMotherOrganization(),studentList.getMotherOfficeAdd(),
				studentList.getMotherSchoolEmp(), studentList.getHostel(),studentList.getSname1(),studentList.getSclassid1(),
				studentList.getSname2(),studentList.getSclassid2(),studentPhone,studentList.getHouse(),documentsSubmitted);
		if(i==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Detail Updated Successfully"));

			//			int count=0;
			//			String name="",schid=DBM.schoolId();
			//
		}

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

	public void grUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_guardian_profile_"+dd+"."+exten[exten.length-1];
		info.setG_image(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void senUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_sen_profile_"+dd+"."+exten[exten.length-1];
		info.setSen_file(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fpptUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_father_ppt_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setParent_ppt(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fvisaUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_father_visa_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setParent_visa(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void femidUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_father_emid_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setParent_emid(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void mpptUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_mother_ppt_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setMother_ppt(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void mvisaUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_mother_visa_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setMother_visa(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void memidUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_mother_emid_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setMother_emid(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void stpptUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_student_ppt_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setStudent_ppt(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void stvisaUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_student_visa_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setStudent_visa(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void stemidUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_student_emid_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setStudent_emid(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void grpptUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_guardian_ppt_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setG_ppt(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void grvisaUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_guardian_visa_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setG_visa(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void gremidUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_guardian_emid_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setG_emid(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void birthUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_st_birthCert_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setBirth_cert(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void vaccrepUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_st_vaccReport_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setVaccination(ls.getDownloadpath()+fnm);
		DBM.makeProfileSchid(schoolId,event.getFile(), fnm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fambookUpload(FileUploadEvent event)
	{
		Connection conn = DataBaseConnection.javaConnection();
		String dd="",fnm="";
		int rendomNumber =0 ;
		dd = new SimpleDateFormat("yMdHms").format(new Date());
		rendomNumber=(int)(Math.random()*9000)+1000;
		dd = dd+rendomNumber;
		String exten[]=event.getFile().getFileName().split("\\.");
		fnm = "new_st_familyBook_profile_"+dd+"."+exten[exten.length-1];
		info.getDocinfo().setFamily_book(ls.getDownloadpath()+fnm);
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

	public boolean isGdocReq() {
		return gdocReq;
	}

	public void setGdocReq(boolean gdocReq) {
		this.gdocReq = gdocReq;
	}

	public boolean ismReq() {
		return mReq;
	}

	public void setmReq(boolean mReq) {
		this.mReq = mReq;
	}

	public boolean isFdocReq() {
		return fdocReq;
	}

	public void setFdocReq(boolean fdocReq) {
		this.fdocReq = fdocReq;
	}

	public boolean isgReq() {
		return gReq;
	}

	public void setgReq(boolean gReq) {
		this.gReq = gReq;
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

	public OnlineAdmInfo getInfo() {
		return info;
	}

	public void setInfo(OnlineAdmInfo info) {
		this.info = info;
	}

	public ArrayList<SiblingAEInfo> getCompleteList() {
		return completeList;
	}

	public void setCompleteList(ArrayList<SiblingAEInfo> completeList) {
		this.completeList = completeList;
	}

	public UploadedFile getGrImage() {
		return grImage;
	}

	public void setGrImage(UploadedFile grImage) {
		this.grImage = grImage;
	}

	public UploadedFile getSenImage() {
		return senImage;
	}

	public void setSenImage(UploadedFile senImage) {
		this.senImage = senImage;
	}

	public boolean isShowSen() {
		return showSen;
	}

	public void setShowSen(boolean showSen) {
		this.showSen = showSen;
	}

	public boolean isFamBook() {
		return famBook;
	}

	public void setFamBook(boolean famBook) {
		this.famBook = famBook;
	}

	public UploadedFile getF_ppt() {
		return f_ppt;
	}

	public void setF_ppt(UploadedFile f_ppt) {
		this.f_ppt = f_ppt;
	}

	public UploadedFile getF_emid() {
		return f_emid;
	}

	public void setF_emid(UploadedFile f_emid) {
		this.f_emid = f_emid;
	}

	public UploadedFile getF_visa() {
		return f_visa;
	}

	public void setF_visa(UploadedFile f_visa) {
		this.f_visa = f_visa;
	}

	public UploadedFile getM_ppt() {
		return m_ppt;
	}

	public void setM_ppt(UploadedFile m_ppt) {
		this.m_ppt = m_ppt;
	}

	public UploadedFile getM_emid() {
		return m_emid;
	}

	public void setM_emid(UploadedFile m_emid) {
		this.m_emid = m_emid;
	}

	public UploadedFile getM_visa() {
		return m_visa;
	}

	public void setM_visa(UploadedFile m_visa) {
		this.m_visa = m_visa;
	}

	public UploadedFile getSt_ppt() {
		return st_ppt;
	}

	public void setSt_ppt(UploadedFile st_ppt) {
		this.st_ppt = st_ppt;
	}

	public UploadedFile getSt_emid() {
		return st_emid;
	}

	public void setSt_emid(UploadedFile st_emid) {
		this.st_emid = st_emid;
	}

	public UploadedFile getSt_visa() {
		return st_visa;
	}

	public void setSt_visa(UploadedFile st_visa) {
		this.st_visa = st_visa;
	}

	public UploadedFile getGr_ppt() {
		return gr_ppt;
	}

	public void setGr_ppt(UploadedFile gr_ppt) {
		this.gr_ppt = gr_ppt;
	}

	public UploadedFile getGr_emid() {
		return gr_emid;
	}

	public void setGr_emid(UploadedFile gr_emid) {
		this.gr_emid = gr_emid;
	}

	public UploadedFile getGr_visa() {
		return gr_visa;
	}

	public void setGr_visa(UploadedFile gr_visa) {
		this.gr_visa = gr_visa;
	}

	public UploadedFile getBirth_cert() {
		return birth_cert;
	}

	public void setBirth_cert(UploadedFile birth_cert) {
		this.birth_cert = birth_cert;
	}

	public UploadedFile getVac_report() {
		return vac_report;
	}

	public void setVac_report(UploadedFile vac_report) {
		this.vac_report = vac_report;
	}

	public UploadedFile getFam_book() {
		return fam_book;
	}

	public void setFam_book(UploadedFile fam_book) {
		this.fam_book = fam_book;
	}

	public boolean isGimg() {
		return gimg;
	}

	public void setGimg(boolean gimg) {
		this.gimg = gimg;
	}

	public boolean isgReqPpt() {
		return gReqPpt;
	}

	public void setgReqPpt(boolean gReqPpt) {
		this.gReqPpt = gReqPpt;
	}

	public boolean isgReqEmid() {
		return gReqEmid;
	}

	public void setgReqEmid(boolean gReqEmid) {
		this.gReqEmid = gReqEmid;
	}

	public boolean isfReqVisa() {
		return fReqVisa;
	}

	public void setfReqVisa(boolean fReqVisa) {
		this.fReqVisa = fReqVisa;
	}

	public boolean ismReqVisa() {
		return mReqVisa;
	}

	public void setmReqVisa(boolean mReqVisa) {
		this.mReqVisa = mReqVisa;
	}

	public boolean isStReqVisa() {
		return stReqVisa;
	}

	public void setStReqVisa(boolean stReqVisa) {
		this.stReqVisa = stReqVisa;
	}

	public String getSrno() {
		return srno;
	}

	public void setSrno(String srno) {
		this.srno = srno;
	}

	public boolean isDisableSrNo() {
		return disableSrNo;
	}

	public void setDisableSrNo(boolean disableSrNo) {
		this.disableSrNo = disableSrNo;
	}

	public SchoolInfoList getLs() {
		return ls;
	}

	public void setLs(SchoolInfoList ls) {
		this.ls = ls;
	}

}
