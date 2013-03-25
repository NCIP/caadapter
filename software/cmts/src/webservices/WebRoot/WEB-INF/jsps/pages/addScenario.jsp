<%--L
  Copyright SAIC, SAIC-Frederick.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/caadapter/LICENSE.txt for details.
L--%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<!-- target of anchor to skip menus -->
<a name="content" />
      
<table summary="" cellpadding="0" cellspacing="0" border="0" width="800" height="100%">
      <!-- banner begins -->      
		<tr>
			<td width="100%" class="welcomeTitleCopy2" height="20">Welcome to the caAdapter CMTS Web Services Management Portal</td>
		</tr>
		<tr>
			<td valign="top">
				<FORM ACTION="AddNewScenario" ENCTYPE="multipart/form-data" METHOD="POST" autocomplete="off">
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
<%
                    String methodDef = "addScenarioRegistration";
                    String methodS = request.getParameter("methd");
                    if ((methodS == null)||(methodS.trim().equals(""))) methodS = methodDef;
                    else methodS = methodS.trim();

              if (methodS.equals(methodDef))
              {
%>
                    <tr>
						<td>Transformation Type:</td>
						<td>
							<menu>
								<input type="radio" value ="map" name="transformationType" checked>Mapping
								<input type="radio" value ="xsl" name="transformationType">XSLT
								<input type="radio" value ="xq" name="transformationType">XQuery
							</menu>
						</td>
                        <td>&nbsp;</td>
                    </tr>
					<tr>
					<td>Transformation Scenario Name:</td><td> <input type="text" name="scenarioName" ><br></td><td>&nbsp;</td>
					</tr>
					<tr>
					<td>Transformation Instruction(.map, .xsl, .xql):</td><td><INPUT TYPE="FILE" NAME="mappingFileName" accept="*.map, *.xsl, *.xslt, *.xql, *.xq"></td><td>&nbsp;</td>
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
					<td>Source Schema File(xsd or zip):</td><td><INPUT TYPE="FILE" NAME="sourceXsdName<%= iStr%>" accept="text/xml, application/rdf+xml, application/zip"></td>
                    <td><a href="addScenario.do?methd=<%= methodS%>&sourceNum=<%= (srcNum + 1)%>&targetNum=<%= tgtNum%>">Add a Source XSD field</a></td>
                    </tr>
                <%
                         }
                         else
                         {
                %>
                    <tr>
					<td>&nbsp;</td><td><INPUT TYPE="FILE" NAME="sourceXsdName<%= iStr%>" accept="text/xml, application/rdf+xml, application/zip"></td><td>&nbsp;</td>
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
					<td>Target Schema File(xsd or zip):</td><td><INPUT TYPE="FILE" NAME="targetXsdName<%= iStr%>" accept="text/xml, application/rdf+xml, application/zip"></td>
                    <td><a href="addScenario.do?methd=<%= methodS%>&sourceNum=<%= srcNum%>&targetNum=<%= (tgtNum+1)%>">Add a Target XSD field</a></td>
                    </tr>
                <%
                         }
                         else
                         {
                %>
                    <tr>
					<td>&nbsp;</td><td><INPUT TYPE="FILE" NAME="targetXsdName<%= iStr%>" accept="text/xml, application/rdf+xml, application/zip"></td><td>&nbsp;</td>
					</tr>

                <%
                         }
                    }
                %>
                    <!--<tr>
					<td>Delete Confirmation Code(Required):</td><td><INPUT TYPE="text" NAME="deleteSecurityCode"></td>
                    <td>Keep this value for delete this scenario</td>
                    </tr> -->
                    <tr><td colspan=3>
                    <INPUT TYPE="hidden" NAME="methd" VALUE="<%= methodS%>">
                    <input type="submit" value="Add Transformation Scenario"></td>
					</tr>
                <%
              }
              else if (methodS.equalsIgnoreCase("deleteScenario"))
              {
                %>
                    <tr>
					<td>Scenario Name:</td><td> <input type="text" name="scenarioName"><br></td><td>&nbsp;</td>
					</tr>
                    <tr>
					<td>Delete Confirmation Code:</td><td><INPUT TYPE="text" NAME="deleteSecurityCode"></td>
                    <td>&nbsp;</td>
                    </tr>
                    <tr><td colspan=3>
                    <INPUT TYPE="hidden" NAME="methd" VALUE="<%= methodS%>">
                    <input type="submit" value="Delete Transformation Scenario"></td>
					</tr>
 <%
              }
 %>
                </FORM>
			</td>
		</tr>												
 </table>