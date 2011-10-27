<%@ page import="java.util.*" %>
<%@ taglib uri="/WEB-INF/tld/camod.tld" prefix="camod" %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
  	<h1><center>
  			caAdapter Domain Value Mapping Service (DVMS) 1.0
		</center>
	</h1>
	<br>
	<hr>
	<h1>Scope:</h1>
	<ul>
		<li>Support domain vocabulary mapping </li>
		<li>Support domain vocabulary translating via Web and Restful Service</li>
		<li>Provide API tool for domain vocabulary translating</li>	
		<li>Provide GUI tool for creating domain vocabulary mapping data (future plan)</li>	 
	</ul>
	<p>
	<h1>Steps One -- Create a Vocabulary Mapping (VOM) File</h1>
	<ul>
	    <li>A VOM file includes vocabulary mapping information and is essentially needed for this service.</li>
		<li>Unfotunately, caAdapter cannot currently support to creating VOM file but the future.</li>
		<li>One VOM file is composed of <b>'Domains'</b> such as 'marital status' or 'insurance type'.</li>
		<li>You can see the VOM schema if you click here.</li>
		<li>You can see an example VOM file if you click here.</li>
	</ul>
	<h1>Step Two -- Create a VOM Context</h1>
	<p>
	<ul>
		<li>VOM context means a big of VOM files shared and used by one business communit. </li>
		<li>For using this service, user doesn't have to know each VOM file name, but the context name. </li>
		<li>One context must be mapped its physical link information such as local folder, web URL or data base link point. </li>
		<li>This link information is stored to 'contextLinkAddress.properties' file located in the system's working directory or its sub folder.</li>
		<li>This service provides a prototype for creating a web based VOM context at <a href="./MenuStart">here.</a></li>
	</ul>
	<h1>Step Three -- Translating vocabulary</h1>
	<p>
	<ul>
		<li>For vocabulary translating, user or system has to provide at least three data - context name, domain name and input value, and more 'inverse' tag if you want inverse translation.</li>
		<li>Translating with java API: download jar file for java programming and developing.</li>
		<li>Translating via web service: You can see an example usage if you click here.</li>
		<li>Translating via restful service: You can see an example usage if you click here.</li>
		
	</ul>
	<hr>  
	
