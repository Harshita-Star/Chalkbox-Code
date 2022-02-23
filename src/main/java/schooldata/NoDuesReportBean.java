package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import library_module.BookInfo;
import library_module.DataBaseMethodsLibraryModule;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;

@ManagedBean(name="noDuesReport")
@ViewScoped
public class NoDuesReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String name,selectedCLassSection,selectedSection,dateString,userType;
	int totalStudent;
	ArrayList<StudentInfo> studentList;
	StudentInfo selectedStudent;
	static int count=1;
	String month,sectionName,className,typeMessage;
	Date date;
	boolean show;
	int totalAmount;
	ArrayList<FeeStatus> feeStatus;
	ArrayList<FeeInfo> feelist = new ArrayList<>();
	double totalDueAmount,libraryFineAmount;
	DatabaseMethods1 obj=new DatabaseMethods1();
	SchoolInfoList schoolDetails;
	ArrayList<SelectItem> concessionlist = new ArrayList<>();
	String selectedConcession,checkBookAssigned;
	DataBaseMethodsLibraryModule objLibrary= new DataBaseMethodsLibraryModule();


	public  NoDuesReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		try
		{
		 date = new Date();
		 schoolDetails = obj.fullSchoolInfo(conn);
		 concessionlist = obj.allConnsessionType(conn);
		 HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		 userType = (String) httpSession.getAttribute("type");
	
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

	

	public void searchStudent()
	{
		totalDueAmount=0;
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj;
		String schoolIdd = DBM.schoolId();
		ArrayList<BookInfo> bookList;
		try
		{
			SchoolInfoList list1=DBM.fullSchoolInfo(conn);
			

			String session=DatabaseMethods1.selectedSessionDetails(schoolIdd,conn);

			int index=name.lastIndexOf(":")+1;
			String id=name.substring(index);
			
			bookList = new ArrayList<BookInfo>();
			bookList=objLibrary.assignedBookListOfStudent(schoolIdd,id,conn);
			if(bookList.size()==0)
			{
				checkBookAssigned = "No";
				libraryFineAmount = 0;
			}
			else
			{
                checkBookAssigned = "Yes";
                libraryFineAmount = 0;
                double lateFeeAmt=objLibrary.getlatefees(conn);
    			String penaltySetting=list1.getPenaltySetting();
                for(BookInfo ll:bookList)
    			{
    				if(ll.getExpiringDate().before(date))
    				{
    					if(penaltySetting.equalsIgnoreCase("yes"))
    					{
    						Age a= new AgeWiseReport().ageCalculator(ll.getExpiringDate(), date);
    						double a1=(a.getYears()*12*30)+(a.getMonths()*30)+(a.getDays());
    						
    						libraryFineAmount += Double.valueOf(a1)*lateFeeAmt;
    					}
    				}
    			}	
			}
			
			

			studentList=DBM.searchStudentListForDueFeeReport(id,date,session,conn,"dueReport","active");
			SchoolInfoList info = DBM.fullSchoolInfo(conn);
		
			if(studentList.size()>0)
			{
				for(StudentInfo ll:studentList)
				{
					totalDueAmount+=Double.parseDouble(ll.getTutionFeeDueAmount());
				}
				selectedCLassSection=studentList.get(0).getClassId();
				selectedSection=studentList.get(0).getSectionid();
				feelist=DBM.classFeesForStudentForDues(selectedCLassSection,session,studentList.get(0).getStudentStatus(),studentList.get(0).getConcession(),conn);
				feelist=DBM.addPreviousFee(feelist,studentList.get(0).getAddNumber(),conn);

				className=DBM.classNameFromidSchid(DBM.schoolId(),selectedCLassSection,session,conn);
				sectionName=DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection,conn);
				totalStudent=studentList.size();
				month=studentList.get(0).month;
				show=true;
		
			}
			else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Record found", "Validation error"));
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
	
	

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection con = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethodSession().searchStudentListWithPreSessionStudent("byName",query, "full", con,"","");
		
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	
		



	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
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
	
	public String getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getTotalStudent() {
		return totalStudent;
	}
	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}
	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}
	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(double totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}


	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public ArrayList<SelectItem> getConcessionlist() {
		return concessionlist;
	}

	public void setConcessionlist(ArrayList<SelectItem> concessionlist) {
		this.concessionlist = concessionlist;
	}

	public String getSelectedConcession() {
		return selectedConcession;
	}

	public void setSelectedConcession(String selectedConcession) {
		this.selectedConcession = selectedConcession;
	}

	public String getCheckBookAssigned() {
		return checkBookAssigned;
	}

	public void setCheckBookAssigned(String checkBookAssigned) {
		this.checkBookAssigned = checkBookAssigned;
	}

	public double getLibraryFineAmount() {
		return libraryFineAmount;
	}

	public void setLibraryFineAmount(double libraryFineAmount) {
		this.libraryFineAmount = libraryFineAmount;
	}
	
	


}

