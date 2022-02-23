package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;
@ManagedBean(name="subjectAllocationReport")
@ViewScoped
public class SubjectAllocationReport implements Serializable
{
	ArrayList<ClassTeacherInfo>list=new ArrayList<>();
	ArrayList<SelectItem> subjectList=new ArrayList<>();
	ArrayList<String> subjectListString=new ArrayList<>();
	ArrayList<String> selectedSubjectList=new ArrayList<>();
	ArrayList<SelectItem> teacherlist;
	String selectedteacher;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	ClassTeacherInfo selectedRow;
	boolean b=false,showSubject=false;
	
	public SubjectAllocationReport()
	{
		showSubject=false;
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		teacherlist=obj.allteacher(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		viewReport();
	}

	public void viewReport()
	{
		list=new ArrayList<>();
		Connection conn=DataBaseConnection.javaConnection();

		ArrayList<ClassTeacherInfo> alist=obj.subjectAllocationReport(conn);
		ArrayList<String> allocatedSubjects=obj.allocatedSubjects(conn);

		Set<String> hs = new HashSet<>();
		hs.addAll(allocatedSubjects);
		allocatedSubjects.clear();
		allocatedSubjects.addAll(hs);

		String subj="";
		for(String ss : allocatedSubjects)
		{
			if(subj.equals(""))
			{
				subj=ss;
			}
			else
			{
				subj=subj+","+ss;
			}
		}

		ArrayList<ClassTeacherInfo> ulist=obj.unallocatedSubjects(subj,conn);

		list.addAll(alist);
		list.addAll(ulist);
		////// // System.out.println("sds : "+subj);

		int count=1;
		if(list.size()>0)
		{
			for(ClassTeacherInfo cc : list)
			{
				cc.setSno(count++);
			}
			b=true;
		}
		else
		{
			b=false;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void preDeallocate()
	{
		subjectList=new ArrayList<>();
		subjectListString=new ArrayList<>();
		selectedSubjectList=new ArrayList<>();

		////// // System.out.println("size : "+subjectList.size());

		String tempSubjectId=selectedRow.getSubjectId();
		String tempSubjectName=selectedRow.getSubjectName();
		if(tempSubjectId.contains(","))
		{
			String tempSubidArr[]=tempSubjectId.split(",");
			String tempSubnmArr[]=tempSubjectName.split(",");

			for(int i=0;i<tempSubidArr.length;i++)
			{
				SelectItem ss=new SelectItem();

				ss.setLabel("                    "+tempSubnmArr[i]);
				ss.setValue(tempSubidArr[i]);

				subjectList.add(ss);

				subjectListString.add(tempSubidArr[i]);
			}

			PrimeFaces.current().executeInitScript("PF('viewDialog').show()");
			PrimeFaces.current().ajax().update("viewForm");
		}
		else
		{
			subjectListString.add(tempSubjectId);
			selectedSubjectList.add(tempSubjectId);

			deallocateSubject();
			PrimeFaces.current().ajax().update("form");
		}
	}

	public void deallocateSubject()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedSubjectList.size()>0)
		{
			for(String ss : selectedSubjectList)
			{
				subjectListString.remove(ss);
			}

			int i=obj.deallocateSubject(selectedRow.getId(),subjectListString,conn);
			if(i>=1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Deallocated Successfully"));
				viewReport();

				//return "subjectAllocationReport.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error Occured.Please Try Again.","Error"));
				//return "";
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please Select Atleast One Subject. Try Again.","Error"));
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
		String language= "";
		
		for(String vv: selectedSubjectList)
		{
			language += vv.toString()+" -- ";
		}
		if(!language.equalsIgnoreCase(""))
		 language = language.substring(0, language.length()-4);
		
		value = selectedRow.getId()+ " ---- ";
		
		for(String vv: selectedSubjectList)
		{
			value += vv.toString()+" -- ";
		}
		if(selectedSubjectList.size()>0)
		{
			value = value.substring(0, value.length()-4);
		}
		
	
		String refNo = workLg.saveWorkLogMehod(language,"Deallocate Subject","WEB",value,conn);
		return refNo;
	}
	
	//For transfer
	public String addWorkLog2(Connection conn,String subject)
	{
	    String value = "";
		String language= selectedteacher;
		
		value = selectedRow.getClassid()+" --- "+subject+" --- "+selectedteacher+" --- "+selectedRow.getSectionId();
		
	
		String refNo = workLg.saveWorkLogMehod(language,"Transfer Subject","WEB",value,conn);
		return refNo;
	}


	public void preTransfer()
	{
		subjectList=new ArrayList<>();
		subjectListString=new ArrayList<>();
		selectedSubjectList=new ArrayList<>();

		////// // System.out.println("size : "+subjectList.size());

		String tempSubjectId=selectedRow.getSubjectId();
		String tempSubjectName=selectedRow.getSubjectName();
		if(tempSubjectId.contains(","))
		{
			String tempSubidArr[]=tempSubjectId.split(",");
			String tempSubnmArr[]=tempSubjectName.split(",");

			for(int i=0;i<tempSubidArr.length;i++)
			{
				SelectItem ss=new SelectItem();

				ss.setLabel("                    "+tempSubnmArr[i]);
				ss.setValue(tempSubidArr[i]);

				subjectList.add(ss);

				subjectListString.add(tempSubidArr[i]);
			}

			showSubject=true;

			PrimeFaces.current().executeInitScript("PF('trnDialog').show()");
			PrimeFaces.current().ajax().update("trnForm");
		}
		else
		{

			subjectListString.add(tempSubjectId);
			selectedSubjectList.add(tempSubjectId);

			showSubject=false;

			PrimeFaces.current().executeInitScript("PF('trnDialog').show()");
			PrimeFaces.current().ajax().update("trnForm");
		}
	}

	public void transferSubject()
	{
		Connection conn=DataBaseConnection.javaConnection();

		String subject="";
		if(selectedSubjectList.size()>0)
		{
			for(String ss : selectedSubjectList)
			{
				subjectListString.remove(ss);
				if(subject.equals(""))
				{
					subject=ss;
				}
				else
				{
					subject=subject+","+ss;
				}
			}

			int i=obj.deallocateSubject(selectedRow.getId(),subjectListString,conn);
			if(i>=1)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn,subject);
				} catch (Exception e) {
					// TODO: handle exception
				}
				obj.addAllocationOnTransfer(selectedRow.getClassid(),subject,selectedteacher,selectedRow.getSectionId(),conn);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Transferred Successfully"));
				selectedteacher="";
				viewReport();

				//return "subjectAllocationReport.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error Occured.Please Try Again.","Error"));
				selectedteacher="";
				//return "";
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please Select Atleast One Subject. Try Again.","Error"));
			selectedteacher="";
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ClassTeacherInfo> getList() {
		return list;
	}
	public void setList(ArrayList<ClassTeacherInfo> list) {
		this.list = list;
	}
	public boolean isB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}

	public ClassTeacherInfo getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(ClassTeacherInfo selectedRow) {
		this.selectedRow = selectedRow;
	}

	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<String> getSubjectListString() {
		return subjectListString;
	}

	public void setSubjectListString(ArrayList<String> subjectListString) {
		this.subjectListString = subjectListString;
	}

	public ArrayList<String> getSelectedSubjectList() {
		return selectedSubjectList;
	}

	public void setSelectedSubjectList(ArrayList<String> selectedSubjectList) {
		this.selectedSubjectList = selectedSubjectList;
	}

	public ArrayList<SelectItem> getTeacherlist() {
		return teacherlist;
	}

	public void setTeacherlist(ArrayList<SelectItem> teacherlist) {
		this.teacherlist = teacherlist;
	}

	public String getSelectedteacher() {
		return selectedteacher;
	}

	public void setSelectedteacher(String selectedteacher) {
		this.selectedteacher = selectedteacher;
	}

	public boolean isShowSubject() {
		return showSubject;
	}

	public void setShowSubject(boolean showSubject) {
		this.showSubject = showSubject;
	}
}
