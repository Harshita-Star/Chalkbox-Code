package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
@ManagedBean(name="generalmessage")
@ViewScoped
public class UserTypeMessage  implements Serializable{

	private static final long serialVersionUID = 1L;
	String typeMessage,language,mobile;
	boolean show,englishShow,hindiShow;
	ArrayList<SelectItem> categoryList;
	String selectedCategory;
	ArrayList<String> ulist;

	public UserTypeMessage() {
		language="english";
		if(language.equalsIgnoreCase("english"))
		{	show=true;
		englishShow=true;hindiShow=false;
		}
		else
		{		show=true;
		englishShow=false;hindiShow=true;
		}



	}



	public void checkLanguage()
	{

		if(language.equalsIgnoreCase("english"))
		{	show=true;
		englishShow=true;hindiShow=false;
		}
		else
		{		show=true;
		englishShow=false;hindiShow=true;
		}

	}

	public String sendGeneralMessage() throws UnsupportedEncodingException
	{
		ulist = new ArrayList<>();
		for (String s : mobile.split(","))
			ulist.add((s));

		return "userTypeMessage.xhtml";




	}

	/*public void sendMessageCategoryWiseEmployee() throws UnsupportedEncodingException
	{
		int counter=1;
		int secondCounter=0;
		String contactNumber = "";
		Date date=new Date();
		String messageTemp=typeMessage;
		typeMessage=URLEncoder.encode(typeMessage,"UTF-8");
		for(EmployeeInfo info : selectedEmployeeList)
		{
			secondCounter++;
			if(counter==99 || secondCounter==selectedEmployeeList.size())
			{
				contactNumber=contactNumber+"91"+info.getMobile()+",";
				DataBaseMethodsSMSModule.sentMessage(info.getId(),messageTemp,info.getMobile(),date,"","Employee");
				String url="http://199.189.250.157/smsclient/api.php?username=sd-smart&password=62922178&source=update&dmobile="+contactNumber+"+&message="+typeMessage;
				try
				{
				    URL myURL = new URL(url);
				    myURL.openStream();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				counter=0;contactNumber="";
			}
			else
			{
				contactNumber=contactNumber+"91"+info.getMobile()+",";
				counter++;
				DataBaseMethodsSMSModule.sentMessage(info.getId(),messageTemp,info.getMobile(),date,"","Employee");
			}

		}

		FacesContext fc=FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("You have sent message to "+selectedEmployeeList.size()+" Employees "));

		selectedEmployeeList=null;
		typeMessage=null;
	    selectedCategory=null;
		show=false;
	}
	 */
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

	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public boolean isEnglishShow() {
		return englishShow;
	}
	public void setEnglishShow(boolean englishShow) {
		this.englishShow = englishShow;
	}
	public boolean isHindiShow() {
		return hindiShow;
	}
	public void setHindiShow(boolean hindiShow) {
		this.hindiShow = hindiShow;
	}
	public String getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


}
