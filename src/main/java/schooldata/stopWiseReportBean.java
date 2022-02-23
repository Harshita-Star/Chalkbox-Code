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

@ManagedBean(name = "stopWise")
@ViewScoped

public class stopWiseReportBean implements Serializable {

	ArrayList<StudentInfo> arrayList;
	ArrayList<SelectItem> routeNameList;
	ArrayList<Category> sectionList;
	ArrayList<RouteDetail> routeDetailList;
	ArrayList<StudentInfo> stopList;
	String selectedRoute;
	boolean show;
	String selectedSection;
	boolean b;
	int totalStudent;
	ArrayList<StudentInfo> studentList = new ArrayList<>();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	String routeNumber, vehicleNumber, contactNumber, driverName;
	ArrayList<SelectItem> showStopList;
	String StopName;
	String groupId;
	String SHowTotalStudent;
	String route;
	ArrayList<String> list;




	/*
	 * public List<String> autoCompleteStudentInfo(String query) {
	 * //// // System.out.println("welcome"); stopList=new
	 * DatabaseMethods1().searchStopListList(query);
	 * ArrayList<String>showStopList=new ArrayList<String>(); for(StudentInfo
	 * tt:stopList){ //// // System.out.println("sndjkshdjshsd"+tt.getStopName());
	 * showStopList.add(tt.getStopName()); setGroupId(tt.getGroupid()); } return
	 * showStopList; }
	 */

	public void StudentStopName()

	{
		loop: for (SelectItem ss : showStopList) {
			if (ss.getValue().equals(selectedRoute)) {
				route = ss.getLabel();
				break loop;
			}
		}

	Connection conn = DataBaseConnection.javaConnection();
	studentList = new DatabaseMethods1().studentStopWiseRecord(selectedRoute, conn);
	for (StudentInfo tt : studentList) {
		setSHowTotalStudent(tt.getTotalStudentShowInStopWiseReport());
	}
	if (studentList.isEmpty()) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "No Record found", "Validation Error"));
		show = false;
	} else {

	}

	try {
		conn.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	}

	public stopWiseReportBean() {
		Connection conn = DataBaseConnection.javaConnection();
		showStopList = new DatabaseMethods1().searchStopListList(conn);
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		//routeNameList = new DatabaseMethods1().allTransportRoutes(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportStopWisePdf() throws DocumentException, IOException, FileNotFoundException {

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
		}
		catch (Exception e) {
			// TODO: handle exception
		}

		try {
			Chunk c8 = new Chunk("\n                                                                Stop Wise Student Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			//           Chunk c9 = new Chunk("\n Total Students : "+totalStudent+" \n\n",fo );
			//           Paragraph p9 = new Paragraph();
			//           p9.add(c9);
			//           document.add(p9);
			//     p8.setAlignment(Element.ALIGN_CENTER);



			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sno");
			table.addCell("Add. No.");
			table.addCell("Student Name");
			table.addCell("Father's Name");

			table.addCell("Father's Phone No.");
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
				//   table.addCell(new Phrase(String.valueOf(studentList.get(i).getRouteStop()),font));
				//         table.addCell(new Phrase(String.valueOf(studentList.get(i).getCurrentAddress()),font));

				//    table.addCell(new Phrase(String.valueOf(studentList.get(i).getResidencePhone())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone())));
				//     table.addCell(new Phrase(String.valueOf(studentList.get(i).getMothersPhone()),font));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Stop_Wise_Student_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Stop_Wise_Student_Report.pdf").stream(()->isFromFirstData).build();


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

	public ArrayList<StudentInfo> getStopList() {
		return stopList;
	}

	public void setStopList(ArrayList<StudentInfo> stopList) {
		this.stopList = stopList;
	}

	public String getStopName() {
		return StopName;
	}

	public void setStopName(String stopName) {
		StopName = stopName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getSHowTotalStudent() {
		return SHowTotalStudent;
	}

	public void setSHowTotalStudent(String sHowTotalStudent) {
		SHowTotalStudent = sHowTotalStudent;
	}

	public ArrayList<SelectItem> getShowStopList() {
		return showStopList;
	}

	public void setShowStopList(ArrayList<SelectItem> showStopList) {
		this.showStopList = showStopList;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
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


}
