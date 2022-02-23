package schooldata;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.model.file.UploadedFile;
@ManagedBean(name="viewEditDeleteGallery")
@ViewScoped
public class ViewEditDeleteGallery implements Serializable
{
	ArrayList<HomeWorkInfo> newsList;
	ArrayList<HomeWorkInfo>imagesList,newImageList;
	HomeWorkInfo selectedNews,selectedImage;
	String image,gallaryName,selectedCLassSection;
	boolean view=false;
	UploadedFile file;
	int j=0;
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<SelectItem> classList;
	
	public ViewEditDeleteGallery()
	{
		Connection conn=DataBaseConnection.javaConnection();
		newsList=obj.allImages(conn);
		
		try
		{
			classList=obj.allClass(conn);
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
	}

	public void showImage()
	{
		Connection conn=DataBaseConnection.javaConnection();
		imagesList=obj.imageName(selectedNews.getTag(),conn);
		gallaryName = selectedNews.getTag();
		selectedCLassSection = selectedNews.getClassId();
		newImageList =new ArrayList<>();
		for(int i=1;i<=5;i++)
		{
			HomeWorkInfo in=new HomeWorkInfo();
			in.setsNo(String.valueOf(i));
			newImageList.add(in);
			j=i;
		}

		view=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String deleteImage()
	{
		int i=0;
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info = obj.fullSchoolInfo(conn);
		for(HomeWorkInfo ss:imagesList)
		{
			i++;
			if(ss.getImageName().equals(selectedImage.getImageName()))
			{
				break;
			}


		}

		if(imagesList.size()>1)
		{
			imagesList.remove(i-1);
			int a=obj.updateImage(imagesList,selectedNews.getTag(),conn);
			if(a>0)
			{
				File file = new File(info.getUploadpath()+selectedImage.getImageName());
				file.delete();
				
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("Image deleted from school gallery successfully"));
				try {
					conn.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				return "viewEditDeleteImages.xhtml";
			}
			else
			{
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("Some error Occur please try Again"));
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}
		}
		else
		{
			int a=obj.deleteImage(selectedNews.getTag(),obj.schoolId(),conn);
			if(a>0)
			{
				File file = new File(info.getUploadpath()+selectedImage.getImageName());
				file.delete();
				
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("Image deleted from school gallery successfully...."+selectedImage.getImageName()));
				try {
					conn.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				return "viewEditDeleteImages.xhtml";
			}
			else
			{
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("Some error Occur please try Again"));
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}
		}




	}
	public String sendMessage()
	{
	   if((gallaryName.trim().contains("'")&&gallaryName.trim().contains("--"))||(gallaryName.trim().contains("'")&&gallaryName.trim().contains("#")))
	   {
			  FacesContext context1 = FacesContext.getCurrentInstance();
			  context1.addMessage(null, new FacesMessage("Please Enter Proper Gallery Name"));
			  return "";
	   }
	   else
	   {	
		String name="";
		Connection conn=DataBaseConnection.javaConnection();
		if (imagesList.size() > 0)
		{

			for(HomeWorkInfo in:imagesList )
			{
				if(name.equals(""))
				{
					name=in.getImageName();
				}
				else
				{
					name=name+","+in.getImageName();
				}

			}
		}
		int i=obj.gallery(newImageList,name,gallaryName,selectedNews.getId(),selectedCLassSection,conn);
		if(i>0)
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Gallery Added successfully"));
			//obj.notification("Gallery","Some New Images Added In Your Gallery",obj.schoolId(),conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "viewEditDeleteImages.xhtml";
		}
		else
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Some error Occur please try Again"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}
	   }	
	}



	public ArrayList<HomeWorkInfo> getNewsList() {
		return newsList;
	}
	public void setNewsList(ArrayList<HomeWorkInfo> newsList) {
		this.newsList = newsList;
	}
	public HomeWorkInfo getSelectedNews() {
		return selectedNews;
	}
	public void setSelectedNews(HomeWorkInfo selectedNews) {
		this.selectedNews = selectedNews;
	}

	public boolean isView() {
		return view;
	}
	public void setView(boolean view) {
		this.view = view;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public ArrayList<HomeWorkInfo> getImagesList() {
		return imagesList;
	}
	public void setImagesList(ArrayList<HomeWorkInfo> imagesList) {
		this.imagesList = imagesList;
	}
	public HomeWorkInfo getSelectedImage() {
		return selectedImage;
	}
	public void setSelectedImage(HomeWorkInfo selectedImage) {
		this.selectedImage = selectedImage;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public ArrayList<HomeWorkInfo> getNewImageList() {
		return newImageList;
	}

	public void setNewImageList(ArrayList<HomeWorkInfo> newImageList) {
		this.newImageList = newImageList;
	}

	public String getGallaryName() {
		return gallaryName;
	}

	public void setGallaryName(String gallaryName) {
		this.gallaryName = gallaryName;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}





}
