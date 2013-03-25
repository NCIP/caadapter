<%--L
  Copyright SAIC, SAIC-Frederick.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/caadapter/LICENSE.txt for details.
L--%>

<%@ page import="java.util.*" %>
<%@ taglib uri="/WEB-INF/tld/camod.tld" prefix="camod" %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
  	<h1><center>
  			caAdapter Domain Value Translation Service (DVTS) 1.0
		</center>
	</h1>
	<br>
	<hr>
	<h1>Scope:</h1>
	<ul>
		<li>Support domain value translating via Web Service</li>
		<li>Support domain value translating via Restful Service</li>
		<li>Provide API tool for domain value translating</li>
		<li>Provide GUI tool for creating domain value mapping data (future plan)</li>
	</ul>
	<p>
	<h1>Steps One -- Create a VOcabulary Mapping (VOM) File</h1>
	<ul>
	    <li>A VOM file includes domain value mapping information and is essentially needed for this service.</li>
        <!--li>Before using the term 'DVM', 'VOM' (VOcabulary Mapping) was used. If you see 'VOM' in caAdapter domain, you should understand that it is the same meaning as 'DVM'.</li-->
	    <li>Unfotunately, caAdapter cannot currently support to creating VOM file but in the future.</li>
		<li>One VOM file is composed of <b>'Domains'</b> such as 'marital status' or 'insurance type'.</li>
		<li>You can see the VOM schema if you click <a target="_blank" href="./xsd/vom.xsd">here</a>.</li>
		<li>You can see an example VOM file if you click <a target="_blank" href="./examples/V2_to_V3.vom">here</a>.</li>
	</ul>
	<h1>Step Two -- Create a VOM Context</h1>
	<p>
	<ul>
		<li>VOM context means a bunch of VOM files shared and managed by one business community or organization. </li>
		<li>For using this service, user doesn't have to know each VOM file name, but the context name. </li>
		<li>One context must be mapped with its physical link information such as local folder, web URL or data base link point. </li>
		<li>This link information is stored to 'contextLinkAddress.properties' file located in the system's working directory or its sub folder.</li>
		<li>You can see an example property file if you click <a target="_blank" href="./examples/contextLinkAddress.properties_example.html">here</a>.</li>
		<li>This service provides a prototype for creating a web based VOM context at <a target="_blank" href="./ManageCaadapterWSUser">here.</a> Please check the 'Installation Guide' document for administrator ID and password. </li>
        <li>After creating your context in this service, you can register your VOM file at <a target="_blank" href="./VOMFileRegistration">here</a>.</li>
    </ul>
	<h1>Step Three -- Translating vocabulary with web browser</h1>
	<p>
	<ul>
		<li>For vocabulary translating, user or system has to provide at least three data - context name, domain name and input value, and one more - 'inverse' tag if you want inverse translation.</li>
		<li>Translating via web service: You can see an example usage if you click <a target="_blank" href="./ContextVOMTranslation?context=SampleContext01&domain=maritalStatus&value=S&inverse=false">here</a>.</li>
		<li>Translating via restful service: You can see an example usage if you click <a target="_blank" href="./restful/context/SampleContext01/domain/maritalStatus/value/S/inverse">here</a>.</li>

	</ul>
    <h1>Step Four -- Translating vocabulary with JAVA API</h1>
	<p>
	<ul>
        <li>Translating with JAVA API: <a href="./download/caAdapter-dvts.jar">download jar file</a>download jar file for java programming and developing.</li>
        <li>The follwing line is a sample Java code for simple translation (return value is single String.).</li>
        <li><font face="courier">translatedValue = gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation.translate(addressFile, context, domain, value, inverse);</font></li>

        <ul>
            <li>This code must be caught by an Exception block.</li>
            <li>Parameter 1 - String addressFile: the physical file location of 'contextLinkAddress.properties' file which is mentioned at 'Step two'. If this file is located in the system working folder or its sub-folder, this parameter value can be null or omitted.</li>
            <li>Parameter 2 - String context: Context symbolic abbreviation included in the 'contextLinkAddress.properties' file. If not included or the property file is not prepared, this value must be a physical URL or file folder including VOM files. null value not allowed.</li>
            <li>Parameter 3 - String domain: domain name, null value not allowed.</li>
            <li>Parameter 4 - String value: source value to be translated, null value not allowed.</li>
            <li>Parameter 5 - boolean inverse: If this inverse translation is applied, this value must 'true'. This parameter can be omitted, and the default value is 'false'</li>
            <li>Return value: single String object</li>
        </ul>
        <li>The follwing line is a sample Java code for Response Object.</li>
        <li><font face="courier">responseObject = gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation.translateWithObj(addressFile, context, domain, value, inverse);</font></li>

        <ul>
            <li>No Exception catching block is needed.</li>
            <li>The Parameters are the same as the above sample code.</li>
            <li>Return object: single gov.nih.nci.caadapter.dvts.common.meta.VocabularyMappingData</li>
            <ul>
                <li>From this object, the translated value can be received with the following code.</li>
                <li><font face="courier">String translatedValue = responseObject.getMappingResults().getResult().get(0)</font></li>

            </ul>
        </ul>
        <li>At a command window, this command line calls the following GUI screen for domain value translating test.</li>
        <li><font face="courier">java -classpath <i>&lt;caAdapter-dvts.jar file path&gt;</i> gov.nih.nci.caadapter.dvts.ui.ContextVOMTranslationGUI <i>&lt;contextLinkAddress.properties file path&gt;</i></font></li>
        <li><img src="./images/guitest_screen.jpg" width="300" height="150"></img></li>


    </ul>
    <hr>
	
