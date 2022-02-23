package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeInfo;
import schooldata.StudentInfo;

@ManagedBean(name="studentFeeCollectionJSON")
@ViewScoped

public class StudentFeeCollectionJsonBean implements Serializable
{
	String json;
	StudentInfo sList;
	ArrayList<FeeInfo> classFeeList;
	int dueAmount=0;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	public StudentFeeCollectionJsonBean() {


		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String addmissionNumber = params.get("studentid");
			String schid=params.get("Schoolid");
			String date = params.get("date");

			Date d=null;
			try {
				d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				String preSession=DatabaseMethods1.selectedSessionDetails(schid,conn);
				DBJ.fullSchoolInfo(schid,conn);

				sList=DBJ.studentDetailslistByAddNo(addmissionNumber, schid, conn);

				ArrayList<StudentInfo>studentList=DBJ.searchStudentListForDueFeeReport(addmissionNumber,d,preSession,conn,schid);

				HashMap<String,String>map=(HashMap<String, String>) studentList.get(0).getFeesMap();

				classFeeList=DBJ.classFeesForStudent(sList.getClassId(),preSession,sList.getStudentStatus(),sList.getConcession(),conn,schid);
				classFeeList=DBM.addPreviousFee(schid,classFeeList,addmissionNumber,conn);
				dueAmount=0;
				for(FeeInfo ls:classFeeList)
				{
					if(!ls.getFeeId().equals("-2")&&!ls.getFeeId().equals("-3"))
					{
						ls.setDueamount(map.get(ls.getFeeName()));
						ls.setPayAmount(Integer.parseInt(map.get(ls.getFeeName())));
						dueAmount+=Integer.parseInt(map.get(ls.getFeeName()));

					}
				}

				if(new DatabaseMethods1().schoolId().equals("206"))
				{
					int i =DBJ.totalStudentExpense(addmissionNumber,conn,schid);
					classFeeList.get(classFeeList.size()-1).setFeeAmount(i);

				}

				ArrayList<FeeInfo> tempList = new ArrayList<>();
				tempList.addAll(classFeeList);
				for (FeeInfo ls : tempList)
				{
					if(ls.getDueamount()==null)
					{

					}
					else if(ls.getDueamount().equals("0") || ls.getDueamount().equals("0.0"))
					{
						classFeeList.remove(ls);
					}
				}

				for(FeeInfo ff : classFeeList)
				{
					JSONObject obj = new JSONObject();

					obj.put("feeName", ff.getFeeName());
					obj.put("feeId", ff.getFeeId());
					if(ff.getFeeId().equals("-2") || ff.getFeeId().equals("-3"))
					{
						obj.put("dueAmount", "0");
					}
					else
					{
						obj.put("dueAmount", ff.getDueamount());
					}

					obj.put("discount", String.valueOf(ff.getPayDiscount()));
					obj.put("amount", String.valueOf(ff.getPayAmount()));

					arr.add(obj);
				}
			}

			json=arr.toJSONString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		
		
	}


	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}

}
