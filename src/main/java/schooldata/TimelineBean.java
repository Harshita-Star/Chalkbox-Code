package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="timelineBean")
@ViewScoped
public class TimelineBean implements Serializable
{
	
	ArrayList<TimelineList> list=new ArrayList<>();
	
   public TimelineBean()
   {
	   Connection conn= DataBaseConnection.javaConnection();
	   list=new DatabaseMethods1().alltimelist(new DatabaseMethods1().schoolId(),conn);
   }

public ArrayList<TimelineList> getList() {
	return list;
}

public void setList(ArrayList<TimelineList> list) {
	this.list = list;
}
   
   
   
   
}
