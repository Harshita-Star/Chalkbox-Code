package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import session_work.QueryConstants;
import student_module.RegistrationColumnName;

@ManagedBean(name="studentUserIdInfo")
@ViewScoped
public class StudentUserIdInfoBean implements Serializable
{
	String mobileNo,selectedId;
	ArrayList<SelectItem> userIdlist;
	boolean show;
	ArrayList<String> list=new ArrayList<>();
	
	public StudentUserIdInfoBean()
	{
		list.add(RegistrationColumnName.SCHID);
	}
	
	public void getInformation()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		userIdlist=DBM.studentUserIdByMobileNo(mobileNo,conn);
		if(userIdlist.size()==1)
		{
			String userId=(String) userIdlist.get(0).getValue();

			
			String schid=new DataBaseMethodStudent().studentDetail(userId, "", "", QueryConstants.ADD_NUMBER, QueryConstants.SCH_ID, null,null,"","","","","","", list, conn).get(0).getSchid();
			SchoolInfoList info = DBM.fullSchoolInfo(schid, conn);
			String password=DBM.studentPasswordById(userId,schid,conn);

			String message="Dear User, Your User Id is:- "+userId+" and Password is :- "+password+"  \\n\\nRegards, \r\n"+ info.getSchoolName();
			//(message+"..."+mobileNo+"............"+userId);
			DBM.messageurl1(mobileNo, message, userId, conn,schid,"");
			try
			{
				FacesContext.getCurrentInstance().getExternalContext().redirect("ChalkboxLogin.xhtml");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			show=true;
			//(userIdlist.size());
		}
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void getUserId()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM= new DatabaseMethods1();


		String schid=new DataBaseMethodStudent().studentDetail(selectedId, "", "", QueryConstants.ADD_NUMBER, QueryConstants.SCH_ID, null,null,"","","","","","", list, conn).get(0).getSchid();
		SchoolInfoList info = DBM.fullSchoolInfo(schid, conn);
		String password=DBM.studentPasswordById(selectedId,schid,conn);

		String message="Dear User, Your User Id is:- "+selectedId+" and Password is :- "+password+"  \\n\\nRegards, \\n\"\r\n"+ info.getSchoolName();
		//(message+"..."+mobileNo+"............"+selectedId);
		DBM.messageurl1(mobileNo, message, selectedId, conn,schid,"");

		try
		{
			FacesContext.getCurrentInstance().getExternalContext().redirect("ChalkboxLogin.xhtml");
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getSelectedId() {
		return selectedId;
	}

	public void setSelectedId(String selectedId) {
		this.selectedId = selectedId;
	}

	public ArrayList<SelectItem> getUserIdlist() {
		return userIdlist;
	}

	public void setUserIdlist(ArrayList<SelectItem> userIdlist) {
		this.userIdlist = userIdlist;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

}
