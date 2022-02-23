package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.ImprestList;

@ManagedBean(name="studentimprestledgerJson")
@ViewScoped
public class StudentImprestLedgerBeanJson implements Serializable {

	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();

	public StudentImprestLedgerBeanJson() {

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String start_date = params.get("start_date");
			String end_date = params.get("end_date");
			String schid=  params.get("schid");

			Date newDate=null;
			Date endDate=null;
			try {
				newDate= new SimpleDateFormat("dd/MM/yyyy").parse(start_date);
				endDate=new SimpleDateFormat("dd/MM/yyyy").parse(end_date);

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
				ArrayList<ImprestList>list=DBM.showimaprestlist(studentid,conn,newDate,endDate,schid);

				for(ImprestList ls:list)
				{
					JSONObject obj = new JSONObject();

					obj.put("date",ls.getAdd_date());
					obj.put("desc", ls.getDescription());
					obj.put("debit",ls.getDebitamount());
					obj.put("credit",ls.getCreditAmount());

					arr.add(obj);
				}
			}

			json=arr.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
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
