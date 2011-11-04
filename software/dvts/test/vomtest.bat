echo **Test1 context as a VOM Directory
java -classpath ..\dist\caAdapter-dvts.jar gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation C:\apache-tomcat-6.0.20\temp\test\caAdapterWS\META-INF\caAdapterWS\scenarios\demo0\vom maritalStatus M 
echo **Test2 context as a VOM Directory with inverse translation
java -classpath ..\dist\caAdapter-dvts.jar gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation C:\apache-tomcat-6.0.20\temp\test\caAdapterWS\META-INF\caAdapterWS\scenarios\demo0\vom maritalStatus2 Single true
echo **Test3 context as a web service (error)
java -classpath ..\dist\caAdapter-dvts.jar gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation http://165.112.133.125:8080/caadapter-dvts/ContextVOMTranslation?context=demo0 maritalStatus3 W1  
echo **Test4 context as a web service (error)
java -classpath ..\dist\caAdapter-dvts.jar gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation http://165.112.133.125:8080/caadapter-dvts/ContextVOMTranslation?context=CTEP maritalStatus3 W1  
echo **Test5 context as a web VOM file 
java -classpath ..\dist\caAdapter-dvts.jar gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation http://165.112.132.250:8080/file_exchange/exampleXML.vom OtherCategory aa1
echo **Test6 context as a local VOM file
java -classpath ..\dist\caAdapter-dvts.jar gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation C:\apache-tomcat-6.0.20\temp\test\caAdapterWS\META-INF\caAdapterWS\scenarios\demo0\vom\exampleXML2.vom maritalStatus3 vc true

