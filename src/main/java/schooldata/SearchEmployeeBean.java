package schooldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.Charsets;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


@ManagedBean(name="searchEmployee")
@ViewScoped
public class SearchEmployeeBean implements Serializable
{


	private static final long serialVersionUID = 1L;
	ArrayList<EmployeeInfo> employeeList;
	boolean show;
	ArrayList<SelectItem> categoryList;
	EmployeeInfo selectedEmployee;
	int totalEmployee;
	String selectedCategory;
	String name;
	String selectedCategoryName;
	StreamedContent file;

	String type="";
	public SearchEmployeeBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		categoryList=new DatabaseMethods1().allEmployeeCategory(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}


	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		employeeList=new DatabaseMethods1().searchEmployeebyName(query,conn);
		List<String> studentListt=new ArrayList<>();

		for(EmployeeInfo info : employeeList)
		{
			studentListt.add(info.getFname()+" / "+info.getLname()+ "-"+info.getId());
		}
        
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	public void searchEmployeeByName()
	{
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(EmployeeInfo info : employeeList)
			{
				if(String.valueOf(info.getId()).equals(id))
				{
					try
					{
						employeeList=new ArrayList<>();
						employeeList.add(info);

						show=true;
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
			type="byName";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select employee name from Autocomplete list", "Validation error"));

		}

	}

	public String deleteEmployee()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			int i=new DatabaseMethods1().deleteEmployee(selectedEmployee.getId(),conn);
			if(i==1)
			{
				
				String refNo;
		          try {
		        	  refNo=addWorkLog(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				new DatabaseMethods1().blockUser(selectedEmployee.getEmplyeeuserid(),"INACTIVE",conn);
			   
				String refNo2;
		          try {
		        	  refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if(type.equalsIgnoreCase("byName"))
				{
					employeeList=new ArrayList<>();
					show=false;
					name=null;
					selectedCategory=null;
	            		
				}
				else
				{
					searchByCategory();
				}
			    FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Selected Employee Inactivated successfully"));
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Selected Id-"+selectedEmployee.getId(); 
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Inactive Employee","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Selected Id-"+selectedEmployee.getEmplyeeuserid(); 
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Block user","WEB",value,conn);
		return refNo;
	}



	public void searchByCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		try
		{

			employeeList=obj.searchEmployeebyCategorySchidd(obj.schoolId(),selectedCategory,conn);
			totalEmployee=employeeList.size();
			if(selectedCategory.equalsIgnoreCase("All"))
			{
				selectedCategoryName="All";
			}
			else
			{
				selectedCategoryName=obj.employeeCategoryByIdSchid(obj.schoolId(),selectedCategory,conn);
			}

			type="category";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		show=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public String backToManageEmployeeDashboard()
	{

		show=false;
		name=null;
		selectedEmployee=null;

		return "manageEmployeeDashboard";

	}
	public void editNow() throws IOException
	{
		if(selectedEmployee!=null)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

			ss.setAttribute("selectedEmployee", selectedEmployee);
			show=false;
			name=null;
			selectedEmployee=null;

			ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
			cc.redirect("editEmployeeDetails.xhtml");

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select a Employee from the list", "Validation error"));
		}

	}

	public void viewDetails() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//EmployeeInfo selectedStudent=new DatabaseMethods1().teacherInfoByUserName(selectedEmployee.getEmplyeeuserid(),conn);
		ss.setAttribute("selectedEmployee", selectedEmployee);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
		cc.redirect("viewEmployeeDetails.xhtml");
	}
	
	public void downloadMethod()
	{
		FacesContext context = FacesContext.getCurrentInstance();
        int id = (int) UIComponent.getCurrentComponent(context).getAttributes().get("empRowId");
        EmployeeInfo selectedEmp=new EmployeeInfo();
        for(EmployeeInfo info:employeeList)
        {
        	if(info.getId()==id)
        	{
        		selectedEmp=info;
        		break;
        	}
        }
        
		String fl=selectedEmp.getEmpImageTemp();
		
		if(fl.equalsIgnoreCase(""))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Image not Present"));
		}
		else
		{	
		 int first=fl.lastIndexOf(".")+1;
		 int last=fl.length();
		 String ext=fl.substring(first, last);
		
		 String fileName=selectedEmp.getFname()+"_"+selectedEmp.getEmplyeeuserid()+".jpg";

		 /*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		 String realPath = ctx.getRealPath("/");
		 //// // System.out.println(realPath);*/
		 String realPath = selectedEmp.getUploadPath();
		 try 
		 {
			InputStream stream = new FileInputStream(new File(realPath+fl));
			
			if(ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif"))
			{
//				file = new DefaultStreamedContent(stream, "file/"+ext, fileName);
				file = new DefaultStreamedContent().builder().contentType("file/"+ext).name(fileName).stream(()->stream).build();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Unsupported File Format!!"));
			}
			
		 }
		 catch (Exception e1) 
		 {
			e1.printStackTrace();
		 }
		} 
	}
	
	public void downloadAllStudentPhoto () 
	{   
		String fileName=selectedCategoryName+"_Category_Images.zip";
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();

	    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
	    ec.setResponseContentType(Charsets.UTF_8.name()); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
		
	    String realPath=employeeList.get(0).getUploadPath();
	    
		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/
	    byte[] buffer = new byte[1024];
		try
		{
			OutputStream output = ec.getResponseOutputStream();
		 
			ZipOutputStream zout = new ZipOutputStream(output);
			int i = 1;
			for(EmployeeInfo p : employeeList)
			{
				if(!p.getEmpImageTemp().equals("") && !p.getEmpImageTemp().contains("www.chalkboxerp.in")) 
				{
					FileInputStream fin = new FileInputStream(realPath+p.getEmpImageTemp());
	                zout.putNextEntry(new ZipEntry(p.getFname()+i+".jpg"));
	                int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					  
					//close the InputStream
					fin.close();
					
					i=i+1;
				}
			}
			zout.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fc.responseComplete();
	}
	
	public void downloadDocumentMethod()
	{
		Connection conn=DataBaseConnection.javaConnection();
		FacesContext context = FacesContext.getCurrentInstance();
        int id = (int) UIComponent.getCurrentComponent(context).getAttributes().get("empRowId1");
        EmployeeInfo selectedEmp=new EmployeeInfo();
        
        for(EmployeeInfo info : employeeList)
		{
        	if(info.getId()==id)
        	{
        		selectedEmp=info;
        		break;
        	}
        }
        
        String fileName=selectedEmp.getFname()+"_"+selectedEmp.getEmplyeeuserid()+".zip";
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();

	    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
	    ec.setResponseContentType(Charsets.UTF_8.name()); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
		
	    String uploadPath="";
		SchoolInfoList ls =new DatabaseMethods1().fullSchoolInfo(conn);
		if (ls.getProjecttype().equalsIgnoreCase("online"))
		{
			uploadPath=ls.getUploadpath();
		}
		
	    String realPath=uploadPath;
	    
		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/
	    byte[] buffer = new byte[1024];
		try
		{
			OutputStream output = ec.getResponseOutputStream();
		 
			ZipOutputStream zout = new ZipOutputStream(output);
		 
			for(SelectItem doc:selectedEmp.getDocList())
			{
				if(doc.getValue()!=null && !doc.getValue().equals(""))
				{
					FileInputStream fin = new FileInputStream(realPath+doc.getValue());
					String ext=String.valueOf(doc.getValue()).substring(String.valueOf(doc.getValue()).lastIndexOf(".")+1);
	                zout.putNextEntry(new ZipEntry(doc.getLabel()+"."+ext));
	                int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					  
					//close the InputStream
					fin.close();
				}
			}
			zout.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fc.responseComplete();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void downloadAllDocuments() 
	{   
		Connection conn=DataBaseConnection.javaConnection();
		String fileName="TeacherDocuments.zip";
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();

	    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
	    ec.setResponseContentType(Charsets.UTF_8.name()); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    //ec.setResponseContentLength(); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
		
	    String uploadPath="";
		SchoolInfoList ls =new DatabaseMethods1().fullSchoolInfo(conn);
		if (ls.getProjecttype().equalsIgnoreCase("online"))
		{
			uploadPath=ls.getUploadpath();
		}
		
	    String realPath=uploadPath;
	    
		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/
	    byte[] buffer = new byte[1024];
		try
		{
			OutputStream output = ec.getResponseOutputStream();
		 
			ZipOutputStream zout = new ZipOutputStream(output);
		 
			for(EmployeeInfo p : employeeList)
			{
				for(SelectItem doc:p.getDocList())
				{
					FileInputStream fin = new FileInputStream(realPath+doc.getValue());
					String ext=String.valueOf(doc.getValue()).substring(String.valueOf(doc.getValue()).lastIndexOf(".")+1);
	                zout.putNextEntry(new ZipEntry(p.getFname()+"_"+p.getEmplyeeuserid()+"/"+doc.getLabel()+"."+ext));
	                int length;
					while((length = fin.read(buffer)) > 0)
					{
					   zout.write(buffer, 0, length);
					}
					zout.closeEntry();
					  
					//close the InputStream
					fin.close();
				}
			}
			zout.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		fc.responseComplete();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public EmployeeInfo getSelectedEmployee() {
		return selectedEmployee;
	}
	public void setSelectedEmployee(EmployeeInfo selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<EmployeeInfo> getEmployeeList() {
		return employeeList;
	}
	public void setEmployeeList(ArrayList<EmployeeInfo> employeeList) {
		this.employeeList = employeeList;
	}
	public String getSelectedCategoryName() {
		return selectedCategoryName;
	}
	public void setSelectedCategoryName(String selectedCategoryName) {
		this.selectedCategoryName = selectedCategoryName;
	}
	public int getTotalEmployee() {
		return totalEmployee;
	}
	public void setTotalEmployee(int totalEmployee) {
		this.totalEmployee = totalEmployee;
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}
	public String getSelectedCategory() {
		return selectedCategory;
	}
	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public StreamedContent getFile() {
		return file;
	}


	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
