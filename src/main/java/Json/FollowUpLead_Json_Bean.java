package Json;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean(name="followUpLeadJson")
@ViewScoped
public class FollowUpLead_Json_Bean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String json;
	public FollowUpLead_Json_Bean()
	{/*
		Connection conn=DataBaseConnection.javaConnection();
		Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		Date fDate=null,searchDate=null;
		try {
			fDate = new SimpleDateFormat("yyyy-dd-MM").parse(params.get("followDate"));
			searchDate = new SimpleDateFormat("yyyy-dd-MM").parse(params.get("searchDate"));
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		String leadId=params.get("leadId");
		String id=params.get("id");
		String actionName=params.get("actionName");
        String description = params.get("description");
        String leadType = params.get("leadType");

        String searchType = params.get("searchType");
		String userId = params.get("userId");

		ArrayList<LeadInfo> leadList=new ArrayList<>();
		if( actionName.equals("record") || actionName.equals("overdue"))
		{
			if(actionName.equals("record"))
			{
				leadList=new DataBaseMethodsLead().enquiryForFollowUp(searchDate,searchType,userId,conn);
			}
			else if(actionName.equals("overdue"))
			{
				leadList=new DataBaseMethodsLead().overDueEnquiry(searchDate,userId,conn);
			}
			if(leadList.size()>0)
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				for(LeadInfo ll:leadList)
				{
					JSONObject obj = new JSONObject();
					obj.put("sNo",ll.getsNo());
				    obj.put("clientName",ll.getClientName());
				    obj.put("contactNo",ll.getContactNo());
				    obj.put("city",ll.getCity());
				    obj.put("status",ll.getStatus());
				    obj.put("productType",ll.getTypeName());
				    arr.add(obj);
				}
				mainobj.put("LeadJson", arr);
				json=mainobj.toJSONString();
			}
			else
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "No Records Found");
			    arr.add(obj);
				mainobj.put("LeadJson", arr);
				json=mainobj.toJSONString();
			}
		}
		else if(actionName.equals("history"))
		{
			leadList=new DataBaseMethodsLead().historyOfEnquiry(leadId,conn);
			if(leadList.size()>0)
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				for(LeadInfo ll:leadList)
				{
					JSONObject obj = new JSONObject();
					obj.put("sNo",ll.getsNo());
				    obj.put("followDateStr",ll.getFollowDateStr());
				    obj.put("status",ll.getStatus());
				    obj.put("description",ll.getDescription());
				    obj.put("leadType",ll.getLeadType());
				    arr.add(obj);
				}
				mainobj.put("LeadJson", arr);
				json=mainobj.toJSONString();
			}
			else
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "No Records Found");
			    arr.add(obj);
				mainobj.put("LeadJson", arr);
				json=mainobj.toJSONString();
			}
		}
		else if(actionName.equals("accept"))
		{
			int i=new DataBaseMethodsLead().acceptEnquiry(id,leadId,conn);
			if(i==1)
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "Lead Accepted Sucessfully");
			    arr.add(obj);
				mainobj.put("LeadJson", arr);
				json=mainobj.toJSONString();
			}
			else
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "An Error Occured");
				arr.add(obj);
				mainobj.put("LeadJson", arr);
				json=mainobj.toJSONString();
			}
		}
		else if(actionName.equals("denieds"))
		{
			int i=new DataBaseMethodsLead().denyEnquiry(id,leadId,conn);
			if(i==1)
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "Lead Denied Sucessfully");
			    arr.add(obj);
				mainobj.put("LeadJson", arr);
				json=mainobj.toJSONString();
			}
			else
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "An Error Occured");
				arr.add(obj);
				mainobj.put("LeadJson", arr);
				json=mainobj.toJSONString();
			}
		}
		else if(actionName.equals("followUp"))
		{
			int i=new DataBaseMethodsLead().addFollowUp(leadId,fDate, "pending", description,id,leadType,conn);
			if(i==1)
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "Lead Accepted Sucessfully");
			    arr.add(obj);
				mainobj.put("LeadJson", arr);
				json=mainobj.toJSONString();
			}
			else
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "An Error Occured");
				arr.add(obj);
				mainobj.put("LeadJson", arr);
				json=mainobj.toJSONString();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
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

	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
}


