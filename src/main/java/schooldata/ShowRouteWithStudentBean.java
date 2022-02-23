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

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;

@ManagedBean(name = "showStudentRouteBean")
@ViewScoped
public class ShowRouteWithStudentBean implements Serializable {

	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> arrayList;
	ArrayList<SelectItem> routeNameList;
	ArrayList<Category> sectionList;
	ArrayList<RouteDetail> routeDetailList;
	String selectedRoute;
	boolean show;
	String selectedSection;
	boolean b;
	int totalStudent;
	String routeNumber="", vehicleNumber="", contactNumber="", driverName="",conductorName="",attendantName="",conductorNo="",attendantNo="";
	ArrayList<String> list;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;

	ArrayList<StudentInfo> studentList;

	public void getStudentRoute() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		DataBaseMeathodJson dbj = new DataBaseMeathodJson();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		routeDetailList = DBM.getAllStudentRoute(selectedRoute, conn);
		studentList = dbr.getAllStudentStop1(routeDetailList, conn);
		list = DBM.getAllDetailOfRoute(selectedRoute, conn);
		if (studentList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No Record found", "Validation Error"));
			show = false;
		} else {
			Route r = dbj.allTransportRouteListbyId(selectedRoute, DBM.schoolId(), conn);
			routeNumber = r.getName();
			driverName = r.getDriverName();
			conductorName = r.getConductorName();
			attendantName = r.getAttendantName();
			
			contactNumber = r.getDriverNo();
			conductorNo = r.getConductorNo();
			attendantNo = r.getAttendNo();
			totalStudent = studentList.size();
			vehicleNumber = r.getVehicleNumber();
			show = true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ShowRouteWithStudentBean() {
		Connection conn = DataBaseConnection.javaConnection();
		routeNameList = new DatabaseMethods1().allTransportRoutes(conn);
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportRouteWisePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);




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
				// TODO: handle exception
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
		}catch (Exception e) {
			// TODO: handle exception
		}

		try{
			Chunk c8 = new Chunk("\n                                                                  Route Wise Student Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\n Route : "+routeNumber+"            Driver : "+driverName+"               Conductor : "+conductorName+"               Attendant : "+attendantName+" \nVehicle : "+vehicleNumber+"                 Total Students : "+totalStudent+" \n\n",fo );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			//     p8.setAlignment(Element.ALIGN_CENTER);



			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sno");
			table.addCell("Sr. No.");
			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Stop");
			table.addCell("Address");
			table.addCell("Ph(resi.)");
			table.addCell("Father's Phone No.");
			table.addCell("Mother's Phone No.");
			table.addCell("Class");
			table.addCell("Section");



			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<studentList.size();i++){
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno()),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSrNo()),font));

				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFullName())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersName())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getRouteStop()),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getCurrentAddress()),font));

				table.addCell(new Phrase(String.valueOf(studentList.get(i).getResidencePhone())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getMothersPhone()),font));

				table.addCell(new Phrase(String.valueOf(studentList.get(i).getClassName())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSectionName())));



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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Route_Wise_Student_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Route_Wise_Student_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}



	}

	public ArrayList<RouteDetail> getRouteDetailList() {
		return routeDetailList;
	}

	public void setRouteDetailList(ArrayList<RouteDetail> routeDetailList) {
		this.routeDetailList = routeDetailList;
	}

	public String getRouteNumber() {
		return routeNumber;
	}

	public void setRouteNumber(String routeNumber) {
		this.routeNumber = routeNumber;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public int getTotalStudent() {
		return totalStudent;
	}

	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}

	public ArrayList<SelectItem> getRouteNameList() {
		return routeNameList;
	}

	public void setRouteNameList(ArrayList<SelectItem> routeNameList) {
		this.routeNameList = routeNameList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getSelectedRoute() {
		return selectedRoute;
	}

	public void setSelectedRoute(String selectedRoute) {
		this.selectedRoute = selectedRoute;
	}

	public ArrayList<StudentInfo> getArrayList() {
		return arrayList;
	}

	public void setArrayList(ArrayList<StudentInfo> arrayList) {
		this.arrayList = arrayList;
	}

	public ArrayList<Category> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<Category> sectionList) {
		this.sectionList = sectionList;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getConductorName() {
		return conductorName;
	}

	public void setConductorName(String conductorName) {
		this.conductorName = conductorName;
	}

	public String getAttendantName() {
		return attendantName;
	}

	public void setAttendantName(String attendantName) {
		this.attendantName = attendantName;
	}

	public String getConductorNo() {
		return conductorNo;
	}

	public void setConductorNo(String conductorNo) {
		this.conductorNo = conductorNo;
	}

	public String getAttendantNo() {
		return attendantNo;
	}

	public void setAttendantNo(String attendantNo) {
		this.attendantNo = attendantNo;
	}


}
