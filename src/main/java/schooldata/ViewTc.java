package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

@ViewScoped
@ManagedBean(name = "viewTc")
public class ViewTc implements Serializable {

	TCInfo selected;
	ArrayList<TCInfo> list = new ArrayList();

	public ViewTc() {
		Connection conn = DataBaseConnection.javaConnection();
		list = new DatabaseMethods1().getTcViewDetails(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void printDetail() {
		Connection conn = DataBaseConnection.javaConnection();
		selected = new DatabaseMethods1().getTcDetails(conn);
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("List", selected);
		PrimeFaces.current().executeInitScript("window.open('govtCollegePrintFormat.xhtml')");

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<TCInfo> getList() {
		return list;
	}

	public void setList(ArrayList<TCInfo> list) {
		this.list = list;
	}

	public TCInfo getSelected() {
		return selected;
	}

	public void setSelected(TCInfo selected) {
		this.selected = selected;
	}

}
