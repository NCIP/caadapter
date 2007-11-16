rem setup class path
SET LIB=lib
SET EXC_LIB=%LIB%/caAdapter.jar;%LIB%/ojdbc14.jar

SET MAP_FILE=testDB.map
SET TARGET_SPEC=define.xml
SET OUT_PUT=dbOutput

rem setup database connection information
SET DB_DRIVER=oracle.jdbc.OracleDriver
SET DB_URL=jdbc:oracle:thin:@cbiodb2-d:1521:cadev
SET DB_USER=caadapter
SET DB_PASS=go!ar987r

java -cp dbToRdsTest.jar;%EXC_LIB% -Ddriver=%DB_DRIVER% -Durl=%DB_URL% -Duser=%DB_USER% -Dpass=%DB_PASS% gov.nih.nci.caadapter.sdtm.TestDBAPITransform4RDS %MAP_FILE% %OUT_PUT%