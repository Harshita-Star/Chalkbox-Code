package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;

@ManagedBean(name="oldStudentFeeCollection")
@ViewScoped
public class OldStudentFeeCollection implements Serializable
{

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String srNo,name,fathersName,paymentMode="Cash",bankName,className,chequeNumber,remark="",challanNo,neftNo,schoolid="",message="",preSession;
	Date recipietDate=new Date(),dueDate=new Date();
	long cntctNo;
	Date challanDate,neftDate;
	int submitAmount=0,amount;
	StudentInfo sList=new StudentInfo();
	ArrayList<FeeInfo> feeList,selectedFees=new ArrayList<>();
	boolean showPaymentMode, showFeeList,showChallan,showNeft,showCheque;

	public OldStudentFeeCollection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		feeList=new DatabaseMethods1().viewFeeList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void calculateamt()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		int k = (int) UIComponent.getCurrentComponent(context).getAttributes().get("auto");
		submitAmount=0;

		if(selectedFees.size()>0)
		{
			for(FeeInfo ff:selectedFees)
			{
				if(k==ff.getSno())
				{
					ff.setDueamount(String.valueOf(0));
					ff.setPayDiscount(0);
					if(ff.getFeeName().equalsIgnoreCase("Late Fee"))
					{
						//ff.setDueamount(String.valueOf(ff.getPayAmount()+ff.getPayDiscount()));
					}
					else
					{
						if(ff.getDueamount().equals("0"))
						{
							ff.setPayAmount(0);
							ff.setPayDiscount(0);
							ff.setDueamount(String.valueOf(0));
						}
					}
				}
				submitAmount+=ff.getPayAmount();
			}
		}
	}

	public void calculatePayAmount()
	{
		submitAmount=0;
		if(selectedFees.size()>0)
		{
			for(FeeInfo ff:selectedFees)
			{
				ff.setDueamount("0");
				submitAmount+=ff.getPayAmount();
			}
		}
	}
	public String submit()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message=(String) ss.getAttribute("feesubmit");
		DatabaseMethods1 DBM=new DatabaseMethods1();
		schoolid=DBM.schoolId();
		preSession=DatabaseMethods1.selectedSessionDetails(schoolid,conn);
		try
		{
			int amoutnt=0;
			int ii=0;
			String num="";
			int	number=DBM.feeserailno(DBM.schoolId(),conn);
			if(String.valueOf(number).length()==1)
			{
				num="0000"+String.valueOf(number);
			}
			else if(String.valueOf(number).length()==2)
			{
				num="000"+String.valueOf(number);
			}
			else if(String.valueOf(number).length()==3)
			{
				num="00"+String.valueOf(number);
			}
			else if(String.valueOf(number).length()==4)
			{
				num="0"+String.valueOf(number);
			}
			else if(String.valueOf(number).length()==5)
			{
				num=String.valueOf(number);
			}

			for(FeeInfo ff:selectedFees )
			{
				ff.setDueamount("0");
				if(ff.getPayAmount()>0)
				{
					sList.setSrNo(srNo);sList.setAddNumber(srNo);sList.setFathersPhone(cntctNo);sList.setFname(name);sList.setFullName(name);sList.setFathersName(fathersName);sList.setClassName(className);
					ii=DBM.submitFeeSchid(DBM.schoolId(),sList,ff.getPayAmount(),ff.getFeeId(),paymentMode,bankName,chequeNumber
							,num,0,preSession,recipietDate,challanNo,neftNo,challanDate,neftDate,conn,remark,new Date(),String.valueOf(0),"old","N/A");
					/*if(ii>=1 && ff.getFeeName().equals("Previous Fee"))
						{
							DBM.updatePaidAmountOfPreviousFee(srNo,(ff.getPayAmount()+ff.getPayDiscount()),conn);
						}*/
					amoutnt+=ff.getPayAmount();
				}
			}
			if(ii>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fees Added Successfully"));
				SchoolInfoList info=DBM.fullSchoolInfo(conn);
				String typeMessage="Dear Parent, \n\nReceived payment of Rs."+amoutnt+" in favour of fee by "+paymentMode+" via Receipt No. "+num+". Cheque payments are subjected to clearance. \n\nRegards, \n"+info.getSchoolName();
				DBM.entryInOldStudentDetailTable(srNo,name,fathersName,className,preSession,cntctNo,conn);
				/*if(info.getCtype().equalsIgnoreCase("foster") || info.getCtype().equalsIgnoreCase("fosterCBSE"))
					{
						if(paymentMode.equalsIgnoreCase("Cash"))
						{
							DBM.increaseCompanyCapitalAmount(Double.valueOf(amount),conn);
						}
					}*/
				if(message.equals("true"))
				{
					DBM.messageurl1(String.valueOf(cntctNo), typeMessage,srNo,conn,DBM.schoolId(),"");
				}

				DBM.notification(DBM.schoolId(),"Fees", typeMessage,cntctNo+"-"+DBM.schoolId(),conn);
				DBM.notification(DBM.schoolId(),"Fees", typeMessage,cntctNo+"-"+DBM.schoolId(),conn);
				HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				rr.setAttribute("type1", "origanal");
				ss.setAttribute("selectedStudent",sList);
				rr.setAttribute("paymentmode", paymentMode);
				rr.setAttribute("bankname", bankName);
				rr.setAttribute("chequeno", chequeNumber);
				rr.setAttribute("receiptNumber", num);
				rr.setAttribute("selectedFee", selectedFees);
				rr.setAttribute("receiptDate", recipietDate);
				rr.setAttribute("chaqueDate", challanDate);
				rr.setAttribute("remark", remark);
				rr.setAttribute("feeupto", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
				if(!schoolid.equals("206"))
				{
					PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");
				}
				return "oldStudentFeeCollection";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select At Least One Fee Type"));
			}
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	public void paymentModeListener()
	{
		showFeeList=true;
		if(paymentMode.equals("Cheque"))
		{
			showPaymentMode=true;showCheque=true;showChallan=false;showNeft=false;
		}
		else if(paymentMode.equals("Challan"))
		{
			showPaymentMode=showChallan=true;showNeft=showCheque=false;
		}
		else if(paymentMode.equals("Net Banking"))
		{
			showPaymentMode=showNeft=true;showChallan=showCheque=false;
		}
		else
		{
			showPaymentMode=showChallan=showNeft=showCheque=false;
		}
	}


	public String getSrNo() {
		return srNo;
	}


	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public long getCntctNo() {
		return cntctNo;
	}


	public void setCntctNo(long cntctNo) {
		this.cntctNo = cntctNo;
	}


	public String getFathersName() {
		return fathersName;
	}


	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}


	public String getPaymentMode() {
		return paymentMode;
	}


	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}


	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public String getChequeNumber() {
		return chequeNumber;
	}


	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getChallanNo() {
		return challanNo;
	}


	public void setChallanNo(String challanNo) {
		this.challanNo = challanNo;
	}


	public String getNeftNo() {
		return neftNo;
	}


	public void setNeftNo(String neftNo) {
		this.neftNo = neftNo;
	}


	public String getSchoolid() {
		return schoolid;
	}


	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getPreSession() {
		return preSession;
	}


	public void setPreSession(String preSession) {
		this.preSession = preSession;
	}


	public Date getRecipietDate() {
		return recipietDate;
	}


	public void setRecipietDate(Date recipietDate) {
		this.recipietDate = recipietDate;
	}


	public Date getChallanDate() {
		return challanDate;
	}


	public void setChallanDate(Date challanDate) {
		this.challanDate = challanDate;
	}


	public Date getNeftDate() {
		return neftDate;
	}


	public void setNeftDate(Date neftDate) {
		this.neftDate = neftDate;
	}


	public int getSubmitAmount() {
		return submitAmount;
	}


	public void setSubmitAmount(int submitAmount) {
		this.submitAmount = submitAmount;
	}


	public ArrayList<FeeInfo> getFeeList() {
		return feeList;
	}


	public void setFeeList(ArrayList<FeeInfo> feeList) {
		this.feeList = feeList;
	}


	public ArrayList<FeeInfo> getSelectedFees() {
		return selectedFees;
	}


	public void setSelectedFees(ArrayList<FeeInfo> selectedFees) {
		this.selectedFees = selectedFees;
	}


	public boolean isShowPaymentMode() {
		return showPaymentMode;
	}


	public void setShowPaymentMode(boolean showPaymentMode) {
		this.showPaymentMode = showPaymentMode;
	}


	public boolean isShowFeeList() {
		return showFeeList;
	}


	public void setShowFeeList(boolean showFeeList) {
		this.showFeeList = showFeeList;
	}


	public boolean isShowChallan() {
		return showChallan;
	}


	public void setShowChallan(boolean showChallan) {
		this.showChallan = showChallan;
	}


	public boolean isShowNeft() {
		return showNeft;
	}


	public void setShowNeft(boolean showNeft) {
		this.showNeft = showNeft;
	}


	public boolean isShowCheque() {
		return showCheque;
	}


	public void setShowCheque(boolean showCheque) {
		this.showCheque = showCheque;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}
	



}
