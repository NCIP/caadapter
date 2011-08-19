set source=workingspace\simpleMapping\shiporder.xml 
set mapping=workingspace\simpleMapping\mapping.map
set result=workingspace\simpleMapping\mappingOut.xml
set libpath=lib\saxon9.jar;lib\saxon9-xqj.jar;lib\jdom.jar;dist\cmts.jar;lib\xercesImpl.jar;lib\caAdapter.jar

java -cp %libpath% gov.nih.nci.cbiit.cmts.transform.MappingTransformer %source% %mapping% %result%
