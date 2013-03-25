<%--L
  Copyright SAIC, SAIC-Frederick.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/caadapter/LICENSE.txt for details.
L--%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<!-- target of anchor to skip menus -->
<a name="content" />
      
<table summary="" cellpadding="0" cellspacing="0" border="0" width="600" height="100%">
      <!-- banner begins -->      
		<tr>
			<td width="100%" class="welcomeTitleCopy2" height="20">Welcome to the caAdapter 4.3 Web Services Management Portal</td>
		</tr>
		<tr>
			<td valign="top">
				<FORM ACTION="/caAdapterWS/AddNewScenario" ENCTYPE="multipart/form-data" METHOD=POST>
					<table boarder=0>
					<tr>
					<td>Mapping Scenario Name:</td><td> <input type=text name="MSName"><br></td>
					</tr>
					<tr>
					<td>Mapping file:</td><td><INPUT TYPE=FILE NAME=mappingFileName></td>
					</tr>
					<tr>
					<td>SCS file:</td><td><INPUT TYPE=FILE NAME=scsFileName></td>
					</tr>
					<tr>
					<td>H3S file:</td><td><INPUT TYPE=FILE NAME=h3sFileName></td>
					</tr>
					<tr>
						<td>Vocabulary mapping file:</td><td><INPUT TYPE=FILE NAME=vocabularyMappingFileName></td>
					</tr>
					<tr><td colspan=2>
					<input type=submit value="Add Mapping Scenario"></td>
					</tr>
				</FORM>
			</td>
		</tr>												
 </table>