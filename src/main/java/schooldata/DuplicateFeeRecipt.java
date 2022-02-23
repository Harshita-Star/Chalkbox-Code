package schooldata;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="duplicatefeereceiptBean")
@ViewScoped
public class DuplicateFeeRecipt implements Serializable
{
	private static final long serialVersionUID = 1L;
	String addnumber,date,studentname,studentclass,fathername,mothername,activityfeeRupee,artfeeRupee,termfeerupee,admissionfeeRupee,annualfeeRupee,examfeerupee,tutuionfeerupee,transportfeerupee,total,paymentmode,chequeno,bankName;
	String sno;
	String totalAmountInWords;
	boolean show;
	DailyFeeCollectionBean selectedstudent;
	@SuppressWarnings("deprecation")
	public DuplicateFeeRecipt()
	{

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

		selectedstudent= (DailyFeeCollectionBean) ss.getAttribute("selectedstudent");
		sno=selectedstudent.reciptno;
		studentname=selectedstudent.studentname;
		fathername=selectedstudent.fathername;
		mothername=selectedstudent.mothername;
		addnumber=selectedstudent.studentid;
		Date feedate=selectedstudent.feedate;
		int day=feedate.getDate();
		int month=feedate.getMonth();
		month=month+1;

		int year=feedate.getYear();
		year=year+1900;
		date=String.valueOf(day)+"-"+String.valueOf(month)+"-"+String.valueOf(year);
		studentclass=selectedstudent.classname;
		admissionfeeRupee=selectedstudent.registrationfee;
		tutuionfeerupee=selectedstudent.tutionfee;
		transportfeerupee=selectedstudent.transportationfee;
		annualfeeRupee=selectedstudent.annualfee;
		examfeerupee=selectedstudent.examfee;
		artfeeRupee=selectedstudent.getArtfee();
		termfeerupee=selectedstudent.getTermfee();
		activityfeeRupee=selectedstudent.getActivityfee();
		total=selectedstudent.amount;

		totalAmountInWords=new DatabaseMethods1().numberToWords(Integer.parseInt(total));
		paymentmode=selectedstudent.paymentmode;
		if(paymentmode.equals("Cheque"))
		{
			bankName=selectedstudent.bankname;
			chequeno=selectedstudent.chequenumber;
			show=true;
		}

	}
	public String getActivityfeeRupee() {
		return activityfeeRupee;
	}
	public void setActivityfeeRupee(String activityfeeRupee) {
		this.activityfeeRupee = activityfeeRupee;
	}
	public String getArtfeeRupee() {
		return artfeeRupee;
	}
	public void setArtfeeRupee(String artfeeRupee) {
		this.artfeeRupee = artfeeRupee;
	}
	public String getTermfeerupee() {
		return termfeerupee;
	}
	public void setTermfeerupee(String termfeerupee) {
		this.termfeerupee = termfeerupee;
	}
	public String getTotalAmountInWords() {
		return totalAmountInWords;
	}
	public void setTotalAmountInWords(String totalAmountInWords) {
		this.totalAmountInWords = totalAmountInWords;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}


	public String getSno() {
		return sno;
	}
	public void setSno(String sno) {
		this.sno = sno;
	}
	public String getAddnumber() {
		return addnumber;
	}
	public void setAddnumber(String addnumber) {
		this.addnumber = addnumber;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStudentname() {
		return studentname;
	}
	public void setStudentname(String studentname) {
		this.studentname = studentname;
	}
	public String getStudentclass() {
		return studentclass;
	}
	public void setStudentclass(String studentclass) {
		this.studentclass = studentclass;
	}
	public String getFathername() {
		return fathername;
	}
	public void setFathername(String fathername) {
		this.fathername = fathername;
	}
	public String getMothername() {
		return mothername;
	}
	public void setMothername(String mothername) {
		this.mothername = mothername;
	}
	public String getAdmissionfeeRupee() {
		return admissionfeeRupee;
	}
	public void setAdmissionfeeRupee(String admissionfeeRupee) {
		this.admissionfeeRupee = admissionfeeRupee;
	}
	public String getAnnualfeeRupee() {
		return annualfeeRupee;
	}
	public void setAnnualfeeRupee(String annualfeeRupee) {
		this.annualfeeRupee = annualfeeRupee;
	}
	public String getExamfeerupee() {
		return examfeerupee;
	}
	public void setExamfeerupee(String examfeerupee) {
		this.examfeerupee = examfeerupee;
	}
	public String getTutuionfeerupee() {
		return tutuionfeerupee;
	}
	public void setTutuionfeerupee(String tutuionfeerupee) {
		this.tutuionfeerupee = tutuionfeerupee;
	}
	public String getTransportfeerupee() {
		return transportfeerupee;
	}
	public void setTransportfeerupee(String transportfeerupee) {
		this.transportfeerupee = transportfeerupee;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getPaymentmode() {
		return paymentmode;
	}
	public void setPaymentmode(String paymentmode) {
		this.paymentmode = paymentmode;
	}
	public String getChequeno() {
		return chequeno;
	}
	public void setChequeno(String chequeno) {
		this.chequeno = chequeno;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public DailyFeeCollectionBean getSelectedstudent() {
		return selectedstudent;
	}
	public void setSelectedstudent(DailyFeeCollectionBean selectedstudent) {
		this.selectedstudent = selectedstudent;
	}
}
