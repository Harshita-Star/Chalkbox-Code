package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;


@ManagedBean(name="headerBean")
@ViewScoped
public class SchoolHeaderBean implements Serializable
{

	String name,address1,address2,address3,address4,phoneno,mobileno,website,logo,finalAddress,affiliationNo,type,headerImagePath;
	String regno="";
	DatabaseMethods1 dbm=new DatabaseMethods1();

	public SchoolHeaderBean()
	{
		String savePath="";
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=dbm.fullSchoolInfo(conn);
		name=info.getSchoolName();
		address1=info.getAdd1();
		address2=info.getAdd2();
		address3=info.getAdd3();
		address4=info.getAdd4();
		phoneno=info.getPhoneNo();
		mobileno=info.getMobileNo();
		website=info.getWebsite();
		type=info.getClient_type();
		if(type.equalsIgnoreCase("institute"))
		{
			type="Institute";
		}
		else if(type.equalsIgnoreCase("school"))
		{
			type="School";
		}


		if(info.getProjecttype().equals("online"))
		{
			String folderName=info.getDownloadpath();
			savePath=folderName;
		}

		logo=savePath+info.getImagePath();
		headerImagePath=savePath+info.getMarksheetHeader();
		regno=info.getRegNo();
		finalAddress=address1;

		if(address2==null || address2.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address2;
		}

		if(address3==null || address3.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address3;
		}

		affiliationNo=address4;



		/*name="Dynamic Public School";
		address1="Alwar 301001";
		address2="Govt. Recognized";
		//address3="Run By : Smart Education & Social welfare Society (Reg. No. 143 Alwar 2009-10)";
		address4="IMG GLOBAL INFOTECH,Jai Complex, Alwar";*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	public String getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getAddress4() {
		return address4;
	}
	public void setAddress4(String address4) {
		this.address4 = address4;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getFinalAddress() {
		return finalAddress;
	}
	public void setFinalAddress(String finalAddress) {
		this.finalAddress = finalAddress;
	}
	public String getAffiliationNo() {
		return affiliationNo;
	}
	public void setAffiliationNo(String affiliationNo) {
		this.affiliationNo = affiliationNo;
	}
	public String getRegno() {
		return regno;
	}
	public void setRegno(String regno) {
		this.regno = regno;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHeaderImagePath() {
		return headerImagePath;
	}
	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
	}


}
