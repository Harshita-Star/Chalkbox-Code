package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

@ManagedBean(name = "groupMessage")
@ViewScoped
public class GroupMessage implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	List<SelectItem> alllist = new ArrayList<>();
	String[] selectedStudent;

	@PostConstruct
	public void init() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		ArrayList<ClassInfo> list = obj.allClassList("studentalso", conn);
		for (ClassInfo ls : list) {
			SelectItemGroup germanCars = new SelectItemGroup(ls.getClassName());
			ArrayList<SelectItem> list1 = obj.allSection(ls.getGroupid(), conn);
			germanCars.setSelectItems(list1.toArray(new SelectItem[list1.size()]));

			alllist.add(germanCars);

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<SelectItem> getAlllist() {
		return alllist;
	}

	public void setAlllist(List<SelectItem> alllist) {
		this.alllist = alllist;
	}

	public String[] getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(String[] selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
}
