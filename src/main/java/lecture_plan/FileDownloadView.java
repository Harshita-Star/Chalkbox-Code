package lecture_plan;

import java.io.InputStream;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
@ManagedBean(name="fileDownloadView")
@ViewScoped

public class FileDownloadView implements Serializable
{
	private static final long serialVersionUID = 1L;
	StreamedContent file;
    
    public FileDownloadView()
    {        
    	InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("lecturePlanSample.xls");
//        file = new DefaultStreamedContent(stream, "image/xls", "sample_lecture_plan.xls");
    	file = new DefaultStreamedContent().builder().contentType("image/xls").name("sample_lecture_plan.xls").stream(()->stream).build();
    }
 
    public StreamedContent getFile() {
        return file;
    }
	public void setFile(StreamedContent file) {
		this.file = file;
	}
}
