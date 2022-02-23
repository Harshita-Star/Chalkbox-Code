package schooldata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean (name="viewHealthCheckUp")
@ViewScoped
public class ViewHealthCheckUpBean implements Serializable{

	HealthCheckUpInfo selectedCheckUp;

	public ViewHealthCheckUpBean() {

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selectedCheckUp=(HealthCheckUpInfo) ss.getAttribute("selectedCheckUp");
	}

	public HealthCheckUpInfo getSelectedCheckUp() {
		return selectedCheckUp;
	}

	public void setSelectedCheckUp(HealthCheckUpInfo selectedCheckUp) {
		this.selectedCheckUp = selectedCheckUp;
	}
}
