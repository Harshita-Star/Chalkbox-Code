package schooldata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
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

import session_work.QueryConstants;

@ManagedBean(name="masterMiniDailyFeeCollection")
@ViewScoped

public class MasterMiniDailyFeeCollectionBean implements Serializable
{
	Date feedate=new Date(),endDate;
	String date;
	boolean show;

	String remark,selectedOperator,session;
	String feetype,studentclass,studentname,fathername,selectedClass,selectedSection;
	ArrayList<DailyFeeCollectionBean> feedetail,dailycollection,onlineFeeCollecton,dailyfee=new ArrayList<>();
	double cashAmount,chequeAmount;
	String cashAmountString,checkAmountString,totalamountString;
	static int count=1;
	ArrayList<Feecollectionc> getdailyfeecollection;
	ArrayList<SelectItem> classList,sectionList,operatorList,branchList;
	DailyFeeCollectionBean selectedstudent;
	ArrayList<StudentInfo> studentList;
	String name;
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	ArrayList<SelectItem> installmentList;
	String[] checkMonthSelected = {};
	double tamount,tdiscount;
	ArrayList<FeeInfo>feelist;
	String otp, otpInput;
	String discoutnNo;
	boolean check;
	ArrayList<Feecollectionc> feesSelected=new ArrayList<>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");

	boolean showClass,showSchool;
	String schname,address1,address2,address3,address4,phoneno,mobileno,website,logo,finalAddress,affiliationNo,type,headerImagePath;
	String regno="",schid,branches;
	transient StreamedContent file;

	public void branchWiseWork()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedSection="-1";
		selectedClass="-1";
		selectedOperator = "all";
		sectionList = new ArrayList<>();
		show=false;

		if(schid.equals("-1"))
		{
			showClass = false;
			showSchool=true;
			schname="B.L.M Academy";
			finalAddress="Haldwani";
			affiliationNo="";
			phoneno="";
			operatorList = obj.allOperatorList(branches,conn);
		}
		else
		{
			showClass = true;
			showSchool=false;
			schoolInfo(schid);

			operatorList = obj.allOperatorList(schid,conn);
			classList=new DatabaseMethods1().allClass(schid,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	String newschid;
	public MasterMiniDailyFeeCollectionBean()
	{
		showClass = false;
		showSchool=true;
		schid="-1";
		selectedOperator = "all";
		Connection conn = DataBaseConnection.javaConnection();
		feedate=new Date();
		endDate=new Date();
		
		//classList=obj.allClass(conn);
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");
		branches="";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
					newschid=String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
					newschid=newschid+","+String.valueOf(in.getValue());
				}
			}
		}

		operatorList = obj.allOperatorList(branches,conn);
		selectedSection="-1";
		selectedClass="-1";
		session = DatabaseMethods1.selectedSessionDetails(newschid,conn);
		showdailyfeelist(newschid);
		installment();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void installment()
	{
		installmentList = new ArrayList<>();

		SelectItem ss1 = new SelectItem();
		ss1.setLabel("April");
		ss1.setValue("4");
		installmentList.add(ss1);

		SelectItem ss2 = new SelectItem();
		ss2.setLabel("May-June");
		ss2.setValue("6");
		installmentList.add(ss2);

		SelectItem ss3 = new SelectItem();
		ss3.setLabel("Jul-Aug");
		ss3.setValue("8");
		installmentList.add(ss3);

		SelectItem ss4 = new SelectItem();
		ss4.setLabel("September");
		ss4.setValue("9");
		installmentList.add(ss4);

		SelectItem ss5 = new SelectItem();
		ss5.setLabel("Oct-Nov");
		ss5.setValue("11");
		installmentList.add(ss5);

		SelectItem ss6 = new SelectItem();
		ss6.setLabel("December");
		ss6.setValue("12");
		installmentList.add(ss6);

		SelectItem ss7 = new SelectItem();
		ss7.setLabel("January");
		ss7.setValue("13");
		installmentList.add(ss7);

		SelectItem ss8 = new SelectItem();
		ss8.setLabel("Feb-Mar");
		ss8.setValue("15");
		installmentList.add(ss8);
		
		SelectItem ss9 = new SelectItem();
		ss9.setLabel("Previous_Fee");
		ss9.setValue("-1");
		installmentList.add(ss9);
	}

	public void allSections()
	{
		sectionList = new ArrayList<>();
		selectedSection="-1";
		if(!selectedClass.equals("-1"))
		{
			Connection conn = DataBaseConnection.javaConnection();
			sectionList=obj.allSection(schid,selectedClass,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}


	/*public void calculateTotal(Object o) {
	   // it .total = 0.0d;
	    String name = "";
	    if(o != null) {
	        if(o instanceof String) {
	            name = (String) o;
	            for(FeeCollectionBean p : (List<FeeCollectionBean>) DataTa getWrappedData()) { // The loop should find the sortBy value rows in all dataTable data.
	                switch(sortColumnCase) { // sortColumnCase was set in the onSort event
	                    case 0:
	                        if(p.getcolumn0data().getName().equals(name)) {
	                            this.total += p.getcolumn0data().getValue();
	                        }
	                        break;
	                    case 1:
	                        if(p.getcolumn1data().getName().equals(name)) {
	                            this.total += p.getcolumn1data().getValue();
	                        }
	                        break;
	                }
	            }
	        }
	    }
	}*/



	public void onSummaryRow(Object filter)
	{

		//(filter.toString());
		FacesContext facesContext = FacesContext.getCurrentInstance();

		int total=0;

		for(DailyFeeCollectionBean ls:dailyfee)
		{
			if(ls.getFeedate().equals(filter))
			{
				total += Integer.parseInt(ls.getAmount());
			}
		}
		DataTable table = (DataTable) UIComponent.getCurrentComponent(facesContext);
		List<UIComponent> children = table.getSummaryRow().getChildren();
		UIComponent column = children.get(children.size() - 1);
		column.getAttributes().put("total", total);
	}

	public void showReport()
	{
		
		if(schid.equals("-1"))
		{
			showdailyfeelist(newschid);
		}
		else
		{
			showdailyfeelist(schid);
		}
	}

	public void showdailyfeelist(String branches)
	{
		Connection conn=DataBaseConnection.javaConnection();
		//int registfee = 0,annualfee=0,tutionfee=0,transportfee=0,eaxmfee =0,artFee=0,termfee=0;
		count=1;cashAmount=0.0;chequeAmount=0;tamount=tdiscount=0;
		dailyfee=new ArrayList<>();

		String sectionid="";
		if(selectedClass.equals("-1"))
		{
			sectionid="-1";
		}
		else if(selectedSection.equals("-1"))
		{
			for(SelectItem ii : sectionList)
			{
				if(sectionid.equals(""))
				{
					sectionid = String.valueOf(ii.getValue());
				}
				else
				{
					sectionid = sectionid + "," + String.valueOf(ii.getValue());
				}
			}
		}
		else
		{
			sectionid = selectedSection;
		}
		
		//For installments
		
		
		
		String allInstallments = "-1";
		for(int i =0; i<checkMonthSelected.length; i++)
		{
		 if(i==0)
		 {

			 allInstallments = "" ;
			 allInstallments = allInstallments + checkMonthSelected[i];
				
		 }
		 else
		 {
			allInstallments = allInstallments + "," + checkMonthSelected[i];

		 }
		}
		
		if(!allInstallments.equals("-1"))
		{
			if(checkMonthSelected.length==installmentList.size())
			{
				allInstallments="-1";
			}
				
		}
		
		
		HashMap<String, String> tempMap=new HashMap<>();
		dailyfee=obj.tempGetDateWiseFeecollection(branches, feedate, sectionid, endDate, tempMap, conn, selectedOperator,allInstallments);
		Collections.sort(dailyfee);


		/*	getdailyfeecollection=obj.tempGetfeedailydetail1(branches,feedate,sectionid,endDate,tempMap,conn,selectedOperator);
		 ArrayList<String> temp=new ArrayList<String>();
		 for(Feecollectionc mm:getdailyfeecollection)
		 {
			 temp.add(mm.getRecipietNo()+"-"+mm.getSchid());
		 }
		 String a[]=new String[temp.size()];
		 for(int i=0;i<temp.size();i++)
		 {
			 a[i]=temp.get(i);
		 }
		 a=removeDuplicates(a);

		 String value="";
		 ArrayList<String>tempdeatils;
		 for(int i=0;i<a.length;i++)
		 {
			 tempdeatils=new ArrayList<>();
			 HashMap<String, String> feecllection=new HashMap<>();
			 HashMap<String, String> totalAmoutMap=new HashMap<>();
			 HashMap<String, String> discountMap=new HashMap<>();
			 DailyFeeCollectionBean ll=new DailyFeeCollectionBean();
			 int totalamuont=0,totalDiscount=0;

			 for(Feecollectionc list : getdailyfeecollection)
			 {

				 if((list.getRecipietNo()+"-"+list.getSchid()).equals(String.valueOf(a[i])))
				 {
					 ll.setSchid(list.getSchid());
					 ll.setSchname(list.getSchname());
					 ll.setStudentname(list.getStudentname());
					 ll.setClassname(list.getClassName());
					 ll.setFathername(list.getFathername());
					 ll.setStudentid(list.getStudentid());
					 ll.setUser(list.getUser());
					 ll.setSrNo(list.getSrNo());
					 ll.setMothername(list.getMotherName());
					 ll.setSection(list.getSectionName());
					 ll.setFeeRemark(list.getFeeRemark());
					 ll.setFeedate(list.getFeedate());
					 ll.setChallanDate(list.getChallanDate());
					 ll.setDueDateStr(list.getDueDateStr());
					 ll.setReciptno(list.getRecipietNo());
					 ll.setFeeDateStr(sdf.format(list.getFeedate()));
					 String feetype=list.getFeeName();
					 feecllection.put(feetype, String.valueOf(list.getFeeamunt()));
					 discountMap.put(feetype, String.valueOf(list.getDiscount()));
					 totalAmoutMap.put(feetype, String.valueOf(list.getTotalAmount()));
				     ll.setAllFess(feecllection);
				     ll.setAllDiscount(discountMap);
				     ll.setAllTotalAmount(totalAmoutMap);
					 ll.setBankname(list.getBankname());
			         ll.setChequenumber(list.getChequeno());
		             ll.setPaymentmode(list.getPaymentmode());
			         if(tempdeatils.size()==0)
			         {
			        	 	value="orignal";
			         }
			         else
			         {
				        	 for(String ls:tempdeatils)
				        	 {
					        	   if(ls.equals(feetype))
					        	   {
					        		   value="duplicate";
					        		   break;
					        	   }
					        	   else
					        	   {
					        		   value="orignal";
					        	   }
				        	 }
			         }

			         if(value.equals("orignal"))
			         {
				        	 tempdeatils.add(feetype);
				        	 totalamuont+=list.getFeeamunt();
				        	 totalDiscount+=list.getDiscount();
			         }

			         ll.setAmount(String.valueOf(totalamuont));
			         ll.setDiscount(String.valueOf(totalDiscount));
			         ll.setPaymentmode(list.getPaymentmode());
			         ll.setSrno(count);

				 }



			 }
			 dailyfee.add(ll);


			// registfee = 0;annualfee=0;tutionfee=0;transportfee=0;eaxmfee = 0;artFee=0;termfee=0;
			 count++;
			 show=true;
		 }

		 onlineFeeCollecton=new DatabaseMethods1().getOnlineFees(feedate,endDate,conn);
		 dailyfee.addAll(onlineFeeCollecton);
		 */




		if(dailyfee.size()>0)
		{
			for(DailyFeeCollectionBean info:dailyfee)
			{
				if(info.getPaymentmode().equalsIgnoreCase("Cash"))
				{
					cashAmount+=Integer.parseInt(info.getAmount());
				}
				else if(info.getPaymentmode().equalsIgnoreCase("PAYTM"))
				{
					chequeAmount+=Integer.parseInt(info.getAmount());
				}
				else
				{
					chequeAmount+=Integer.parseInt(info.getAmount());
				}

				tdiscount+=Integer.parseInt(info.getDiscount());
			}
			tamount=cashAmount+chequeAmount;

			cashAmountString=String.format ("%.0f", cashAmount);
			checkAmountString=String.format ("%.0f", chequeAmount);
			totalamountString=String.format ("%.0f", tamount);

		}
		else
		{
			cashAmountString="0";
			checkAmountString="0";
			totalamountString="0";
			tdiscount=0;
		}

		date=sdf.format(new Timestamp(feedate.getTime()))+"-"+sdf.format(new Timestamp(endDate.getTime()));
		show=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public void toNum(Object doc)
	{





		XSSFWorkbook book = (XSSFWorkbook)doc;

		    XSSFSheet sheet = book.getSheetAt(0);




	         sheet.createFreezePane(0, 4);

	         //FOR HEADERS
		    CellStyle style = book.createCellStyle();
		    style.setFillForegroundColor(IndexedColors.PINK.getIndex());
		    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		    Font font = book.createFont();
		        font.setColor(IndexedColors.BLACK.getIndex());
		        style.setVerticalAlignment(VerticalAlignment.TOP);
		        style.setFont(font);

			XSSFRow ro =sheet.getRow(3);

			ro.setHeight((short)500);
			for(int i=0;i<11;i++)
			{
			XSSFCell cel = ro.getCell(i);

			cel.setCellStyle(style);
			}


			//FOR IMAGE
			   try
				{
					   FileInputStream my_banner_image = new FileInputStream("http://www.chalkboxerp.in/BLMSRS/BLMLogo.jpg");
			           byte[] bytes = IOUtils.toByteArray(my_banner_image);
			     XSSFDrawing drawing = sheet.createDrawingPatriarch();

				       ClientAnchor my_anchor = new XSSFClientAnchor();
				       my_anchor.setRow1(0);
				       my_anchor.setRow2(2);
				       my_anchor.setCol1(0);
				       my_anchor.setCol2(2);
				       int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
				    //   cellll.setCellValue(my_picture_id);
				       my_banner_image.close();

				      XSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
				     //   my_picture.resize();
				  //     FileOutputStream out = new FileOutputStream(new File("C:\\Users\\user\\Desktop\\exc.xlsx"));
				  //     book.write(out);
				//       out.close();




			  } catch (IOException ioex) {
						  //("fgg");
					  }

		//FOR DATA



		    XSSFRow header = sheet.getRow(5);

		   int colCount = header.getPhysicalNumberOfCells();

		    int rowCount = sheet.getPhysicalNumberOfRows();

		    ArrayList<Integer> rowIndexList = new ArrayList<>();
		    ArrayList<Double> DatePerSum = new ArrayList<>();
		    Double dateTotal=0.0;

		    for(int rowInd = 5; rowInd < rowCount-1; rowInd++) {
			    XSSFRow row = sheet.getRow(rowInd);
			    XSSFRow row2 = sheet.getRow(rowInd+1);

			    XSSFCell	cell = row.getCell(1);
			    XSSFCell	cell2 = row2.getCell(1);

			    String strVal = cell.getStringCellValue();
			    String strVal2 = cell2.getStringCellValue();

			    XSSFCell	sumcell = row.getCell(7);


			    if(strVal.equalsIgnoreCase(strVal2))
			    {
			    	dateTotal += Double.valueOf(sumcell.getStringCellValue());
			    }
			    else
			    {

			    	//(rowInd);
			    	rowIndexList.add(rowInd);

			    	dateTotal += Double.valueOf(sumcell.getStringCellValue());
			    	DatePerSum.add(dateTotal);
			    	dateTotal =0.0;

			    }

		    }

		    for(int g=0;g<DatePerSum.size();g++)
		    {
		    	//("datepersuym"+DatePerSum.get(g));
		    }

		    //(sheet.getLastRowNum());
		    int lastRow= sheet.getLastRowNum();
		   int counterOfRowShift =1;
		    for(int i=0;i<rowIndexList.size();i++)
		    {
		    	//("finalis"+rowIndexList.get(i));

		   	sheet.shiftRows(rowIndexList.get(i)+counterOfRowShift,lastRow-1+counterOfRowShift , 1);
		  XSSFRow r1 = 	sheet.createRow(rowIndexList.get(i)+counterOfRowShift);


		   	for(int k=0;k<11;k++)
		   	{
		   		if(k==7)
		   		{
		   		XSSFCell	ttcell  = r1.createCell(k);
			   	ttcell.setCellValue(String.valueOf(DatePerSum.get(i)));

		   		}
		   		else
		   		{
		   	XSSFCell	ttcell  = r1.createCell(k);
		   	ttcell.setCellValue("");
		   		}
		   	}
		   	counterOfRowShift++;
		    }
		    //("test"+rowCount);
		    //(lastRow-1+counterOfRowShift);

	XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);



		for(int rowInd = 5; rowInd < lastRow+counterOfRowShift; rowInd++) {
		    XSSFRow row = sheet.getRow(rowInd);



	             for(int colInd = 0; colInd < colCount; colInd++) {

		    //(rowInd);



		    XSSFCell	cell = row.getCell(colInd);
		    CellStyle my_style = book.createCellStyle();

		   my_style.setBorderLeft(XSSFBorderStyle.THIN);
            my_style.setBorderRight(XSSFBorderStyle.THIN);
            my_style.setBorderTop(XSSFBorderStyle.THIN);
            my_style.setBorderBottom(XSSFBorderStyle.THIN);

            my_style.setBottomBorderColor(IndexedColors.YELLOW.getIndex());
            my_style.setTopBorderColor(IndexedColors.YELLOW.getIndex());
            my_style.setLeftBorderColor(IndexedColors.YELLOW.getIndex());
            my_style.setRightBorderColor(IndexedColors.YELLOW.getIndex());
		       cell.setCellStyle(my_style);

		        int status=0,counter=0,dot=0;
		        Character ch[] = new Character[200];

		        String strVal = cell.getStringCellValue();
		        //("strval"+strVal);
		        for(int i=0;i<strVal.length();i++)
		        {

		          ch[i] = strVal.charAt(i);
		          String s =Character.toString(ch[i]);


		          if(Character.isDigit(ch[i]))
		          {
		        	  counter++;
		          }
		          if(s.equals("."))
		          {
		        	  dot++;
		          }
		          if(s.equals(""))
		          {
		        	  status=1;
		          }
		        }


		        if(status==1)
		        {
		        	cell.setCellValue(strVal);
		        }


		        else if((dot+counter)==strVal.length())
		       {
		        	try {

		    	  Double ds = Double.parseDouble(strVal);


		            cell.setCellType(Cell.CELL_TYPE_NUMERIC);

		    	  DataFormat fmt = book.createDataFormat();
		    	  CellStyle sty = book.createCellStyle();
		    	  sty.setDataFormat(fmt.getFormat("#,##0.00"));
		  	    sty.setBorderLeft(XSSFBorderStyle.THIN);
	           sty.setBorderRight(XSSFBorderStyle.THIN);
	            sty.setBorderTop(XSSFBorderStyle.THIN);
	            sty.setBorderBottom(XSSFBorderStyle.THIN);



	            sty.setBottomBorderColor(IndexedColors.YELLOW.getIndex());

	            sty.setTopBorderColor(IndexedColors.YELLOW.getIndex());

	            sty.setLeftBorderColor(IndexedColors.YELLOW.getIndex());

	            sty.setRightBorderColor(IndexedColors.YELLOW.getIndex());
		    	  cell.setCellStyle(sty);
		            cell.setCellValue(ds);
		            //("done");
		        	}
		        	catch (Exception e) {
						// TODO: handle exception
					}

		       }
		        else
		        {
		        	  cell.setCellValue(strVal);
		        }

		    }



		}
		//FOR LAST DATE AMOUNT SUM
		   Double curre=0.0 ;int coun =0;

		   XSSFRow rowoffinalDt = sheet.getRow(lastRow+counterOfRowShift-1);

		    XSSFCell	cal = rowoffinalDt.getCell(1);
		    String celVal = cal.getStringCellValue();
		    //("celval"+celVal);

		   for(int j=lastRow+counterOfRowShift-1;j>4;j--)
		   {
			   XSSFRow rowoffinalDte = sheet.getRow(j);
			   XSSFCell	ceel = rowoffinalDte.getCell(1);
			   String celVa = ceel.getStringCellValue();
			   //("celv"+celVa);

			   if(celVa.equalsIgnoreCase(celVal))
			   {
				   coun++;
				   XSSFCell	ceel2 = rowoffinalDte.getCell(7);
				   curre = curre + ceel2.getNumericCellValue();
				   //("pal"+curre);
			   }
			   else
			   {
				   break;
			   }

		   }

			  XSSFRow finalRow = sheet.createRow(lastRow+counterOfRowShift+1);

		        XSSFCell finalDte = finalRow.createCell(7);
		        //("done");
		        finalDte.setCellType(Cell.CELL_TYPE_NUMERIC);

		    	  DataFormat fmt = book.createDataFormat();
		    	  CellStyle sty = book.createCellStyle();
		    	  sty.setDataFormat(fmt.getFormat("#,##0.00"));
		    	  finalDte.setCellStyle(sty);
		        finalDte.setCellValue(curre);

	}

//	public void toNum(Object doc)
//    {
//
//
//
//
//
//        XSSFWorkbook book = (XSSFWorkbook)doc;
//
//            XSSFSheet sheet = book.getSheetAt(0);
//
//
//
//             sheet.createFreezePane(0, 4);
//
//             //FOR HEADERS
//
//
//            XSSFRow headrow =sheet.getRow(3);
//             headrow.setHeight((short)700);
//
//        //     XSSFColor colour = new XSSFColor(new Color(255, 255, 255));
//
//                CellStyle style = book.createCellStyle();
//
//
//                style.setFillForegroundColor(IndexedColors.PINK.getIndex());
//                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//
//                    style.setVerticalAlignment(VerticalAlignment.TOP);
//
//
//            for(int i=0;i<11;i++)
//            {
//            XSSFCell cel = headrow.getCell(i);
//
//
//            cel.setCellStyle(style);
//            }
//
//        //     Connection con=DataBaseConnection.javaConnection();
//            //   SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo("251", con);
//
//
////               try {
////                con.close();
////            } catch (SQLException e1) {
////                
////                e1.printStackTrace();
////            }
//
//
//
//        /*     String home = System.getProperty("user.home");
//            //FOR IMAGE
//               try
//                {
//                   String imageUrl = ls.getDownloadpath()+ls.getImagePath();
//                destinationFilePath = ls.getUploadpath()+"par.jpg"; // For windows something like c:\\path\to\file\test.jpg
//
//                    InputStream inputStream = null;
//                    try {
//                        inputStream = new URL(imageUrl).openStream();
//                        Files.copy(inputStream, Paths.get(destinationFilePath));
//                    } catch (IOException e) {
//                        //("Exception Occurred " + e);
//                    } finally {
//                        if (inputStream != null) {
//                            try {
//                                inputStream.close();
//                            } catch (IOException e) {
//                                // Ignore
//                            }
//                        }
//                    }
//
//                  // byte[] response = out.toByteArray();
//                  // String home = System.getProperty("user.home");
//                //   OutputStream st = new FileOutputStream(new File("https://www.w3schools.com/bootstrap4/paris.jpg"));
//                //   InputStream in = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/images/YOUR_IMAGE_NAME");
//                       InputStream my_banner_image = new FileInputStream(new File(ls.getDownloadpath()+"par.jpg"));
//
//
//                       byte[] bytes = IOUtils.toByteArray(my_banner_image);
//                 XSSFDrawing drawing = sheet.createDrawingPatriarch();
//
//                       ClientAnchor my_anchor = new XSSFClientAnchor();
//                       my_anchor.setRow1(0);
//                       my_anchor.setRow2(2);
//                       my_anchor.setCol1(0);
//                       my_anchor.setCol2(2);
//                       int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
//                    //   cellll.setCellValue(my_picture_id);
//                       my_banner_image.close();
//
//                      XSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
//                     //   my_picture.resize();
//                  //     FileOutputStream out = new FileOutputStream(new File("C:\\Users\\user\\Desktop\\exc.xlsx"));
//                  //     book.write(out);
//                //       out.close();
//
//
//
//
//              } catch (IOException ioex) {
//                          //("fgg");
//                      } */
	//
	//        //FOR DATA
	//
	//
	//
	//            XSSFRow header = sheet.getRow(5);
	//
	//           int colCount = header.getPhysicalNumberOfCells();
	//
	//            int rowCount = sheet.getPhysicalNumberOfRows();
	//
	//            ArrayList<Integer> rowIndexList = new ArrayList<>();
	//            ArrayList<Double> DatePerSum = new ArrayList<>();
	//            Double dateTotal=0.0;
	//
	//            for(int rowInd = 6; rowInd < rowCount-1; rowInd++) {
	//                XSSFRow row = sheet.getRow(rowInd);
	//                XSSFRow row2 = sheet.getRow(rowInd+1);
	//
	//                XSSFCell     cell = row.getCell(1);
	//                XSSFCell     cell2 = row2.getCell(1);
	//
	//                String strVal = cell.getStringCellValue();
	//                String strVal2 = cell2.getStringCellValue();
	//
	//                XSSFCell     sumcell = row.getCell(7);
	//
	//
	//                if(strVal.equalsIgnoreCase(strVal2))
	//                {
	//                    dateTotal += Double.valueOf(sumcell.getStringCellValue());
	//                }
	//                else
	//                {
	//
	//                    //(rowInd);
	//                    rowIndexList.add(rowInd);
	//
	//                    dateTotal += Double.valueOf(sumcell.getStringCellValue());
	//                    DatePerSum.add(dateTotal);
	//                    dateTotal =0.0;
	//
	//                }
	//
	//            }
	//
	//            for(int g=0;g<DatePerSum.size();g++)
	//            {
	//                //("datepersuym"+DatePerSum.get(g));
	//            }
	//
	//            //(sheet.getLastRowNum());
	//            int lastRow= sheet.getLastRowNum();
	//           int counterOfRowShift =1;
	//
	//
	//
	//          ArrayList<Integer> totRow = new ArrayList<>();
	//
	//            for(int i=0;i<rowIndexList.size();i++)
	//            {
	//                //("finalis"+rowIndexList.get(i));
	//
	//               sheet.shiftRows(rowIndexList.get(i)+counterOfRowShift,lastRow-1+counterOfRowShift , 1);
	//          XSSFRow r1 =     sheet.createRow(rowIndexList.get(i)+counterOfRowShift);
	//
	//          totRow.add(rowIndexList.get(i)+counterOfRowShift);
	//
	//
	//
	//               for(int k=0;k<11;k++)
	//               {
	//                   if(k==7)
	//                   {
	//                    XSSFCell    ttcell  = r1.createCell(k);
	//
	//
	//                    ttcell.setCellValue(String.valueOf(DatePerSum.get(i)));
	//
	//
	//                   }
	//                   else
	//                   {
	//                XSSFCell    ttcell  = r1.createCell(k);
	//
	//          //      ttcell.setCellValue("d");
	//          //     ttcell.setCellStyle(styleTotall);
	//                   }
	//               }
	//
	//
	//
	//
	//               counterOfRowShift++;
	//            }
	//            //("test"+rowCount);
	//            //(lastRow-1+counterOfRowShift);
	//
	//            for(int f=0;f<totRow.size();f++)
	//            {
	//                //("ttr"+totRow.get(f).toString());
	//            }
	//
	//    XSSFCellStyle intStyle = book.createCellStyle();
	//        intStyle.setDataFormat((short)1);
	//
	//        XSSFCellStyle decStyle = book.createCellStyle();
	//        decStyle.setDataFormat((short)2);
	//
	//        XSSFCellStyle dollarStyle = book.createCellStyle();
	//        dollarStyle.setDataFormat((short)5);
	//
	//
	//
	//        for(int rowInd = 5; rowInd < lastRow+counterOfRowShift; rowInd++) {
	//            XSSFRow row = sheet.getRow(rowInd);
	//
	//
	//
	//                 for(int colInd = 0; colInd < colCount; colInd++) {
	//
	//            //(rowInd);
	//
	//
	//
	//            XSSFCell     cell = row.getCell(colInd);
	//            CellStyle my_style = book.createCellStyle();
	//
	//           my_style.setBorderLeft(XSSFBorderStyle.THIN);
	//            my_style.setBorderRight(XSSFBorderStyle.THIN);
	//            my_style.setBorderTop(XSSFBorderStyle.THIN);
	//            my_style.setBorderBottom(XSSFBorderStyle.THIN);
	//
	//            my_style.setBottomBorderColor(IndexedColors.YELLOW.getIndex());
	//            my_style.setTopBorderColor(IndexedColors.YELLOW.getIndex());
	//            my_style.setLeftBorderColor(IndexedColors.YELLOW.getIndex());
	//            my_style.setRightBorderColor(IndexedColors.YELLOW.getIndex());
	//
	//            for(int f=0;f<totRow.size();f++)
	//            {
	//
	//                 if( String.valueOf(rowInd).equalsIgnoreCase(totRow.get(f).toString()) )
	//                 {
	//
	//                      XSSFFont font = book.createFont();
	//                       font.setBold(true);
	//                         font.setColor(IndexedColors.BLUE.getIndex());
	//                         font.setFontHeightInPoints((short)30);
	//                         my_style.setFont(font);
	//                       my_style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	//                    my_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	//
	//                     my_style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
	//                     my_style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
	//                 }
	//                 else
	//                 {
	//
	//
	//                 }
	//            }
	//
	//
	//               cell.setCellStyle(my_style);
	//
	//                int status=0,counter=0,dot=0;
	//                Character ch[] = new Character[200];
	//
	//                String strVal = cell.getStringCellValue();
	//                //("strval"+strVal);
	//                for(int i=0;i<strVal.length();i++)
	//                {
	//
	//                  ch[i] = strVal.charAt(i);
	//                  String s =Character.toString(ch[i]);
	//
	//
	//                  if(Character.isDigit(ch[i]))
	//                  {
	//                      counter++;
	//                  }
	//                  if(s.equals("."))
	//                  {
	//                      dot++;
	//                  }
	//                  if(s.equals(""))
	//                  {
	//                      status=1;
	//                  }
	//                }
	//
	//
	//                if(status==1)
	//                {
	//                    cell.setCellValue(strVal);
	//                }
	//
	//
	//                else if((dot+counter)==strVal.length())
	//               {
	//                    try {
	//
	//                  Double ds = Double.parseDouble(strVal);
	//
	//
	//                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
	//
	//                  DataFormat fmt = book.createDataFormat();
	//                  CellStyle sty = book.createCellStyle();
	//                  sty.setDataFormat(fmt.getFormat("#,##0.00"));
	//
	//                 sty.setBorderRight(XSSFBorderStyle.THIN);
	//                 sty.setRightBorderColor(IndexedColors.YELLOW.getIndex());
	//                  cell.setCellStyle(sty);
	//                    cell.setCellValue(ds);
	//                    //("done");
	//                    }
	//                    catch (Exception e) {
	//                        // TODO: handle exception
	//                    }
	//
	//               }
	//                else
	//                {
	//                      cell.setCellValue(strVal);
	//                }
	//
	//            }
	//
	//
	//
	//        }
	//
	//
	//        //FOR LAST DATE AMOUNT SUM
	//           Double curre=0.0 ;int coun =0;
	//
	//           XSSFRow rowoffinalDt = sheet.getRow(lastRow+counterOfRowShift-1);
	//
	//            XSSFCell     cal = rowoffinalDt.getCell(1);
	//            String celVal = cal.getStringCellValue();
	//            //("celval"+celVal);
	//
	//           for(int j=lastRow+counterOfRowShift-1;j>4;j--)
	//           {
	//               XSSFRow rowoffinalDte = sheet.getRow(j);
	//               XSSFCell     ceel = rowoffinalDte.getCell(2);
	//               String celVa = ceel.getStringCellValue();
	//               //("celv"+celVa);
	//
	//               if(celVa.equalsIgnoreCase(celVal))
	//               {
	//                   coun++;
	//                   XSSFCell     ceel2 = rowoffinalDte.getCell(8);
	//                   curre = curre + ceel2.getNumericCellValue();
	//                   //("pal"+curre);
	//               }
	//               else
	//               {
	//                   break;
	//               }
	//
	//           }
	//
	//              XSSFRow finalRow = sheet.createRow(lastRow+counterOfRowShift);
	//                finalRow.setHeight((short)667);
	//
	//                for(int k=0;k<8;k++)
	//                {
	//                     XSSFCell finalDte = finalRow.createCell(k);
	//
	//
	//                          CellStyle styl = book.createCellStyle();
	//
	//
	//                          XSSFFont fonte = book.createFont();
	//
	//                                 fonte.setFontHeightInPoints((short)35);
	//                               DataFormat fmt = book.createDataFormat();
	//
	//                           styl.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	//                                styl.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	//                          styl.setDataFormat(fmt.getFormat("#,##0.00"));
	//                          finalDte.setCellStyle(styl);
	//
	//
	//                }
	//                XSSFCell finalD = finalRow.createCell(7);
	//                finalD.setCellType(Cell.CELL_TYPE_NUMERIC);
	//
	//                  DataFormat fm = book.createDataFormat();
	//                  CellStyle stylee = book.createCellStyle();
	//                  stylee.setDataFormat(fm.getFormat("#,##0.00"));
	//                  finalD.setCellStyle(stylee);
	//                  finalD.setCellStyle(stylee);
	//                  finalD.setCellValue(curre);
	//
	//                for(int k=8;k<11;k++)
	//                {
	//                     XSSFCell finalDt = finalRow.createCell(k);
	//                          CellStyle st = book.createCellStyle();
	//                       XSSFFont fonte = book.createFont();
	//                         fonte.setFontHeightInPoints((short)35);
	//                         st.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	//                            st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	//                            finalDt.setCellStyle(st);
	//                }
	//
	//    }





	public  void exportPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo("251", con);


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


			Chunk c = new Chunk("\nBLM Academy Sr. Secondary School                                 \n",fo );

			Chunk c3 = new Chunk(im, -400, 15);

			Chunk c1 = new Chunk("Padampur Devaliya, Gora Parao, Haldwani, Distt. Nainital                  \n\n",fo);

			Paragraph p1 = new Paragraph();



			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_RIGHT);

			document.add(p1);


		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		Chunk c8 = new Chunk("\n                                                        Daily Fee Collection Report\n\n\n");
		Paragraph p8 = new Paragraph();
		p8.add(c8);
		document.add(p8);
		p8.setAlignment(Element.ALIGN_CENTER);

		//Table

		int las = dailyfee.size();
		Double lastTot =0.0,lastDisTot=0.0;
		for(int j=las-1;j>0;j--)
		{
			if(dailyfee.get(j).getFeeDateStr().equalsIgnoreCase(dailyfee.get(j-1).getFeeDateStr()))
			{
				lastTot += Double.valueOf(dailyfee.get(j).getAmount());
				lastDisTot += Double.valueOf(dailyfee.get(j).getDiscount());
			}
			else
			{
				lastTot += Double.valueOf(dailyfee.get(j).getAmount());
				lastDisTot += Double.valueOf(dailyfee.get(j).getDiscount());
				break;
			}
		}
		//  //("Sdfc");

		Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
		PdfPTable table = new PdfPTable(new float[] { 0.8f, 0.8f,1 ,1 ,0.6f,1,0.8f,1,0.8f,0.8f,0.7f,0.5f});

		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell("Sno");
		table.addCell("Date");
		table.addCell("Student Name");
		table.addCell("Father's Name");
		table.addCell("Class");
		table.addCell("Receipt No.");
		
		table.addCell("School");
		table.addCell("Amount");
		table.addCell("Discount");
		table.addCell("Pay   Mode");
		table.addCell("Installment");
		table.addCell("User");

		table.setHeaderRows(1);

		// BaseColor bs = new BaseColor(45, 20, 35);
		PdfPCell[] cells = table.getRow(0).getCells();
		for(int i=0;i<cells.length;i++)
		{
			cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

			cells[i].setBorderWidth(2);

		}
		//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

		Double dateTot=0.0,discountTot=0.0;int count =0;
		for (int i=0;i<dailyfee.size()-1;i++){
			table.addCell(new Phrase(dailyfee.get(i).getSrNo(),font));
			table.addCell(new Phrase(dailyfee.get(i).getFeeDateStr(),font));

			table.addCell(new Phrase(dailyfee.get(i).getStudentname(),font));
			table.addCell(new Phrase(dailyfee.get(i).getFathername(),font));
			table.addCell(new Phrase(dailyfee.get(i).getClassname(),font));
			table.addCell(new Phrase(dailyfee.get(i).getReciptno(),font));
			table.addCell(new Phrase(dailyfee.get(i).getSchname(),font));
			table.addCell(new Phrase(dailyfee.get(i).getAmount(),font));
			table.addCell(new Phrase(dailyfee.get(i).getDiscount(),font));
			table.addCell(new Phrase(dailyfee.get(i).getPaymentmode(),font));
			table.addCell(new Phrase(dailyfee.get(i).getInstallmentName(),font));
			table.addCell(new Phrase(dailyfee.get(i).getUser(),font));

			if( dailyfee.get(i).getFeeDateStr().equalsIgnoreCase(dailyfee.get(i+1).getFeeDateStr()))
			{
				dateTot += Double.valueOf(dailyfee.get(i).getAmount());
				discountTot += Double.valueOf(dailyfee.get(i).getDiscount());
			}
			else
			{


				dateTot += Double.valueOf(dailyfee.get(i).getAmount());
				discountTot += Double.valueOf(dailyfee.get(i).getDiscount());
				table.addCell("-");
				table.addCell("-");

				table.addCell("-");
				table.addCell("-");
				table.addCell("-");
				table.addCell("-");
				

				table.addCell("TOTAL");
				table.addCell(String.valueOf(dateTot));
				table.addCell(String.valueOf(discountTot));
				table.addCell("-");
				table.addCell("-");
				table.addCell("-");
				count++;
				dateTot=0.0;
				discountTot=0.0;
			}
		}

		for (int i=dailyfee.size()-1;i<dailyfee.size();i++)
		{
			table.addCell(new Phrase(dailyfee.get(i).getSrNo(),font));
			table.addCell(new Phrase(dailyfee.get(i).getFeeDateStr(),font));

			table.addCell(new Phrase(dailyfee.get(i).getStudentname(),font));
			table.addCell(new Phrase(dailyfee.get(i).getFathername(),font));
			table.addCell(new Phrase(dailyfee.get(i).getClassname(),font));
			table.addCell(new Phrase(dailyfee.get(i).getReciptno(),font));
			table.addCell(new Phrase(dailyfee.get(i).getSchname(),font));
			table.addCell(new Phrase(dailyfee.get(i).getAmount(),font));
			table.addCell(new Phrase(dailyfee.get(i).getDiscount(),font));
			table.addCell(new Phrase(dailyfee.get(i).getPaymentmode(),font));
			table.addCell(new Phrase(dailyfee.get(i).getInstallmentName(),font));
			table.addCell(new Phrase(dailyfee.get(i).getUser(),font));

			table.addCell("-");
			table.addCell("-");

			table.addCell("-");
			table.addCell("-");
			table.addCell("-");
			table.addCell("-");
			

			table.addCell("TOTAL");
			table.addCell(String.valueOf(lastTot));
			table.addCell(String.valueOf(lastDisTot));
			table.addCell("-");
			table.addCell("-");
			table.addCell("-");
		}

		for (int i=1;i<dailyfee.size()+count+2;i++){
			PdfPCell[] cell = table.getRow(i).getCells();
			for(int j=0;j<cell.length;j++)
			{

				cell[j].setBorderColor(BaseColor.RED);
			}
		}
		table.setWidthPercentage(110);
		document.add(table);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Daily_Fee_Collection.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Daily_Fee_Collection_Report.pdf").stream(()->isFromFirstData).build();






	}



	public void toNum(Object doc)
	{





		XSSFWorkbook book = (XSSFWorkbook)doc;

		XSSFSheet sheet = book.getSheetAt(0);


		sheet.getRow(0);


		//FOR HEADERS styling in 4th row
		XSSFRow headerRow = sheet.getRow(4);

		//  headerRow.setHeight((short)1200);
		CellStyle style = book.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		//  Font font = book.createFont();
		//       font.setColor(IndexedColors.BLACK.getIndex());
		//   style.setFont(font);
		
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.NONE);

		style.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<12;i++) {
			XSSFCell ce1 = headerRow.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);

			ce1.setCellStyle(style);


		}

		//For header styling in 5th row
		XSSFRow headerRow2 = sheet.getRow(5);
		CellStyle style22 = book.createCellStyle();
		XSSFColor color11 = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style22).setFillForegroundColor(color11);
		style22.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		//  Font font = book.createFont();
		//       font.setColor(IndexedColors.BLACK.getIndex());
		//   style.setFont(font);
		// style.setVerticalAlignment(XSSFCellStyle.TOP);
		
		style22.setBorderLeft(BorderStyle.THIN);
		style22.setBorderRight(BorderStyle.THIN);
		style22.setBorderTop(BorderStyle.NONE);
		style22.setBorderBottom(BorderStyle.THIN);

		style22.setBottomBorderColor(IndexedColors.RED.getIndex());
		style22.setTopBorderColor(IndexedColors.WHITE.getIndex());
		style22.setLeftBorderColor(IndexedColors.RED.getIndex());
		style22.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<12;i++) {
			XSSFCell ce111 = headerRow2.createCell(i);

			ce111.setCellStyle(style22);
			ce111.setCellValue("");


		}



        //For School Header Image
		try
		{
			URL url = new URL("http://www.chalkboxerp.in/BLMSRS/hp.jpg");
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(4);
			my_anchor.setCol1(0);
			my_anchor.setCol2(11);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

			my_banner_image.close();

			drawing.createPicture(my_anchor, my_picture_id);

		} catch (IOException ioex) {
		
		}


		
        //For Data
		XSSFRow header = sheet.getRow(6);

		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();

		ArrayList<Integer> rowIndexList = new ArrayList<>();
		ArrayList<Double> DatePerSum = new ArrayList<>();
		Double dateTotal=0.0;

		for(int rowInd = 6; rowInd < rowCount-1; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			XSSFRow row2 = sheet.getRow(rowInd+1);

			XSSFCell     cell = row.getCell(1);
			XSSFCell     cell2 = row2.getCell(1);

			String strVal = cell.getStringCellValue();
			String strVal2 = cell2.getStringCellValue();

			XSSFCell     sumcell = row.getCell(7);


			if(strVal.equalsIgnoreCase(strVal2))
			{
				try {
					dateTotal += Double.valueOf(sumcell.getStringCellValue());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			else
			{

				rowIndexList.add(rowInd);

				dateTotal += Double.valueOf(sumcell.getStringCellValue());
				DatePerSum.add(dateTotal);
				dateTotal =0.0;

			}

		}


		int lastRow= sheet.getLastRowNum();
		int counterOfRowShift =1;


		ArrayList<Integer> totRow = new ArrayList<>();

		for(int i=0;i<rowIndexList.size();i++)
		{
			

			sheet.shiftRows(rowIndexList.get(i)+counterOfRowShift,lastRow-1+counterOfRowShift , 1);
			XSSFRow r1 =     sheet.createRow(rowIndexList.get(i)+counterOfRowShift);

			totRow.add(rowIndexList.get(i)+counterOfRowShift);




			for(int k=0;k<11;k++)
			{
				if(k==7)
				{
					XSSFCell    ttcell  = r1.createCell(k);
					CellStyle st4 = book.createCellStyle();
					XSSFColor color7 = new XSSFColor(new java.awt.Color(254,254,251));
					((XSSFCellStyle) st4).setFillForegroundColor(color7);
					st4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					st4.setBorderLeft(BorderStyle.THIN);
					st4.setBorderRight(BorderStyle.THIN);
					st4.setBorderTop(BorderStyle.THIN);
					st4.setBorderBottom(BorderStyle.THIN);

					st4.setBottomBorderColor(IndexedColors.RED.getIndex());
					st4.setTopBorderColor(IndexedColors.RED.getIndex());
					st4.setLeftBorderColor(IndexedColors.RED.getIndex());
					st4.setRightBorderColor(IndexedColors.RED.getIndex());
					ttcell.setCellStyle(st4);
					ttcell.setCellValue(String.valueOf(DatePerSum.get(i)));


				}
				else
				{
					XSSFCell    ttcell  = r1.createCell(k);
					CellStyle st4 = book.createCellStyle();
					XSSFColor color7 = new XSSFColor(new java.awt.Color(246,233,217));
					((XSSFCellStyle) st4).setFillForegroundColor(color7);
					st4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					st4.setBorderLeft(BorderStyle.THIN);
					st4.setBorderRight(BorderStyle.THIN);
					st4.setBorderTop(BorderStyle.THIN);
					st4.setBorderBottom(BorderStyle.THIN);

					st4.setBottomBorderColor(IndexedColors.RED.getIndex());
					st4.setTopBorderColor(IndexedColors.RED.getIndex());
					st4.setLeftBorderColor(IndexedColors.RED.getIndex());
					st4.setRightBorderColor(IndexedColors.RED.getIndex());
					ttcell.setCellStyle(st4);
					ttcell.setCellValue("");
					
				}
			}




			counterOfRowShift++;
		}
	


		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);



		for(int rowInd = 6; rowInd < lastRow+counterOfRowShift; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);



			for(int colInd = 0; colInd < colCount; colInd++) {


				if(colInd!=5) {

					XSSFCell     cell = row.getCell(colInd);
					CellStyle my_style = book.createCellStyle();

					my_style.setBorderLeft(BorderStyle.THIN);
					my_style.setBorderRight(BorderStyle.THIN);
					my_style.setBorderTop(BorderStyle.THIN);
					my_style.setBorderBottom(BorderStyle.THIN);

					my_style.setBottomBorderColor(IndexedColors.RED.getIndex());
					my_style.setTopBorderColor(IndexedColors.RED.getIndex());
					my_style.setLeftBorderColor(IndexedColors.RED.getIndex());
					my_style.setRightBorderColor(IndexedColors.RED.getIndex());

					for(int f=0;f<totRow.size();f++)
					{

						if( String.valueOf(rowInd).equalsIgnoreCase(totRow.get(f).toString()) )
						{

							XSSFColor color7 = new XSSFColor(new java.awt.Color(246,233,217));
							((XSSFCellStyle) my_style).setFillForegroundColor(color7);
							my_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							my_style.setBorderLeft(BorderStyle.THIN);
							my_style.setBorderRight(BorderStyle.THIN);
							my_style.setBorderTop(BorderStyle.THIN);
							my_style.setBorderBottom(BorderStyle.THIN);

							my_style.setBottomBorderColor(IndexedColors.RED.getIndex());
							my_style.setTopBorderColor(IndexedColors.RED.getIndex());
							my_style.setLeftBorderColor(IndexedColors.RED.getIndex());
							my_style.setRightBorderColor(IndexedColors.RED.getIndex());
						}
						else
						{


						}
					}

                     try {
                    	 cell.setCellStyle(my_style);
					} catch (Exception e) {
						
					}
					

					int status=0,counter=0,dot=0;
					Character ch[] = new Character[200];

					String strVal = "";
					try {
						 strVal = cell.getStringCellValue();
					} catch (Exception e) {
						
					}
					
					for(int i=0;i<strVal.length();i++)
					{

						ch[i] = strVal.charAt(i);
						String s =Character.toString(ch[i]);


						if(Character.isDigit(ch[i]))
						{
							counter++;
						}
						if(s.equals("."))
						{
							dot++;
						}
						if(s.equals(""))
						{
							status=1;
						}
					}


					if(status==1)
					{
						CellStyle st2 = book.createCellStyle();
						if(rowInd%2==0) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
							((XSSFCellStyle) st2).setFillForegroundColor(color6);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						else {
							XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
							((XSSFCellStyle) st2).setFillForegroundColor(color2);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						st2.setBorderLeft(BorderStyle.THIN);
						st2.setBorderRight(BorderStyle.THIN);
						st2.setBorderTop(BorderStyle.THIN);
						st2.setBorderBottom(BorderStyle.THIN);

						
						st2.setBottomBorderColor(IndexedColors.RED.getIndex());
						st2.setTopBorderColor(IndexedColors.RED.getIndex());
						st2.setLeftBorderColor(IndexedColors.RED.getIndex());
						st2.setRightBorderColor(IndexedColors.RED.getIndex());
						cell.setCellStyle(st2);
						cell.setCellValue(strVal);
					}


					else if((dot+counter)==strVal.length())
					{
						try {

							Double ds = Double.parseDouble(strVal);


							cell.setCellType(Cell.CELL_TYPE_NUMERIC);

							DataFormat fmt = book.createDataFormat();
							CellStyle st2 = book.createCellStyle();
							st2.setDataFormat(fmt.getFormat("#,##0.00"));

							if(rowInd%2==0) {
								XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
								((XSSFCellStyle) st2).setFillForegroundColor(color6);
								st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							}
							else {
								XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
								((XSSFCellStyle) st2).setFillForegroundColor(color2);
								st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							}
							st2.setBorderLeft(BorderStyle.THIN);
							st2.setBorderRight(BorderStyle.THIN);
							st2.setBorderTop(BorderStyle.THIN);
							st2.setBorderBottom(BorderStyle.THIN);

							st2.setBottomBorderColor(IndexedColors.RED.getIndex());
							st2.setTopBorderColor(IndexedColors.RED.getIndex());
							st2.setLeftBorderColor(IndexedColors.RED.getIndex());
							st2.setRightBorderColor(IndexedColors.RED.getIndex());
							cell.setCellStyle(st2);
							
							cell.setCellValue(ds);
							
						}
						catch (Exception e) {
							
						}

					}
					else
					{
						CellStyle st2 = book.createCellStyle();
						if(rowInd%2==0) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
							((XSSFCellStyle) st2).setFillForegroundColor(color6);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						else {
							XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
							((XSSFCellStyle) st2).setFillForegroundColor(color2);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						st2.setBorderLeft(BorderStyle.THIN);
						st2.setBorderRight(BorderStyle.THIN);
						st2.setBorderTop(BorderStyle.THIN);
						st2.setBorderBottom(BorderStyle.THIN);

						st2.setBottomBorderColor(IndexedColors.RED.getIndex());
						st2.setTopBorderColor(IndexedColors.RED.getIndex());
						st2.setLeftBorderColor(IndexedColors.RED.getIndex());
						st2.setRightBorderColor(IndexedColors.RED.getIndex());
						cell.setCellStyle(st2);
						cell.setCellValue(strVal);
					}

				}
			}


		}

		for(int rowInd = 6; rowInd < lastRow+counterOfRowShift; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);

			for(int cellInd = 5; cellInd <6 ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if(rowInd%2==0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}

				st2.setBorderLeft(BorderStyle.THIN);
				st2.setBorderRight(BorderStyle.THIN);
				st2.setBorderTop(BorderStyle.THIN);
				st2.setBorderBottom(BorderStyle.THIN);

				st2.setBottomBorderColor(IndexedColors.RED.getIndex());
				st2.setTopBorderColor(IndexedColors.RED.getIndex());
				st2.setLeftBorderColor(IndexedColors.RED.getIndex());
				st2.setRightBorderColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(st2);
				cell.setCellValue(strVal);
			}
		}


		
		
		//FOR LAST DATE AMOUNT SUM 
		Double curre=0.0 ;

		XSSFRow rowoffinalDt = sheet.getRow(lastRow+counterOfRowShift-1);

		XSSFCell     cal = rowoffinalDt.getCell(1);
		String celVal = cal.getStringCellValue();

		for(int j=lastRow+counterOfRowShift-1;j>4;j--)
		{
			XSSFRow rowoffinalDte = sheet.getRow(j);
			XSSFCell     ceel = rowoffinalDte.getCell(1);
			String celVa = ceel.getStringCellValue();
			

			if(celVa.equalsIgnoreCase(celVal))
			{
				XSSFCell     ceel2 = rowoffinalDte.getCell(7);
			
				if(ceel2.getCellType()==Cell.CELL_TYPE_NUMERIC)
				{

					
					curre = curre + ceel2.getNumericCellValue();
				}
				else
				{
					
					curre = curre + Double.valueOf(ceel2.getStringCellValue());
				}
				
			}
			else
			{
				break;
			}

		}

		//For creating final row
		XSSFRow finalRow = sheet.createRow(lastRow+counterOfRowShift);


		
		
		//For Styling 0 to 6th column
		for(int k=0;k<7;k++)
		{
			XSSFCell finalDte = finalRow.createCell(k);


			CellStyle sty = book.createCellStyle();

			DataFormat fmt = book.createDataFormat();

			sty.setDataFormat(fmt.getFormat("#,##0.00"));
			
			XSSFColor color7 = new XSSFColor(new java.awt.Color(246,233,217));
			((XSSFCellStyle) sty).setFillForegroundColor(color7);
			sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			sty.setBorderLeft(BorderStyle.THIN);
			sty.setBorderRight(BorderStyle.THIN);
			sty.setBorderTop(BorderStyle.THIN);
			sty.setBorderBottom(BorderStyle.THIN);

			sty.setBottomBorderColor(IndexedColors.RED.getIndex());
			sty.setTopBorderColor(IndexedColors.RED.getIndex());
			sty.setLeftBorderColor(IndexedColors.RED.getIndex());
			sty.setRightBorderColor(IndexedColors.RED.getIndex());
			finalDte.setCellStyle(sty);


		}
		
		
		//For Styling 7 column
		XSSFCell finalD = finalRow.createCell(7);
		finalD.setCellType(Cell.CELL_TYPE_NUMERIC);

		DataFormat fm = book.createDataFormat();
		CellStyle stylee = book.createCellStyle();
		stylee.setDataFormat(fm.getFormat("#,##0.00"));
		XSSFColor color7 = new XSSFColor(new java.awt.Color(254,254,251));
		((XSSFCellStyle) stylee).setFillForegroundColor(color7);
		stylee.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		stylee.setBorderLeft(BorderStyle.THIN);
		stylee.setBorderRight(BorderStyle.THIN);
		stylee.setBorderTop(BorderStyle.THIN);
		stylee.setBorderBottom(BorderStyle.THIN);

		stylee.setBottomBorderColor(IndexedColors.RED.getIndex());
		stylee.setTopBorderColor(IndexedColors.RED.getIndex());
		stylee.setLeftBorderColor(IndexedColors.RED.getIndex());
		stylee.setRightBorderColor(IndexedColors.RED.getIndex());
		finalD.setCellStyle(stylee);
		finalD.setCellStyle(stylee);
		finalD.setCellValue(curre);

		
		
		//For Styling 8 to 11 column
		for(int k=8;k<11;k++)
		{
			XSSFCell finalDt = finalRow.createCell(k);
			CellStyle sty = book.createCellStyle();
		
			XSSFColor color8 = new XSSFColor(new java.awt.Color(246,233,217));
			((XSSFCellStyle) sty).setFillForegroundColor(color8);
			sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			sty.setBorderLeft(BorderStyle.THIN);
			sty.setBorderRight(BorderStyle.THIN);
			sty.setBorderTop(BorderStyle.THIN);
			sty.setBorderBottom(BorderStyle.THIN);

			sty.setBottomBorderColor(IndexedColors.RED.getIndex());
			sty.setTopBorderColor(IndexedColors.RED.getIndex());
			sty.setLeftBorderColor(IndexedColors.RED.getIndex());
			sty.setRightBorderColor(IndexedColors.RED.getIndex());
			finalDt.setCellStyle(sty);
		}

	}




	public void checkcancelOTP()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String schoolid = selectedstudent.getSchid();
		SchoolInfoList lst=obj.fullSchoolInfo(schoolid,conn);
		////(lst.getCancalfee());
		if(lst.getCancalfee().equalsIgnoreCase("yes"))
		{
			int randomPIN = (int) (Math.random() * 9000) + 1000;
			otp = String.valueOf(randomPIN);
			discoutnNo = lst.getDiscountOtpNo();
			String typemessage = "Hello Sir, \nSomeone wants to give DISCOUNT while collecting fee.Use given OTP ("
					+ randomPIN + ") to allow for Cancel Fee.Treat this as confidential.Thank You.  \n"
					+ lst.getSchoolName();
			obj.messageurlStaff(lst.getDiscountOtpNo(), typemessage, "admin", conn,schoolid,"");
			check=true;
		}
		else
		{
			check=false;
		}

	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList = obj.searchStudentList(query,conn);
		List<String> studentListt = new ArrayList<>();

		for (StudentInfo info : studentList)
		{
			//studentListt.add(info.getFname() + "-" + info.getAddNumber());
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	/*public void update()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i =new DatabaseMethods1().udpatefees(feesSelected,conn);
		showReport();
	}
	 */

	public void duplicateFee()
	{
		HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		rr.setAttribute("selectedStudent", selectedstudent);
		rr.setAttribute("type1", "duplicate");
		rr.setAttribute("chaqueDate", selectedstudent.getChallanDate());
		rr.setAttribute("paymentmode", selectedstudent.getPaymentmode());
		rr.setAttribute("bankname", selectedstudent.getBankname());
		rr.setAttribute("chequeno",selectedstudent.getChequenumber());
		rr.setAttribute("remark", selectedstudent.getFeeRemark());
		rr.setAttribute("type1","duplicate");
		rr.setAttribute("receiptNumber", String.valueOf(selectedstudent.getReciptno()));
		rr.setAttribute("feeupto", selectedstudent.getDueDateStr());
		ArrayList<FeeInfo> selectedFees=new ArrayList<>();
		int i=1;
		//HashMap<String, String>hasmap=selectedstudent.getAllFess();
		Connection conn = DataBaseConnection.javaConnection();
		feelist=obj.viewFeeList1(selectedstudent.getSchid(),conn);

		for(FeeInfo ff:feelist)
		{
			String fee=selectedstudent.getAllFess().get(ff.getFeeId());
			String disc=selectedstudent.getAllDiscount().get(ff.getFeeId());
			String totalAmt=selectedstudent.getAllTotalAmount().get(ff.getFeeId());
			if(fee==null)
			{

			}
			else
			{

				FeeInfo info=new FeeInfo();
				info.setSno(i);
				info.setFeeName(ff.getFeeName());
				info.setPayAmount(Integer.parseInt(fee));
				info.setPayDiscount(Integer.valueOf(disc));
				info.setDueamount(totalAmt);
				selectedFees.add(info);
				i=i+1;

			}
		}



		rr.setAttribute("selectedFee", selectedFees);
		rr.setAttribute("receiptDate", selectedstudent.getFeedate());
		PrimeFaces.current().executeInitScript("window.open('masterFeeReceipt.xhtml')");

	}


	public void editFee()
	{
		/*HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		rr.setAttribute("selectedStudent", selectedstudent);
		rr.setAttribute("type1", "duplicate");
		rr.setAttribute("chaqueDate", selectedstudent.getChallanDate());
		rr.setAttribute("paymentmode", selectedstudent.getPaymentmode());
		rr.setAttribute("bankname", selectedstudent.getBankname());
		rr.setAttribute("chequeno",selectedstudent.getChequenumber());
		rr.setAttribute("remark", selectedstudent.getFeeRemark());
		rr.setAttribute("type1","duplicate");
		rr.setAttribute("receiptNumber", String.valueOf(selectedstudent.getReciptno()));
		rr.setAttribute("feeupto", selectedstudent.getDueDateStr());*/
		feesSelected=new ArrayList<>();
		//HashMap<String, String>hasmap=selectedstudent.getAllFess();
		Connection conn = DataBaseConnection.javaConnection();
		// feelist=obj.viewFeeList1(selectedstudent.getSchid(),conn);

		feesSelected=obj.studetFeeCollectionByRecipietNo(selectedstudent.getStudentid(),selectedstudent.getReciptno(),selectedstudent.getSchid(),conn);

		/*  for(FeeInfo ff:feelist)
	    {
		    	String fee=selectedstudent.getAllFess().get(ff.getFeeId());
		    	String disc=selectedstudent.getAllDiscount().get(ff.getFeeId());
		    	String totalAmt=selectedstudent.getAllTotalAmount().get(ff.getFeeId());
		    	if(fee==null)
		    	{

		    	}
		    	else
		    	{

		    		FeeInfo info=new FeeInfo();
		    		info.setSno(i);
		    		info.setFeeName(ff.getFeeName());
	            	info.setPayAmount(Integer.parseInt(fee));
	            	info.setPayDiscount(Integer.valueOf(disc));
	            	info.setDueamount(totalAmt);
	            	feesSelected.add(info);
	            	i=i+1;

		    	}
	    }
		 */

		//(feesSelected.size());


		/*	rr.setAttribute("selectedFee", selectedFees);
		rr.setAttribute("receiptDate", selectedstudent.getFeedate());
		PrimeFaces.current().executeInitScript("window.open('masterFeeReceipt.xhtml')");*/

	}

	public void searchStudentByName()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=obj;

		feelist=DBM.viewFeeList1(DBM.schoolId(),conn);

		dailyfee=new ArrayList<>();
		//int registfee = 0,annualfee=0,tutionfee=0,transportfee=0,eaxmfee =0,artFee=0,termfee=0;
		count=1;cashAmount=0;chequeAmount=0;tamount=tdiscount=0;
		/*int index = name.indexOf("-") + 1;
		String id = name.substring(index);*/
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);

		getdailyfeecollection = DBM.getduplicatefeedetail(id,DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),conn);
		ArrayList<String> temp = new ArrayList<>();
		for (Feecollectionc mm : getdailyfeecollection)
		{
			temp.add(mm.getRecipietNo());
		}

		String a[] = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++)
		{
			a[i] = temp.get(i);
		}
		a = removeDuplicates(a);

		String value="";
		ArrayList<String>tempdeatils;

		for (int i = 0; i < a.length; i++)
		{
			tempdeatils=new ArrayList<>();
			HashMap<String, String> feecllection=new HashMap<>();
			HashMap<String, String> discountMap=new HashMap<>();
			HashMap<String, String> totalAmountMap=new HashMap<>();

			int totalamuont=0,totalDiscount=0;
			DailyFeeCollectionBean ll = new DailyFeeCollectionBean();
			ArrayList<String> tempList=objStd.basicFieldsForStudentList();
			
			for (Feecollectionc list : getdailyfeecollection)
			{
				if (list.getRecipietNo().equals(String.valueOf(a[i])))
				{

					ll.setFeedate(list.getFeedate());
					ll.setReciptno(list.getRecipietNo());
					ll.setFeeDateStr(sdf.format(list.getFeedate()));
					String feetype=list.getFeeName();
					ll.setDueDateStr(list.getDueDateStr());
					ll.setBankname(list.getBankname());
					ll.setChequenumber(list.getChequeno());
					feecllection.put(feetype, String.valueOf(list.getFeeamunt()));
					discountMap.put(feetype, String.valueOf(list.getDiscount()));
					totalAmountMap.put(feetype, String.valueOf(list.getTotalAmount()));
					ll.setAllFess(feecllection);
					ll.setAllDiscount(discountMap);
					ll.setAllTotalAmount(totalAmountMap);
					StudentInfo info=objStd.studentDetail(id,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, DBM.schoolId(), tempList, conn).get(0);
					ll.setSrNo(info.getSrNo());
					ll.setStudentname(info.getFname());
					ll.setUser(list.getUser());
					ll.setClassname(info.getClassName().substring(0,info.getClassName().indexOf("-")));
					ll.setSection(info.getSectionName());
					ll.setFathername(info.getFathersName());
					ll.setFeeRemark(list.getFeeRemark());
					ll.setMothername(info.getMotherName());
					ll.setChallanDate(list.getChallanDate());
					if(tempdeatils.size()==0)
					{

						value="orignal";
					}
					else
					{
						for(String ls:tempdeatils)
						{
							if(ls.equals(feetype))
							{
								value="duplicate";
								break;
							}
							else
							{
								value="orignal";
							}
						}

					}

					if(value.equals("orignal"))
					{
						tempdeatils.add(feetype);
						totalamuont+=list.getFeeamunt();
						totalDiscount+=list.getDiscount();
					}

					ll.setAmount(String.valueOf(totalamuont));
					ll.setDiscount(String.valueOf(totalDiscount));
					ll.setPaymentmode(list.getPaymentmode());
					ll.setSrno(count);

				}


			}
			dailyfee.add(ll);
			count++;
			//registfee = 0; annualfee = 0; transportfee = 0; eaxmfee = 0;termfee = 0; artFee = 0;
			show = true;
		}
		/*Collections.sort(dailyfee);
		 for(int i=0;i<dailyfee.size();i++)
		 {
			dailyfee.get(i).setSrno(i+1);
		 }*/
		if(dailyfee.size()>0)
		{
			for(DailyFeeCollectionBean info:dailyfee)
			{
				if(info.getPaymentmode().equalsIgnoreCase("Cash"))
				{
					cashAmount+=Integer.parseInt(info.getAmount());
				}
				else
				{
					chequeAmount+=Integer.parseInt(info.getAmount());
				}
				tdiscount+=Integer.parseInt(info.getDiscount());
			}
			tamount=cashAmount+chequeAmount;
		}

		date=new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String[] removeDuplicates(String[] arr)
	{
		Set<String> alreadyPresent = new HashSet<>();
		String[] whitelist = new String[0];
		for (String nextElem : arr)
		{
			if (!alreadyPresent.contains(nextElem)) {
				whitelist = Arrays.copyOf(whitelist, whitelist.length + 1);
				whitelist[whitelist.length - 1] = nextElem;
				alreadyPresent.add(nextElem);
			}
		}
		return whitelist;
	}

	public void cancelReceipt()
	{
		String schoolid = selectedstudent.getSchid();
		Connection conn = DataBaseConnection.javaConnection();
		boolean checkotp=false;
		if(check==true)
		{
			if(otp.equalsIgnoreCase(otpInput))
			{
				checkotp=false;
			}
			else
			{
				checkotp=true;
			}
		}

		if(checkotp==false) {

			int i=obj.cancelReceipt(schoolid,selectedstudent.getReciptno(),remark,conn);
			if(i>=1)
			{
				int prevFee = Integer.valueOf(selectedstudent.getAllFess().get("-1"));
				StudentInfo info=new StudentInfo();
				info.setAddNumber(selectedstudent.getStudentid());
				int paidPrevFee = obj.FeePaidRecord(obj.schoolId(),info, session, "-1",conn);
				//int paidPrevFee = obj.previousFeePaidRecord(schoolid,selectedstudent.getStudentid(), conn);

				//obj.cancelPaidPreviousFee(schoolid,selectedstudent.getStudentid(),newPrevFee,conn);
				////("Prev Fee Paid : "+prevFee);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Receipt Cancelled Sucessfully"));
				show=false;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured"));
			}

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Not Matched"));

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void schoolInfo(String schid)
	{
		String savePath="";
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(schid,conn);
		schname=info.getSchoolName();
		address1=info.getAdd1();
		address2=info.getAdd2();
		address3=info.getAdd3();
		address4=info.getAdd4();
		phoneno=info.getPhoneNo();
		mobileno=info.getMobileNo();
		website=info.getWebsite();
		type=info.getClient_type();
		if(type.equalsIgnoreCase("institute"))
		{
			type="Institute";
		}
		else if(type.equalsIgnoreCase("school"))
		{
			type="School";
		}


		if(info.getProjecttype().equals("online"))
		{
			String folderName=info.getDownloadpath();
			savePath=folderName;
		}

		logo=savePath+info.getImagePath();
		headerImagePath=savePath+info.getMarksheetHeader();
		regno=info.getRegNo();
		finalAddress=address1;

		if(address2==null || address2.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address2;
		}

		if(address3==null || address3.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address3;
		}

		affiliationNo=address4;



		/*name="Dynamic Public School";
		address1="Alwar 301001";
		address2="Govt. Recognized";
		//address3="Run By : Smart Education & Social welfare Society (Reg. No. 143 Alwar 2009-10)";
		address4="IMG GLOBAL INFOTECH,Jai Complex, Alwar";*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}

	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}

	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public Date getFeedate() {
		return feedate;
	}
	public void setFeedate(Date feedate) {
		this.feedate = feedate;
	}
	public ArrayList<Feecollectionc> getGetdailyfeecollection() {
		return getdailyfeecollection;
	}
	public void setGetdailyfeecollection(
			ArrayList<Feecollectionc> getdailyfeecollection) {
		this.getdailyfeecollection = getdailyfeecollection;
	}
	public ArrayList<DailyFeeCollectionBean> getDailycollection() {
		return dailycollection;
	}
	public void setDailycollection(ArrayList<DailyFeeCollectionBean> dailycollection) {
		this.dailycollection = dailycollection;
	}
	public ArrayList<DailyFeeCollectionBean> getDailyfee() {
		return dailyfee;
	}
	public void setDailyfee(ArrayList<DailyFeeCollectionBean> dailyfee) {
		this.dailyfee = dailyfee;
	}
	public ArrayList<DailyFeeCollectionBean> getFeedetail() {
		return feedetail;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public void setFeedetail(ArrayList<DailyFeeCollectionBean> feedetail) {
		this.feedetail = feedetail;
	}
	public String getDate() {
		return date;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public DailyFeeCollectionBean getSelectedstudent() {
		return selectedstudent;
	}
	public void setSelectedstudent(DailyFeeCollectionBean selectedstudent) {
		this.selectedstudent = selectedstudent;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public double getTamount() {
		return tamount;
	}

	public void setTamount(double tamount) {
		this.tamount = tamount;
	}

	public double getTdiscount() {
		return tdiscount;
	}

	public void setTdiscount(double tdiscount) {
		this.tdiscount = tdiscount;
	}

	public String getSelectedOperator() {
		return selectedOperator;
	}

	public void setSelectedOperator(String selectedOperator) {
		this.selectedOperator = selectedOperator;
	}

	public ArrayList<SelectItem> getOperatorList() {
		return operatorList;
	}

	public void setOperatorList(ArrayList<SelectItem> operatorList) {
		this.operatorList = operatorList;
	}

	public double getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(double cashAmount) {
		this.cashAmount = cashAmount;
	}

	public double getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(double chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public String getCashAmountString() {
		return cashAmountString;
	}

	public void setCashAmountString(String cashAmountString) {
		this.cashAmountString = cashAmountString;
	}

	public String getCheckAmountString() {
		return checkAmountString;
	}

	public void setCheckAmountString(String checkAmountString) {
		this.checkAmountString = checkAmountString;
	}

	public String getTotalamountString() {
		return totalamountString;
	}

	public void setTotalamountString(String totalamountString) {
		this.totalamountString = totalamountString;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getOtpInput() {
		return otpInput;
	}

	public void setOtpInput(String otpInput) {
		this.otpInput = otpInput;
	}

	public String getDiscoutnNo() {
		return discoutnNo;
	}

	public void setDiscoutnNo(String discoutnNo) {
		this.discoutnNo = discoutnNo;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getFeetype() {
		return feetype;
	}

	public void setFeetype(String feetype) {
		this.feetype = feetype;
	}

	public String getStudentclass() {
		return studentclass;
	}

	public void setStudentclass(String studentclass) {
		this.studentclass = studentclass;
	}

	public String getStudentname() {
		return studentname;
	}

	public void setStudentname(String studentname) {
		this.studentname = studentname;
	}

	public String getFathername() {
		return fathername;
	}

	public void setFathername(String fathername) {
		this.fathername = fathername;
	}

	public static int getCount() {
		return count;
	}

	public static void setCount(int count) {
		MasterMiniDailyFeeCollectionBean.count = count;
	}

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}

	public DatabaseMethods1 getObj() {
		return obj;
	}

	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}

	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

	public boolean isShowClass() {
		return showClass;
	}

	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}

	public String getSchname() {
		return schname;
	}

	public void setSchname(String schname) {
		this.schname = schname;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getFinalAddress() {
		return finalAddress;
	}

	public void setFinalAddress(String finalAddress) {
		this.finalAddress = finalAddress;
	}

	public String getAffiliationNo() {
		return affiliationNo;
	}

	public void setAffiliationNo(String affiliationNo) {
		this.affiliationNo = affiliationNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHeaderImagePath() {
		return headerImagePath;
	}

	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
	}

	public String getRegno() {
		return regno;
	}

	public void setRegno(String regno) {
		this.regno = regno;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getBranches() {
		return branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	public boolean isShowSchool() {
		return showSchool;
	}

	public void setShowSchool(boolean showSchool) {
		this.showSchool = showSchool;
	}

	public ArrayList<DailyFeeCollectionBean> getOnlineFeeCollecton() {
		return onlineFeeCollecton;
	}

	public void setOnlineFeeCollecton(ArrayList<DailyFeeCollectionBean> onlineFeeCollecton) {
		this.onlineFeeCollecton = onlineFeeCollecton;
	}

	public ArrayList<Feecollectionc> getFeesSelected() {
		return feesSelected;
	}

	public void setFeesSelected(ArrayList<Feecollectionc> feesSelected) {
		this.feesSelected = feesSelected;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public ArrayList<SelectItem> getInstallmentList() {
		return installmentList;
	}

	public void setInstallmentList(ArrayList<SelectItem> installmentList) {
		this.installmentList = installmentList;
	}

	public String[] getCheckMonthSelected() {
		return checkMonthSelected;
	}

	public void setCheckMonthSelected(String[] checkMonthSelected) {
		this.checkMonthSelected = checkMonthSelected;
	}
	




}
