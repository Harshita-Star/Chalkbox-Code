package Json;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean(name="allLeadJson")
@ViewScoped
public class AllLead_Json_Bean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String json;
	public AllLead_Json_Bean()
	{
		/*Connection conn=DataBaseConnection.javaConnection();

		Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String leadId=params.get("leadId");
		String actionName=params.get("actionName");
        String searchStatus = params.get("searchStatus");
        String userId = params.get("userId");
        String type = params.get("type");
        String selectedEmp = params.get("selectedEmp");
        String searchType = params.get("searchType");

        String name = params.get("name");
		String address = params.get("address");
		String cpNo = params.get("cpNo");
		String cpName = params.get("cpName");
		String otherNo = params.get("otherNo");
		String city = params.get("city");

		ArrayList<LeadInfo> list=new ArrayList<>();
		if(actionName.equals("record"))
		{
			list=new DataBaseMethodsLead().allEnquiryList(searchStatus,userId,type,selectedEmp,searchType,conn);
			if(list.size()>0)
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				for(LeadInfo ll:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("sNo",ll.getsNo());
				    obj.put("clientName",ll.getClientName());
				    obj.put("contactNo",ll.getContactNo());
				    obj.put("city",ll.getCity());
				    obj.put("leadType",ll.getLeadType());
				    obj.put("status",ll.getStatus());
				    obj.put("productType",ll.getTypeName());
				    obj.put("addDateStr",ll.getAddDateStr());
				    obj.put("addedByName",ll.getAddedByName());
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
			list=new DataBaseMethodsLead().historyOfEnquiry(leadId,conn);
			if(list.size()>0)
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				for(LeadInfo ll:list)
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
		else if(actionName.equals("updateLead"))
		{
			int i=new DataBaseMethodsLead().updateLead(leadId,name,address,cpName,cpNo,otherNo,city,conn);
			if(i==1)
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "Lead Updated Sucessfully");
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


