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
			<td width="100%" class="welcomeTitleCopy2" height="20">Please login to the caAdapter Common Mapping and Transformation Service (CMTS) Web Services Management Portal</td>
		</tr>
	
		<tr>
			<td valign="top">
				<FORM ACTION="/caAdapterWS/validateUser"  METHOD=POST>
					<table boarder=0>
					<tr>
					<td>Enter UserId:</td><td> <input type=text name="userid"><br></td>
					</tr>
					<tr>
					<td>Enter Password:</td><td> <input type=password name="password"><br></td>
					</tr>
					<tr><td colspan=2>
					<input type=submit value="Login"></td>
					</tr>
				</FORM>
			</td>
		</tr>												
 </table>