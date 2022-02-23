package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;
import trigger_module.DataBaseMethods;
@ManagedBean(name="schoolCalender")
@ViewScoped
public class SchoolCalender implements Serializable
{
	String regex=RegexPattern.REGEX;
	Date selectedDay,time,endDate,minDateForEdit;
	String selectedOne,desc,selectedTime,holidayFor,notify="";
	String  hr,min,otherValue="";
	SchoolInfoList schoolDetails;
	ArrayList<SchoolCalenderInfo> list;
	SchoolCalenderInfo selectedActivity;
	ArrayList<SelectItem>eventList;
	ArrayList<ClassInfo> classSectionList,selectedClassList,eventClassList;
	ClassInfo selectedClass = new ClassInfo();
	transient StreamedContent file;
	
	boolean show=true,showTable=false,showHolidayFor=false,showEdit=false,showEditUpdate=false,showOtherEntry=false,showOtherEntryUpdate=false;

	public SchoolCalender()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		schoolDetails =obj.fullSchoolInfo(conn);
		minDateForEdit = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(minDateForEdit);
		c.add(Calendar.DATE, 1);  // number of days to add
		minDateForEdit = c.getTime();
		
		
		selectedDay=new Date();
		endDate=new Date();
		list=new DataBaseMethods().viewEventList(obj.schoolId(), "", "calendar", conn);
		
		
		
		eventList=obj.allEvents(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void checkTiming()
	{

		if(selectedTime.equalsIgnoreCase("FullDay"))
		{
			hr="";
			min="";
			show=false;
			showEditUpdate = false;
		}
		else
		{
			show=true;
			showEditUpdate = true;
		}

	}

	public void checkType()
	{
		holidayFor="all";
		selectedClassList=new ArrayList<>();
		classSectionList=new ArrayList<>();
		showTable=false;
		
		if(selectedOne.equalsIgnoreCase("Other"))
		{
			showOtherEntry = true;
		}
		else
		{
			showOtherEntry = false;
		}

//		if(selectedOne.equalsIgnoreCase("Holiday"))
//		{
			showHolidayFor=true;
			holidayFor="all";
//		}
//		else
//		{
//			showHolidayFor=false;
//		}
	}
	
	public void checkTypeUpdate()
	{
	
		if(selectedOne.equalsIgnoreCase("Other"))
		{
			showOtherEntryUpdate = true;
		}
		else
		{
			showOtherEntryUpdate = false;
		}

	}

	public void checkHolidayType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedClassList=new ArrayList<>();
		classSectionList=new ArrayList<>();

		if(holidayFor.equals("all"))
		{
			showTable=false;
		}
		else
		{
			showTable=true;
			classSectionList=new DatabaseMethods1().allSectionList(conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addEvent() throws IOException
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		notify = (String) ss.getAttribute("eventNotify");
		
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		
		if(selectedDay.after(endDate))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Dates properly"));
		}
		else
		{	
		if(/*selectedOne.equalsIgnoreCase("Holiday") && */holidayFor.equalsIgnoreCase("particular") && selectedClassList.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select class(es)"));
		}
		else
		{
		  if(selectedOne.equalsIgnoreCase("Other")&&otherValue.trim().equalsIgnoreCase(""))
		  {
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Fill Specify Type"));
		  }
		  else if(selectedOne.equalsIgnoreCase("Other")&&(otherValue.trim().equalsIgnoreCase("Exam")||otherValue.trim().equalsIgnoreCase("Event")||otherValue.trim().equalsIgnoreCase("PTM")||otherValue.trim().equalsIgnoreCase("Holiday")))
		  {
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Fill some other value in specify type"));
		  }
		  else
		  {
			int i = 0;
			ArrayList<Date> dates = new ArrayList<>();
			long interval = 24*1000 * 60 * 60; // 1 hour in millis
			long endTime1 =endDate.getTime() ; // create your endtime here, possibly using Calendar or Date
			long curTime = selectedDay.getTime();
			while (curTime <= endTime1) {
				dates.add(new Date(curTime));
				curTime += interval;
			}
			for(int j=0;j<dates.size();j++){
				Date lDate =dates.get(j);
				i=obj.addEventt(lDate,selectedOne,desc,hr,min,selectedTime,holidayFor,otherValue.trim(),conn);
				if(i>0)
				{
					String refNo;
					try {
						refNo=addWorkLog(conn,lDate);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(selectedOne.equalsIgnoreCase("Holiday") && holidayFor.equalsIgnoreCase("particular") && selectedClassList.size()>0)
					{
						String dt = new SimpleDateFormat("yyyy-MM-dd").format(dates.get(j));
						obj.addClassHoliday(selectedClassList,String.valueOf(i),obj.schoolId(),dt,conn);
						
						String refNo2;
						try {
							refNo2=addWorkLog2(conn,dt,i);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else if(!selectedOne.equalsIgnoreCase("Holiday") && holidayFor.equalsIgnoreCase("particular") && selectedClassList.size()>0)
					{
						String dt = new SimpleDateFormat("yyyy-MM-dd").format(dates.get(j));
						obj.addClassEvents(selectedClassList,String.valueOf(i),obj.schoolId(),dt,conn);
						
						String refNo3;
						try {
							refNo3=addWorkLog3(conn,dt,i);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			if(i>0)
			{
				if(notify.equalsIgnoreCase("true"))
				{
					String message = "";
					String showDt = new SimpleDateFormat("dd-MM-yyyy").format(selectedDay);
					String showEndDt = new SimpleDateFormat("dd-MM-yyyy").format(endDate);
					String eventTime = "", eventDate = "";
					
					if(selectedTime.equalsIgnoreCase("FullDay"))
					{
						eventTime = "Full Day";
					}
					else
					{
						eventTime = hr+" to "+min;
					}
					
					if(showDt.equalsIgnoreCase(showEndDt))
					{
						eventDate = showDt;
					}
					else
					{
						eventDate = "From "+showDt+" to "+showEndDt;
					}

					/*if(selectedOne.equalsIgnoreCase("Holiday") && holidayFor.equalsIgnoreCase("particular") && selectedClassList.size()>0)
					{
						for(int j=0;j<dates.size();j++){
							String dt = new SimpleDateFormat("yyyy-MM-dd").format(dates.get(j));
							obj.addClassHoliday(selectedClassList,String.valueOf(i),obj.schoolId(),dt,conn);
						}
						
					}
					else if(!selectedOne.equalsIgnoreCase("Holiday") && holidayFor.equalsIgnoreCase("particular") && selectedClassList.size()>0)
					{
						for(int j=0;j<dates.size();j++){
							String dt = new SimpleDateFormat("yyyy-MM-dd").format(dates.get(j));
							obj.addClassEvents(selectedClassList,String.valueOf(i),obj.schoolId(),dt,conn);
						}
					}*/
					
					message = selectedOne.toUpperCase()+"\n"+"Date : "+eventDate+"\nTime : "+eventTime+"\n"+desc;
					
					/*if(selectedOne.equalsIgnoreCase("Holiday"))
					{
						message = selectedOne.toUpperCase()+"\n"+"Date : "+eventDate+"\nTime : "+eventTime+"\n"+desc;
						if(holidayFor.equalsIgnoreCase("particular") && selectedClassList.size()>0)
						{
							for(ClassInfo cc : selectedClassList)
							{
								new DataBaseMeathodJson().notification("School Calendar",message,cc.getSectionId()+"-"+cc.getClassid()+"-"+obj.schoolId(),obj.schoolId(),"",conn);
							}
						}
						else
						{
							new DataBaseMeathodJson().notification("School Calendar",message,obj.schoolId(),obj.schoolId(),"",conn);
						}
					}
					else if(!selectedOne.equalsIgnoreCase("Exam"))
					{
						message = selectedOne.toUpperCase()+"\n"+"Date : "+eventDate+"\nTime : "+eventTime+"\n"+desc;
						new DataBaseMeathodJson().notification("School Calendar",message,obj.schoolId(),obj.schoolId(),"",conn);
					}*/
					if(holidayFor.equalsIgnoreCase("particular") && selectedClassList.size()>0)
					{
						for(ClassInfo cc : selectedClassList)
						{
							new DataBaseMeathodJson().notification("School Calendar",message,cc.getSectionId()+"-"+cc.getClassid()+"-"+obj.schoolId(),obj.schoolId(),"",conn);
						}
					}
					else
					{
						new DataBaseMeathodJson().notification("School Calendar",message,obj.schoolId(),obj.schoolId(),"",conn);
					}
				}

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Add in Calendar Sucessfully"));

				selectedDay=new Date();
				endDate=new Date();
				//list=obj.viewEventList(conn);
				desc=hr=min="";
				holidayFor="all";
				selectedClassList=new ArrayList<>();
				classSectionList=new ArrayList<>();
				showTable=false;
				showHolidayFor=false;

				FacesContext.getCurrentInstance().getExternalContext().redirect("schoolCalender.xhtml");
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
			}
		}
		}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn,Date lDate)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String dt = formatter.format(lDate);
		//String end = formatter.format(endDate);
		
		value = "Date-"+dt+" --Type-"+selectedOne+" --Holiday For-"+holidayFor+" --Selected Time-"+selectedTime+" --Description-"+desc+" Start Time-"+hr+" --End Time-"+min;
	
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Calendar Event","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn,String datee,int hid)
	{
	    String value = "";
		String language= "";
		
		language = "Date-"+datee+" --Hid-"+hid;
		for (ClassInfo cc : selectedClassList) {
			
			value += "(ClassId"+cc.getClassid()+" -SectionId-"+cc.getSectionId()+")";
		}
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Class holiday","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog3(Connection conn,String datee,int hid)
	{
	    String value = "";
		String language= "";
		
		language = "Date-"+datee+" --Hid-"+hid;
		for (ClassInfo cc : selectedClassList) {
			
			value += "(ClassId"+cc.getClassid()+" -SectionId-"+cc.getSectionId()+")";
		}
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Class event","WEB",value,conn);
		return refNo;
	}
	
	

	public void editActivityDetails()
	{

		selectedDay=selectedActivity.getDate();
		selectedTime=selectedActivity.getEventTime();
		desc=selectedActivity.getDesc();
		hr=selectedActivity.getHr();
		min=selectedActivity.getMin();
		if(!selectedTime.equalsIgnoreCase("FullDay")){
			
			showEditUpdate = true;
		}
		else
		{
			showEditUpdate = false;
		}
		
		if(selectedActivity.getEvent().equalsIgnoreCase("Exam")||selectedActivity.getEvent().equalsIgnoreCase("Event")||selectedActivity.getEvent().equalsIgnoreCase("PTM")||selectedActivity.getEvent().equalsIgnoreCase("Holiday"))
		{
			selectedOne=selectedActivity.getEvent();
			showOtherEntryUpdate = false;
		}
		else
		{
			selectedOne = "Other";
			otherValue = selectedActivity.getEvent();
			showOtherEntryUpdate = true;	
		}

		PrimeFaces.current().ajax().update("editForm");
		PrimeFaces.current().executeInitScript("PF('editDialog').show();");

	}
	
	public void viewActivityDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		
		eventClassList=new DataBaseMeathodJson().allClassSectionListForEvent(selectedActivity.getEvent(),obj.schoolId(),String.valueOf(selectedActivity.getId()),conn);
		
		PrimeFaces.current().ajax().update("viewForm");
		PrimeFaces.current().executeInitScript("PF('viewDialog').show();");

	}

	public void delete() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		int i=obj.deleteEvents(String.valueOf(selectedActivity.getId()),conn);
		if(i>=1)
		{
			String refNo4;
			try {
				refNo4=addWorkLog4(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Event deleted successfully"));
			if(/*selectedActivity.getEvent().equalsIgnoreCase("Holiday") && */selectedActivity.getHolidayFor().equalsIgnoreCase("particular"))
			{
				obj.deleteClassHoliday(selectedActivity.getEvent(),selectedActivity.getId(),conn);
				String refNo5;
				try {
					refNo5=addWorkLog5(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/*else if(!selectedActivity.getEvent().equalsIgnoreCase("Holiday") && selectedActivity.getHolidayFor().equalsIgnoreCase("particular"))
			{
				obj.deleteClassHoliday(selectedActivity.getId(),conn);
			}*/
			
			FacesContext.getCurrentInstance().getExternalContext().redirect("schoolCalender.xhtml");

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog4(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Selected Id-"+selectedActivity.getId();
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Calendar Event","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog5(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Selected Id-"+selectedActivity.getId()+" --Selected Event-"+selectedActivity.getEvent();
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Class Holiday","WEB",value,conn);
		return refNo;
	}
	
	public void deleteClass() throws IOException
	{
		String msg = "Class deleted successfully from selected event!";
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		obj.deleteParticularClassFromEvent(selectedActivity.getEvent(),String.valueOf(selectedClass.getId()),conn);
		String refNo9;
		try {
			refNo9=addWorkLog9(conn);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(selectedActivity.getHolidayFor().equalsIgnoreCase("particular"))
		{
			eventClassList=new DataBaseMeathodJson().allClassSectionListForEvent(selectedActivity.getEvent(),obj.schoolId(),String.valueOf(selectedActivity.getId()),conn);
			if(eventClassList.size()<=0)
			{
				obj.deleteEvents(String.valueOf(selectedActivity.getId()),conn);
				String refNo8;
				try {
					refNo8=addWorkLog8(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				list=new DataBaseMethods().viewEventList(obj.schoolId(), "", "calendar", conn);
				msg = "Event deleted successfully";
			}
		}
		
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage(msg));
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	private String addWorkLog9(Connection conn) {
		
		  String value = "";
			String language= "";
			
			value = "Selected Class Id-"+String.valueOf(selectedClass.getId())+" --Selected Event-"+selectedActivity.getEvent();
		
			String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete particular class from Event ","WEB",value,conn);
			return refNo;
	}
	
	private String addWorkLog8(Connection conn) {
		
		  String value = "";
			String language= "";
			
			value = "Selected Id-"+selectedActivity.getId();
		
			String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete class Event ","WEB",value,conn);
			return refNo;
	}
	

	public void editNow()
	{
		
		
		if(selectedOne.equalsIgnoreCase("Other")&&otherValue.trim().equalsIgnoreCase(""))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Fill Specify Type"));
		}
		else if(selectedOne.equalsIgnoreCase("Other")&&(otherValue.trim().equalsIgnoreCase("Exam")||otherValue.trim().equalsIgnoreCase("Event")||otherValue.trim().equalsIgnoreCase("PTM")||otherValue.trim().equalsIgnoreCase("Holiday")))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please fill some other value in specify type"));

		}
		else
		{	
		  Connection conn=DataBaseConnection.javaConnection();
		  DatabaseMethods1 obj=new DatabaseMethods1();
		  int i=obj.updateEvents(selectedDay,selectedOne,desc,hr,min,selectedActivity.getId(),selectedTime,otherValue.trim(),conn);
		  if(i==1)
		  {

			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Details updated successfully"));
			selectedDay=null;
			selectedOne=null;
			selectedTime=null;
			desc=null;
			hr=null;
			min=null;
			otherValue = "";
			list=new DataBaseMethods().viewEventList(obj.schoolId(), "", "calendar", conn);

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}	
	}
	
	
	public void deleteMultiple()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		SchoolInfoList info = obj.fullSchoolInfo(conn);
			
		int checkSelections=0;
		for(SchoolCalenderInfo ff: list)
		{
		   if(ff.getSelection().equals(true))
		  {
			checkSelections=1;		
		  }
		}	
		
		if(checkSelections==1)
		{	
			
			int errorCount=0;
			
			for(SchoolCalenderInfo ff: list)
			{	
			 if(ff.getSelection().equals(true))
			 {
					int i=obj.deleteEvents(String.valueOf(ff.getId()),conn);
					if(i>=1)
					{
						String refNo6;
						try {
							refNo6=addWorkLog6(conn,ff.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						if(/*selectedActivity.getEvent().equalsIgnoreCase("Holiday") && */ff.getHolidayFor().equalsIgnoreCase("particular"))
						{
							obj.deleteClassHoliday(ff.getEvent(),ff.getId(),conn);
							String refNo7;
							try {
								refNo7=addWorkLog7(conn,ff.getEvent(),ff.getId());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						/*else if(!selectedActivity.getEvent().equalsIgnoreCase("Holiday") && selectedActivity.getHolidayFor().equalsIgnoreCase("particular"))
						{
							obj.deleteClassHoliday(selectedActivity.getId(),conn);
						}*/
						

					}
			 else
			 {
				 errorCount =1;
			 }
			} 
		  }
			
			if(errorCount ==0)
			{
				list=new DataBaseMethods().viewEventList(obj.schoolId(), "", "calendar", conn);
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Events Deleted Successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
		
			}
			}
	    else
		{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select AtLeast 1 Event"));

		}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	
	

	private String addWorkLog7(Connection conn, String event, int id) {
		
		String value = "";
		String language= "";
		
		value = "Selected Id-"+id+" --Selected Event-"+event;
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Class Holiday multiple","WEB",value,conn);
		return refNo;
	}
	private String addWorkLog6(Connection conn, int id) {
		
		  String value = "";
			String language= "";
			
			value = "Selected Id-"+id;
		
			String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Calendar Event multiple","WEB",value,conn);
			return refNo;
	}
	
	
	public  void exportnewPdf() throws DocumentException, IOException, FileNotFoundException {
		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);
		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.getProperty("user.home");
		PdfWriter.getInstance(document, baos);
		document.open();
		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		try {
			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im = null;
			try {
				 im  =Image.getInstance(src);
					im.setAlignment(Element.ALIGN_LEFT);
					im.scaleAbsoluteHeight(60);
					im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				
				// TODO: handle exception
			}

			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = null;
			try {
				 c3  = new Chunk(im, -250, 15);
			} catch (Exception e) {
				// TODO: handle exception
			}
			Chunk c1 = new Chunk("              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);
			Paragraph p1 = new Paragraph();

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}

		try {
			Chunk c8 = new Chunk("\n                                                               School Calender  \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1});
			PdfPTable table2 = new PdfPTable(new float[] {1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Date");
			table.addCell("Activity");
			table.addCell("Description");
			table.addCell("Event For");
			table2.addCell("Class");
			table2.addCell("Section");
			table.addCell(table2);
			table.setHeaderRows(1);
			table2.setHeaderRows(1);
			table2.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			PdfPCell[] cells = table.getRow(0).getCells();
			PdfPCell[] cells2 = table2.getRow(0).getCells();
			
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			for(int j = 0;j<cells2.length;j++) {
				cells2[j].setBackgroundColor(new BaseColor(242, 234, 221));
				
				cells2[j].setBorderWidth(2);
			}
			
			
			
			for (int i=0;i<list.size();i++){
				table.addCell(new Phrase(String.valueOf(list.get(i).getSno()),font));
				table.addCell(new Phrase(list.get(i).getDate().toString(),font));
				table.addCell(new Phrase(list.get(i).getEvent(),font));
				table.addCell(new Phrase(list.get(i).getDesc().toUpperCase(),font));
				if(list.get(i).getHolidayFor().equalsIgnoreCase("all")) {
					table.addCell(new Phrase("All Classes",font));
					table2 = new PdfPTable(2);
					table2.getDefaultCell().setBorder(PdfPCell.ALIGN_JUSTIFIED_ALL);
					table2.addCell(new Phrase("-",font));
					table2.addCell(new Phrase("-",font));
					table.addCell(table2);
					
				}else {
					eventClassList=new DataBaseMeathodJson().allClassSectionListForEvent(list.get(i).getEvent(),new DatabaseMethods1().schoolId(),String.valueOf(list.get(i).getId()),con);
					table.addCell(new Phrase(list.get(i).getHolidayFor(),font));
					 // System.out.println(eventClassList.size());
					table2 = new PdfPTable(2);
					table2.getDefaultCell().setBorder(PdfPCell.ALIGN_JUSTIFIED_ALL);
					for(ClassInfo xo : eventClassList) {
						table2.addCell(new Phrase(xo.getClassName(),font));
						table2.addCell(new Phrase(xo.getSectionName(),font));
						
					}
					table.addCell(table2);
				}
			}
			table.setWidthPercentage(110);
			document.add(table);
		}  catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);
		document.close();
		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","School_Calender.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("School_Calender.pdf").stream(()->isFromFirstData).build();
		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	
	
	
	public StreamedContent getFile() {
		return file;
	}
	public void setFile(StreamedContent file) {
		this.file = file;
	}
	public String getHr() {
		return hr;
	}

	public void setHr(String hr) {
		this.hr = hr;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getSelectedDay() {
		return selectedDay;
	}

	public void setSelectedDay(Date selectedDay) {
		this.selectedDay = selectedDay;
	}

	public String getSelectedOne() {
		return selectedOne;
	}

	public void setSelectedOne(String selectedOne) {
		this.selectedOne = selectedOne;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}


	public ArrayList<SchoolCalenderInfo> getList() {
		return list;
	}


	public void setList(ArrayList<SchoolCalenderInfo> list) {
		this.list = list;
	}


	public SchoolCalenderInfo getSelectedActivity() {
		return selectedActivity;
	}


	public void setSelectedActivity(SchoolCalenderInfo selectedActivity) {
		this.selectedActivity = selectedActivity;
	}

	public ArrayList<SelectItem> getEventList() {
		return eventList;
	}

	public void setEventList(ArrayList<SelectItem> eventList) {
		this.eventList = eventList;
	}
	public String getSelectedTime() {
		return selectedTime;
	}
	public void setSelectedTime(String selectedTime) {
		this.selectedTime = selectedTime;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<ClassInfo> getClassSectionList() {
		return classSectionList;
	}
	public void setClassSectionList(ArrayList<ClassInfo> classSectionList) {
		this.classSectionList = classSectionList;
	}
	public ArrayList<ClassInfo> getSelectedClassList() {
		return selectedClassList;
	}
	public void setSelectedClassList(ArrayList<ClassInfo> selectedClassList) {
		this.selectedClassList = selectedClassList;
	}
	public String getHolidayFor() {
		return holidayFor;
	}
	public void setHolidayFor(String holidayFor) {
		this.holidayFor = holidayFor;
	}
	public boolean isShowTable() {
		return showTable;
	}
	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}
	public boolean isShowHolidayFor() {
		return showHolidayFor;
	}
	public void setShowHolidayFor(boolean showHolidayFor) {
		this.showHolidayFor = showHolidayFor;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public ArrayList<ClassInfo> getEventClassList() {
		return eventClassList;
	}
	public void setEventClassList(ArrayList<ClassInfo> eventClassList) {
		this.eventClassList = eventClassList;
	}
	public ClassInfo getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(ClassInfo selectedClass) {
		this.selectedClass = selectedClass;
	}
	public boolean isShowEdit() {
		return showEdit;
	}
	public void setShowEdit(boolean showEdit) {
		this.showEdit = showEdit;
	}
	public boolean isShowOtherEntry() {
		return showOtherEntry;
	}
	public void setShowOtherEntry(boolean showOtherEntry) {
		this.showOtherEntry = showOtherEntry;
	}
	public String getOtherValue() {
		return otherValue;
	}
	public void setOtherValue(String otherValue) {
		this.otherValue = otherValue;
	}
	public boolean isShowOtherEntryUpdate() {
		return showOtherEntryUpdate;
	}
	public void setShowOtherEntryUpdate(boolean showOtherEntryUpdate) {
		this.showOtherEntryUpdate = showOtherEntryUpdate;
	}
	public boolean isShowEditUpdate() {
		return showEditUpdate;
	}
	public void setShowEditUpdate(boolean showEditUpdate) {
		this.showEditUpdate = showEditUpdate;
	}
	public Date getMinDateForEdit() {
		return minDateForEdit;
	}
	public void setMinDateForEdit(Date minDateForEdit) {
		this.minDateForEdit = minDateForEdit;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}



}
