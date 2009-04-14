<%@ page import="java.util.*" %>
<%@ taglib uri="/WEB-INF/tld/camod.tld" prefix="camod" %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
  	<h1><center>
  			Generate Health Seven Version Message Using caAdapter 4.3
		</center>
	</h1>
	<br>
	<hr>
	<h1>Scope:</h1>
	<ul>
		<li>Support HL7 Normative 2005, 2006, 2008</li>
		<li>Support source data in CSV format</li>
		<li>Support source data of HL7 V2 message in version:
			<i>2.1, 2.2, 2.3, 2.3.1, 2.4, 2.5, 2.5.1</i></li>
		 
	</ul>
	<p>
	<h1>Steps One -- Create a Mapping Scenario</h1>
	<ul>
		<li>Create a target specification file: <b>h3s</b></li>
		<li>Create a CSV specification if source data is CSV format: <b>scs</b></li>
		<li>Create one or more vocabulary mapping files is source is HL7 V2 message: <b>vom</b></li>
		<li>Create a map between a target specification file <b>h3s</b> and a source specification file <b>scs</b> or a HL7 V2 message type</li>
	</ul>
	<h1>Step Two -- Register a Mapping Scenario</h1>
	<p>
	<ul>
		<li>Set a unique scenario name</li>
		<li>Upload the target specification file: <b>h3s</b></li>
		<li>Upload the source specification file if source data is CSV format: <b>scs</b></li>
		<li>Upload one or more vocabulary mapping files is source is HL7 V2 message: <b>vom</b></li>
		<li>Upload map file: <b>map</b></li>
	</ul>
	<h1>Step Three -- Generate HL7 V3 Message</h1>
	<p>
	<ul>
		<li>Create a caAdatper Web Service client</li>
		<li>Prepare source data</li>
		<li>Invoke transformation service given <i><b>Scenario Name</b></i>&nbsp; and &nbsp;<i><b>Source Data</b></i></li>
	</ul>
	<hr>  
	
