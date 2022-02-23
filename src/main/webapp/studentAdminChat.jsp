<%@page import="schooldata.StudentInfo"%>
<%@page import="schooldata.HomeWorkInfo"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="schooldata.SchoolInfoList"%>
<%@page import="Json.DataBaseMeathodJson"%>
<%@page import="schooldata.SchoolInfo"%>
<%@page import="java.sql.Connection"%>
<%@page import="schooldata.DataBaseConnection"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>
<%@ page import="javax.servlet.http.*"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.fileupload.disk.*"%>
<%@ page import="org.apache.commons.fileupload.servlet.*"%>
<%@ page import="org.apache.commons.io.output.*"%>

<%
	File file;
	int maxFileSize = 10000 * 1024;
	int maxMemSize = 10000 * 1024;
	ServletContext context = pageContext.getServletContext();
	Connection conn = DataBaseConnection.javaConnection();
	
	String groupId=request.getParameter("groupid");
	String message=request.getParameter("msg");
	String type=request.getParameter("type");
	String schid=request.getParameter("Schoolid");
	String studentId=request.getParameter("studentId");
	
	String messageId=groupId+type+studentId+message+(int)(Math.random()*9000)+1000;
	
	StudentInfo info=new DataBaseMeathodJson().studentDetailslistByAddNo(groupId,schid,conn);
	SchoolInfoList ls = new DataBaseMeathodJson().fullSchoolInfo(schid, conn);
	String filePath = ls.getUploadpath();
	
	//(filePath);
	String contentType = request.getContentType();
	if ((contentType.indexOf("multipart/form-data") >= 0)) {

		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxMemSize);

		// Location to save data that is larger than maxMemSize.
		factory.setRepository(new File(ls.getUploadpath()));

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// maximum file size to be uploaded.
		upload.setSizeMax(maxFileSize);

		try {
			// Parse the request to get file items.
			List fileItems = upload.parseRequest(request);

			// Process the uploaded file items
			Iterator i = fileItems.iterator();

			out.println("<html>");
			out.println("<head>");
			out.println("<title>JSP File upload</title>");
			out.println("</head>");
			out.println("<body>");
			String allfilename = "";
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (!fi.isFormField()) {
					// Get the uploaded file parameters
					String fieldName = fi.getFieldName();
					String time = new SimpleDateFormat("yMdhms").format(new Date());
					String fileName = fi.getName();
					int randomPIN = (int) (Math.random() * 9000) + 1000;
					String fileName1 = "new_file_broadcast_" + time + "_" + String.valueOf(randomPIN) + ".jpg";

					if (allfilename.equals("")) {
						allfilename = fileName1;
					} else {
						allfilename = allfilename + "," + fileName1;

					}
					
					boolean isInMemory = fi.isInMemory();
					long sizeInBytes = fi.getSize();

					// Write the file
					if (fileName1.lastIndexOf("\\") >= 0) {
						file = new File(filePath + fileName1.substring(fileName1.lastIndexOf("\\")));
					} else {
						file = new File(filePath + fileName1.substring(fileName1.lastIndexOf("\\") + 1));
					}
					fi.write(file);

					out.println("Uploaded Filename: " + filePath + fileName1 + "<br>");
				}
			}
			//(allfilename);

		if(type.equals("admin"))
		{
			int j=new DataBaseMeathodJson().insertBroadcastDetailsindivisual(groupId, message,type, messageId, schid,studentId,allfilename,conn);
			if(allfilename.equals(""))
			{
				new DataBaseMeathodJson().notification("Broadcast","Admin-New Message Received",info.getFathersPhone()+"-"+groupId+"-"+schid,schid,"",conn);
			}
			else
			{
				new DataBaseMeathodJson().notification("Broadcast","Admin-New Message Received",info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,ls.getDownloadpath()+allfilename,conn);
			}
		}
		else
		{
			int j=new DataBaseMeathodJson().insertBroadcastDetailsindivisual(groupId, message,type, messageId, schid, studentId,allfilename,conn);
			String tp = "SingleStudentBroadcast-"+groupId;
			new DataBaseMeathodJson().adminnotification("Broadcast",info.getFname()+"-New Message Received","admin-"+schid,schid,tp,conn);
		}

			out.println("</body>");
			out.println("</html>");
		} catch (Exception ex) {
			//(ex);
		}
	} else {
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Servlet upload</title>");
		out.println("</head>");
	}
%>
