<%--L
  Copyright SAIC.

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
				<th class="dataTableHeader" scope="col" align="center">Transformation Type</th>
				<th class="dataTableHeader" scope="col" align="center">Instruction File (<i>map</i>)</th>
				<th class="dataTableHeader" scope="col" align="center">Source Schema File (<i>xsd</i>)</th>
				<th class="dataTableHeader" scope="col" align="center">Target Schema File (<i>xsd</i>)</th>
				<!-- th class="dataTableHeader" scope="col" align="center">Vocabulary Mappings (<i>vom</i>)</th -->
				<th class="dataTableHeader" scope="col" align="center">Date Created</th>
			</tr>
 			<logic:iterate id="item" name="scenarioList" 
						type="gov.nih.nci.cbiit.cmts.ws.object.ScenarioRegistration">
				<tr class="dataRowLight">
					<td class="dataCellText">
						<bean:write name="item" property="name"/>
					</td>
					<td class="dataCellText">
						<logic:notPresent name="item"  property="transferType">&nbsp;</logic:notPresent>
						<bean:write name="item" property="transferType"/>
					</td>
					<td class="dataCellText">
						<bean:write name="item" property="mappingFile"/>
					</td>
					<td class="dataCellText">
						<logic:notPresent name="item" property="sourceSpecFile">
							<i>N/A</i>
						</logic:notPresent>
						<logic:present name="item"  property="sourceSpecFile">	
				 			<bean:write name="item" property="sourceSpecFile"/>
                        </logic:present>
                        <logic:present name="item" property="subSourceSpecFiles">
                            <ul>
                                <lh>Included Files</lh>
                                <logic:iterate name="item" id="data" property="subSourceSpecFiles">
                                    <li><bean:write name="data"/></li>
                                </logic:iterate>
                            </ul>
                        </logic:present>
                    </td>
					<td class="dataCellText">
						<logic:notPresent name="item"  property="targetFile">					
							<i>N/A</i>
						</logic:notPresent>
						<logic:present name="item"  property="targetFile">
							<bean:write name="item" property="targetFile"/>
                        </logic:present>
                        <logic:present name="item" property="subTargetSpecFiles">
                            <ul>
                                <lh>Included Files</lh>
                                <logic:iterate name="item" id="data" property="subTargetSpecFiles">
                                    <li><bean:write name="data"/></li>
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

