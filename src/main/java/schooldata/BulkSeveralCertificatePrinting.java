package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;

@ManagedBean(name = "bulkSCP")
@SessionScoped
public class BulkSeveralCertificatePrinting implements Serializable {
	private static final long serialVersionUID = 1L;
	String addNumber, date1, year, headerImage, session, website, studentName, startSession, endSession, affNo, board,
			heShe, cls;
	String sy[];
	boolean showPrint, showBirth, showBank, showBonafied, showPic, showStatic, showCharacter, showAffiliation, showFee,
			showPassportApply, showLOC,showBirthnorthwood;
	SchoolInfoList schinfo;
	
	
	String selectedCLassSection,selectedSection, username, schoolid, userType;
	ArrayList<SelectItem> sectionList,classSection;
	ArrayList<StudentInfo> listOfStudent = new ArrayList<StudentInfo>();
	String check = "";

	@PostConstruct
	public void init() {
		Connection conn = DataBaseConnection.javaConnection();

		DatabaseMethods1 obj = new DatabaseMethods1();
		schinfo = obj.fullSchoolInfo(conn);
		affNo = schinfo.getAdd4();
		board = schinfo.getBoardType().toUpperCase();
		
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
			classSection = obj.allClass(conn);
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classSection = obj.cordinatorClassList(empid, schoolid, conn);
		}

		selectedCLassSection = "";
		selectedSection = ""; 
		studentName = "";

		try {
			conn.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	public String toBulk()
	{
		return "severalCertificatePrinting.xhtml";
	}
	
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	

	

	

	public void locCertificate() {
		
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		
		listOfStudent = new ArrayList<StudentInfo>();
		listOfStudent = obj.searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);

		SchoolInfoList info = obj.fullSchoolInfo(conn);
		String savePath = "";
		if (info.getProjecttype().equals("online")) {
			savePath = info.getDownloadpath();
		}
		headerImage = savePath + info.getMarksheetHeader();
		session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
		endSession = session.split("-")[1];
		website = info.getWebsite();	
		
	  if(listOfStudent.size()!=0)
	  {	
		
		for(StudentInfo sf : listOfStudent)
		{	
         
		
			ArrayList<SubjectInfo>subjList = new ArrayList<>();
			ArrayList<SubjectInfo>manSubList=obj.manadatorySubjectListForStudent(sf.getClassId(), session, sf.getAddNumber(), sf.getSectionid(), "scholastic", conn);
			ArrayList<SubjectInfo>optSubList=obj.optionalSubjectListForStudent(sf.getClassId(), session, sf.getAddNumber(), sf.getSectionid(), "scholastic", conn);
			subjList.addAll(manSubList);
			subjList.addAll(optSubList);
			
			//// // System.out.println(subjList.size());
			
			sy = new String[subjList.size()];
			
			for(int i=0;i<subjList.size();i++)
			{
				
				sy[i] = subjList.get(i).getSubName();
			}	
			
			sf.setSy(sy);
			
//			for(int i=0;i<subjList.size();i++)
//			{
//				if(i==0)
//				{
//					sf.setSubject1(subjList.get(i).getSubName());
//				}
//				else if(i==1)
//				{
//					sf.setSubject2(subjList.get(i).getSubName());
//							
//				}
//				else if(i==2)
//				{
//					sf.setSubject3(subjList.get(i).getSubName());					
//				}
//				else if(i==3)
//				{
//					sf.setSubject4(subjList.get(i).getSubName());					
//				}
//				else if(i==4)
//				{
//					sf.setSubject5(subjList.get(i).getSubName());					
//				}
//					
//			}
			
			
			sf.setDobInWord(dobInWords(sf.getDob()));
			if (sf.getGender().equalsIgnoreCase("male")) {
				
				sf.setHisHer("His");
			} else {
				sf.setHisHer("Her");

			}
		}
			
		showLOC = showPrint = true;
			
			PrimeFaces.current().executeInitScript("window.open('printBulkSeveralCerificate.xhtml')");
			
			
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"No Student In Class", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	public String dobInWords(Date dob) {
		
		String date1 = transform(dob.getDate());
		String wordmonth = new DatabaseMethods1().monthNameByNumber(dob.getMonth() + 1);
		String year1 = year(dob.getYear() + 1900);
		String dobInWord = date1 + " " + wordmonth + " " + year1;

        return dobInWord;
	}

	public String transform(int num) {
		String one[] = { " ", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten",
				" Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen",
				" Nineteen" };
		String ten[] = { " ", " ", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", "Seventy", " Eighty",
				" Ninety" };
		if (num <= 99) {
			if (num <= 19) {
				date1 = one[num];
			} else {
				int num1 = num / 10;
				int num2 = num % 10;
				date1 = ten[num1] + one[num2];
			}
		}
		return date1;
	}

	

	public String year(int num) {
		String one[] = { " ", " One ", " Two ", " Three ", " Four ", " Five ", " Six ", " Seven ", " Eight ", " Nine ",
				" Ten ", " Eleven ", " Twelve ", " Thirteen ", " Fourteen ", " Fifteen ", " Sixteen ", " Seventeen ",
				" Eighteen ", " Nineteen " };
		String ten[] = { " ", " ", " Twenty ", " Thirty ", " Forty ", " Fifty ", " Sixty ", "Seventy ", " Eighty ",
				" Ninety " };

		if (num >= 1000 && num <= 9999) {
			int num1 = num / 1000;
			int num2 = num % 1000;
			if (num2 <= 99) {
				if (num2 <= 19) {
					year = one[num1] + "Thousand" + one[num2];

				} else {
					int num4 = num2 / 10;
					int num5 = num2 % 10;
					year = one[num1] + "Thousand" + ten[num4] + one[num5];
				}
			} else {
				int num6 = num2 / 100;
				int num7 = num2 % 100;
				if (num7 <= 19) {
					String num8 = one[num7];
					year = one[num1] + "Thousand" + one[num6] + "Hundred" + num8;
				} else {
					int num0 = num7 / 10;
					int num9 = num7 % 10;
					year = one[num1] + "Thousand" + one[num6] + "Hundred" + ten[num0] + one[num9];
				}
			}
		}
		return year;
	}



	public SchoolInfoList getSchinfo() {
		return schinfo;
	}

	public void setSchinfo(SchoolInfoList schinfo) {
		this.schinfo = schinfo;
	}

	public String getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}

	

	public boolean isShowPrint() {
		return showPrint;
	}

	public void setShowPrint(boolean showPrint) {
		this.showPrint = showPrint;
	}

	public boolean isShowBirth() {
		return showBirth;
	}

	public void setShowBirth(boolean showBirth) {
		this.showBirth = showBirth;
	}

	public boolean isShowBank() {
		return showBank;
	}

	public void setShowBank(boolean showBank) {
		this.showBank = showBank;
	}

	public boolean isShowBonafied() {
		return showBonafied;
	}

	public void setShowBonafied(boolean showBonafied) {
		this.showBonafied = showBonafied;
	}

	

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public boolean isShowPic() {
		return showPic;
	}

	public void setShowPic(boolean showPic) {
		this.showPic = showPic;
	}

	public boolean isShowStatic() {
		return showStatic;
	}

	public void setShowStatic(boolean showStatic) {
		this.showStatic = showStatic;
	}

	public String getStartSession() {
		return startSession;
	}

	public void setStartSession(String startSession) {
		this.startSession = startSession;
	}

	public String getEndSession() {
		return endSession;
	}

	public void setEndSession(String endSession) {
		this.endSession = endSession;
	}

	public boolean isShowCharacter() {
		return showCharacter;
	}

	public void setShowCharacter(boolean showCharacter) {
		this.showCharacter = showCharacter;
	}

	public boolean isShowAffiliation() {
		return showAffiliation;
	}

	public void setShowAffiliation(boolean showAffiliation) {
		this.showAffiliation = showAffiliation;
	}

	public String getAffNo() {
		return affNo;
	}

	public void setAffNo(String affNo) {
		this.affNo = affNo;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public boolean isShowFee() {
		return showFee;
	}

	public void setShowFee(boolean showFee) {
		this.showFee = showFee;
	}



	public boolean isShowPassportApply() {
		return showPassportApply;
	}

	public void setShowPassportApply(boolean showPassportApply) {
		this.showPassportApply = showPassportApply;
	}

	public String getHeShe() {
		return heShe;
	}

	public void setHeShe(String heShe) {
		this.heShe = heShe;
	}

	public boolean isShowLOC() {
		return showLOC;
	}

	public void setShowLOC(boolean showLOC) {
		this.showLOC = showLOC;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	

	public boolean isShowBirthnorthwood() {
		return showBirthnorthwood;
	}

	public void setShowBirthnorthwood(boolean showBirthnorthwood) {
		this.showBirthnorthwood = showBirthnorthwood;
	}



	public ArrayList<StudentInfo> getListOfStudent() {
		return listOfStudent;
	}

	public void setListOfStudent(ArrayList<StudentInfo> listOfStudent) {
		this.listOfStudent = listOfStudent;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String[] getSy() {
		return sy;
	}

	public void setSy(String[] sy) {
		this.sy = sy;
	}
	

}

