package reports_module;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

@ManagedBean(name="registrationExcelPlugin")
@ViewScoped

public class RegistrationExcelPluginBean implements Serializable
{
	StreamedContent file;
	transient UploadedFile excelFile;
	
	public RegistrationExcelPluginBean() 
	{
		
	}
	
	public void importFromExcel() throws IOException
	{
		
	}

	public void downloadSample() 
	{ 
		
		InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("registrationSampleSheet.xlsx");
       
//		file = new DefaultStreamedContent(stream, "image/xlsx", "sample_registration_sheet.xlsx");
		file = new DefaultStreamedContent().builder().contentType("image/xlsx").name("sample_registration_sheet.xlsx").stream(()->stream).build();

	}
	
	public void downloadSampleAll() 
	{ 
		
		InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("registrationSampleSheetAllClass.xlsx");
   
//		file = new DefaultStreamedContent(stream, "image/xlsx", "sample_registration_sheet.xlsx");
		file = new DefaultStreamedContent().builder().contentType("image/xlsx").name("sample_registration_sheet.xlsx").stream(()->stream).build();

	}
	
	
	
	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public UploadedFile getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(UploadedFile excelFile) {
		this.excelFile = excelFile;
	}
	
	
}

