package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.Age;
import schooldata.AgeWiseReport;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodsIncome_Expense;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.RegexPattern;

@ManagedBean (name="receiveBook")
@ViewScoped
public class ReceiveBookBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String studentName,bookId,bookName,remark,penaltySetting,receiveAmount="0",discount="0";
	Date addDate=new Date();
	double penaltyAmount,penaltyDays;
	ArrayList<BookInfo> bookList;
	ArrayList<BookInfo> selectedBook;
	BookInfo bookInfo;
	ArrayList<StudentInfo> studentList;
	ArrayList<String> bookIdList;
	DataBaseMethodsLibraryModule objLibrary= new DataBaseMethodsLibraryModule();
	boolean showTable,books=false,showPanel,showPenalty;
	Date expireDate;
	String totalPenaltyAmount,totalDays,charges;
	StudentInfo info = new StudentInfo();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsLibraryModule dbl = new DataBaseMethodsLibraryModule();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
    String schid; 


	public ReceiveBookBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
        schid = obj.schoolId();
		SchoolInfoList info=obj.fullSchoolInfo(conn);
		penaltySetting=info.getPenaltySetting();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteStudent(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=obj.searchStudentTeacherList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();
		for(StudentInfo info : studentList)
		{
			if(info.getStudentType().equals("Student"))
				studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber()+"/Student");
			else
				studentListt.add(info.getFname()+"-"+info.getAddNumber()+"/"+"Teacher");
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public ArrayList<String> autoCompleteBookId(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		bookIdList=objLibrary.bookIdForAutoCompleteByAssignBook(query,conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bookIdList;
	}

	public void checkBookIdSelected()
	{
		Connection conn=DataBaseConnection.javaConnection();
		BookInfo ll=objLibrary.bookInfoBySubBookId(bookId,conn);
		bookName=ll.getBookName()+ " - "+ll.getAuthorName()+" - "+ll.getPublicationName()+" - "+ll.getArticleId();
		info=objLibrary.studentInfoFromAssignBookByBookId(bookId,conn);
		if(info.getStudentType().equals("Student"))
		{
			studentName=info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber()+"/Student";
		}
		else
		{
			studentName=info.getFname()+"-"+info.getAddNumber()+"/"+"Teacher";
		}

		String studentId=studentName.substring(studentName.lastIndexOf("-")+1,studentName.lastIndexOf("/"));
		expireDate=objLibrary.expireDateOfBookIdByStudent(bookId,studentId,conn);
		if(expireDate.before(addDate))
		{
			double latefees=objLibrary.getlatefees(conn);
			Age a= new AgeWiseReport().ageCalculator(expireDate, addDate);
			penaltyDays=(a.getYears()*12*30)+(a.getMonths()*30)+(a.getDays());
			penaltyAmount=Double.valueOf(penaltyDays)*latefees;

			if(penaltyAmount>0)
			{
				totalPenaltyAmount=String.valueOf(penaltyAmount);
				totalDays=String.valueOf(penaltyDays);
				showPenalty=true;
			}
			else
			{
				showPenalty=false;
			}
		}
		else
		{
			penaltyDays=0;
			penaltyAmount=0;
			totalPenaltyAmount=totalDays="0";
		}
		bookInfo=new BookInfo();
		bookInfo.setId(String.valueOf(info.getId()));
		bookInfo.setBookId(bookId);
		bookInfo.setPenaltyAmount(totalPenaltyAmount);
		bookInfo.setPenalty(totalDays);
		bookInfo.setBookName(bookName.split("-")[0]);
		
		showPanel=true;
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String studentId=studentName.substring(studentName.lastIndexOf("-")+1,studentName.lastIndexOf("/"));

		bookList=objLibrary.assignedBookListOfStudent(schid,studentId,conn);
		double latefees=objLibrary.getlatefees(conn);

		if(bookList.size()>0)
		{
			for(BookInfo ll:bookList)
			{
				if(ll.getExpiringDate().before(addDate))
				{
					if(penaltySetting.equalsIgnoreCase("yes"))
					{
						Age a= new AgeWiseReport().ageCalculator(ll.expiringDate, addDate);
						double a1=(a.getYears()*12*30)+(a.getMonths()*30)+(a.getDays());
						ll.setPenalty(String.valueOf(a1));
						ll.setPenaltyAmount(String.valueOf((Double.valueOf(a1)*latefees)));
					}
					else
					{
						ll.setPenalty("0");
						ll.setPenaltyAmount("0");
					}
				}
				else
				{
					ll.setPenalty("0");
					ll.setPenaltyAmount("0");
				}
			}
			showTable=true;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Records Found"));
			showTable=false;
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void preDiscardDialog()
	{
		if(bookInfo!=null)
		{
			totalPenaltyAmount=totalDays="0";
			totalPenaltyAmount=bookInfo.getPenaltyAmount();
			totalDays=bookInfo.getPenalty();
			if(Double.parseDouble(totalPenaltyAmount)>0 && penaltySetting.equals("Yes"))
			{
				showPenalty=true;
			}
			else
			{
				showPenalty=false;
			}
		}
		else
		{
			showPenalty=false;
		}
	}

	public void discardBook()
	{
		String status="discard";
		Connection conn=DataBaseConnection.javaConnection();
		String studentId=studentName.substring(studentName.lastIndexOf("-")+1,studentName.lastIndexOf("/"));

		if(discount.equals(""))
			discount="0";
		if(receiveAmount.equals(""))
			receiveAmount="0";

		if((Integer.parseInt(receiveAmount)+Integer.parseInt(discount))< Double.parseDouble(totalPenaltyAmount) && penaltySetting.equals("Yes"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Clear PenaltyAmount"));
		}
		else if(Double.parseDouble(discount)>Double.parseDouble(totalPenaltyAmount))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Discount can not be greater than penalty amount"));
		}
		else
		{
			int i=objLibrary.discardBook(studentId,bookInfo,addDate,charges,remark,totalDays,totalPenaltyAmount,receiveAmount,discount,status,conn);
			if(i==1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn,studentId,status);
				} catch (Exception e) {
					e.printStackTrace();
				}
				PrimeFaces.current().ajax().update("form");
				PrimeFaces.current().ajax().update("discardForm");

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Discarded Successfully"));
				String arr[] = studentName.split("/");
				String type = arr[arr.length-1];
				
				if(type.equalsIgnoreCase("Student"))
				{
					StudentInfo info=DBJ.studentDetailslistByAddNo(studentId,schid,conn);
					if(info.getFathersPhone()==info.getMothersPhone())
					{
						DBJ.notification("Library","A book - "+bookInfo.getBookName().trim()+" has been discarded or lost by your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
					}
					else
					{
						DBJ.notification("Library","A book - "+bookInfo.getBookName().trim()+" has been discarded or lost by your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
						DBJ.notification("Library","A book - "+bookInfo.getBookName().trim()+" has been discarded or lost by your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
					}
				}
				else
				{
					DBJ.adminnotification("Library","A book - "+bookInfo.getBookName().trim()+" has been discarded or lost by you. Please make sure to clear the penalty (If any. Please ignore,if paid).","staff"+"-"+studentId+"-"+schid,schid,"BookAssignStaff-"+studentId,conn);
				}
				
				showTable=false;bookList=new ArrayList<>();
				showPanel=false;bookId=studentName=studentId=bookName="";
				penaltyAmount=penaltyDays=0;receiveAmount=discount="0";showPenalty=false;charges="0";remark="";totalPenaltyAmount=totalDays="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
			}
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn,String studentId,String status)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(addDate);
		
	    value = "StudentId-"+studentId+" --Book-"+bookInfo.getBookId()+" --Add date-"+addDt+" --Charges-"+charges+" --remark-"+remark+" --Totaldays-"+totalDays+" --Total Penalty Amount-"+totalPenaltyAmount+" --Receive Amount-"+receiveAmount+" --Discount-"+discount+" --Status-"+status;
		
		String refNo = workLg.saveWorkLogMehod(language,"Discard Book","WEB",value,conn);
		return refNo;
	}


	public void damageBook()
	{
		if(discount.equals(""))
			discount="0";
		if(receiveAmount.equals(""))
			receiveAmount="0";

		String status="damage";
		Connection conn=DataBaseConnection.javaConnection();
		String studentId=studentName.substring(studentName.lastIndexOf("-")+1,studentName.lastIndexOf("/"));

		if((Integer.parseInt(receiveAmount)+Integer.parseInt(discount))< Double.parseDouble(totalPenaltyAmount) && penaltySetting.equals("Yes"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Clear PenaltyAmount"));
		}
		else if(Double.parseDouble(discount)>Double.parseDouble(totalPenaltyAmount))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Discount can not be greater than penalty amount"));
		}
		else
		{
			int i=objLibrary.discardBook(studentId,bookInfo,addDate,charges,remark,totalDays,totalPenaltyAmount,receiveAmount,discount,status,conn);
			if(i==1)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn,studentId,status);
				} catch (Exception e) {
					e.printStackTrace();
				}

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Damaged Successfully"));
				String arr[] = studentName.split("/");
				String type = arr[arr.length-1];
				
				if(type.equalsIgnoreCase("Student"))
				{
					StudentInfo info=DBJ.studentDetailslistByAddNo(studentId,schid,conn);
					if(info.getFathersPhone()==info.getMothersPhone())
					{
						DBJ.notification("Library","A book - "+bookInfo.getBookName().trim()+" has been damaged by your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
					}
					else
					{
						DBJ.notification("Library","A book - "+bookInfo.getBookName().trim()+" has been damaged by your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
						DBJ.notification("Library","A book - "+bookInfo.getBookName().trim()+" has been damaged by your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
					}
				}
				else
				{
					DBJ.adminnotification("Library","A book - "+bookInfo.getBookName().trim()+" has been damaged by you. Please make sure to clear the penalty (If any. Please ignore,if paid).","staff"+"-"+studentId+"-"+schid,schid,"BookAssignStaff-"+studentId,conn);
				}
				
				showTable=false;bookList=new ArrayList<>();
				showPanel=false;bookId=studentName=studentId=bookName="";
				penaltyAmount=penaltyDays=0;receiveAmount=discount="0";showPenalty=false;charges="0";remark="";totalPenaltyAmount=totalDays="";

				PrimeFaces.current().ajax().update("form1");
				PrimeFaces.current().ajax().update("form");
				PrimeFaces.current().ajax().update("damageForm");


			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
			}
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog2(Connection conn,String studentId,String status)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(addDate);
		
	    value = "StudentId-"+studentId+" --Book-"+bookInfo.getBookId()+" --Add date-"+addDt+" --Charges-"+charges+" --remark-"+remark+" --Totaldays-"+totalDays+" --Total Penalty Amount-"+totalPenaltyAmount+" --Receive Amount-"+receiveAmount+" --Discount-"+discount+" --Status-"+status;
		
		String refNo = workLg.saveWorkLogMehod(language,"Damage Book","WEB",value,conn);
		return refNo;
	}

	public void preReceiveBook()
	{
		if(selectedBook!=null)
		{
			double totalAmount=0,tDays=0;
			for(BookInfo ll:selectedBook)
			{
				totalAmount+=Double.parseDouble(ll.getPenaltyAmount());
				tDays+=Double.parseDouble(ll.getPenalty());
			}

			totalPenaltyAmount=String.valueOf(totalAmount);
			totalDays=String.valueOf(tDays);
			if(Double.parseDouble(totalPenaltyAmount)>0 && penaltySetting.equals("Yes"))
			{
				showPenalty=true;
				PrimeFaces.current().executeInitScript("PF('penaltyDialog').show()");
				PrimeFaces.current().ajax().update("penaltyForm");
			}
			else
			{
				receiveBook();
			}
		}
	}

	public void receiveBook()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String studentId=studentName.substring(studentName.lastIndexOf("-")+1,studentName.lastIndexOf("/"));
		
		if(selectedBook!=null)
		{
			if(discount.equals(""))
				discount="0";
			if(receiveAmount.equals(""))
				receiveAmount="0";
			if((Integer.parseInt(receiveAmount)+Integer.parseInt(discount))< Double.parseDouble(totalPenaltyAmount) && penaltySetting.equals("Yes"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Clear PenaltyAmount"));
			}
			else if(Double.parseDouble(discount)>Double.parseDouble(totalPenaltyAmount))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Discount can not be greater than penalty amount"));
			}
			else
			{
				int i=0;
				String bnm = "";
				for(BookInfo ll:selectedBook)
				{
					i+=objLibrary.receiveBook(studentId,ll,addDate,totalDays, totalPenaltyAmount, receiveAmount, discount,conn);
					if(i>0)
					{
						String refNo4;
						try {
							refNo4=addWorkLogReceive(conn,studentId,ll);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(Double.parseDouble(receiveAmount)>0 || Double.parseDouble(discount)>0 )
						{
							String taxMonth=String.valueOf(addDate.getMonth()+1);
							String taxYear=String.valueOf(addDate.getYear()+1900);
							String voucherNo=obj.generateVoucherNo("income_table",conn);
							String selectedCategoy=obj.incomeExpenseCategoryIdByName("income_cat","Library Late fee Collection",conn);
							remark="Library Late Fee Collection";
							new DataBaseMethodsIncome_Expense().addIncome(addDate, selectedCategoy, "Cash", receiveAmount, "", "", "", null, remark, "", voucherNo, "0", taxMonth, taxYear, remark, discount,"other",receiveAmount, conn);
							String refNo5;
							try {
								refNo5=addWorkLogAddIncome(conn,selectedCategoy,voucherNo,taxMonth,taxYear);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					if(bnm.equals(""))
					{
						bnm = ll.getBookName();
					}
					else
					{
						bnm = bnm + ", "+ll.getBookName();
					}
				}
				if(i>=1)
				{
					PrimeFaces.current().ajax().update("form");
					PrimeFaces.current().ajax().update("penaltyForm");
					
					String arr[] = studentName.split("/");
					String type = arr[arr.length-1];
					
					if(type.equalsIgnoreCase("Student"))
					{
						StudentInfo info=obj.selectedStudentDetailByAddNo(studentId, conn);
						if(info.getFathersPhone()==info.getMothersPhone())
						{
							DBJ.notification("Library","Book(s) - "+bnm.trim()+" has been received by school library from your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
						}
						else
						{
							DBJ.notification("Library","Book(s) - "+bnm.trim()+" has been received by school library from your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
							DBJ.notification("Library","Book(s) - "+bnm.trim()+" has been received by school library from your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
						}
					}
					else
					{
						DBJ.adminnotification("Library","Book(s) - "+bnm.trim()+" has been received by school library from you. Please make sure to clear the penalty (If any. Please ignore,if paid).","staff"+"-"+studentId+"-"+schid,schid,"BookAssignStaff-"+studentId,conn);
					}

					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Received Successfully"));
					showTable=false;bookList=new ArrayList<>();
					showPanel=false;bookId=studentName=studentId=bookName="";
					penaltyAmount=penaltyDays=0;receiveAmount=discount="0";showPenalty=false;totalPenaltyAmount=totalDays="";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
				}
			}
		}
		else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Book For Return"));
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private String addWorkLogAddIncome(Connection conn, String selectedCategoy, String voucherNo, String taxMonth,
			String taxYear) {
		
		String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(addDate);
		
	    value = "Category-"+selectedCategoy+" --Add Date-"+addDt+" --Receive Amt-"+receiveAmount+" --remark-"+remark+" --VoucherNo-"+voucherNo+" --taxmonth-"+taxMonth+" --taxyear-"+taxYear+" --Discount-"+discount;
		
		String refNo = workLg.saveWorkLogMehod(language,"Receive Book Add Income","WEB",value,conn);
		return refNo;
	}

	private String addWorkLogReceive(Connection conn, String studentId, BookInfo ll) {
		
		String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(addDate);
		
	    value = "StudentId-"+studentId+" --BookId-"+ll.getBookId()+" --Id-"+ll.getId()+" --Add date-"+addDt+" --Totaldays-"+totalDays+" --Total Penalty Amount-"+totalPenaltyAmount+" --Receive Amount-"+receiveAmount+" --Discount-"+discount;
		
		String refNo = workLg.saveWorkLogMehod(language,"Receive Book","WEB",value,conn);
		return refNo;
	}

	public void receiveSingleBook()
	{
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn=DataBaseConnection.javaConnection();
		String studentId=studentName.substring(studentName.lastIndexOf("-")+1);

		BookInfo ll=new BookInfo();
		ll.setBookId(bookId);
		ll.setArticleId(bookName.substring(bookName.lastIndexOf(" - ")+3));
		ll.setId(String.valueOf(info.getId()));

		if(discount.equals(""))
			discount="0";
		if(receiveAmount.equals(""))
			receiveAmount="0";
		
		if((Integer.parseInt(receiveAmount)+Integer.parseInt(discount))< Double.parseDouble(totalPenaltyAmount) && penaltySetting.equals("Yes"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Clear PenaltyAmount"));
		}
		else if(Double.parseDouble(discount)>Double.parseDouble(totalPenaltyAmount))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Discount can not be greater than penalty amount"));
		}
		else
		{
			int i=objLibrary.receiveBook(studentId,ll,addDate,totalDays, totalPenaltyAmount, receiveAmount, discount,conn);
			if(i>=1)
			{
				String refNo6;
				try {
					refNo6=addWorkLogReceive(conn,studentId,ll);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String arr[] = studentName.split("/");
				String type = arr[arr.length-1];
				
				if(type.equalsIgnoreCase("Student"))
				{
					StudentInfo info=DBJ.studentDetailslistByAddNo(studentId,schid,conn);
					if(info.getFathersPhone()==info.getMothersPhone())
					{
						DBJ.notification("Library","A book - "+bookInfo.getBookName().trim()+" has been received by school library from your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
					}
					else
					{
						DBJ.notification("Library","A book - "+bookInfo.getBookName().trim()+" has been received by school library from your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
						DBJ.notification("Library","A book - "+bookInfo.getBookName().trim()+" has been received by school library from your ward "+info.getFname()+". Please make sure to clear the penalty (If any. Please ignore,if paid).", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
					}
				}
				else
				{
					DBJ.adminnotification("Library","A book - "+bookInfo.getBookName().trim()+" has been received by school library from you. Please make sure to clear the penalty (If any. Please ignore,if paid).","staff"+"-"+studentId+"-"+schid,schid,"BookAssignStaff-"+studentId,conn);
				}
				
				String taxMonth=String.valueOf(addDate.getMonth()+1);
				String taxYear=String.valueOf(addDate.getYear()+1900);
				String voucherNo=obj.generateVoucherNo("income_table",conn);
				String selectedCategoy=obj.incomeExpenseCategoryIdByName("income_cat","Library Late fee Collection",conn);

				if(Double.parseDouble(receiveAmount)>0 ||Double.parseDouble(discount)>0 )
				{
					remark="Library Late Fee Collection";
					new DataBaseMethodsIncome_Expense().addIncome(addDate, selectedCategoy, "Cash", receiveAmount, "", "", "", null, remark, "", voucherNo, "0", taxMonth, taxYear, remark, discount,"other",receiveAmount, conn);
					String refNo7;
					try {
						refNo7=addWorkLogAddIncome(conn,selectedCategoy,voucherNo,taxMonth,taxYear);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Received Successfully"));
				showTable=false;bookList=new ArrayList<>();
				showPanel=false;bookId=studentName=studentId=bookName="";
				penaltyAmount=penaltyDays=0;receiveAmount=discount="0";showPenalty=false;totalPenaltyAmount=totalDays="";

				PrimeFaces.current().ajax().update("form1");

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
			}
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public ArrayList<BookInfo> getBookList() {
		return bookList;
	}
	public void setBookList(ArrayList<BookInfo> bookList) {
		this.bookList = bookList;
	}
	public boolean isShowTable() {
		return showTable;
	}
	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public Double getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(Double penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}
	public boolean isBooks() {
		return books;
	}

	public void setBooks(boolean books) {
		this.books = books;
	}

	public ArrayList<BookInfo> getSelectedBook() {
		return selectedBook;
	}

	public void setSelectedBook(ArrayList<BookInfo> selectedBook) {
		this.selectedBook = selectedBook;
	}
	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public boolean isShowPanel() {
		return showPanel;
	}

	public void setShowPanel(boolean showPanel) {
		this.showPanel = showPanel;
	}

	public double getPenaltyDays() {
		return penaltyDays;
	}

	public void setPenaltyDays(double penaltyDays) {
		this.penaltyDays = penaltyDays;
	}

	public void setPenaltyAmount(double penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTotalPenaltyAmount() {
		return totalPenaltyAmount;
	}

	public void setTotalPenaltyAmount(String totalPenaltyAmount) {
		this.totalPenaltyAmount = totalPenaltyAmount;
	}

	public String getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(String totalDays) {
		this.totalDays = totalDays;
	}

	public String getCharges() {
		return charges;
	}

	public void setCharges(String charges) {
		this.charges = charges;
	}

	public boolean isShowPenalty() {
		return showPenalty;
	}

	public void setShowPenalty(boolean showPenalty) {
		this.showPenalty = showPenalty;
	}

	public String getReceiveAmount() {
		return receiveAmount;
	}

	public void setReceiveAmount(String receiveAmount) {
		this.receiveAmount = receiveAmount;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public BookInfo getBookInfo() {
		return bookInfo;
	}

	public void setBookInfo(BookInfo bookInfo) {
		this.bookInfo = bookInfo;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
