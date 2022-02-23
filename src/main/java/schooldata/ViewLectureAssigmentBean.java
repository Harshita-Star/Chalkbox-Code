package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="viewLectureAssigmentBean")
@ViewScoped
public class ViewLectureAssigmentBean implements Serializable
{
	boolean show=false;
	ArrayList<StudentInfo>list=new ArrayList<>();
    
	StudentInfo selected;
	ArrayList<StudentInfo> imageList;
	SchoolInfoList lst=null;
	public ViewLectureAssigmentBean() 
    {
		Connection conn=DataBaseConnection.javaConnection();
 		DatabaseMethods1 obj=new DatabaseMethods1();
 		
 		
 		
 		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
 		String lectureId = (String) ses.getAttribute("lectureId");
 	
 		 lst=obj.fullSchoolInfo(obj.schoolId(), conn);
 		
		list=obj.allLactureAssignment(lectureId,obj.schoolId(),conn);
		
    
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
    }
	
	public void viewassignemnt()
	{
		String[] selectedimage=selected.getLactureImage().split(",");
		imageList=new ArrayList<>();
		
		for(int i=0;i<selectedimage.length;i++)
		{
			StudentInfo ls=new StudentInfo();
			
			int j=i+1;
			
			ls.setFileName("file "+j);
			// // System.out.println(selectedimage[i]);
			ls.setFileName1(lst.getDownloadpath()+selectedimage[i]);
			imageList.add(ls);
			
		}
		
	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

	public StudentInfo getSelected() {
		return selected;
	}

	public void setSelected(StudentInfo selected) {
		this.selected = selected;
	}

	public ArrayList<StudentInfo> getImageList() {
		return imageList;
	}

	public void setImageList(ArrayList<StudentInfo> imageList) {
		this.imageList = imageList;
	}
	
	
	
}
