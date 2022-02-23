package reports_module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

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
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="busWiseRFIDRecord")
@ViewScoped

public class BusWiseRFIDRecordBean implements Serializable
{
	Date date = new Date();
	//ArrayList<Route> busList;
	ArrayList<SelectItem> busList;
	String selectedBus,busName;
	String dateStr;
	SchoolInfoList schoolDetails;
	ArrayList<StudentInfo> slist;
	transient StreamedContent file;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();
    String schoolId,sessionValue;
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	String inBus1="",outBus1="",inSch="",outSch="",inBus2="",outBus2="";
	String inBusMorn="",notInBusMorn="",outBusMorn="",notOutBusMorn="",inSchool="",notInSchool="",outSchool="",notOutSchool="",
			inBusEven="",notInBusEven="",outBusEven="",notOutBusEven="",otherInMorn="",otherOutMorn="",otherInEven="",otherOutEven="";
	
	public BusWiseRFIDRecordBean() 
	{
		date = new Date();
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId,conn);
		//busList = dbr.rfidDeviceList(dbm.schoolId(),"Transport",conn);
		busList=dbj.allBuses(schoolId, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void showRFID()
	{
		inBus1=outBus1=inSch=outSch=inBus2=outBus2="";
		inBusMorn=notInBusMorn=outBusMorn=notOutBusMorn=inSchool=notInSchool=outSchool=notOutSchool=inBusEven=notInBusEven=outBusEven=notOutBusEven="";
		
		Connection conn = DataBaseConnection.javaConnection();
		dateStr = new SimpleDateFormat("dd-MM-yyyy").format(date);
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		
		
//		SelectItem deviceinfo = dbr.rfidDeviceInfo(selectedBus, conn);
//		busName=deviceinfo.getLabel();
		
		loop:for(SelectItem ss : busList)
		{
			if(String.valueOf(ss.getValue()).equalsIgnoreCase(selectedBus))
			{
				busName = ss.getLabel();
				break loop;
			}
		}
		
		ArrayList<String> routeList = dbr.routeIdListByBusId(selectedBus,schoolId,conn);
		if(routeList.size()>0)
		{
			String rid = "";
			for(String rt : routeList)
			{
				if(rid.equals(""))
				{
					rid = rt;
				}
				else
				{
					rid = rid+"','"+rt;
				}
			}
			
			ArrayList<String> stopList = dbr.stopIdListByRouteId(rid,schoolId,conn);
			if(stopList.size()>0)
			{
				/*String sid = "";
				for(String st : stopList)
				{
					if(sid.equals(""))
					{
						sid = st;
					}
					else
					{
						sid = sid+"','"+st;
					}
				}*/
				
				//slist = dbr.studentListForRfidBusWise(selectedBus,strDate,schid,conn);
				slist = dbr.studentListForRfidBusWiseNew(stopList,selectedBus,strDate,schoolId,conn);
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please create stops in the route in which this bus is assigned."));
			}
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please first assign this bus to a route."));
		}
		
		//slist = dbr.studentListForRfidBusWise(selectedBus,strDate,schid,conn);
		
		if(slist.size()>0)
		{
			int ib1=0,ob1=0,isch=0,osch=0,ib2=0,ob2=0,othInMorn=0,othOutMorn=0,othInEven=0,othOutEven=0;
			
			for(StudentInfo tt : slist)
			{
				if(!tt.getRfidDataInfo().getInBusMorn().equals(""))
				{
					ib1+=1;
				}
				
				if(!tt.getRfidDataInfo().getInBusMornName().equals(""))
				{
					othInMorn+=1;
				}
				
				if(!tt.getRfidDataInfo().getOutBusMorn().equals(""))
				{
					ob1+=1;
				}
				
				if(!tt.getRfidDataInfo().getOutBusMornName().equals(""))
				{
					othOutMorn+=1;
				}
				
				if(!tt.getRfidDataInfo().getInBusEven().equals(""))
				{
					ib2+=1;
				}
				
				if(!tt.getRfidDataInfo().getInBusEvenName().equals(""))
				{
					othInEven+=1;
				}
				
				if(!tt.getRfidDataInfo().getOutBusEven().equals(""))
				{
					ob2+=1;
				}
				
				if(!tt.getRfidDataInfo().getOutBusEvenName().equals(""))
				{
					othOutEven+=1;
				}
				
				if(!tt.getRfidDataInfo().getInSchool().equals(""))
				{
					isch+=1;
				}
				
				if(!tt.getRfidDataInfo().getOutSchool().equals(""))
				{
					osch+=1;
				}
				
			}
			
			inBus1 = "Total Students = "+slist.size()+",\n"+"Students Punched = "+(ib1-othInMorn)+",\n"+"Student Punched in Other Bus = "+othInMorn+",\n"+"Students Not Punched = "+(slist.size()-ib1);
			outBus1 = "Total Students = "+slist.size()+",\n"+"Students Punched = "+(ob1-othOutMorn)+",\n"+"Student Punched in Other Bus = "+othOutMorn+",\n"+"Students Not Punched = "+(slist.size()-ob1);
			
			inSch = "Total Students = "+slist.size()+",\n"+"Students Punched = "+isch+",\n"+"Students Not Punched = "+(slist.size()-isch);
			outSch = "Total Students = "+slist.size()+",\n"+"Students Punched = "+osch+",\n"+"Students Not Punched = "+(slist.size()-osch);
			
			inBus2 = "Total Students = "+slist.size()+",\n"+"Students Punched = "+(ib2-othInEven)+",\n"+"Student Punched in Other Bus = "+othInEven+",\n"+"Students Not Punched = "+(slist.size()-ib2);
			outBus2 = "Total Students = "+slist.size()+",\n"+"Students Punched = "+(ob2-othOutEven)+",\n"+"Student Punched in Other Bus = "+othOutEven+",\n"+"Students Not Punched = "+(slist.size()-ob2);
			
			inBusMorn = "Students Punched = "+(ib1-othInMorn);
			otherInMorn = "Student Punched in Other Bus = "+othInMorn;
			notInBusMorn = "Students Not Punched = "+(slist.size()-ib1);
			
			outBusMorn = "Students Punched = "+(ob1-othOutMorn);
			otherOutMorn = "Student Punched in Other Bus = "+othOutMorn;
			notOutBusMorn = "Students Not Punched = "+(slist.size()-ob1);
			
			inSchool = "Students Punched = "+isch;
			notInSchool = "Students Not Punched = "+(slist.size()-isch);
			outSchool = "Students Punched = "+osch;
			notOutSchool = "Students Not Punched = "+(slist.size()-osch);
			
			inBusEven = "Students Punched = "+(ib2-othInEven);
			otherInEven = "Student Punched in Other Bus = "+othInEven;
			notInBusEven = "Students Not Punched = "+(slist.size()-ib2);
			
			outBusEven = "Students Punched = "+(ob2-othOutEven);
			otherOutEven = "Student Punched in Other Bus = "+othOutEven;
			notOutBusEven = "Students Not Punched = "+(slist.size()-ob2);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportLibPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		//Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src =ls.getDownloadpath()+ls.getImagePath();
			Image im =Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);

			Chunk c = new Chunk(ls.schoolName  +"\n",fo );

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk(  ls.add1+ " " +ls.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();
			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);

			Chunk c8 = new Chunk("\n                                                           RFID Record "+busName+" "+dateStr+" \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8);
			PdfPTable table = new PdfPTable(9);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("S.No.");
			table.addCell("Name ");
			table.addCell("Class");
			
			table.addCell("Pick From Home Stop");
			table.addCell("Drop In School");
			table.addCell("Enter In School");
			table.addCell("Exit From School");
			table.addCell("Pick From School");
			table.addCell("Drop At Home Stop");

			table.setHeaderRows(1);
			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(1);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i=0;i<slist.size();i++)
			{
				table.addCell(new Phrase(String.valueOf(i+1),font));
				table.addCell(new Phrase(slist.get(i).getFullName(),font));
				table.addCell(new Phrase(slist.get(i).getClassName()+" - "+slist.get(i).getSectionName(),font));
				//table.addCell(new Phrase(slist.get(i).getTransStatus()));
				
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInBusMorn()+"\n"+slist.get(i).getRfidDataInfo().getInBusMornName(),font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutBusMorn()+"\n"+slist.get(i).getRfidDataInfo().getOutBusMornName(),font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInSchool(),font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutSchool(),font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInBusEven()+"\n"+slist.get(i).getRfidDataInfo().getInBusEvenName(),font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutBusEven()+"\n"+slist.get(i).getRfidDataInfo().getOutBusEvenName(),font));
				
			}
			
			table.addCell("");
			table.addCell("");
			table.addCell("");
			
			table.addCell(new Phrase(inBus1,font));
			table.addCell(new Phrase(outBus1,font));
			table.addCell(new Phrase(inSch,font));
			table.addCell(new Phrase(outSch,font));
			table.addCell(new Phrase(inBus2,font));
			table.addCell(new Phrase(outBus2,font));
		
			table.setWidthPercentage(110);
			document.add(table);

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);
		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","RFID_Record_"+busName+"_"+dateStr+".pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("RFID_Record_"+busName+"_"+dateStr+".pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/*public ArrayList<Route> getBusList() {
		return busList;
	}

	public void setBusList(ArrayList<Route> busList) {
		this.busList = busList;
	}*/

	public String getSelectedBus() {
		return selectedBus;
	}

	public void setSelectedBus(String selectedBus) {
		this.selectedBus = selectedBus;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public SchoolInfoList getSchoolDetails() {
		return schoolDetails;
	}

	public void setSchoolDetails(SchoolInfoList schoolDetails) {
		this.schoolDetails = schoolDetails;
	}

	public ArrayList<StudentInfo> getSlist() {
		return slist;
	}

	public void setSlist(ArrayList<StudentInfo> slist) {
		this.slist = slist;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getBusName() {
		return busName;
	}

	public void setBusName(String busName) {
		this.busName = busName;
	}

	public String getInBus1() {
		return inBus1;
	}

	public void setInBus1(String inBus1) {
		this.inBus1 = inBus1;
	}

	public String getOutBus1() {
		return outBus1;
	}

	public void setOutBus1(String outBus1) {
		this.outBus1 = outBus1;
	}

	public String getInSch() {
		return inSch;
	}

	public void setInSch(String inSch) {
		this.inSch = inSch;
	}

	public String getOutSch() {
		return outSch;
	}

	public void setOutSch(String outSch) {
		this.outSch = outSch;
	}

	public String getInBus2() {
		return inBus2;
	}

	public void setInBus2(String inBus2) {
		this.inBus2 = inBus2;
	}

	public String getOutBus2() {
		return outBus2;
	}

	public void setOutBus2(String outBus2) {
		this.outBus2 = outBus2;
	}

	public String getInBusMorn() {
		return inBusMorn;
	}

	public void setInBusMorn(String inBusMorn) {
		this.inBusMorn = inBusMorn;
	}

	public String getNotInBusMorn() {
		return notInBusMorn;
	}

	public void setNotInBusMorn(String notInBusMorn) {
		this.notInBusMorn = notInBusMorn;
	}

	public String getOutBusMorn() {
		return outBusMorn;
	}

	public void setOutBusMorn(String outBusMorn) {
		this.outBusMorn = outBusMorn;
	}

	public String getNotOutBusMorn() {
		return notOutBusMorn;
	}

	public void setNotOutBusMorn(String notOutBusMorn) {
		this.notOutBusMorn = notOutBusMorn;
	}

	public String getInSchool() {
		return inSchool;
	}

	public void setInSchool(String inSchool) {
		this.inSchool = inSchool;
	}

	public String getNotInSchool() {
		return notInSchool;
	}

	public void setNotInSchool(String notInSchool) {
		this.notInSchool = notInSchool;
	}

	public String getOutSchool() {
		return outSchool;
	}

	public void setOutSchool(String outSchool) {
		this.outSchool = outSchool;
	}

	public String getNotOutSchool() {
		return notOutSchool;
	}

	public void setNotOutSchool(String notOutSchool) {
		this.notOutSchool = notOutSchool;
	}

	public String getInBusEven() {
		return inBusEven;
	}

	public void setInBusEven(String inBusEven) {
		this.inBusEven = inBusEven;
	}

	public String getNotInBusEven() {
		return notInBusEven;
	}

	public void setNotInBusEven(String notInBusEven) {
		this.notInBusEven = notInBusEven;
	}

	public String getOutBusEven() {
		return outBusEven;
	}

	public void setOutBusEven(String outBusEven) {
		this.outBusEven = outBusEven;
	}

	public String getNotOutBusEven() {
		return notOutBusEven;
	}

	public void setNotOutBusEven(String notOutBusEven) {
		this.notOutBusEven = notOutBusEven;
	}

	public ArrayList<SelectItem> getBusList() {
		return busList;
	}

	public void setBusList(ArrayList<SelectItem> busList) {
		this.busList = busList;
	}

	public String getOtherInMorn() {
		return otherInMorn;
	}

	public void setOtherInMorn(String otherInMorn) {
		this.otherInMorn = otherInMorn;
	}

	public String getOtherOutMorn() {
		return otherOutMorn;
	}

	public void setOtherOutMorn(String otherOutMorn) {
		this.otherOutMorn = otherOutMorn;
	}

	public String getOtherInEven() {
		return otherInEven;
	}

	public void setOtherInEven(String otherInEven) {
		this.otherInEven = otherInEven;
	}

	public String getOtherOutEven() {
		return otherOutEven;
	}

	public void setOtherOutEven(String otherOutEven) {
		this.otherOutEven = otherOutEven;
	}
	
	
}
