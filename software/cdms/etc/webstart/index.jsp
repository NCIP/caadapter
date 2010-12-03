<%
String requestURL=request.getRequestURL().toString();
String requestContext=request.getContextPath();
String requestURI=request.getRequestURI();
String requestHost=requestURL.substring(0, requestURL.indexOf(requestURI));
String codebase=requestHost+requestContext;
%>
<?xml version="1.0" encoding="ISO-8859-1"?>
<jnlp spec="1.0+" codebase="<%= codebase %>">
    <information>
          <title>caAdapter cdms Webstart</title>
          <vendor>ncicb</vendor>
          <description>caAdapter cdms Webstart</description>
       </information>

       <security>
           <all-permissions/>
       </security>
   	<resources>
		<j2se version="1.6+" />
		<jar href="cdms.jar"/>
        <jar href="xml_resources.zip"/>
	</resources>

   <application-desc main-class="gov.nih.nci.cbiit.cdms.formula.gui.FrameMain">
		<argument><%= codebase %></argument>
   </application-desc>
</jnlp>