
package schooldata;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;

@ManagedBean(name="pickUpStudent")
@ViewScoped
public class PickUpStudentBean implements Serializable {
	private static final long serialVersionUID = 1L;

	String name,pickUpName,mobile,relation,filename,newFileName,strRandomOtp,otpMobile,enteredOTP,remark;
	int randomOtp,encryptNumber;
	StudentInfo stdInfo,selectedGuardian;
	ArrayList<StudentInfo> studentList,guardianList;
	transient UploadedFile photofile;

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

	public void searchStudentByName()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					stdInfo=info;
					guardianList=obj.makeGuardianList(obj.schoolId(),stdInfo,conn);
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void oncapture(CaptureEvent captureEvent) throws IOException
	{
		Connection conn = DataBaseConnection.javaConnection();
		String savePath = "";
		newFileName = "";
		String dt = new SimpleDateFormat("yMdhms").format(new Date());
		filename = getRandomImageName()+"_visitor_"+dt+".jpeg";
		byte[] data = captureEvent.getData();

		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(conn);
		if (ls.getProjecttype().equals("online"))
		{
			String folderName = ls.getUploadpath();
			savePath = folderName;
			newFileName = savePath+filename;
		}
		else
		{
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			newFileName = externalContext.getRealPath("") + File.separator + filename;
		}


		FileImageOutputStream imageOutput;
		try
		{
			imageOutput = new FileImageOutputStream(new File(newFileName));
			imageOutput.write(data, 0, data.length);
			imageOutput.close();
		}
		catch(Exception e)
		{
			throw new FacesException("Error in writing captured image.", e);
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getRandomImageName()
	{
		Connection conn=DataBaseConnection.javaConnection();
		Statement st = null;
		ResultSet rr = null;
		int no=1;
		try
		{
			st=conn.createStatement();
			String query="select count(distinct image) from pickup_student";
			rr=st.executeQuery(query);
			if(rr.next())
			{
				no=rr.getInt("count(distinct image)")+1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {

			if(st!=null)
			{
				try {
					st.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
			}

			if(rr!=null)
			{
				try {
					rr.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
			}

		}
		return String.valueOf(no);
	}

	public void checkAction()
	{
		if(selectedGuardian.getName().equals("Other"))
		{
			PrimeFaces.current().executeInitScript("PF('detailDialog').show()");
			PrimeFaces.current().ajax().update("detailForm");
		}
		else
		{
			otpMobile=selectedGuardian.getContactNo();
			createOTP();

			PrimeFaces.current().executeInitScript("PF('otpDialog').show()");
			PrimeFaces.current().ajax().update("otpForm");
		}
	}

	public void checkPickUp()
	{
		if(selectedGuardian.getName().equals("Other"))
		{
			PrimeFaces.current().executeInitScript("PF('detailDialog').show()");
			PrimeFaces.current().ajax().update(":detailForm");
		}
		else
		{
			pickUpStudent();
		}
	}

	public void createOTP()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);
		String schid = new DatabaseMethods1().schoolId();
		if(selectedGuardian.getName().equals("Other"))
		{
			//otpMobile=mobile;
			otpMobile=String.valueOf(stdInfo.getFathersPhone());
			filename="";
			if(photofile!=null && photofile.getSize() > 0)
			{
				String dt = new SimpleDateFormat("yMdhms").format(new Date());
				int randomPIN = (int)(Math.random()*9000)+1000;
				filename = "pickupOther"+dt+"_"+String.valueOf(randomPIN)+".jpg";
				new DatabaseMethods1().makeProfileSchid(new DatabaseMethods1().schoolId(),photofile, filename, conn);
			}
		}
		strRandomOtp = "";
		randomOtp = (int)(Math.random()*9000)+1000;
		encryptNumber=encrypt(randomOtp);
		strRandomOtp = String.valueOf(randomOtp);
		String schName = new DatabaseMethods1().getSMSSchoolName(new DatabaseMethods1().schoolId(), conn);
		String typemessage="Hello,\n"
				+String.valueOf(randomOtp)+ " is  your verification OTP for student pick up. Treat this as confidential.\nRegards.\n"+schName;
		
		if(info.getCountry().equalsIgnoreCase("India"))
		{
			if(otpMobile.length()==10
					&& !otpMobile.equals("2222222222")
					&& !otpMobile.equals("9999999999")
					&& !otpMobile.equals("1111111111")
					&& !otpMobile.equals("1234567890")
					&& !otpMobile.equals("0123456789"))
			{
				new DatabaseMethods1().messageurl1(otpMobile, typemessage, stdInfo.getAddNumber(), conn,new DatabaseMethods1().schoolId(),"");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent. Please Enter OTP."));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid Mobile No."));
			}
		}
		else
		{
			if(stdInfo.getFathersPhone()==stdInfo.getMothersPhone())
			{
				DBJ.notification("Security", typemessage,
						stdInfo.getFathersPhone()+"-"+stdInfo.getAddNumber() + "-" + schid, schid,"", conn);
			}
			else
			{
				DBJ.notification("Security", typemessage,
						stdInfo.getFathersPhone()+"-"+stdInfo.getAddNumber() + "-" + schid, schid,"", conn);
				DBJ.notification("Security", typemessage,
						stdInfo.getMothersPhone()+"-"+stdInfo.getAddNumber() + "-" + schid, schid,"", conn);
			}
		}
		
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public int encrypt(int number)
	{
		int temp = number;
		int digit4 = (temp + 7) % 10;
		temp = temp / 10;
		int digit3 = (temp + 7) % 10;
		temp = temp / 10;
		int digit2 = (temp + 7) % 10;
		temp = temp / 10;
		int digit1 = (temp + 7) % 10;
		return (digit3 * 1000 + digit4 * 100 + digit1 * 10 + digit2);

	}

	public void verifyOTP()
	{
		int checkNumber=encrypt(Integer.parseInt(enteredOTP));
		if(encryptNumber==checkNumber)
		{
			pickUpStudent();
			PrimeFaces.current().ajax().update("otpForm");
			PrimeFaces.current().ajax().update("detailForm");
			PrimeFaces.current().ajax().update("form1");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid OTP. Please Try Again."));
		}
	}

	public void resendOTP()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);
		String schid = new DatabaseMethods1().schoolId();
		String schName = new DatabaseMethods1().getSMSSchoolName(new DatabaseMethods1().schoolId(), conn);
		String typemessage="Hello,\n"
				+String.valueOf(randomOtp)+ " is  your verification OTP for student pick up.Treat this as confidential.\nRegards.\n"+schName;
		if(info.getCountry().equalsIgnoreCase("India"))
		{
			if(otpMobile.length()==10
					&& !otpMobile.equals("2222222222")
					&& !otpMobile.equals("9999999999")
					&& !otpMobile.equals("1111111111")
					&& !otpMobile.equals("1234567890")
					&& !otpMobile.equals("0123456789"))
			{
				new DatabaseMethods1().messageurl1(otpMobile, typemessage, stdInfo.getAddNumber(), conn,new DatabaseMethods1().schoolId(),"");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent. Please Enter OTP."));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid Mobile No."));
			}
		}
		else
		{
			if(stdInfo.getFathersPhone()==stdInfo.getMothersPhone())
			{
				DBJ.notification("Security", typemessage,
						stdInfo.getFathersPhone()+"-"+stdInfo.getAddNumber() + "-" + schid, schid,"", conn);
			}
			else
			{
				DBJ.notification("Security", typemessage,
						stdInfo.getFathersPhone()+"-"+stdInfo.getAddNumber() + "-" + schid, schid,"", conn);
				DBJ.notification("Security", typemessage,
						stdInfo.getMothersPhone()+"-"+stdInfo.getAddNumber() + "-" + schid, schid,"", conn);
			}
		}
		


		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void pickUpStudent()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		DataBaseMeathodJson dbj = new DataBaseMeathodJson();

		if(selectedGuardian.getName().equals("Other"))
		{
			selectedGuardian.setFname(pickUpName);
			selectedGuardian.setContactNo(mobile);
			selectedGuardian.setSignImage(filename);
			selectedGuardian.setRelation(relation);
		}
		int i=obj.pickUpStudent(stdInfo.getAddNumber(),remark,selectedGuardian,obj.schoolId(),conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(stdInfo.getFathersPhone()==stdInfo.getMothersPhone())
			{
				obj.notification(obj.schoolId(),"Security", "Your ward "+stdInfo.getFname()+" has been picked up from school by "+selectedGuardian.getFname()+" ("+selectedGuardian.getRelation()+").",
						stdInfo.getFathersPhone()+"-"+stdInfo.getAddNumber() + "-" + obj.schoolId(), conn);
			}
			else
			{
				obj.notification(obj.schoolId(),"Security", "Your ward "+stdInfo.getFname()+" has been picked up from school by "+selectedGuardian.getFname()+" ("+selectedGuardian.getRelation()+").",
						stdInfo.getFathersPhone()+"-"+stdInfo.getAddNumber() + "-" + obj.schoolId(), conn);
				obj.notification(obj.schoolId(),"Security", "Your ward "+stdInfo.getFname()+" has been picked up from school by "+selectedGuardian.getFname()+" ("+selectedGuardian.getRelation()+").",
						stdInfo.getMothersPhone()+"-"+stdInfo.getAddNumber() + "-" + obj.schoolId(), conn);
			}

			String currentStop = dbj.studentCurrentStop(stdInfo.getAddNumber(),obj.schoolId(),conn);
			if(!currentStop.equalsIgnoreCase("no"))
			{
				HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				String userid=(String) ss.getAttribute("username");
				String currentRoute = dbj.routeIdFromStopGroupId(currentStop, DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn), conn, obj.schoolId());
				String remark = "Left With Parents";
				String pickTime = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(new Date());
				dbj.insertStudentPickDetail(stdInfo.getAddNumber(),obj.schoolId(),currentStop,currentRoute,remark,new Date(),"no","yes","schoolpick",userid,pickTime,conn);
				dbj.insertStudentDropDetail(stdInfo.getAddNumber(), obj.schoolId(), currentStop, currentRoute, remark, new Date(),"no","yes","drop",userid,pickTime, conn);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student PickUp Successfully"));
			stdInfo=null;
			guardianList=new ArrayList<>();
			selectedGuardian=null;name=pickUpName=mobile=otpMobile=filename=strRandomOtp="";enteredOTP="";
			PrimeFaces.current().ajax().update("form1");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error occured... Please try Again"));
		}
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		String pickDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String pickTime = new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + " " + new Date().getHours() + ":"
				+ new Date().getMinutes();
		
		String status = "pickUp";
		String image = "";
		if (selectedGuardian.getSignImage() == null || selectedGuardian.getSignImage().equals("null")) {
			image = "";
		} else if (selectedGuardian.getSignImage().contains("/"))
			image = selectedGuardian.getSignImage().substring(selectedGuardian.getSignImage().lastIndexOf("/") + 1);
		else
			image = selectedGuardian.getSignImage();
		
		
		
		value = "Student"+stdInfo.getAddNumber()+" --Remark-"+remark+" --PickDate-"+pickDate+" --Pick Time-"+pickTime+" --Relation-"+selectedGuardian.getRelation()+
				" --Type-"+selectedGuardian.getName()+" --Name-"+selectedGuardian.getFname()+" --Contact No-"+selectedGuardian.getContactNo()+" --Status-"+status+" --Pick Image-"+image;
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Pick Student","WEB",value,conn);
		return refNo;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StudentInfo getStdInfo() {
		return stdInfo;
	}

	public void setStdInfo(StudentInfo stdInfo) {
		this.stdInfo = stdInfo;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<StudentInfo> getGuardianList() {
		return guardianList;
	}

	public void setGuardianList(ArrayList<StudentInfo> guardianList) {
		this.guardianList = guardianList;
	}

	public String getPickUpName() {
		return pickUpName;
	}

	public void setPickUpName(String pickUpName) {
		this.pickUpName = pickUpName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

	public StudentInfo getSelectedGuardian() {
		return selectedGuardian;
	}

	public void setSelectedGuardian(StudentInfo selectedGuardian) {
		this.selectedGuardian = selectedGuardian;
	}

	public String getEnteredOTP() {
		return enteredOTP;
	}

	public void setEnteredOTP(String enteredOTP) {
		this.enteredOTP = enteredOTP;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public UploadedFile getPhotofile() {
		return photofile;
	}

	public void setPhotofile(UploadedFile photofile) {
		this.photofile = photofile;
	}
}
