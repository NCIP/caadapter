

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
        String messageS2 = messageS;
        String messageT = "";
        while (true)
        {
            String message1 = "";
            int idx = messageS2.toLowerCase().indexOf("<br>");
            if (idx < 0) message1 = messageS2;
            else
            {
                message1 = messageS2.substring(0, idx);
                messageS2 = messageS2.substring(idx+4);
            }

            String message2 = "";

            for (char c:message1.toCharArray())
            {
                byte b = (byte) c;
                int i = (int) b;
                boolean bl = false;
                if ((i >= 97)&&(i <= 122)) bl = true;         // lowercase alphabetic
                else if ((i >= 65)&&(i <= 90)) bl = true;     // uppercase alphabetic
                else if ((i >= 48)&&(i <= 57)) bl = true;     // numeric char
                else if (i == 32) bl = true;                  // space
                else if (i == 58) bl = true;                  // ":"
                else if (i == 59) bl = true;                  // ";"
                else if (i == 46) bl = true;                  // "."
                else if (i == 44) bl = true;                  // ","
                else if (i == 39) bl = true;                  // "'"
                else if (i == 47) bl = true;                  // "/"
                else if (i == 92) bl = true;                  // "\"

                if (bl) message2 = message2 + c;
            }

            if (idx < 0)
            {
                messageT = messageT + message2;
                break;
            }
            messageT = messageT + message2 + "<br>";
        }

        %><%= messageT%><%
    }
%>
        </td></tr>												
 </table>