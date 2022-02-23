package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolCalenderInfo;
import schooldata.StudentInfo;

@ManagedBean(name="activityCalendarBean")
@ViewScoped

public class ActivityCalendarBean implements Serializable
{
	private ScheduleModel lazyEventModel;
	ArrayList<SchoolCalenderInfo> list = new ArrayList<>();
	private ScheduleEvent event = new DefaultScheduleEvent();

	@PostConstruct
	public void init() {

		// Connection conn=DataBaseConnection.javaConnection();
		lazyEventModel = new LazyScheduleModel() {

			@Override
			public void loadEvents(LocalDateTime start, LocalDateTime end) {
				Connection conn = DataBaseConnection.javaConnection();
				
				HttpSession sss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				String schoolId = (String) sss.getAttribute("schoolid");
				String username=(String) sss.getAttribute("username");
				
				Date strt = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
				Date ed = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
				StudentInfo ln=new DataBaseMeathodJson().studentDetailslistByAddNo(username,schoolId,conn);
				
				list = new DatabaseMethods1().viewEvents(strt, ed,"student",ln.getSectionid(),conn);

				for (SchoolCalenderInfo ss : list) {
					Date random = new Date();
					try {
						random = new SimpleDateFormat("yyyy-MM-dd").parse(ss.getDate().toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					/*random.setDate(ss.getDate().getDate() + 1);*/
					
					Instant inst = random.toInstant();
					LocalDateTime ldt = LocalDateTime.ofInstant(inst, ZoneId.systemDefault());

//					addEvent(new DefaultScheduleEvent(ss.getEvent() + ":-" + ss.getDesc(), ldt, ldt));
					addEvent(new DefaultScheduleEvent().builder().title(ss.getEvent()+":-"+ss.getDesc()).startDate(ldt).endDate(ldt).build());
				}
				
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		};
	}

	public ScheduleModel getLazyEventModel() {
		return lazyEventModel;
	}

	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}
}
