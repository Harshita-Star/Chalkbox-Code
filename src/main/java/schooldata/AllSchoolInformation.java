package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
@ManagedBean(name="allSchoolInformation")
@ViewScoped
public class AllSchoolInformation implements Serializable
{
	ArrayList<SchoolInfoList>list= new ArrayList<>();
	SchoolInfoList selectedSchool;
	String schoolName,schid;
	String add1,add2,add3,add4,phoneNo,mobileNo, emailId, website, regNo, imagePath,rname1,rname2,rname3;
	String username,key,url,installSession;
	String downloadpath,uploadpath;
	String projecttype;
	String ctype;
	String apikey;
	String adminApikey;
	int reciept,sno;
	String permissionType,type;
	public AllSchoolInformation()
	{
		Connection conn = DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().schoolInfo(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editSchoolDetails()
	{
		username=selectedSchool.getUsername();
		key=selectedSchool.getKey();
		url=selectedSchool.getUrl();
		installSession=selectedSchool.getInstallSession();
		downloadpath=selectedSchool.getDownloadpath();
		uploadpath=selectedSchool.getDownloadpath();
		projecttype=selectedSchool.getProjecttype();
		ctype=selectedSchool.getCtype();
		imagePath=selectedSchool.getImagePath();
		apikey=selectedSchool.getApikey();
		adminApikey=selectedSchool.getAdminApikey();
		type=selectedSchool.getType();
		schoolName=selectedSchool.getSchoolName();
		schid=selectedSchool.getSchid();
		add1=selectedSchool.getAdd1();
		add2=selectedSchool.getAdd2();
		add3=selectedSchool.getAdd3();
		add4=selectedSchool.getAdd4();
		phoneNo=selectedSchool.getPhoneNo();
		mobileNo=selectedSchool.getMobileNo();
		emailId=selectedSchool.getEmailId();
		website=selectedSchool.getWebsite();
		regNo=selectedSchool.getRegNo();
	}

	public void editNow()
	{

	}

	public SchoolInfoList getSelectedSchool() {
		return selectedSchool;
	}


	public void setSelectedSchool(SchoolInfoList selectedSchool) {
		this.selectedSchool = selectedSchool;
	}


	public ArrayList<SchoolInfoList> getList() {
		return list;
	}

	public void setList(ArrayList<SchoolInfoList> list) {
		this.list = list;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getAdd1() {
		return add1;
	}

	public void setAdd1(String add1) {
		this.add1 = add1;
	}

	public String getAdd2() {
		return add2;
	}

	public void setAdd2(String add2) {
		this.add2 = add2;
	}

	public String getAdd3() {
		return add3;
	}

	public void setAdd3(String add3) {
		this.add3 = add3;
	}

	public String getAdd4() {
		return add4;
	}

	public void setAdd4(String add4) {
		this.add4 = add4;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getRname1() {
		return rname1;
	}

	public void setRname1(String rname1) {
		this.rname1 = rname1;
	}

	public String getRname2() {
		return rname2;
	}

	public void setRname2(String rname2) {
		this.rname2 = rname2;
	}

	public String getRname3() {
		return rname3;
	}

	public void setRname3(String rname3) {
		this.rname3 = rname3;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getInstallSession() {
		return installSession;
	}

	public void setInstallSession(String installSession) {
		this.installSession = installSession;
	}

	public String getDownloadpath() {
		return downloadpath;
	}

	public void setDownloadpath(String downloadpath) {
		this.downloadpath = downloadpath;
	}

	public String getUploadpath() {
		return uploadpath;
	}

	public void setUploadpath(String uploadpath) {
		this.uploadpath = uploadpath;
	}

	public String getProjecttype() {
		return projecttype;
	}

	public void setProjecttype(String projecttype) {
		this.projecttype = projecttype;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public String getAdminApikey() {
		return adminApikey;
	}

	public void setAdminApikey(String adminApikey) {
		this.adminApikey = adminApikey;
	}

	public int getReciept() {
		return reciept;
	}

	public void setReciept(int reciept) {
		this.reciept = reciept;
	}

	public int getSno() {
		return sno;
	}

	public void setSno(int sno) {
		this.sno = sno;
	}

	public String getPermissionType() {
		return permissionType;
	}

	public void setPermissionType(String permissionType) {
		this.permissionType = permissionType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
