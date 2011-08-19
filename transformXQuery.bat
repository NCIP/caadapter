set source=workingspace\simpleMapping\shiporder.xml 
set xquery=workingspace\simpleMapping\testXQ.xq
set result=workingspace\simpleMapping\xqueryOut.xml
set libpath=lib\saxon9.jar;lib\saxon9-xqj.jar;lib\jdom.jar;dist\cmts.jar;

java -cp %libpath% gov.nih.nci.cbiit.cmts.transform.XQueryTransformer %source% %xquery% %result%
