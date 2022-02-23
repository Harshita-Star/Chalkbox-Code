package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import session_work.QueryConstants;

@ManagedBean(name = "preduplicatefee")
@ViewScoped
public class PreDuplicateFeeRecipiet implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	DailyFeeCollectionBean selectedstudent;
	boolean show;
	String name;
	ArrayList<StudentInfo> studentList;
	String studentclass, studentname, fathername;
	String feetype;
	int studentid;
	ArrayList<Feecollectionc> feedetail;
	ArrayList<DailyFeeCollectionBean> dailyfee = new ArrayList<>();
	String mothername;
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	String studentName,fathersName,className,addNumber,date,schid,session;
	
	public PreDuplicateFeeRecipiet()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=new DatabaseMethods1().schoolId();
		session=DatabaseMethods1.selectedSessionDetails(schid, conn);
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query) {
		Connection conn=DataBaseConnection.javaConnection();
		studentList = new DatabaseMethods1().searchStudentList(query,conn);
		List<String> studentListt = new ArrayList<>();

		for (StudentInfo info : studentList) {
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void searchStudentByName() {
		Connection conn=DataBaseConnection.javaConnection();
		dailyfee=new ArrayList<>();
		int registfee = 0, annualfee = 0, tutionfee = 0, transportfee = 0, eaxmfee = 0,termfee = 0, activityfee = 0, artfee = 0;
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);

		feedetail = new DatabaseMethods1().getduplicatefeedetail(id,DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn),conn);
		ArrayList<String> temp = new ArrayList<>();
		for (Feecollectionc mm : feedetail) {
			temp.add(mm.getRecipietNo());
		}

		int a[] = new int[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			a[i] = Integer.parseInt(temp.get(i));
		}
		a = removeDuplicates(a);
		ArrayList<String> tempList=objStd.basicFieldsForStudentList();
		
		for (int i = 0; i < a.length; i++) {
			DailyFeeCollectionBean d = new DailyFeeCollectionBean();
			for (Feecollectionc mm : feedetail) {
				if (mm.getRecipietNo().equals(String.valueOf(a[i]))) {
					d.setReciptno(mm.getRecipietNo());

					d.setStudentid(mm.getStudentid());
					feetype = mm.getFeetype();

					if (feetype.equals("Security Fee")) {
						eaxmfee = mm.getFeeamunt();
						d.setExamfee(String.valueOf(mm.getFeeamunt()));
					}
					if (feetype.equals("Addmission Fee")) {
						registfee = mm.getFeeamunt();
						d.setRegistrationfee(String.valueOf(mm.getFeeamunt()));
					}
					if (feetype.equals("Registration Fee")) {
						annualfee = mm.getFeeamunt();
						d.setAnnualfee(String.valueOf(mm.getFeeamunt()));
					}
					if (feetype.equals("Transport Fee")) {
						transportfee = mm.getFeeamunt();
						d.setTransportationfee(String.valueOf(mm.getFeeamunt()));
					}
					if (feetype.equals("School Fee")) {
						tutionfee = mm.getFeeamunt();
						d.setTutionfee(String.valueOf(mm.getFeeamunt()));
					}
					if (feetype.equals("Activity Fee")) {
						activityfee = mm.getFeeamunt();
						d.setActivityfee(String.valueOf(mm.getFeeamunt()));
					}
					if (feetype.equals("Art Craft Fee")) {
						artfee = mm.getFeeamunt();
						d.setArtfee(String.valueOf(mm.getFeeamunt()));
					}
					if (feetype.equals("Term Fee")) {
						termfee = mm.getFeeamunt();
						d.setTermfee(String.valueOf(mm.getFeeamunt()));
					}
					int totalamuont = eaxmfee + registfee + annualfee
							+ transportfee + tutionfee+termfee+artfee+activityfee;
					d.setFeedate(mm.getFeedate());
					d.setAmount(String.valueOf(totalamuont));
					d.setChequenumber(mm.getChequeno());
					d.setBankname(mm.getBankname());
					d.setFeedate(mm.getFeedate());
					d.setPaymentmode(mm.getPaymentmode());
					StudentInfo info=objStd.studentDetail(id,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schid, tempList, conn).get(0);
					
					d.setStudentname(info.getFname());
					d.setClassname(info.getClassName());
					d.setFathername(info.getFathersName());
					d.setMothername(info.getMotherName());
				}
			}
			dailyfee.add(d);
			registfee = 0; annualfee = 0; tutionfee = 0; transportfee = 0; eaxmfee = 0;termfee = 0; activityfee = 0; artfee = 0;
			show = true;

		}

		StudentInfo info=objStd.studentDetail(id,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schid, tempList, conn).get(0);

		studentName=info.getFname();
		className=info.getClassName();
		fathersName=info.getFathersName();
		addNumber=String.valueOf(info.getAddNumber());
		date=new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static int[] removeDuplicates(int[] arr) {

		Set<Integer> alreadyPresent = new HashSet<>();
		int[] whitelist = new int[0];

		for (int nextElem : arr) {
			if (!alreadyPresent.contains(nextElem)) {
				whitelist = Arrays.copyOf(whitelist, whitelist.length + 1);
				whitelist[whitelist.length - 1] = nextElem;
				alreadyPresent.add(nextElem);
			}
		}
		return whitelist;
	}

	public String duplicateFee() {
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		ss.setAttribute("selectedstudent", selectedstudent);
		return "duplicatefeerecipt";
	}



	public ArrayList<DailyFeeCollectionBean> getDailyfee() {
		return dailyfee;
	}

	public void setDailyfee(ArrayList<DailyFeeCollectionBean> dailyfee) {
		this.dailyfee = dailyfee;
	}

	public ArrayList<Feecollectionc> getFeedetail() {
		return feedetail;
	}

	public DailyFeeCollectionBean getSelectedstudent() {
		return selectedstudent;
	}

	public void setSelectedstudent(DailyFeeCollectionBean selectedstudent) {
		this.selectedstudent = selectedstudent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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



	public String getStudentclass() {
		return studentclass;
	}

	public void setStudentclass(String studentclass) {
		this.studentclass = studentclass;
	}

	public String getStudentname() {
		return studentname;
	}

	public void setStudentname(String studentname) {
		this.studentname = studentname;
	}

	public String getFathername() {
		return fathername;
	}

	public void setFathername(String fathername) {
		this.fathername = fathername;
	}

	public void setFeedetail(ArrayList<Feecollectionc> feedetail) {
		this.feedetail = feedetail;
	}



	public String getMothername() {
		return mothername;
	}

	public void setMothername(String mothername) {
		this.mothername = mothername;
	}


	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getFathersName() {
		return fathersName;
	}

	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
