package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.IncomeList;

@ManagedBean(name="incomeExpenseCombineJson")
@ViewScoped

public class IncomeExpenseCombineJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public IncomeExpenseCombineJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String startDate=params.get("startDate");
			String endDate=params.get("endDate");
			String schid = params.get("schid");
			
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				Date sDate=null,eDate=null;
				try {
					sDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
					eDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
				} catch (ParseException e) {
					
					e.printStackTrace();
				}

				ArrayList<IncomeList> list=DBJ.filteredReportTable(sDate, eDate,conn,schid);
				Collections.sort(list);
				
				for(IncomeList ls:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("category",ls.getCategory());
					obj.put("description",ls.getDescription());
					obj.put("date",ls.getStrIncomeDate());
					obj.put("amount", new DecimalFormat("##").format(ls.getAmount()));
					obj.put("type", ls.getIncome());
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
	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}


}
