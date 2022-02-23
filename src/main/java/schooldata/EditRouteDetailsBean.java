package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import session_work.RegexPattern;


@ManagedBean(name="editRouteDetails")
@ViewScoped
public class EditRouteDetailsBean implements Serializable{

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<Route> routeList;
	ArrayList<SelectItem>busList,employeelist;
	boolean editDetailsShow;
	String sourceTemp,destinationTemp,selectedBus;
	String name,source,destination;
	Date creatingDate,updatingDate;
	Route list;
	String busAttendent,schid,session;
	DatabaseMethods1 obj= new DatabaseMethods1();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseValidator validator = new DataBaseValidator();


	public EditRouteDetailsBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		schoolDetails =obj.fullSchoolInfo(conn);

		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		employeelist=obj.allEmployeeListWithId(conn);
		routeList=obj.allTransportRouteList(conn);
		busList=obj.allBuses(schid,conn);
		updatingDate=new Date();
		
		 // System.out.println("const.....");
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int flag=0;
		if(!(sourceTemp.equals(source) && destinationTemp.equals(destination)  ))
		{
			int status=validator.duplicateTransportRoute(source, destination,conn);
			if(status==0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));
			}
		}
		if(flag==0)
		{
			if(source.equals(destination))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Source and destination can't be same", "Validation error"));
			}
			else
			{
				String routeName = list.getName().split("-")[0]+"-"+name;
				int i= 	obj.updateRoute(list.getId(), routeName, source, destination,creatingDate, updatingDate,conn,busAttendent,selectedBus);

				if(i==1)
				{
					String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Route updated successfully", "Validation error"));

				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));

				}
			}
		}
		routeList=obj.allTransportRouteList(conn);
		editDetailsShow=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void editSeletedRow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(list!=null)
		{
			boolean check=obj.checkPromotionForSession(conn);
			if(check==true)
			{
				editDetailsShow=false;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You Can Not Edit Details.. Because You Promoted Student From This Session"));
			}
			else
			{
				name=list.getName().split("-")[1];
				source=list.getSource();
				destination=list.getDestination();
				creatingDate=list.getCreatingDate();
				selectedBus=list.getSelectedBus();
				sourceTemp=source;
				destinationTemp=destination;
				busAttendent=list.getBusAttnedent();
				editDetailsShow=true;
			}
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select atleast one route","Please select atleast one route"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		boolean check=obj.checkPromotionForSession(conn);
		if(check==true)
		{
			editDetailsShow=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You Can Not Delete Details.. Because You Promoted Student From This Session"));
		}
		else
		{
			int i=obj.deleteRoute(list.getId(),conn);
			if(i==1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				routeList=obj.allTransportRouteList(conn);
				editDetailsShow=false;

				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Selected route deleted successfully"));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = String.valueOf(list.getId());
		String language= String.valueOf(list.getId());
			
		String refNo = workLg.saveWorkLogMehod(language,"Delete Route","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
		String value = "";
		String language= "";
	
		Timestamp postdate = new Timestamp(creatingDate.getTime());
		postdate.setHours(0);
		postdate.setMinutes(0);
		postdate.setSeconds(0);
		postdate.setNanos(0);
		
		
		Timestamp update = new Timestamp(updatingDate.getTime());
		update.setHours(0);
		update.setMinutes(0);
		update.setSeconds(0);
		update.setNanos(0);
		
		language = "Name - "+name+" --- Source - "+source+" --- Bus - "+selectedBus+" --- Attendant - "+busAttendent;
		value = list.getId()+" --- "+list.getName().split("-")[0]+"-"+name+" --- "+source+" --- "+destination+" --- "+postdate+" --- "+update+" --- "+busAttendent+" --- "+selectedBus+" --- ";

			
		String refNo = workLg.saveWorkLogMehod(language,"Edit Route","WEB",value,conn);
		return refNo;
	}



	public  void exportAllRoutePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);



		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();




		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		//Header
		try {

			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im=null;
			try {
			im =Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);	
			} catch (Exception e) {
				e.printStackTrace();
			}
			


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk("              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Chunk c8 = new Chunk("\n                                                                    All Route Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sno");

			table.addCell("Name");

			table.addCell("Gps");




			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<routeList.size();i++){
				table.addCell(new Phrase(String.valueOf(routeList.get(i).getSerialNumber()),font));
				table.addCell(new Phrase(String.valueOf(routeList.get(i).getName()),font));

				table.addCell(new Phrase(routeList.get(i).getGpsAlias()));




			}


			table.setWidthPercentage(110);
			document.add(table);





		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","All_Routes_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("All_Routes_Report.pdf").stream(()->isFromFirstData).build();







	}



	public String getSourceTemp() {
		return sourceTemp;
	}
	public void setSourceTemp(String sourceTemp) {
		this.sourceTemp = sourceTemp;
	}
	public String getDestinationTemp() {
		return destinationTemp;
	}
	public void setDestinationTemp(String destinationTemp) {
		this.destinationTemp = destinationTemp;
	}
	public ArrayList<Route> getRouteList() {
		return routeList;
	}
	public void setRouteList(ArrayList<Route> routeList) {
		this.routeList = routeList;
	}
	public boolean isEditDetailsShow() {
		return editDetailsShow;
	}
	public void setEditDetailsShow(boolean editDetailsShow) {
		this.editDetailsShow = editDetailsShow;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public Date getCreatingDate() {
		return creatingDate;
	}
	public void setCreatingDate(Date creatingDate) {
		this.creatingDate = creatingDate;
	}
	public Date getUpdatingDate() {
		return updatingDate;
	}
	public void setUpdatingDate(Date updatingDate) {
		this.updatingDate = updatingDate;
	}
	public Route getList() {
		return list;
	}
	public void setList(Route list) {
		this.list = list;
	}

	public ArrayList<SelectItem> getEmployeelist() {
		return employeelist;
	}

	public void setEmployeelist(ArrayList<SelectItem> employeelist) {
		this.employeelist = employeelist;
	}

	public String getBusAttendent() {
		return busAttendent;
	}

	public void setBusAttendent(String busAttendent) {
		this.busAttendent = busAttendent;
	}

	public ArrayList<SelectItem> getBusList() {
		return busList;
	}

	public void setBusList(ArrayList<SelectItem> busList) {
		this.busList = busList;
	}

	public String getSelectedBus() {
		return selectedBus;
	}

	public void setSelectedBus(String selectedBus) {
		this.selectedBus = selectedBus;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
