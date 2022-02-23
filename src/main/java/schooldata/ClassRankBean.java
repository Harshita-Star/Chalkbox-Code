package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.ReorderEvent;

@ManagedBean(name="classRank")
@ViewScoped

public class ClassRankBean implements Serializable
{
	String schid;
	ArrayList<ClassInfo> classList;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	String sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public ClassRankBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schid = DBM.schoolId();
		sessionValue = DBM.selectedSessionDetails(schid,conn);
		classList=DBM.allClassListForRank(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void onRowReorder(ReorderEvent event)
    {
//        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Rank Changed", "");
//        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		

		int i = DBM.assignClassRank(classList,conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Rank Assigned Successfully."));
			classList=DBM.allClassListForRank(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
	
		int x=1;
		
		for (ClassInfo ss : classList) {
		
			if(value.equals(""))
			{
				value = "("+DBM.classNameFromidSchid(schid,ss.getGroupid(), sessionValue, conn) +"---"+x+")";
			}
			else
			{
				value = value + "("+DBM.classNameFromidSchid(schid,ss.getGroupid(), sessionValue, conn)+"---"+x+")";
			}

			
		 x=x+1;	
		}
		
		
		String refNo = workLg.saveWorkLogMehod(language,"Class Ordering","WEB",value,conn);
		return refNo;
	}


	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public ArrayList<ClassInfo> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<ClassInfo> classList) {
		this.classList = classList;
	}
}
