package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;
import timetable_module.DataBaseMethodsTimeTable;
import timetable_module.NewTimeTableInfo;
import timetable_module.TimeTableSettingInfo;

@ManagedBean(name="teacherWiseTimeTableJson")
@ViewScoped
public class TeacherWiseTimeTableJson implements Serializable
{

	String json;
	String class_name,section_name,teacher_id,teacher_name,teacher_username;

	ArrayList<NewTimeTableInfo> timeTableList;
	boolean show;
	int totalLoadLab,totalLoadTheory;
	String SchoolId;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public TeacherWiseTimeTableJson()
	{

		Connection conn = DataBaseConnection.javaConnection();
        try {
        	Map<String, String> params =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestParameterMap();

    		teacher_username=params.get("username");
    		SchoolId=params.get("Schoolid");
    		
    		Map<String, String> sysParams =FacesContext.getCurrentInstance().
    				getExternalContext().getRequestHeaderMap();
    		String userAgent = sysParams.get("User-Agent");
    		boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
    		
    		if(checkRequest)
    		{
    			SchoolInfoList sInfo=DBJ.fullSchoolInfo(SchoolId, conn);

    			teacher_id=DBJ.teacherid(teacher_username,SchoolId,conn);
    			teacher_name = DBJ.teacherNameByTeacherId(teacher_id,SchoolId,conn);

    			TimeTableSettingInfo schedule_info = DBJ.timeTableInfo(SchoolId,conn);
    			if(schedule_info!=null)
    			{
    				if(schedule_info.getNo_of_lec()==null || schedule_info.getNo_of_lec().equals(""))
    				{
    					//JSONObject mainobj = new JSONObject();
    					JSONArray arr=new JSONArray();

    					JSONObject obj = new JSONObject();
    					obj.put("Lec_No", "N.A");
    					obj.put("lec_time", "N.A");
    					obj.put("mon_class", "N.A");
    					obj.put("mon_sub", "N.A.");
    					obj.put("tue_class", "N.A.");
    					obj.put("tue_sub", "N.A.");
    					obj.put("wed_class", "N.A.");
    					obj.put("wed_sub", "N.A.");
    					obj.put("thu_class", "N.A.");
    					obj.put("thu_sub", "N.A.");
    					obj.put("fri_class", "N.A.");
    					obj.put("fri_sub", "N.A.");
    					obj.put("sat_class", "N.A.");
    					obj.put("sat_sub", "N.A.");
    					arr.add(obj);

    					json=arr.toJSONString();
    				}
    				else
    				{
    					int noOfLec = Integer.valueOf(schedule_info.getNo_of_lec());
    					String timeOfLec[] =  schedule_info.getTime_of_lec().split(",");
    					String startTime =  schedule_info.getStart_time();
    					
    					String wintimeOfLec[] =  schedule_info.getWinterLecTime().split(",");
    					String winterStartTime =  schedule_info.getWinterStartTime();

    					timeTableList = new ArrayList<>();
    					for(int i=0;i<noOfLec;i++)
    					{
    						NewTimeTableInfo info = new NewTimeTableInfo();
    						try
    						{
    							info.setLecTime(startTime+" - "+timeOfLec[i]);
    							info.setWinterLecTime(winterStartTime+" - "+wintimeOfLec[i]);
    							info.setLecNo(String.valueOf(i+1));
    							startTime=timeOfLec[i];
    							winterStartTime = wintimeOfLec[i];
    						}
    						catch (Exception e )
    						{
    							e.printStackTrace();
    						}
    						timeTableList.add(info);
    					}
    					timeTableList = new DataBaseMethodsTimeTable().teacherTimeTableDetail(teacher_id,timeTableList,SchoolId,conn);
    					totalLoadLab=timeTableList.get(timeTableList.size()-1).getTotalLoadLab();
    					totalLoadTheory=timeTableList.get(timeTableList.size()-1).getTotalLoadTheory();

    					new JSONObject();
    					JSONArray arr=new JSONArray();
    					for(NewTimeTableInfo ls:timeTableList)
    					{
    						JSONObject obj = new JSONObject();
    						obj.put("Lec_No", sInfo.getLectureLabel()+" "+ls.getLecNo());
    						if(sInfo.getTimeTableSchedule().equalsIgnoreCase("summer"))
    						{
    							obj.put("lec_time", ls.getLecTime());
    						}
    						else
    						{
    							obj.put("lec_time", ls.getWinterLecTime());
    						}
    						//obj.put("lec_time", ls.getLecTime());
    						obj.put("mon_class", ls.getMon_class());
    						obj.put("mon_sub", ls.getMon_sub_name());
    						obj.put("tue_class", ls.getTues_class());
    						obj.put("tue_sub", ls.getTues_sub_name());
    						obj.put("wed_class", ls.getWed_class());
    						obj.put("wed_sub", ls.getWed_sub_name());
    						obj.put("thu_class", ls.getThur_class());
    						obj.put("thu_sub", ls.getThur_sub_name());
    						obj.put("fri_class", ls.getFri_class());
    						obj.put("fri_sub", ls.getFri_sub_name());
    						obj.put("sat_class", ls.getSat_class());
    						obj.put("sat_sub", ls.getSat_sub_name());
    						arr.add(obj);
    					}
    					json=arr.toJSONString();
    				}

    			}
    			else
    			{
    				JSONArray arr=new JSONArray();

    				JSONObject obj = new JSONObject();
    				obj.put("Lec_No", "N.A");
    				obj.put("lec_time", "N.A");
    				obj.put("mon_class", "N.A");
    				obj.put("mon_sub", "N.A.");
    				obj.put("tue_class", "N.A.");
    				obj.put("tue_sub", "N.A.");
    				obj.put("wed_class", "N.A.");
    				obj.put("wed_sub", "N.A.");
    				obj.put("thu_class", "N.A.");
    				obj.put("thu_sub", "N.A.");
    				obj.put("fri_class", "N.A.");
    				obj.put("fri_sub", "N.A.");
    				obj.put("sat_class", "N.A.");
    				obj.put("sat_sub", "N.A.");
    				arr.add(obj);

    				json=arr.toJSONString();
    			}
    		}
    		else
    		{
    			JSONArray arr=new JSONArray();

    			JSONObject obj = new JSONObject();
    			obj.put("Lec_No", "N.A");
    			obj.put("lec_time", "N.A");
    			obj.put("mon_class", "N.A");
    			obj.put("mon_sub", "N.A.");
    			obj.put("tue_class", "N.A.");
    			obj.put("tue_sub", "N.A.");
    			obj.put("wed_class", "N.A.");
    			obj.put("wed_sub", "N.A.");
    			obj.put("thu_class", "N.A.");
    			obj.put("thu_sub", "N.A.");
    			obj.put("fri_class", "N.A.");
    			obj.put("fri_sub", "N.A.");
    			obj.put("sat_class", "N.A.");
    			obj.put("sat_sub", "N.A.");
    			arr.add(obj);

    			json=arr.toJSONString();
    		}
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





