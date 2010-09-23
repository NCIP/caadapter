<%@ page import="java.util.*" %>
<%@ taglib uri="/WEB-INF/tld/camod.tld" prefix="camod" %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
  	<h1>
  		<center>Map and Transfer Data Using caAdapter CMTS 1.0</center>
	</h1>
	<br>
	<hr>
	<h1>Scope:</h1>
	<ul>
		<li>Support XML defined with W3C schema</li>
		<li>Enable ISO 20190 data types</li>
		<li>Support source data in CSV format</li>
		<li>Support source data of HL7 v2 message in versions:
			<i>2.1, 2.2, 2.3, 2.3.1, 2.4, 2.5, 2.5.1, 2.6</i>
		</li>
		<li>Support HL7 v3 message</li>
		<li>Support <i>Clinical Document Architecture</i>(CDA) data</li>
	</ul>
	<p>
	<h1>Steps One: Create Mapping Scenario</h1>
	<ul>
		<li>Create source data schema file: <b>xsd</b></li>
		<li>Create target data schema file: <b>xsd</b></li>
		<!-- li>Create a vocabulary mapping file if source is HL7 V2 message: <b>vom</b></li -->
		<li>Create transformation mapping file <b>(map)</b> between source schema file <b>(xsd)</b> and target schema file <b>(xsd)</b></li>
	</ul>
	<h1>Step Two: Register Mapping Scenario</h1>
	<p>
	<ul>
		<li>Set unique scenario name</li>
		<li>Upload transformation mapping file: <b>map</b></li>
		<li>Upload source schema file:<b>xsd</b></li>
		<li>Upload target schema file: <b>xsd</b></li>
		<!-- li>Upload the vocabulary mapping file if source is an HL7 V2 message and a vocabulary mapping file being  used: <b>vom</b></li -->
	</ul>
	<h1>Step Three: Tranfer Source Data into Target Data</h1>
	<p>
	<ul>
		<li>Create caAdatper CMTS Web service client</li>
		<li>Prepare source data</li>
		<li>Invoke transformation service given <i><b>Scenario Name</b></i>&nbsp; and &nbsp;<i><b>Source Data</b></i></li>
	</ul>
	<hr>  
	
