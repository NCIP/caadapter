# QA server
# SET END_URL=http://caadapter-qa.nci.nih.gov/caadapterWS-cmts/services/restful

# DEV server
SET END_URL=http://caadapter-dev.nci.nih.gov/caadapterWS-cmts/services/restful

#local server
#SET END_URL=http://localhost:30210/caadapterWS-cmts/services/restful
#SET END_URL=http://localhost:30210/caadapterWS-cmts/services/restful

# scenarioName
SET SCENARIO_NAME=testXSLT

# opeartionName
#SET OPERATION_NAME=transferData
SET OPERATION_NAME=transferResource

# source data resource identifier

#SET SRC_DATA=shipOrder.xml
SET SRC_DATA=http://caadapter-dev.nci.nih.gov/caadapter-cmts/sourceTest.html


java -cp wsClient.jar;lib\cxf-2.4.2.jar gov.nih.nci.cbiit.cmts.test.restfull.Client %SCENARIO_NAME% %SRC_DATA% %2 %END_URL% %OPERATION_NAME%