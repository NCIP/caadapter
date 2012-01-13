

<!-- target of anchor to skip menus -->
<a name="content" />
      
<table summary="" cellpadding="0" cellspacing="0" border="0" width="600" height="100%">
		<tr><td>
<%

    String messageS = request.getParameter("message");
    if ((messageS == null)||(messageS.trim().equals("")))
    {
%>
            <logic:present name="rtnMessage">
				<h2><bean:write name="rtnMessage"/></h2>
			</logic:present>
			<logic:notPresent name="rtnMessage">
						Error. Please try again.
			</logic:notPresent>
<%
    }
    else
    {
        %><%= messageS%><%
    }
%>
        </td></tr>												
 </table>