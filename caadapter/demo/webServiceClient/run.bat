# QA server
# SET END_URL=http://cbioqa101.nci.nih.gov:49080/caAdapterWS/ws/caAdapterTransformationService
# caAdpater Development server
# SET END_URL=http://cbiovdev5054.nci.nih.gov/caAdapterWS/ws/caAdapterTransformationService
# local server
SET END_URL=http://localhost:8080/caAdapterWS/ws/caAdapterTransformationService

# scenarioName
SET SCENARIO_NAME=local_test

# csvFileName, 
SET CSV_FILE=COCT_MT150003.csv

java -cp caAdapter_ws_client.jar;axis.jar;jaxrpc.jar;commons-logging-1.0.4.jar;commons-discovery-0.2.jar;saaj.jar;wsdl4j-1.5.1.jar gov.nih.nci.caadapter.hl7.demo.TestCaadapterWebservice %SCENARIO_NAME% %CSV_FILE% %2 %END_URL%