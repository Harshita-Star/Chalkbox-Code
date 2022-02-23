package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import reports_module.DataBaseMethodsReports;

@ManagedBean(name="classWiseEnqStrength")
@ViewScoped

public class ClassWiseEnqStrengthBean implements Serializable
{
	ArrayList<SelectItem> classList = new ArrayList<>();
	ArrayList<ClassInfo> list = new ArrayList<>();
	String schid = "";
	int total,totalAccept,totalDeny,totalPending;
	public ClassWiseEnqStrengthBean()
	{
		total=totalAccept=totalDeny=totalPending=0;

		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 dbm = new DatabaseMethods1();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		schid = dbm.schoolId();
		list = new ArrayList<>();
		classList = dbm.allClass(schid, conn);
		for(SelectItem ss : classList)
		{
			ClassInfo info = new ClassInfo();
			int totalEnq = dbr.totalEnquiryByStatusInClass("all",String.valueOf(ss.getValue()),schid,conn);
			int totalAcc = dbr.totalEnquiryByStatusInClass("Done",String.valueOf(ss.getValue()),schid,conn);
			int totalDen = dbr.totalEnquiryByStatusInClass("denied",String.valueOf(ss.getValue()),schid,conn);
			int totalPen = dbr.totalEnquiryByStatusInClass("pending",String.valueOf(ss.getValue()),schid,conn);

			info.setClassid(String.valueOf(ss.getValue()));
			info.setClassName(ss.getLabel());

			info.setTotalEnq(totalEnq);
			info.setAcceptEnq(totalAcc);
			info.setDeniedEnq(totalDen);
			info.setPendingEnq(totalPen);

			total = total + info.getTotalEnq();
			totalAccept = totalAccept + info.getAcceptEnq();
			totalDeny = totalDeny + info.getDeniedEnq();
			totalPending = totalPending + info.getPendingEnq();

			list.add(info);

		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalAccept() {
		return totalAccept;
	}

	public void setTotalAccept(int totalAccept) {
		this.totalAccept = totalAccept;
	}

	public int getTotalDeny() {
		return totalDeny;
	}

	public void setTotalDeny(int totalDeny) {
		this.totalDeny = totalDeny;
	}

	public int getTotalPending() {
		return totalPending;
	}

	public void setTotalPending(int totalPending) {
		this.totalPending = totalPending;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}


}
