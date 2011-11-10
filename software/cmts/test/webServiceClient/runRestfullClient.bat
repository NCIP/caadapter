# QA server

# SET END_URL=http://cbioqa101.nci.nih.gov:49080/caAdapterWS/services/restful/transfer

# caAdpater Development server

# SET END_URL=http://cbiovdev5054.nci.nih.gov/caAdapterWS/services/restful/transfer

# local server
SET END_URL=http://localhost:30210/caadapterWS-cmts/services/restful/transfer

# SET END_URL=http://localhost:8080/caadapterWS-cmts/services/restful/transfer

# DEV server
# SET END_URL=http://caadapter-dev.nci.nih.gov/caadapterWS-cmts/services/restfull/transfer

# scenarioName

SET SCENARIO_NAME=testTwo


# sourceFileName 

SET SRC_FILE=shipOrder.xml

# SET SRC_TYPE=xml




java -cp wsClient.jar;lib\cxf-2.4.2.jar gov.nih.nci.cbiit.cmts.test.restfull.Client %SCENARIO_NAME% %SRC_FILE% %2 %END_URL%