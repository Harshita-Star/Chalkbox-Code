<%@page import="Json.HomeWorkJson"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="schooldata.SchoolInfoList"%>
<%@page import="Json.DataBaseMeathodJson"%>
<%@page import="schooldata.SchoolInfo"%>
<%@page import="java.sql.Connection"%>
<%@page import="schooldata.DataBaseConnection"%>
<%@ page import = "java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import = "javax.servlet.http.*" %>
<%@ page import = "org.apache.commons.fileupload.*" %>
<%@ page import = "org.apache.commons.fileupload.disk.*" %>
<%@ page import = "org.apache.commons.fileupload.servlet.*" %>
<%@ page import = "org.apache.commons.io.output.*" %>

<%
   File file ;
   int maxFileSize = 10000 * 1024;
   int maxMemSize = 10000 * 1024;
   ServletContext context = pageContext.getServletContext();
   Connection conn=DataBaseConnection.javaConnection();
   String userid=request.getParameter("userid");
   String	classid = request.getParameter("classid");
   String sectionid = request.getParameter("sectionid");
   String  subjectid = request.getParameter("subjectid");
   String  desc =request.getParameter("desc");
   String  schid = request.getParameter("Schoolid");
   String  sms = request.getParameter("sms");
   String type=request.getParameter("type");
   String extension=request.getParameter("extension");
 
   

   if(extension==null || extension.equals(""))
   {
	   extension=".jpg";
   }
   
   
   
   
   
   if(type==null||type.equals(""))
   {
	   type="homework";
   }
   
   
   SchoolInfoList ls=new DataBaseMeathodJson().fullSchoolInfo(schid, conn);
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
      upload.setSizeMax( maxFileSize );
      
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
         String allfilename="";
         while ( i.hasNext () ) {
            FileItem fi = (FileItem)i.next();
            if ( !fi.isFormField () ) {
               // Get the uploaded file parameters
               String fieldName = fi.getFieldName();
               String time=new SimpleDateFormat("yMdhms").format(new Date());
               String fileName = fi.getName();
               int randomPIN = (int)(Math.random()*9000)+1000;
               String fileName1 = type+time+"_"+String.valueOf(randomPIN)+extension;

               if(allfilename.equals(""))
               {
            	   allfilename=fileName1;
               }
               else
               {
             	   allfilename=allfilename+","+fileName1;
               }

	    			   
               boolean isInMemory = fi.isInMemory();
               long sizeInBytes = fi.getSize();
            
               // Write the file
               if( fileName1.lastIndexOf("\\") >= 0 ) {
                  file = new File( filePath + 
                 fileName1.substring( fileName1.lastIndexOf("\\"))) ;
               } else {
                  file = new File( filePath + 
                 fileName1.substring(fileName1.lastIndexOf("\\")+1)) ;
               }
               fi.write( file ) ;
               
               
               out.println("Uploaded Filename: " + filePath + 
            	fileName1 + "<br>");
            }
         }
         //(allfilename);
  	     new HomeWorkJson().addHomework(userid, classid, sectionid, subjectid, desc, schid, sms, allfilename,type);
        
         out.println("</body>");
         out.println("</html>");
      } catch(Exception ex) {
         //(ex);
      }
   } else {
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Servlet upload</title>");  
      out.println("</head>");
   } 
   
    
   
    %>  
  