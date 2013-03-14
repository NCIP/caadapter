<%--L
  Copyright SAIC.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/caadapter/LICENSE.txt for details.
L--%>

<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<!-- Layout of the web pages -->
<html>
<head>
	<title>caAdapter CMTS Web Service Managemant Portal</title>

    <link rel="stylesheet" type="text/css" href="css/styleSheet.css" />
	<link rel="icon" href="images/favicon.ico" />
	<link rel="shortcut icon" href="images/favicon.ico" />
	<!--
	<script src="js/script.js" type="text/javascript"></script>
	<script src="js/TipCode.js" type="text/javascript"></script>
	
	<style type="text/css">

		.style1 {
			font-size: 10px;
			font-style: italic;
			color: #FFFFFF;
		}

	</style>
	
	<script language="JavaScript" type="text/JavaScript">	

		function MM_preloadImages() { //v3.0
		  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
		    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
		    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
		}

	</script>
	-->

</head>

<body>

<%
   String user = (String)
   request.getSession().getAttribute("user");
%>

<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
	
	<!-- nci hdr begins -->
    <tr><td><tiles:insert attribute="header"/></td></tr>
  	<!-- nci hdr ends -->
  
  	<tr><td height="100%" valign="top">
      	<table summary="" cellpadding="0" cellspacing="0" border="0" height="100%">
        	<!--<tr><td colspan="2" height="50">&gt;tiles:insert attribute="applicationHeader"/&lt;</td></tr> -->
            <tr><td colspan="2" height="34"><tiles:insert attribute="applicationHeader"/></td></tr>
            <tr>
            	<td width="190" valign="top" class="subMenu"><tiles:insert attribute="subMenu"/></td> 
            	<td valign="top" width="100%">
                <table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="100%"> 
                	<tr>
                  		<td height="20" width="100%" class="mainMenu">
                  			<tiles:insert attribute="mainMenu"/>
                  		<!-- disable security for caAdapter 4.3 release -->
							<!-- table width="100%" height="100%" border="0" align="left" cellspacing="0">
								<tr>
									<td width="100%" height="100%">
										<!-- removed div align="right"class="loginStatus">
										<%
											if(user != null){
										%>
											You are logged in as <em><%= user%></em>
										<%
										}else{
										%>
											You are not logged in
										<%}%>
									</td>                      
								</tr>
							</table -->
						</td>
					</tr>
					<tr></td><td width="100%" valign="top"><tiles:insert attribute="content"/></td></tr>
       				<tr><td height="20" width="100%" class="footerMenu"><tiles:insert attribute="applicationFooter"/></td></tr>
       			</table>
       			</td>
			</tr>
   	  	</table>
      	</td>
  	</tr>  
</table>  
<tiles:insert attribute="footer"/> 
</body>
</html>