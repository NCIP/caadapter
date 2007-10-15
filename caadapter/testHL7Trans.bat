rem setup class path
SET EXC_LIB=lib/caAdapter.jar;lib/jdom.jar;lib/castor-0.9.9.jar;lib/xercesImpl.jar;lib/commons-logging-1.0.4.jar;lib/ostermillerutils_1_04_03_for_java_1_4.jar;junit-4.3.1.jar

SET FILE_HOME=C:/cvsRepository/cleang/caadapter/workingspace/CSV_to_HL7_V3_Example/150003
java -cp ./demoTest.jar;%EXC_LIB% -Dhl7.transformation.src.file=%FILE_HOME%/COCT_MT150003.csv -Dhl7.transformation.map.file=%FILE_HOME%/150003.map junit.textui.TestRunner gov.nih.nci.caadapter.hl7.junit.DemoCSV2HL7v3TransformationTests
