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
import schooldata.IncomeList;

@ManagedBean(name="incomejson")
@ViewScoped
public class IncomeJsonBean implements Serializable {

	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();


	public IncomeJsonBean() throws ParseException
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String stdate=params.get("Sdate");
			String edate= params.get("Edate");

			Date startdate=new SimpleDateFormat("dd/MM/yyyy").parse(stdate);
			Date enddate=new SimpleDateFormat("dd/MM/yyyy").parse(edate);

			

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<IncomeList>incomeList=dbm.filteredIncome(startdate,enddate,"-1","all",conn);

				for(IncomeList ls:incomeList)
				{
					JSONObject obj = new JSONObject();
					obj.put("name", ls.getCategory());
					obj.put("fromName", ls.getCategory());
					obj.put("amount",ls.getAmount());
					obj.put("BoucherNo",ls.getVoucherNo());
					arr.add(obj);
				}
			}

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
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
