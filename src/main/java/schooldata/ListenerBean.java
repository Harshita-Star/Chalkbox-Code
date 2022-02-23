package schooldata;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;


@ManagedBean(name="lis")
@ViewScoped
public class ListenerBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	int as=0;
	public void a() throws IOException
	{
		if(as<5)
		{
			as++;
		}
		else
		{
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ec.redirect(ec.getRequestContextPath() + "/AdminHome.xhtml");
		}
	}
	public int getAs() {
		return as;
	}
	public void setAs(int as) {
		this.as = as;
	}

}
