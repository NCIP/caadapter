<%--L
  Copyright SAIC.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/caadapter/LICENSE.txt for details.
L--%>

<%@ page import="java.util.*" %>
<%@ taglib uri="/WEB-INF/tld/camod.tld" prefix="camod" %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
  	<center><h1>Map and Transfer Data Using caAdapter CMTS 1.0</h1></center>
	<br/>
	<hr/>
	<h1>Scope:</h1>
	<ul>
		<li>Transfer source XML to target XML</li>
		<li>Support mapping transformation</li>
		<li>Support XQuery transformation</li>
 		<li>Support XSLT transformation</li>
 		<li><a href="services/">Available in SOAP and RESTful services</a></li>
	</ul>
	<p/>
	<h1>Steps One: Create Mapping Scenario</h1>
	<ul>
		<li>Create source data schema file: <b>xsd</b></li>
		<li>Create target data schema file: <b>xsd</b></li>
		<li>Create transformation mapping file <b>(map)</b> between source schema file <b>(xsd)</b> and target schema file <b>(xsd)</b></li>
		<li><i>Optional</i>: Generate transformation XQuery file: <b>xql</b></li>
		<li><i>Optional</i>: Generate transformation XSLT file: <b>xsl</b></li>
	</ul>
	<h1>Step Two: Register Mapping Scenario</h1>
	<p>
	<ul>
		<li>Set unique scenario name</li>
		<li>Upload transformation instruction file: <b>map, xql or xsl</b></li>
		<li><i>Optional</i>: Upload source schema file:<b>xsd</b> (required only for mapping transformation)</li>
		<li><i>Optional</i>: Upload target schema file: <b>xsd</b> (required only for mapping transformation)</li>
		<!-- li>Upload the vocabulary mapping file if source is an HL7 V2 message and a vocabulary mapping file being  used: <b>vom</b></li -->
	</ul>
	<h1>Step Three: Tranfer Source Data into Target Data</h1>
	<p>
		<ul>
		<li>Create <a href="services/transfer?wsdl">SOAP service</a> client or <a href="services/restful?_wadl&_type=xml">RESTful service </a> client</li>
		<li>Prepare source data</li>
		<li>Alternate: upload data source to a web server</li>
		<li>Invoke service <b><i>transferData</i></b> operation using <i><b>Scenario Name</b></i>&nbsp; and &nbsp;<i><b>Source Data <u>String</u></b></i></li>
		<li>Alternate: invoke service <b><i>transferResource</i></b> operation using <i><b>Scenario Name</b></i>&nbsp; and &nbsp;<i><b>Source Data <u>URL</u></b></i></li>
	</ul>
	<hr>  

