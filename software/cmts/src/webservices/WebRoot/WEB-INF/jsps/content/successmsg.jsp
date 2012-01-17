<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<!-- target of anchor to skip menus -->
<a name="content" />
      
<table summary="" cellpadding="0" cellspacing="0" border="0" width="600" height="100%">
      <!-- banner begins -->      
		<tr>
			<td width="100%" class="welcomeTitleCopy2" height="20">Welcome to the caAdapter Common Mapping and Transformation Service (CMTS) Web Services Management Portal</td>
		</tr>
	
		<tr>
<%

    String messageS = request.getParameter("message");
    if ((messageS == null)||(messageS.trim().equals("")))
    {
%>
            Success!!
<%
    }
    else
    {
        %><%= messageS%><%
    }
%>

		</tr>												
 </table>