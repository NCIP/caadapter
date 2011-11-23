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
					<tr>
						<td>Transformation Type:</td>
						<td>
							<menu>
								<input type="radio" value ="map" name="tranformationType" checked>Mapping
								<input type="radio" value ="xsl" name="tranformationType">XSLT
								<input type="radio" value ="xq" name="tranformationType">XQuery
							</menu>
						</td>
					</tr>
					<tr>
					<td>Transformation Scenario Name:</td><td> <input type=text name="scenarioName"><br></td>
					</tr>
					<tr>
					<td>Transformation Instruction(.map, .xsl, .xq):</td><td><INPUT TYPE=FILE NAME=mappingFileName></td>
					</tr>
					<tr>
					<td>Source Schema File(.xsd):</td><td><INPUT TYPE=FILE NAME=sourceXsdName></td>
					</tr>
					<tr>
					<td>Target Schema File(.xsd):</td><td><INPUT TYPE=FILE NAME=targetXsdName></td>
					</tr>
					<tr><td colspan=2>
					<input type=submit value="Add Transformation Scenario"></td>
					</tr>
				</FORM>
			</td>
		</tr>												
 </table>