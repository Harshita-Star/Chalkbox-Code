package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name="partStdLedgerBean")
@ViewScoped
public class ParticularStudentFeeLedgerBean implements Serializable
{

	StudentInfo sList;
	ArrayList<FeeInfo> list = new ArrayList<>();
	String schoolid="",autoname,branches,name,fathersName,className;
	boolean waveOffLateFee=false;
	ArrayList<StudentInfo> studentList;
	ArrayList<SelectItem> branchList;
	
	String session;

	public ParticularStudentFeeLedgerBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");

		branches="";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
				}
			}
		}
	}

	public void searchStudentByAutoName()
	{
		int index=autoname.lastIndexOf(":")+1;
		String id=autoname.substring(index);

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
						sList=info;

						lateFeeCalculation();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}

	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(branches,query,conn);
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ "/ "+info.getSrNo()+"-:"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public void lateFeeCalculation()
	{

		Connection conn= DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		list = new ArrayList<>();
		String[] feemonths= {"4","6","8","9","11","12","1","3"};
		String[] allfeemonths= {"April","May-June","Jul-Aug","September","Oct-Nov","December","January","Feb-Mar"};
		String addmissionNumber=sList.getAddNumber();
		name=sList.getFname();
		fathersName=sList.getFathersName();
		className=sList.getClassName();
		schoolid = sList.getSchid();
		//String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		String preSession=session;
		StudentInfo sList=DBJ.studentDetailslistByAddNo(addmissionNumber, schoolid, conn);
		String studentStatus = sList.getStudentStatus();
		String connsessioncategory = sList.getConcession();
		ArrayList<FeeInfo> classFeeList;
		//JSONArray arr=new JSONArray();
		for(int i=0;i<feemonths.length;i++)
		{
			int count=1;
			if(feemonths[i].equals("6")||feemonths[i].equals("8")||feemonths[i].equals("11")||feemonths[i].equals("3"))
			{
				count=2;
			}

			ArrayList<StudentInfo> studentList = DBM.searchStudentListForDueFeeReportForMasterForFees(schoolid,addmissionNumber, Integer.parseInt(feemonths[i]),
					preSession, conn, "feeCollection",count);

			HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();

			classFeeList = DBM.classFeesForStudent(schoolid,sList.getClassId(), preSession, studentStatus, connsessioncategory,
					conn);
			classFeeList = DBM.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);
			for (FeeInfo ls : classFeeList) {
				int mainamount=0;
				String amount="";
				String datet="";
				int balamount = 0;
				if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {
					mainamount=Integer.parseInt(map.get(ls.getFeeName()));
					////// // System.out.println(feemonths[i]+"....main....."+mainamount);


					if(mainamount>0)
					{

						ArrayList<String> totalpaidamount = DBM.FeePaidRecordForCheck1(sList.getSchid(),sList, preSession, ls.getFeeId(),feemonths[i],conn);
						////// // System.out.println(feemonths[i]+"....paid....."+totalpaidamount);


						for(String lss:totalpaidamount)
						{
							String[] test=lss.split("-");


							if(amount.equals(""))
							{
								amount=test[1];
								balamount=Integer.parseInt(test[1]);
								datet=test[0];
							}
							else
							{
								amount=amount+"\n\n\n"+test[1];
								datet=datet+"\n\n\n"+test[0];
								balamount=balamount+Integer.parseInt(test[1]);
							}
						}

						int bamount=mainamount-balamount;

						FeeInfo ff = new FeeInfo();
						ff.setFeeName(ls.getFeeName());
						ff.setDueamount(String.valueOf(mainamount));
						ff.setAmount(bamount);
						ff.setFeeAmount(balamount);
						ff.setStrAmount(amount);
						ff.setStdate(datet);
						ff.setFeeMonth(allfeemonths[i]);
						ff.setFeeMonthInt(Integer.parseInt(feemonths[i]));
						//ff.setTotalFee(ff.getAmount());
						list.add(ff);

					}
				}


			}

		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public StudentInfo getsList() {
		return sList;
	}


	public void setsList(StudentInfo sList) {
		this.sList = sList;
	}


	public ArrayList<FeeInfo> getList() {
		return list;
	}


	public void setList(ArrayList<FeeInfo> list) {
		this.list = list;
	}


	public String getSchoolid() {
		return schoolid;
	}
	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}
	public String getAutoname() {
		return autoname;
	}

	public void setAutoname(String autoname) {
		this.autoname = autoname;
	}

	public String getBranches() {
		return branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
	
	
}
