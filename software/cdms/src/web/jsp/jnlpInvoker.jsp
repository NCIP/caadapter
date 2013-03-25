<%--L
  Copyright SAIC, SAIC-Frederick.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/caadapter/LICENSE.txt for details.
L--%>

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
      <title>caAdapter Model Mapping Services 4.1.1</title>
      <vendor>ncicb</vendor>
      <description>caAdapter Model Mapping Service 4.1.1</description>
   	</information>

   	<security>
       <all-permissions/>
   	</security>

   	<resources>
		<j2se version="1.5+" />		
		<jar href="caAdapter.jar"/>  
		<jar href="caAdapter_ui.jar"/>

		<jar href="BrowserLauncher2-all-10rc4.jar"/> 
		<jar href="castor-0.9.9.jar"/>
		<jar href="cmps.jar"/>
		<jar href="commons-collections-3.2.jar"/>
		<jar href="commons-logging-1.0.4.jar"/>
		<jar href="dom4j-1.4.jar"/>
		<jar href="jaxen-jdom.jar"/>
		<jar href="jdom.jar"/>
		<jar href="jgraph.jar"/>
		<jar href="knuHL7V2tree.jar"/>	
		<jar href="log4j-1.2.8.jar"/>
		<jar href="xercesImpl.jar"/>
		<jar href="xmi.in.out.jar"/>
		<jar href="sdk-codegen.jar"/>
		<jar href="spring.jar"/>
	</resources>

   <application-desc main-class="gov.nih.nci.caadapter.hl7.demo.LaunchUI">
       <argument><%= codebase %></argument> 
   </application-desc>
</jnlp>