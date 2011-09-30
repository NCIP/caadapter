set source=workingspace\simpleMapping\shiporder.xml 
set instruction=workingspace\simpleMapping\testXS.xsl
set result=workingspace\simpleMapping\xsltOut.xml
set libpath=lib\saxon9.jar

java -cp %libpath% net.sf.saxon.Transform -s:%source% -xsl:%instruction% -o:%result%
