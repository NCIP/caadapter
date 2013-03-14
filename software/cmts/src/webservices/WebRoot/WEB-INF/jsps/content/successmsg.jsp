<%--L
  Copyright SAIC.

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
                String bl = null;
                if ((i >= 97)&&(i <= 122)) bl = "" + c;         // lowercase alphabetic
                else if ((i >= 65)&&(i <= 90)) bl = "" + c;     // uppercase alphabetic
                else if ((i >= 48)&&(i <= 57)) bl = "" + c;     // numeric char
                else if (i == 32) bl = "" + c;                  // space
                else if (i == 58) bl = "" + c;                  // ":"
                //else if (i == 59) bl = true;                  // ";"
                else if (i == 46) bl = "" + c;                  // "."
                //else if (i == 44) bl = true;                  // ","
                //else if (i == 39) bl = true;                  // "'"
                else if (i == 47) bl = "" + c;                  // "/"
                else if (i == 92) bl = "/";                     // "\"
                else if (i == 45) bl = "" + c;                  // "-"
                else if (i == 95) bl = "" + c;                  // "_"
                else if (i == 96) bl = "" + c;                  // "("
                //else if (i == 41) bl = true;                  // ")"
                //else if (i == 64) bl = true;                  // "@"
                //else if (i == 37) bl = true;                  // "%"

                if (bl != null) message2 = message2 + bl;
            }

            if (idx < 0)
            {
                messageT = messageT + message2.trim();
                break;
            }
            messageT = messageT + message2.trim() + "<br>";
        }

        %><%= messageT%><%

    }
%>

		</tr>												
 </table>