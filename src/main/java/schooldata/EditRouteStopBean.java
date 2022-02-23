package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

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


@ManagedBean(name="editRoute")
@ViewScoped
public class EditRouteStopBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	boolean editDetailsShow,showAmount,showCategory;
	RouteStop list;
	ArrayList<RouteStop> routeStopList;
	String selectedRoute,stopName,categoryId="0",schid,session;
	int amount;
	ArrayList<SelectItem> routeList,categoryList;
	String selectedRouteTemp,stopNametemp,selectedMonth;
	ArrayList<SelectItem> monthList;
	DatabaseMethods1 obj= new DatabaseMethods1();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseValidator validator = new DataBaseValidator();



	public EditRouteStopBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolDetails =obj.fullSchoolInfo(conn);

		routeStopList=obj.routeStopListNew(conn);
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		categoryList=obj.stopCategoryList(schid, session, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void editSeletedRow() {

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
				routeList=obj.allTransportRoutes(conn);
				selectedRoute=list.getRouteId();
				stopName=list.getStopName();
				amount=list.getAmount();
				categoryId=list.getCategoryId();
				if(categoryId.equals("0"))
				{
					showAmount=true;showCategory=false;
				}
				else
				{
					showCategory=true;showAmount=false;
				}
				selectedRouteTemp=selectedRoute;
				stopNametemp=stopName;
				editDetailsShow=true;
			}
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select atleast one stop","Please select atleast one route"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void deleteNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		boolean check=obj.checkPromotionForSession(conn);
		if(check==true)
		{
			editDetailsShow=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You Can Not Delete Details.. Because You Promoted Student From This Session"));
		}
		else
		{
			int i=obj.deleteRouteStop(list.getGroupid(),conn);
			if(i==1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Selected route stop deleted successfully"));
				routeList=obj.allRoutes(conn);
				routeStopList=obj.routeStopListNew(conn);
				editDetailsShow=false;
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
	    String value = "";
		String language= list.getGroupid();
		
		String refNo = workLg.saveWorkLogMehod(language,"Delete Stop","WEB",value,conn);
		return refNo;
	}


public String addWorkLog2(Connection conn)
	{
	    String value = selectedRoute+" --- "+stopName+ " --- "+amount+" --- "+list.getGroupid()+" --- "+selectedMonth+" --- "+categoryId;
		String language= stopName+" --- "+amount;

		
		String refNo = workLg.saveWorkLogMehod(language,"Edit Stop","WEB",value,conn);
		return refNo;
	}

	
	public void monthDetails()
	{
		monthList=new ArrayList<>();
//		int month=list.getMonth();
		monthList=new DatabaseMethods1().monthListHandling();

	}
	public void updateNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			int flag=0;
			if(amount<=0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Fee amount must be greater than zero", "Validation error"));

			}
			if(!(stopName.equals(stopNametemp) && selectedRoute.equals(selectedRouteTemp)))
			{
				if(flag==0)
				{
					int status=validator.duplicateRouteStop(schid, selectedRoute, stopName,conn,session);
					if(status==0)
					{
						flag++;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,choose a diffenent one", "Validation error"));

					}
				}
			}

			if(flag==0)
			{
				try
				{
					if(!categoryId.equals("0"))
						amount=Integer.parseInt(obj.stopCategoryInfoById(categoryId, conn).getAmount());
					int i=	obj.updateRouteStop(selectedRoute, stopName, amount,list.getGroupid(),selectedMonth,categoryId,conn);

					if(i==1)
					{
						String refNo2;
						try {
							refNo2=addWorkLog2(conn);
						} catch (Exception e) {
							e.printStackTrace();
						}
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Stop updated successfully", "Validation error"));

					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));

					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}

			routeStopList=obj.routeStopListNew(conn);
			editDetailsShow=false;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public  void exportAllStopPdf() throws DocumentException, IOException, FileNotFoundException {

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
			Chunk c8 = new Chunk("\n                                                                      All Stops Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sno");
			table.addCell("Route Name");
			table.addCell("Stop Name");
			table.addCell("Amount");



			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<routeStopList.size();i++){
				table.addCell(new Phrase(String.valueOf(routeStopList.get(i).getSerialNumber()),font));
				table.addCell(new Phrase(routeStopList.get(i).getRouteName(),font));

				table.addCell(new Phrase(routeStopList.get(i).getStopName()));
				table.addCell(new Phrase(String.valueOf(routeStopList.get(i).getAmount())));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","All_Stop_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("All_Stop_Report.pdf").stream(()->isFromFirstData).build();







	}

	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}

	public String getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}
	public boolean isEditDetailsShow() {
		return editDetailsShow;
	}

	public void setEditDetailsShow(boolean editDetailsShow) {
		this.editDetailsShow = editDetailsShow;
	}

	public RouteStop getList() {
		return list;
	}

	public void setList(RouteStop list) {
		this.list = list;
	}

	public ArrayList<RouteStop> getRouteStopList() {
		return routeStopList;
	}

	public void setRouteStopList(ArrayList<RouteStop> routeStopList) {
		this.routeStopList = routeStopList;
	}

	public String getSelectedRoute() {
		return selectedRoute;
	}

	public void setSelectedRoute(String selectedRoute) {
		this.selectedRoute = selectedRoute;
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getSelectedRouteTemp() {
		return selectedRouteTemp;
	}

	public void setSelectedRouteTemp(String selectedRouteTemp) {
		this.selectedRouteTemp = selectedRouteTemp;
	}

	public String getStopNametemp() {
		return stopNametemp;
	}

	public void setStopNametemp(String stopNametemp) {
		this.stopNametemp = stopNametemp;
	}
	public ArrayList<SelectItem> getRouteList() {
		return routeList;
	}

	public void setRouteList(ArrayList<SelectItem> routeList) {
		this.routeList = routeList;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}
	public boolean isShowAmount() {
		return showAmount;
	}
	public void setShowAmount(boolean showAmount) {
		this.showAmount = showAmount;
	}
	public boolean isShowCategory() {
		return showCategory;
	}
	public void setShowCategory(boolean showCategory) {
		this.showCategory = showCategory;
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
