package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import reports_module.DataBaseMethodsReports;

@ManagedBean(name="mySchoolBills")
@ViewScoped

public class MySchoolBillsBean implements Serializable
{
	ArrayList<BillInfo> list = new ArrayList<>();
	BillInfo selectedBill;
	StreamedContent filee;
	String file="na";
	transient StreamedContent filed;
	SchoolInfoList schoolDetails;
    double amount=0;

	public MySchoolBillsBean()
	{
		file="na";
		Connection conn = DataBaseConnection.javaConnection();
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);


		list = new DataBaseMethodsReports().myBills("unpaid",new DatabaseMethods1().schoolId(),conn);
        
		for(BillInfo ls:list)
		{
			amount+=Double.parseDouble(ls.getAmount());
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DataBaseMeathodJson().fullSchoolInfo(new DatabaseMethods1().schoolId(),conn);

		file = selectedBill.getFile();

		if(file==null || file.equalsIgnoreCase("na") || file.equals(""))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Result file unavailable. Please try after sometime."));
		}
		else
		{
			file = ls.getDownloadpath()+selectedBill.getFile();
			PrimeFaces.current().ajax().update("form2");
			PrimeFaces.current().executeInitScript("PF('alrtDlg').show();");
		}

		//PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected.getFilename()+"')");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void purchase()
	{
		

		HttpSession ss= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	//	ss.setAttribute("selectedBill", list);
		String custid=schoolDetails.getSchoolName()+"-"+schoolDetails.getSchid();
		String ord="BILLORDS"+new SimpleDateFormat("ydMhms").format(new Date());
		ss.setAttribute("billOrderid", ord);
		ss.setAttribute("amount", String.valueOf(amount));
		
		try {
		//	FacesContext.getCurrentInstance().getExternalContext().redirect("http://www.chalkboxerp.in/DMM/pgRedirectBillLG.jsp?ORDER_ID="+ord+"&CUST_ID="+custid+"&INDUSTRY_TYPE_ID=PrivateEducation&CHANNEL_ID=WEB&TXN_AMOUNT="+amount);
			FacesContext.getCurrentInstance().getExternalContext().redirect("http://localhost:8083/Chalkbox/pgRedirectBillLG.jsp?ORDER_ID="+ord+"&CUST_ID="+custid+"&INDUSTRY_TYPE_ID=PrivateEducation&CHANNEL_ID=WEB&TXN_AMOUNT="+amount);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Filedownload1()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(new DatabaseMethods1().schoolId(),conn);
		try
		{
			String fl=selectedBill.getFile();
			int first=fl.lastIndexOf(".")+1;
			int last=fl.length();
			String ext=fl.substring(first, last);
			//		        //// // System.out.println(ext);
			//		        //// // System.out.println(fl);
			java.io.InputStream stream = new FileInputStream(new File(info.getUploadpath()+fl));


			if(ext.equalsIgnoreCase("docx"))
			{
//				filee = new DefaultStreamedContent(stream, "file/docx", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/docx").name(info.getDownloadpath()+fl).stream(()->stream).build();
			}
			else if(ext.equalsIgnoreCase("doc"))
			{
//				filee = new DefaultStreamedContent(stream, "file/doc", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/doc").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("txt"))
			{
//				filee = new DefaultStreamedContent(stream, "file/txt", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/txt").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("pdf"))
			{
//				filee = new DefaultStreamedContent(stream, "file/pdf", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/pdf").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("cdr"))
			{
//				filee = new DefaultStreamedContent(stream, "file/cdr", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/cdr").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("png"))
			{
//				filee = new DefaultStreamedContent(stream, "file/png", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/png").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("jpg"))
			{
//				filee = new DefaultStreamedContent(stream, "file/jpg", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/jpg").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("jpeg"))
			{
//				filee = new DefaultStreamedContent(stream, "file/jpeg", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/jpeg").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("gif"))
			{
//				filee = new DefaultStreamedContent(stream, "file/gif", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/gif").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("xls"))
			{
//				filee = new DefaultStreamedContent(stream, "file/xls", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/xls").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("xlsx"))
			{
//				filee = new DefaultStreamedContent(stream, "file/xlsx", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/xlsx").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}

			PrimeFaces context = PrimeFaces.current();
			context.executeInitScript("PF('dwnlad').show();");

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("File Not Found"));
			e.printStackTrace();
		}



	}

	public  void exportMyBillPdf() throws DocumentException, IOException, FileNotFoundException {

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





		//Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src =ls.getDownloadpath()+ls.getImagePath();
			Image im =Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk(  schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);


			Chunk c8 = new Chunk("\n                                                                        My Bills Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sno");
			table.addCell("Bill Number ");
			table.addCell("Bill Date");
			table.addCell("Due Date");






			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<list.size()-1;i++){
				table.addCell(new Phrase(String.valueOf(list.get(i).getSno()),font));
				table.addCell(new Phrase(list.get(i).getBillNo(),font));

				table.addCell(new Phrase(list.get(i).getBillDateStr()));
				table.addCell(new Phrase(list.get(i).getDueDateStr(),font));





			}


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
//		filed = new DefaultStreamedContent(isFromFirstData, "application/pdf","School_Bills_Report.pdf");
		filed = new DefaultStreamedContent().builder().contentType("application/pdf").name("School_Bills_Report.pdf").stream(()->isFromFirstData).build();






	}

	public ArrayList<BillInfo> getList() {
		return list;
	}

	public void setList(ArrayList<BillInfo> list) {
		this.list = list;
	}

	public BillInfo getSelectedBill() {
		return selectedBill;
	}

	public void setSelectedBill(BillInfo selectedBill) {
		this.selectedBill = selectedBill;
	}

	public StreamedContent getFilee() {
		return filee;
	}

	public void setFilee(StreamedContent filee) {
		this.filee = filee;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public StreamedContent getFiled() {
		return filed;
	}

	public void setFiled(StreamedContent filed) {
		this.filed = filed;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	

}
