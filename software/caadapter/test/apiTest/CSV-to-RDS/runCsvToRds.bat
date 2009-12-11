rem setup class path
SET LIB=lib
SET EXC_LIB=%LIB%/caAdapter.jar;%LIB%/castor-0.9.9.jar;%LIB%/xercesImpl.jar;%LIB%/commons-logging-1.0.4.jar
SET MAP_FILE=testMap.map
SET SRC_DATA=testData.csv

java -cp .;csvToRdsTest.jar;%EXC_LIB% gov.nih.nci.caadapter.sdtm.TestCSVAPITransform4RDS %MAP_FILE% %SRC_DATA%


