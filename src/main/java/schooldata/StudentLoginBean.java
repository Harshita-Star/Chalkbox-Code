package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


@ManagedBean(name="studentLogin")
@ViewScoped
public class StudentLoginBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String password;
	String userName;
	String id;

	public void forgetPassword() throws UnsupportedEncodingException
	{

		Connection conn=DataBaseConnection.javaConnection();
		{
			String i=new DatabaseMethods1().updateStudentPass(userName,conn);
			StringBuffer temp=new StringBuffer(i);
			String message=temp.substring(1);
			String temp2=temp.substring(0,1);
			if(i.equals("10"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Your attempts was more than 10, your account is blocked,Please contact Administrator", "Validation error"));
			}
			else if(i.equals("0"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Wrong userName or ID,try again", "Validation error"));
			}
			else if(temp2.equals("1"))
			{

				//String phoneNumber=new DatabaseMethods1().mobileNumberFromAdmissionNumber(userName);

				message="Your new password is "+message+" ";
				message=URLEncoder.encode(message,"UTF-8");
				/*	String uurl="http://www.txtguru.in/imobile/api.php?username=mkmittalimg&password=58809194&source=update&dmobile="+phoneNumber+"&message="+message+"";
				//// // System.out.println(uurl);
				try {
						URI uri=new URI(uurl);

						try {
							URL url=uri.toURL();
							//// // System.out.println("URL  :-"+url);

							try {
								InputStream stream=url.openStream();
							} catch (IOException e) {

								e.printStackTrace();
							}

						} catch (MalformedURLException e) {

							e.printStackTrace();
						}
					} catch (URISyntaxException e) {

						e.printStackTrace();
					}*/


				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Your new password has been sent to your contact number", "Validation error"));
			}


		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String login()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=new DatabaseMethods1().studentAuthentication(id, password,conn);
		try
		{
			if(i==1)
			{

				HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
				ss.setAttribute("studentId", id);

				return "studentWelcomeDashboard";
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Wrong username or password", "Validation error"));
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}



	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setId(String id) {
		this.id = id;
	}
}
