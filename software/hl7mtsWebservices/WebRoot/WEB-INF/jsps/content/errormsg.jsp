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
		<tr>
			<logic:present name="rtnMessage">
				<h2><bean:write name="rtnMessage"/></h2>
			</logic:present>
			<logic:notPresent name="rtnMessage">
						Unable to Login. Please try again.
			</logic:notPresent>
		</tr>												
 </table>