package schooldata;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.http.HttpSession;

import org.primefaces.event.CaptureEvent;
import org.primefaces.model.file.UploadedFile;

import reports_module.VisitorInfo;

@ManagedBean(name="addVisitor")
@ViewScoped
public class AddVisitorBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String visitorNo,name,mobileNo,address,otherDetails,toMeet,purpose,noOfPerson,toMeetName,strStart,strEnd;
	String newFileName = "",schId;
	Date addDate,startDate,endDate;
	VisitorInfo selectedVisitor,visitInfo;
	ArrayList<VisitorInfo> visitorList,inVisitorList;
	int totalVisitor,totalInVisitor;
	ArrayList<SelectItem> nameList;
	ArrayList<SelectItem> personList;
	String inTime="";
	String filename="";
	transient UploadedFile photofile;
	public String getFilename() {
		return filename;
	}

	public void oncapture(CaptureEvent captureEvent) throws IOException
	{
		Connection conn = DataBaseConnection.javaConnection();
		String savePath = "";
		newFileName = "";
		String dt = new SimpleDateFormat("yMdhms").format(new Date());
		int randomPIN = (int)(Math.random()*9000)+1000;
		filename = "visitor"+dt+"_"+String.valueOf(randomPIN)+".jpg";
		//filename = getRandomImageName()+"_visitor_"+dt+".jpeg";
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
		// externalContext.redirect("addExam.xhtml");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public AddVisitorBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schId=new DatabaseMethods1().schoolId();
		visitorNo=new DatabaseMethods1().generateVisitorNo(schId,conn);

		addDate=new Date();
		startDate = new Date();
		endDate = new Date();

		personList=new ArrayList<>();
		for(int i=1;i<=10;i++)
		{
			SelectItem ll=new SelectItem();
			ll.setLabel(String.valueOf(i));
			ll.setValue(String.valueOf(i));
			personList.add(ll);
		}

		allInVisitorList();
		//allVisitorList();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	public void checkNoOfPerson()
	{
		nameList=new ArrayList<>();
		for(int i=1;i<Integer.parseInt(noOfPerson);i++)
		{
			SelectItem ll=new SelectItem();
			ll.setLabel("");
			nameList.add(ll);
		}
	}

	public void allInVisitorList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		inVisitorList=new DatabaseMethods1().allInVisitorList(schId,conn);
		totalInVisitor=inVisitorList.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allVisitorList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		strStart = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
		strEnd = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
		visitorList=new DatabaseMethods1().allVisitorList(schId,strStart,strEnd,conn);
		totalVisitor=visitorList.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteMobileNo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<String> mobileNo = new DatabaseMethods1().autoCompleteMobileNo(query,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mobileNo;
	}

	public void checkVisitor()
	{
		Connection conn=DataBaseConnection.javaConnection();
		visitInfo=new DatabaseMethods1().visitorInfoByMobileNo(schId,mobileNo,conn);
		name=visitInfo.getName();
		address=visitInfo.getAddress();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String personName="";
		if(Integer.parseInt(noOfPerson)>1)
		{
			for(SelectItem s:nameList)
			{
				personName+=s.getLabel()+",";
			}
		}
		if(personName.contains(","))
			personName=personName.substring(0,personName.lastIndexOf(","));
		inTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date());


		filename="";
		if(photofile!=null && photofile.getSize() > 0)
		{
			String dt = new SimpleDateFormat("yMdhms").format(new Date());
			int randomPIN = (int)(Math.random()*9000)+1000;
			filename = "visitor"+dt+"_"+String.valueOf(randomPIN)+".jpg";
			new DatabaseMethods1().makeProfileSchid(new DatabaseMethods1().schoolId(),photofile, filename, conn);
		}

		//inTime= sdf.format(new Date())+" "+addDate.getHours()+":"+addDate.getMinutes()+":"+addDate.getSeconds();
		int i=new DatabaseMethods1().addVisitor(addDate,visitorNo,name,mobileNo,address,toMeet,purpose,otherDetails,noOfPerson,personName,filename,conn,inTime,toMeetName,schId);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn,personName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Visitor added Successfully"));
			allInVisitorList();
			visitorNo=new DatabaseMethods1().generateVisitorNo(schId,conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn,String personName)
	{
	    String value = "";
		String language= "";
		
	    value = "Add Date-"+addDate+" -- Vistor No-"+visitorNo+" -- Name-"+name+" -- Mobile-"+mobileNo+" -- Address-"+address+" -- ToMeet-"+toMeet+" -- purpose-"+purpose+" -- OtherDetail-"+otherDetails+" -- NoOfPerson-"+noOfPerson+" -- PersonName-"+personName+" -- Filename-"+filename+" -- inTime-"+inTime+" -- To MeetName-"+toMeetName;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Vistor","WEB",value,conn);
		return refNo;
	}

	public void clearData()
	{
		addDate=new Date();
		visitorNo=name=mobileNo=address=purpose=toMeet=toMeetName=otherDetails=noOfPerson="";
	}

	public void updateVisitorOutTime()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String out = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date());
		int i=new DatabaseMethods1().updateVisitorOutTime(selectedVisitor.getId(),out,conn);
		if(i>0)
		{
			String refNo;
			try {
				refNo=addWorkLog2(conn,out);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Sucessfully"));
			allInVisitorList();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog2(Connection conn,String out)
	{
	    String value = "";
		String language= "";
		
		value = "Id-"+selectedVisitor.getId()+" -- out Time-"+out;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Visitor Out","WEB",value,conn);
		return refNo;
	}
	

	public void printVisitorPass()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("image",filename);
		ss.setAttribute("name",name);
		ss.setAttribute("address",address);
		ss.setAttribute("contactNo",mobileNo);
		ss.setAttribute("inDate",inTime);
		clearData();
		ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
		try {
			cc.redirect("printVisitorPass.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public String getVisitorNo() {
		return visitorNo;
	}
	public void setVisitorNo(String visitorNo) {
		this.visitorNo = visitorNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getToMeet() {
		return toMeet;
	}
	public void setToMeet(String toMeet) {
		this.toMeet = toMeet;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public ArrayList<VisitorInfo> getVisitorList() {
		return visitorList;
	}
	public void setVisitorList(ArrayList<VisitorInfo> visitorList) {
		this.visitorList = visitorList;
	}
	public VisitorInfo getSelectedVisitor() {
		return selectedVisitor;
	}
	public void setSelectedVisitor(VisitorInfo selectedVisitor) {
		this.selectedVisitor = selectedVisitor;
	}

	public int getTotalVisitor() {
		return totalVisitor;
	}

	public void setTotalVisitor(int totalVisitor) {
		this.totalVisitor = totalVisitor;
	}



	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getStrStart() {
		return strStart;
	}

	public void setStrStart(String strStart) {
		this.strStart = strStart;
	}

	public String getStrEnd() {
		return strEnd;
	}

	public void setStrEnd(String strEnd) {
		this.strEnd = strEnd;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

	public String getOtherDetails() {
		return otherDetails;
	}

	public void setOtherDetails(String otherDetails) {
		this.otherDetails = otherDetails;
	}

	public String getNoOfPerson() {
		return noOfPerson;
	}

	public void setNoOfPerson(String noOfPerson) {
		this.noOfPerson = noOfPerson;
	}

	public String getToMeetName() {
		return toMeetName;
	}

	public void setToMeetName(String toMeetName) {
		this.toMeetName = toMeetName;
	}

	public VisitorInfo getVisitInfo() {
		return visitInfo;
	}

	public void setVisitInfo(VisitorInfo visitInfo) {
		this.visitInfo = visitInfo;
	}


	public ArrayList<SelectItem> getPersonList() {
		return personList;
	}

	public void setPersonList(ArrayList<SelectItem> personList) {
		this.personList = personList;
	}

	public ArrayList<SelectItem> getNameList() {
		return nameList;
	}

	public void setNameList(ArrayList<SelectItem> nameList) {
		this.nameList = nameList;
	}

	public ArrayList<VisitorInfo> getInVisitorList() {
		return inVisitorList;
	}

	public void setInVisitorList(ArrayList<VisitorInfo> inVisitorList) {
		this.inVisitorList = inVisitorList;
	}

	public int getTotalInVisitor() {
		return totalInVisitor;
	}

	public void setTotalInVisitor(int totalInVisitor) {
		this.totalInVisitor = totalInVisitor;
	}

	public UploadedFile getPhotofile() {
		return photofile;
	}

	public void setPhotofile(UploadedFile photofile) {
		this.photofile = photofile;
	}
}
