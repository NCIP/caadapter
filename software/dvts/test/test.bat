echo **Test1 context EVS
java -classpath ..\dist\caAdapter-dvts.jar gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation -f:cc.txt caBIG/EVS maritalStatus3 W1  

echo **Test2 context CTEP
java -classpath ..\dist\caAdapter-dvts.jar gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation -f:cc.txt CTEP DiseaseCodingSystemOID I10  

