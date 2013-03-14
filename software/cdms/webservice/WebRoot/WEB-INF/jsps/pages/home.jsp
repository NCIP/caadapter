<%--L
  Copyright SAIC.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/caadapter/LICENSE.txt for details.
L--%>

<%@ page import="java.util.*" %>
<%@ taglib uri="/WEB-INF/tld/camod.tld" prefix="camod" %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
  	<h1><center>
  			Generate HL7 Version 3 Message Using caAdapter 4.3
		</center>
	</h1>
	<br>
	<hr>
	<h1>Scope:</h1>
	<ul>
		<li>Support HL7 version 3 normative edition <i>2005, 2006, 2008</i></li>
		<li>Support source data in CSV format</li>
		<li>Support source data of HL7 Version 2 (V2) message in versions:
			<i>2.1, 2.2, 2.3, 2.3.1, 2.4, 2.5, 2.5.1</i></li>		 
	</ul>
	<p>
	<h1>Steps One -- Create a Mapping Scenario</h1>
	<ul>
		<li>Create a target specification file: <b>h3s</b></li>
		<li>Create a CSV specification file if source data is CSV format: <b>scs</b></li>
		<li>Create a vocabulary mapping file if source is HL7 V2 message: <b>vom</b></li>
		<li>Create a map between a target specification file <b>h3s</b> and a source specification file <b>scs</b> or a HL7 V2 message type</li>
	</ul>
	<h1>Step Two -- Register a Mapping Scenario</h1>
	<p>
	<ul>
		<li>Set a unique scenario name</li>
		<li>Upload the map file: <b>map</b></li>
		<li>Upload the source specification file if source data is CSV format: <b>scs</b></li>
		<li>Upload the target specification file: <b>h3s</b></li>
		<li>Upload the vocabulary mapping file if source is an HL7 V2 message and a vocabulary mapping file being  used: <b>vom</b></li>
	</ul>
	<h1>Step Three -- Generate HL7 V3 Message</h1>
	<p>
	<ul>
		<li>Create a caAdatper Web Service client</li>
		<li>Prepare source data</li>
		<li>Invoke transformation service given <i><b>Scenario Name</b></i>&nbsp; and &nbsp;<i><b>Source Data</b></i></li>
	</ul>
	<hr>  
	
