package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.DailyFeeCollectionBean;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="collectionReportJson")
@ViewScoped

public class CollectionReportJsonBean implements Serializable
{
	String json="",schoolid="";
	ArrayList<DailyFeeCollectionBean> dailyfee = new ArrayList<>();
	ArrayList<DailyFeeCollectionBean> getdailyfeecollection;
	Date feedate,endDate;
	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();

	public CollectionReportJsonBean()
	{
		Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();
		schoolid=params.get("schoolid");
		String start_date = params.get("startdate");
		String end_date = params.get("enddate");
		
		JSONArray arr=new JSONArray();

		Map<String, String> sysParams =FacesContext.getCurrentInstance().
				getExternalContext().getRequestHeaderMap();
		String userAgent = sysParams.get("User-Agent");
		boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
		
		if(checkRequest)
		{
			feedate=null;
			endDate=null;
			try {
				feedate= new SimpleDateFormat("dd/MM/yyyy").parse(start_date);
				endDate=new SimpleDateFormat("dd/MM/yyyy").parse(end_date);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			showdailyfeelist();

			for(DailyFeeCollectionBean dd : getdailyfeecollection)
			{
				JSONObject obj = new JSONObject();
				obj.put("name", dd.getStudentname());
				obj.put("father", dd.getFathername());
				obj.put("class", dd.getClassname());
				obj.put("receipt", dd.getReciptno());
				obj.put("amount", dd.getAmount());
				obj.put("discount", dd.getDiscount());
				obj.put("mode", dd.getPaymentmode());
				obj.put("date", dd.getFeeDateStr());
				obj.put("user", dd.getUser());
				arr.put(obj);

			}
		}
		

		json = arr.toString();
	}

	public void showdailyfeelist()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			new DataBaseMeathodJson();
			//int registfee = 0,annualfee=0,tutionfee=0,transportfee=0,eaxmfee =0,artFee=0,termfee=0;

			dailyfee=new ArrayList<>();

			///feelist=obj.viewFeeList1(conn);

			HashMap<String, String> tempMap=new HashMap<>();
			getdailyfeecollection=DBM.tempGetDateWiseFeecollection(schoolid,feedate,"-1",endDate,tempMap,conn,"all","-1");
			/* ArrayList<String> temp=new ArrayList<String>();
			 for(Feecollectionc mm:getdailyfeecollection)
			 {
				 temp.add(mm.getRecipietNo());
			 }
			 String a[]=new String[temp.size()];
			 for(int i=0;i<temp.size();i++)
			 {
				 a[i]=temp.get(i);
			 }
			 a=removeDuplicates(a);

			 String value="";
			 ArrayList<String>tempdeatils;

			 for(int i=0;i<a.length;i++)
			 {
				 tempdeatils=new ArrayList<>();
				 HashMap<String, String> feecllection=new HashMap<>();
				 HashMap<String, String> totalAmoutMap=new HashMap<>();
				 HashMap<String, String> discountMap=new HashMap<>();
				 DailyFeeCollectionBean ll=new DailyFeeCollectionBean();
				 int totalamuont=0,totalDiscount=0;

				 for(Feecollectionc list : getdailyfeecollection)
				 {

					 if(list.getRecipietNo().equals(String.valueOf(a[i])))
					 {
						 ll.setSchid(list.getSchid());//
						 ll.setStudentname(list.getStudentname());//
						 ll.setClassname(list.getClassName());//
						 ll.setFathername(list.getFathername());//
						 ll.setStudentid(list.getStudentid());//
						 ll.setUser(list.getUser());//
						 ll.setSrNo(list.getSrNo());//
						 ll.setMothername(list.getMotherName());//
						 ll.setSection(list.getSectionName());
						 ll.setFeeRemark(list.getFeeRemark());
						 ll.setFeedate(list.getFeedate());
						 ll.setChallanDate(list.getChallanDate());
						 ll.setDueDateStr(list.getDueDateStr());
						 ll.setReciptno(list.getRecipietNo());
						 ll.setFeeDateStr(sdf.format(list.getFeedate()));

						 String feetype=list.getFeeName();
						 feecllection.put(feetype, String.valueOf(list.getFeeamunt()));
						 discountMap.put(feetype, String.valueOf(list.getDiscount()));
						 totalAmoutMap.put(feetype, String.valueOf(list.getTotalAmount()));
					     ll.setAllFess(feecllection);
					     ll.setAllDiscount(discountMap);
					     ll.setAllTotalAmount(totalAmoutMap);
						 ll.setBankname(list.getBankname());
				         ll.setChequenumber(list.getChequeno());
			             ll.setPaymentmode(list.getPaymentmode());
				         if(tempdeatils.size()==0)
				         {
				        	 	value="orignal";
				         }
				         else
				         {
					        	 for(String ls:tempdeatils)
					        	 {
						        	   if(ls.equals(feetype))
						        	   {
						        		   value="duplicate";
						        		   break;
						        	   }
						        	   else
						        	   {
						        		   value="orignal";
						        	   }
					        	 }
				         }

				         if(value.equals("orignal"))
				         {
					        	 tempdeatils.add(feetype);
					        	 totalamuont+=list.getFeeamunt();
					        	 totalDiscount+=list.getDiscount();
				         }

				         ll.setAmount(String.valueOf(totalamuont));
				         ll.setDiscount(String.valueOf(totalDiscount));
				         ll.setPaymentmode(list.getPaymentmode());
				         //ll.setSrno(count);

					 }



				 }
				 dailyfee.add(ll);

			 }
			 */	
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

	public static String[] removeDuplicates(String[] arr)
	{
		Set<String> alreadyPresent = new HashSet<>();
		String[] whitelist = new String[0];
		for (String nextElem : arr)
		{
			if (!alreadyPresent.contains(nextElem)) {
				whitelist = Arrays.copyOf(whitelist, whitelist.length + 1);
				whitelist[whitelist.length - 1] = nextElem;
				alreadyPresent.add(nextElem);
			}
		}
		return whitelist;
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
