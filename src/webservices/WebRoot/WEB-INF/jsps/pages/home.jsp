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
		<li>Transfer source XML to target XML</li>
		<li>Support mapping transformation</li>
		<li>Support XQuery transformation</li>
 		<li>Support XSLT transformation</li>
	</ul>
	<p>
	<h1>Steps One: Create Mapping Scenario</h1>
	<ul>
		<li>Create source data schema file: <b>xsd</b></li>
		<li>Create target data schema file: <b>xsd</b></li>
		<!-- li>Create a vocabulary mapping file if source is HL7 V2 message: <b>vom</b></li -->
		<li>Create transformation mapping file <b>(map)</b> between source schema file <b>(xsd)</b> and target schema file <b>(xsd)</b></li>
		<li><i>Optional</i>: Generate transformation XQuery file: <b>xq</b></li>
		<li><i>Optional</i>: Generate transformation XSLT file: <b>xsl</b></li>
	</ul>
	<h1>Step Two: Register Mapping Scenario</h1>
	<p>
	<ul>
		<li>Set unique scenario name</li>
		<li>Upload transformation instruction file: <b>map, xq, xsl</b></li>
		<li><i>Optional</i>: Upload source schema file:<b>xsd</b> (required only for mapping transformation)</li>
		<li><i>Optional</i>: Upload target schema file: <b>xsd</b> (required only for mapping transformation)</li>
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
	
