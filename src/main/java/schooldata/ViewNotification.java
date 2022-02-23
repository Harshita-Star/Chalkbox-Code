package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;




@ManagedBean(name="viewNotification")
@ViewScoped
public class ViewNotification implements Serializable
{
	ArrayList<NotificationInfo> list;
	boolean show;
	public ViewNotification()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String userName=(String) ss.getAttribute("username");

		list=new DatabaseMethods1().allMessage(userName,conn);
		if(list.size()<=0)
		{
			show=false;
		}
		else
		{
			show=true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<NotificationInfo> getList() {
		return list;
	}
	public void setList(ArrayList<NotificationInfo> list) {
		this.list = list;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}


}
