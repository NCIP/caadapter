#java -cp saxon9.jar net.sf.saxon.Query samples\query\tour.xq start=e5 >tour1.html 
#java -jar saxon9.jar -it main samples\styles\tour.xsl start=e5 >tour.html
#java net.sf.saxon.Transform -s:source -xsl:stylesheet -o:output
#java -jar .\..\..\lib\saxon9.jar -s:XSLT\famousePerson.xml -xsl:XSLT\person.xsl -o:XSLT\testOut.xml
java -jar .\..\..\lib\saxon9.jar -s:famousePerson.xml -xsl:person.xsl -o:testOut.xml
