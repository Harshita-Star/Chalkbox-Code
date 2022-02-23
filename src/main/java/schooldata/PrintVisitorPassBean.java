package schooldata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="printVisitorPass")
@ViewScoped
public class PrintVisitorPassBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String image,name,address,contactNo,inDate;

	public PrintVisitorPassBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		image=(String) ss.getAttribute("image");
		name=(String) ss.getAttribute("name");
		address=(String) ss.getAttribute("address");
		contactNo=(String) ss.getAttribute("contactNo");
		inDate=(String) ss.getAttribute("inDate");
	}


	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getInDate() {
		return inDate;
	}
	public void setInDate(String inDate) {
		this.inDate = inDate;
	}
}
