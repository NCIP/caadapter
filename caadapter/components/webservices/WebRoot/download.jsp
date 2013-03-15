<%--L
  Copyright SAIC.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/caadapter/LICENSE.txt for details.
L--%>

<%@ page language="java" import="java.util.*,java.io.*,java.net.URLConnection"%>
<%


		  String filePath=""; 
          String fileNameNew = (String) request.getQueryString();
		  System.out.println(getServletContext().getRealPath("/"));
		  
//        filePath= "/cabigprotocolfiles/" + fileNameNew;
		//  filePath= "/local/content/cabigprotocols/files/" + fileNameNew;
		
		//filePath="c:\\spores\\uploads\\" + fileNameNew;
		Properties _properties = new Properties();
	    _properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
	    filePath = (String)_properties.getProperty("FILEPATH");
	    filePath = "/local/content/spores/files/"+fileNameNew;       
		System.out.println("path: " + request.getServerName());
		
		
          response.setContentType("application/unknown");
          response.addHeader("Content-Disposition", "attachment; filename="+fileNameNew);
          try{
               File uFile= new File(filePath);
			   System.out.println("last mod: " + uFile.lastModified());
               int fSize=(int)uFile.length();
               FileInputStream fis = new FileInputStream(uFile);
               PrintWriter pw =  response.getWriter();
                 int c=-1;
                 // Loop to read and write bytes.
                 //pw.print("Test");
                 
                 while ((c = fis.read()) != -1){
                      pw.print((char)c);
                 }
                 // Close output and input resources. 
                 fis.close();
               pw.flush();
               pw=null;                                             
          }catch(Exception e){
          }
		  
		  
%>
