<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<!-- target of anchor to skip menus -->
<a name="content" />
      
<table summary="" cellpadding="0" cellspacing="0" border="0" width="600" height="100%">
      <!-- banner begins -->      
		<tr>
			<td width="100%" class="welcomeTitleCopy2" height="20">Welcome to the caAdapter CMTS Web Services Management Portal</td>
		</tr>
		<tr>
			<td valign="top">
				<FORM ACTION="AddNewScenario" ENCTYPE="multipart/form-data" METHOD=POST>
					<table border="0">
                    <!--<tr>
						<td>Job Type:</td>
						<td>
							<menu>
								<input type="radio" value ="add" name="jobType" checked>New Scenario
								<input type="radio" value ="update" name="jobType">Update Scenario
								<input type="radio" value ="delete" name="jobType">Delete Scenarion
							</menu>
						</td>
                        <td>&nbsp;</td>
                    </tr>  -->
                    <tr>
						<td>Transformation Type:</td>
						<td>
							<menu>
								<input type="radio" value ="map" name="tranformationType" checked>Mapping
								<input type="radio" value ="xsl" name="tranformationType">XSLT
								<input type="radio" value ="xq" name="tranformationType">XQuery
							</menu>
						</td>
                        <td>&nbsp;</td>
                    </tr>
					<tr>
					<td>Transformation Scenario Name:</td><td> <input type="text" name="scenarioName"><br></td><td>&nbsp;</td>
					</tr>
					<tr>
					<td>Transformation Instruction(.map, .xsl, .xql):</td><td><INPUT TYPE="FILE" NAME="mappingFileName"></td><td>&nbsp;</td>
					</tr>
                <%

                    String strS = request.getParameter("sourceNum");
                    String strT = request.getParameter("targetNum");

                    int srcNum = 1;
                    if ((strS != null)&&(!strS.trim().equals("")))
                    {
                        try
                        {
                            srcNum = Integer.parseInt(strS);
                        }
                        catch(NumberFormatException ne)
                        {
                            srcNum = 1;
                        }
                    }

                    int tgtNum = 1;

                    if ((strT != null)&&(!strT.trim().equals("")))
                    {
                        try
                        {
                            tgtNum = Integer.parseInt(strT);
                        }
                        catch(NumberFormatException ne)
                        {
                            tgtNum = 1;
                        }
                    }

                    for(int i=0;i<srcNum;i++)
                    {
                        String iStr = "" + i;

                        if (i == 0)
                        {
                %>
                    <tr>
					<td>Source Schema File(.xsd):</td><td><INPUT TYPE="FILE" NAME="sourceXsdName<%= iStr%>"></td>
                    <td><a href="addScenario.do?methd=addScenarioRegistration&sourceNum=<%= (srcNum + 1)%>&targetNum=<%= tgtNum%>">Add a Source XSD field</a></td>
                    </tr>
                <%
                         }
                         else
                         {
                %>
                    <tr>
					<td>&nbsp;</td><td><INPUT TYPE="FILE" NAME="sourceXsdName<%= iStr%>"></td><td>&nbsp;</td>
					</tr>
                <%
                         }
                    }

                    for(int i=0;i<tgtNum;i++)
                    {
                        String iStr = "" + i;

                        if (i == 0)
                        {
                %>
                    <tr>
					<td>Target Schema File(.xsd):</td><td><INPUT TYPE="FILE" NAME="targetXsdName<%= iStr%>"></td>
                    <td><a href="addScenario.do?methd=addScenarioRegistration&sourceNum=<%= srcNum%>&targetNum=<%= (tgtNum+1)%>">Add a Target XSD field</a></td>
                    </tr>
                <%
                         }
                         else
                         {
                %>
                    <tr>
					<td>&nbsp;</td><td><INPUT TYPE="FILE" NAME="targetXsdName<%= iStr%>"></td><td>&nbsp;</td>
					</tr>
                <%
                         }
                    } %>
                    <tr><td colspan=3>
					<input type=submit value="Add Transformation Scenario"></td>
					</tr>
				</FORM>
			</td>
		</tr>												
 </table>