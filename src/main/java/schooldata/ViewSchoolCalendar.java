package schooldata;

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

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@ManagedBean(name = "viewSchoolCalendar")
@ViewScoped
public class ViewSchoolCalendar implements Serializable {
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
//				//// // System.out.println("start : "+start);
//				//// // System.out.println("end : "+end);
				
				Date strt = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
				Date ed = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
				
				list = new DatabaseMethods1().viewEvents(strt, ed,"admin","",conn);
				////// // System.out.println(list.size());
				for (SchoolCalenderInfo ss : list) {
					
					Date random = new Date();
					try {
						random = new SimpleDateFormat("yyyy-MM-dd").parse(ss.getDate().toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//System.out.println(new Date());
					
					////// // System.out.println("Random : "+random);
					//random.setDate(ss.getDate().getDate()+1);
					System.out.println(random);
					//Instant inst = random.toInstant();
					//LocalDateTime ldt = LocalDateTime.ofInstant(inst, ZoneId.systemDefault());
					
					LocalDateTime ldt = LocalDateTime.ofInstant(random.toInstant(), ZoneId.systemDefault());

	
//					addEvent(new DefaultScheduleEvent(ss.getEvent() + ":-" + ss.getDesc(), ldt, ldt, true));
					addEvent(new DefaultScheduleEvent().builder().title(ss.getEvent()+":-"+ss.getDesc()).startDate(ldt).endDate(ldt).overlapAllowed(true).build());
					
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
