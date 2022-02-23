package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name="studentLedgerBean")
@ViewScoped
public class MasterStudentLedgerBean implements Serializable
{

	StudentInfo sList;
	ArrayList<FeeInfo> list = new ArrayList<>();
	String schoolid="",name,fathersName,className;
	boolean waveOffLateFee=false;
	public MasterStudentLedgerBean() {

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		sList = (StudentInfo) ss.getAttribute("selectedStudent");
		lateFeeCalculation();
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
		String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		String preSession=DatabaseMethods1.selectedSessionDetails(schoolid,conn);
		StudentInfo sList=DBJ.studentDetailslistByAddNo(addmissionNumber, schoolid, conn);
		String studentStatus = sList.getStudentStatus();
		String connsessioncategory = sList.getConcession();
		ArrayList<FeeInfo> classFeeList;
		new ArrayList<>();
		//JSONArray arr=new JSONArray();
		String[] session=preSession.split("-");
		for(int i=0;i<feemonths.length;i++)
		{
			String findDate1="";
			if(Integer.parseInt(feemonths[i])>3)
			{
				findDate1="01/"+feemonths[i]+"/"+session[0];
			}
			else
			{
				findDate1="01/"+feemonths[i]+"/"+session[1];
			}


			/*if(i > 4)
			{
				Integer.parseInt(feemonths[i])+12;
			}
			else
			{
				Integer.parseInt(feemonths[i]);
			}*/


			try {
				new SimpleDateFormat("dd/MM/yyyy").parse(findDate1);
				new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
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


					if(mainamount>0)
					{

						ArrayList<String> totalpaidamount = DBM.FeePaidRecordForCheck1(sList.getSchid(),sList, preSession, ls.getFeeId(),feemonths[i],conn);


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

			//JSONObject obj = new JSONObject();




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



}
