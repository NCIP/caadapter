<%--L
  Copyright SAIC, SAIC-Frederick.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/caadapter/LICENSE.txt for details.
L--%>

	<h1>
		<center>
			<bean:message key="caAdapter.webservice.access.message" />
		</center>
	</h1>
    <hr />
    <logic:messagesPresent>
      <html:messages id="msg">
          <p><strong><font color="red"><bean:write name="msg" /></font></strong></p>
      </html:messages>
    </logic:messagesPresent>

    <logic:messagesPresent message="true">
      <html:messages message="true" id="msg">
          <p><strong><bean:write name="msg" /></strong></p>
      </html:messages>
    </logic:messagesPresent>

    <logic:messagesNotPresent message="true">
      <logic:messagesNotPresent>
          <p>&nbsp;</p>
      </logic:messagesNotPresent>
    </logic:messagesNotPresent>

	<logic:present name="rtnMessage">
		<h2><bean:write name="rtnMessage"/></h2>
	</logic:present>

	<logic:present name="results" >
		<bean:define id="scenarioList"
					type="java.util.ArrayList"
					name="results"/> 
 		<table cellpadding="3" cellspacing="0" border="0" class="dataTable" width="100%">
			<tr><th class="dataTableHeader" scope="col" align="center">Scenario Name</th>
				<th class="dataTableHeader" scope="col" align="center">Map File (<i>map</i>)</th>
				<th class="dataTableHeader" scope="col" align="center">Source Specification (<i>scs</i>)</th>
				<th class="dataTableHeader" scope="col" align="center">Target Specification (<i>h3s</i>)</th>
				<th class="dataTableHeader" scope="col" align="center">Vocabulary Mappings (<i>vom</i>)</th>
				<th class="dataTableHeader" scope="col" align="center">Date Created</th>
			</tr>
 			<logic:iterate id="item" name="scenarioList" 
						type="gov.nih.nci.caadapter.ws.object.ScenarioRegistration">
				<tr class="dataRowLight">
					<td class="dataCellText">
						<bean:write name="item" property="name"/>
					</td>
					<td class="dataCellText">
						<bean:write name="item" property="mappingFile"/>
					</td>
					<td class="dataCellText">
						<logic:notPresent name="item"  property="sourceSpecFile">					
							<i>N/A</i>
						</logic:notPresent>
						<logic:present name="item"  property="sourceSpecFile">	
				 			<bean:write name="item" property="sourceSpecFile"/>
						</logic:present>
					</td>
					<td class="dataCellText">
						<bean:write name="item" property="targetFile"/>
					</td>
					<td class="dataCellText">
						<logic:notPresent name="item"  property="vocabuaryMappings">					
							<i>N/A</i>
						</logic:notPresent>
						<logic:present name="item"  property="vocabuaryMappings">
							<bean:define id="vomList" name="item" property="vocabuaryMappings"/>
								<ul>							
									<logic:iterate id="vomItem" name="vomList" 
										type="java.lang.String">
										<li><bean:write name="vomItem"/></li>							
									</logic:iterate>
								</ul>
						</logic:present>
					</td>
					<td class="dataCellText">
						<bean:write name="item" property="dateCreate"/>
						<!-- bean:define id="cDate" name="item" property="dateCreate"/  -->
						<!-- fmt:formatDate value="${cDate}" type="date" dateStyle="short"/ -->
					</td>
				</tr>
			</logic:iterate>
		</table>
	</logic:present>

