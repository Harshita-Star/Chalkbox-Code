
package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="printFeeCertificateForBLM")
@ViewScoped
public class PrintFeeCertificateForBLMBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String addNumber,date1,year,headerImage,session,website,studentName,affNo,board,check="",branches;
	String checkMonth[];
	StudentInfo studentInfo;
	String currentDate=new SimpleDateFormat("dd MMM yyyy").format(new Date());
	boolean showPic,showStatic;
	SchoolInfoList schinfo;
	ArrayList<FeeInfo> list,feeList;
	int totalPaidAmount;
	String type="";
	public PrintFeeCertificateForBLMBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		addNumber=(String) ss.getAttribute("selectedStudent");
		checkMonth=(String[]) ss.getAttribute("months");
		String schid=(String) ss.getAttribute("schId");
		schinfo=new DatabaseMethods1().fullSchoolInfo(schid,conn);
		affNo=schinfo.getAdd4();
		session=(String) ss.getAttribute("session");
		board=schinfo.getBoardType().toUpperCase();
		studentName="";

		DatabaseMethods1 obj=new DatabaseMethods1();

		feeList=new ArrayList<>();
		studentInfo=obj.studentDetailslistByAddNo(obj.schoolId(),addNumber,conn);
		totalPaidAmount=0;
		list=obj.viewFeeList(conn);
		int i=1;
		int feePaidAmount=0;
		for(FeeInfo ls:list)
		{
			if(!ls.getFeeId().equals("-1"))
			{
				feePaidAmount=0;
				for(String month:checkMonth)
				{
					feePaidAmount+= new DatabaseMethods1().FeePaidByFeeIdForCheckBLM(schid,ls.getFeeId(),addNumber,session, month,conn);
				}

				if(feePaidAmount>0)
				{
					FeeInfo ns=new FeeInfo();
					ns.setFeeName(ls.getFeeName());
					ns.setPayAmount(feePaidAmount);
					ns.setSno(i);
					i++;
					if(ls.getFeeId().equals("-2"))
					{
						
					}
					else
					{
						feeList.add(ns);
					}
				}

				totalPaidAmount=totalPaidAmount+feePaidAmount;

			}

		}
		if(studentInfo.gender.equalsIgnoreCase("male"))
		{
			studentInfo.setFname("Master "+studentInfo.getFname()+" son");
			check="His particulars as per school records are";
			type="He";
		}
		else
		{
			studentInfo.setFname("Miss "+studentInfo.getFname()+" daughter");
			check="Her particulars as per school records are";
			type="She";
		}
		String savePath="";
		if(schinfo.getProjecttype().equals("online"))
		{
			savePath=schinfo.getDownloadpath();
		}
		headerImage=savePath+schinfo.getMarksheetHeader();

		website=schinfo.getWebsite();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String transform(int num)
	{
		String  one[]={" "," One"," Two"," Three"," Four"," Five"," Six"," Seven"," Eight"," Nine"," Ten"," Eleven"," Twelve"," Thirteen"," Fourteen"," Fifteen"," Sixteen"," Seventeen"," Eighteen"," Nineteen"};
		String ten[]={" "," "," Twenty"," Thirty"," Forty"," Fifty"," Sixty","Seventy"," Eighty"," Ninety"};
		if(num<=99)
		{
			if(num<=19) {
				date1=one[num];
			}
			else {
				int num1=num/10;
				int num2=num%10;
				date1=ten[num1]+one[num2];
			}
		}
		return date1;
	}

	public SchoolInfoList getSchinfo() {
		return schinfo;
	}

	public void setSchinfo(SchoolInfoList schinfo) {
		this.schinfo = schinfo;
	}

	public String year(int num)
	{
		String  one[]={" "," One "," Two "," Three "," Four "," Five "," Six "," Seven "," Eight "," Nine "," Ten "," Eleven "," Twelve "," Thirteen "," Fourteen "," Fifteen "," Sixteen "," Seventeen "," Eighteen "," Nineteen "};
		String ten[]={" "," "," Twenty "," Thirty "," Forty "," Fifty "," Sixty ","Seventy "," Eighty "," Ninety "};

		if(num>=1000 && num<=9999){
			int num1=num/1000;
			int num2=num%1000;
			if(num2<=99){
				if(num2<=19) {
					year=one[num1]+"Thousand"+one[num2];

				}
				else {
					int num4=num2/10;
					int num5=num2%10;
					year=one[num1]+"Thousand"+ten[num4]+one[num5];
				}
			}
			else{
				int num6=num2/100;
				int num7=num2%100;
				if(num7<=19){
					String num8=one[num7];
					year=one[num1]+"Thousand"+one[num6]+"Hundred"+num8;
				}
				else {
					int num0=num7/10;
					int num9=num7%10;
					year=one[num1]+"Thousand"+one[num6]+"Hundred"+ten[num0]+one[num9];
				}
			}
		}
		return year;
	}
	public String getAddNumber() {
		return addNumber;
	}
	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}
	public StudentInfo getStudentInfo() {
		return studentInfo;
	}
	public void setStudentInfo(StudentInfo studentInfo) {
		this.studentInfo = studentInfo;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public boolean isShowPic() {
		return showPic;
	}

	public void setShowPic(boolean showPic) {
		this.showPic = showPic;
	}

	public boolean isShowStatic() {
		return showStatic;
	}

	public void setShowStatic(boolean showStatic) {
		this.showStatic = showStatic;
	}

	public String getAffNo() {
		return affNo;
	}

	public void setAffNo(String affNo) {
		this.affNo = affNo;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}
	public ArrayList<FeeInfo> getFeeList() {
		return feeList;
	}

	public void setFeeList(ArrayList<FeeInfo> feeList) {
		this.feeList = feeList;
	}

	public int getTotalPaidAmount() {
		return totalPaidAmount;
	}

	public void setTotalPaidAmount(int totalPaidAmount) {
		this.totalPaidAmount = totalPaidAmount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
