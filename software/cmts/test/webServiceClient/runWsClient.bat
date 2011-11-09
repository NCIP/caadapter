# QA server

# SET END_URL=http://cbioqa101.nci.nih.gov:49080/caAdapterWS/ws/caAdapterTransformationService

# caAdpater Development server

# SET END_URL=http://cbiovdev5054.nci.nih.gov/caAdapterWS/ws/caAdapterTransformationService

# local server
SET 
# END_URL=http://localhost:30210/caadapterWS-cmts/services/transfer

# SET END_URL=http://localhost:8080/caadapterWS-cmts/services/transfer

# DEV server
SET END_URL=http://caadapter-dev.nci.nih.gov/caadapterWS-cmts/services/transfer

# SET END_URL=http://localhost:30210/caadapterWS-cmts/ws/AxisServlet/cmtsTransformationService

# scenarioName

SET SCENARIO_NAME=testTwo


# sourceFileName 

SET SRC_FILE=shipOrder.xml

# SET SRC_TYPE=xml


#java -cp cmtsWsClient.jar;lib/axis.jar;lib/jaxrpc.jar;lib/commons-logging-1.0.4.jar;lib/commons-discovery-0.2.jar;lib/saaj.jar;lib/wsdl4j-1.5.1.jar gov.nih.nci.cbiit.cmts.test.ws.client.CmtsWebserviceClient %SCENARIO_NAME% %SRC_FILE% %2 %END_URL%

java -cp cmtsWsClient.jar;lib/axis.jar;lib/jaxrpc.jar;lib/commons-logging-1.0.4.jar;lib/commons-discovery-0.2.jar;lib/saaj.jar;lib/wsdl4j-1.5.1.jar gov.nih.nci.cbiit.cmts.test.ws.client.CmtsWebserviceClient %SCENARIO_NAME% %SRC_FILE% %2 %END_URL%