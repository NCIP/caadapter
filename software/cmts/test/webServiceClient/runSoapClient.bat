# QA server
#SET END_URL=http://caadapter-qa.nci.nih.gov/caadapterWS-cmts/services/transfer

# DEV server
SET END_URL=http://caadapter-dev.nci.nih.gov/caadapterWS-cmts/services/transfer

# local server
#SET  END_URL=http://localhost:30210/caadapterWS-cmts/services/transfer
#SET END_URL=http://localhost:8080/caadapterWS-cmts/services/transfer

# scenarioName
SET SCENARIO_NAME=testTwo

# opeartionName
#SET OPERATION_NAME=transferData
SET OPERATION_NAME=transferResource

#source data resource identifier
#SET SRC_DATA=shipOrder.xml

#SET SRC_DATA=http://localhost:30210/caadapter-cmts/sourceTest.html
SET SRC_DATA=http://caadapter-dev.nci.nih.gov/caadapter-cmts/sourceTest.html

java -cp wsClient.jar;lib/axis.jar;lib/jaxrpc.jar;lib/commons-logging-1.0.4.jar;lib/commons-discovery-0.2.jar;lib/saaj.jar;lib/wsdl4j-1.5.1.jar gov.nih.nci.cbiit.cmts.test.ws.client.CmtsWebserviceClient %SCENARIO_NAME% %SRC_DATA% %2 %END_URL% %OPERATION_NAME%