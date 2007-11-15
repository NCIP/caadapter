rem setup class path
SET LIB=lib
SET EXC_LIB=%LIB%/caAdapter.jar;%LIB%/jdom.jar;%LIB%/castor-0.9.9.jar;%LIB%/xercesImpl.jar;%LIB%/commons-logging-1.0.4.jar;%LIB%/junit-4.3.1.jar

rem SET FILE_HOME=C:/cvsRepository/cleang/caadapter/workingspace/CSV_to_HL7_V3_Example/150003
rem SET FILE_HOME=C:/cvsRepository/QARelease/cleanTest/apiTest
SET FILE_HOME=.
java -cp ./demoTest.jar;%EXC_LIB% -Dhl7.transformation.src.file=%FILE_HOME%/COCT_MT150003.csv -Dhl7.transformation.map.file=%FILE_HOME%/150003.map junit.textui.TestRunner gov.nih.nci.caadapter.hl7.junit.DemoCSV2HL7v3TransformationTests
