/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*




* <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.function.FunctionUtil;
import gov.nih.nci.caadapter.common.function.FunctionVocabularyMappingEventHandler;
import gov.nih.nci.caadapter.common.function.FunctionVocabularyXMLMappingEventHandler;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.ClassLoaderUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.XMLValidator;

import java.io.*;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.net.ConnectException;
import java.net.URL;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.*;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since HL7 SDK v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:53:50 $
 */
public class FunctionVocabularyMapping
{

    /**
     * Logging VocabularyMapping used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: FunctionVocabularyMapping.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/FunctionVocabularyMapping.java,v 1.4 2008-06-09 19:53:50 phadkes Exp $";

    //private String domain = "";
    private String[] typeNamePossibleList = {"VOM_File_Local", "URL", "VOM_File_URL"};
    private String[] methodNamePossibleList = {"translateValue", "translateInverseValue"};
    private String type = typeNamePossibleList[0];
    private String value;
    private String tagForXMLFormatVOMFile = "%%XMLFormatVOMFile%%";
    //private String filePathOfVOMxsd = FileUtil.getWorkingDirPath() + "\\map\\functions\\vom.xsd";
    private String defaultDomainName = "defaultDomain";
    private String pathNameJustBeforeValidated = "";
    private boolean inverseTag = false;

    public FunctionVocabularyMapping()
    {
    }
    /*
    public FunctionVocabularyMapping(String fileName) throws FunctionException
    {
        setValue(fileName);
    }
    public FunctionVocabularyMapping(String typ, String fileName) throws FunctionException
    {
        setType(typ);
        setValue(fileName);
    }
    */
    public FunctionVocabularyMapping(String typ, String fileName, boolean inverse) throws FunctionException
    {
        if ((inverse)&&(typ.trim().equals(typeNamePossibleList[1])))
            throw new FunctionException("URL cannot be applied in case of inverse mapping : " + type, 720, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        inverseTag = inverse;
        setType(typ);
        setValue(fileName);
    }

    public String translateValue(String input) throws FunctionException
    {
        if (inverseTag) throw new FunctionException("Mis-assigning forward mapping : ", 724, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        if (type.equals(typeNamePossibleList[0]))
        {
            String mapData = checkMappingFileAndExtractData(value);
            if (mapData.equals(tagForXMLFormatVOMFile))
            {
                return searchXMLMappingFile(input, false, value);
            }
            else return searchMappingFile(input, false, mapData);
        }
        else if (type.equals(typeNamePossibleList[1]))
        {
            return searchMappingURL(input);
        }
        else if (type.equals(typeNamePossibleList[2]))
        {
            return searchXMLMappingFile(input, false, value);
        }
        else throw new FunctionException("This is not a valid Vocabulary mapping type(forward) : " + type, 710, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

    }
    public String translateInverseValue(String input) throws FunctionException
    {
        if (!inverseTag) throw new FunctionException("Mis-assigning inverse mapping : ", 722, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        if (type.equals(typeNamePossibleList[0]))
        {
            String mapData = checkMappingFileAndExtractData(value);
            if (mapData.equals(tagForXMLFormatVOMFile))
            {
                return searchXMLMappingFile(input, true, value);
            }
            else return searchMappingFile(input, true, mapData);
        }
        else if (type.equals(typeNamePossibleList[2]))
        {
            return searchXMLMappingFile(input, true, value);
        }
        else throw new FunctionException("This is not a valid Vocabulary mapping type(inverse) : " + type, 711, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

    }

    public String[] extractDomainAndFileName(String fileNameSrc) throws FunctionException
    {
        String domain = "";
        String fileName = "";
        if (fileNameSrc.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fileNameSrc = fileNameSrc.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());

        int idx = fileNameSrc.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR);
        if (idx > 0)
        {
            fileName = fileNameSrc.substring(0, idx);
            domain = fileNameSrc.substring(idx + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR.length());
        }
        else fileName = fileNameSrc;
        String[] arrayRes = {domain, fileName};
        return arrayRes;
    }
    private String checkMappingFileAndExtractData(String fileNameSrc) throws FunctionException
    {
        String[] arrayRes = extractDomainAndFileName(fileNameSrc);
        String domain = arrayRes[0];
        String fileName = arrayRes[1];
        File file = new File(fileName);

        if (type.equals(typeNamePossibleList[2]))
        {
            return getDomains(fileName).get(0);
        }


        if (file.exists())
        {
            if (file.isFile()) {}
            else throw new FunctionException("This vocabulary mapping path is not a file. : " + fileName, 702, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        else throw new FunctionException("Not exist vocabulary mapping file : " + fileName, 701, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        //String fileName = value;
        //if (fileName.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());
        String readLineOfFile = "";
        String content = "";
        boolean domainTag = false;
        boolean findTag = false;
        if (domain.equals(""))
        {
            domainTag = true;
            findTag = true;
        }
        try
        {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            int n = 0;
            while((readLineOfFile=br.readLine())!=null)
            {
                readLineOfFile = readLineOfFile.trim();

                if (readLineOfFile.equals("")) continue;

                if ((n==0)&&(readLineOfFile.startsWith("<")))
                {
                    fr.close();
                    br.close();
                    return tagForXMLFormatVOMFile;
                }
                n++;
                if (readLineOfFile.startsWith("#")) continue;

                String startDomain = "&StartDomain:";
                String endDomain = "&EndDomain";
                if (readLineOfFile.toUpperCase().startsWith(startDomain.toUpperCase()))
                {
                    if (domain.equals("")) throw new FunctionException("Domain name is not given. : " + value + " : "+fileNameSrc, 708, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
                    readLineOfFile = readLineOfFile.substring(startDomain.length()).trim();
                    String domainName = "";
                    String achar = "";
                    for (int i=0;i<readLineOfFile.length();i++)
                    {
                        achar = readLineOfFile.substring(i, i+1);
                        if ((achar.equals(" "))||(achar.equals(";"))||(achar.equals("\t"))) break;
                        domainName = domainName + achar;
                    }
                    if (domainName.equalsIgnoreCase(domain))
                    {
                        findTag = true;
                        domainTag = true;
                        readLineOfFile = readLineOfFile.substring(domainName.length()).trim();
                        if (readLineOfFile.startsWith(";")) readLineOfFile = readLineOfFile.substring(1).trim();
                        if (readLineOfFile.equals("")) continue;
                    }
                }
                if (readLineOfFile.toUpperCase().startsWith(endDomain.toUpperCase()))
                {
                    domainTag = false;
                    if ((!domain.equals(""))&&(findTag)) break;
                }
                if (domainTag)
                {
                    if (!readLineOfFile.equals("")) content = content + readLineOfFile + "\t";
                }
            }
            fr.close();
            br.close();
        }
        catch(IOException e)
        {
            throw new FunctionException("Vocabulary mapping file reading error. : " + value, 703, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        if (!findTag) throw new FunctionException("This domain name is not found. : " + domain, 709, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        return content;
    }

    private String searchMappingFile(String searchStr, boolean searchInverse, String content) throws FunctionException
    {
        if (content == null) content = "";
        if (searchStr == null) searchStr = "";
        if (content.trim().equals("")) throw new FunctionException("Content is Empty.", 786, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        if (searchStr.trim().equals("")) throw new FunctionException("Search word is empty.", 787, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        //String content = mapData;
        String readLineOfFile = "";

        String s1 = "";
        String s2 = "";
        String st = "";
        boolean ignoreTag = false;
        String cont = "";
        for (int i=0;i<content.length();i++)
        {
            s1 = content.substring(i, i+1);
            if (s1.equals(";")) s1 = "\t";
            st = s2 + s1;
            if (st.equals("/*")) ignoreTag = true;
            if (st.equals("*/"))
            {
                ignoreTag = false;
                s1 = "";
                s2 = "";
            }
            if (!ignoreTag) cont = cont + s2;

            s2 = s1;
        }

        int idx1 = 0;
        StringTokenizer strTok = new StringTokenizer(cont, "\t");
        String elseCasesTag = "&elsecase";
        String elseInverseCasesTag = "&inverseelsecase";
        String caseKeepOriginalValue = "keepvalue";
        String caseDoubleQuotation = "doublequotation";
        String caseSingleQuotation = "singlequotation";
        String caseSpace = "space";
        String caseZero = "zero";
        String caseNull = "null";
        String caseValueAssign = "valueassign";
        String caseErrorMessage = "errormessage";
        String caseMakeAnError = "makeanerror";
        String caseTaggingSuffix = "taggingsuffix";
        String caseTaggingPrefix = "taggingprefix";
        String elseCasesWhatToDo = "";
        String assignedValue = "";
        String elseTag = "";
        if (searchInverse) elseTag = elseInverseCasesTag;
        else elseTag = elseCasesTag;
        while(strTok.hasMoreTokens())
        {
            readLineOfFile = (strTok.nextToken()).trim();
            if ((searchInverse)&&(readLineOfFile.toLowerCase().startsWith(elseCasesTag))) continue;
            if ((!searchInverse)&&(readLineOfFile.toLowerCase().startsWith(elseInverseCasesTag))) continue;
            if (readLineOfFile.toLowerCase().startsWith(elseTag))
            {
                elseCasesWhatToDo = readLineOfFile.substring(elseTag.length()).trim();
                String temp = "";
                boolean colonTag = false;
                for(int i=0;i<elseCasesWhatToDo.length();i++)
                {
                    String achar = elseCasesWhatToDo.substring(i, i+1);
                    if (achar.equals(":"))
                    {
                        colonTag = true;
                        continue;
                    }
                    if (!colonTag) continue;
                    if (!achar.equals(" ")) temp = temp + achar;
                    if ((temp.toLowerCase().equals(caseValueAssign))||
                        (temp.toLowerCase().equals(caseTaggingSuffix))||
                        (temp.toLowerCase().equals(caseTaggingPrefix)))
                    {
                        String temp2 = elseCasesWhatToDo.substring(i+1).trim();
                        if (temp2.startsWith("=")) temp2 = temp2.substring(1).trim();
                        else throw new FunctionException("Invalid Systax : '=' character has to be the next of "+temp + "' tag.", 797, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);

                        if ((temp2.startsWith("'"))||(temp2.startsWith("\""))) temp2 = temp2.substring(1);
                        if ((temp2.endsWith("'"))||(temp2.endsWith("\""))) temp2 = temp2.substring(0,temp2.length()-1);
                        assignedValue = temp2;
                        if (assignedValue.equals("")) throw new FunctionException("'"+temp + "' tag has to have its own value : ", 798, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
                        break;
                    }
                }
                if (!colonTag) throw new FunctionException("Invalid ElseCase or InverseElseCase syntax, colon ':' character must be follow.", 788, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

                elseCasesWhatToDo = temp.toLowerCase();
                continue;
            }
            idx1 = readLineOfFile.indexOf(Config.VOCABULARY_MAP_FILE_VALUE_SEPARATOR);

            if (idx1 <= 0) continue;

            String src = "";
            String tgt = "";

            if(!searchInverse)
            {
                src = readLineOfFile.substring(0, idx1).trim();
                tgt = readLineOfFile.substring(idx1 + 2).trim();
            }
            else
            {
                tgt = readLineOfFile.substring(0, idx1).trim();
                src = readLineOfFile.substring(idx1 + 2).trim();
                //System.out.println("Invers case : search : " + searchStr + ", src : " + src + ", tgt : " + tgt);
                //if (src.equals(searchStr)) System.out.println("BINGO!!! ");
            }
            if (src.equals(searchStr)) return tgt;
        }
        String inverse;
        if (searchInverse) inverse = "inverse";
        else inverse = "forward";
        String errMsg = "Vocabulary mapping (" + inverse + ") search failure. Not found '" + searchStr + "' in " + value + " file";

        if (elseCasesWhatToDo.equals(caseKeepOriginalValue)) return searchStr;
        else if (elseCasesWhatToDo.equals(caseKeepOriginalValue + "s")) return searchStr;
        else if (elseCasesWhatToDo.equals(caseDoubleQuotation)) return "\"\"";
        else if (elseCasesWhatToDo.equals(caseSingleQuotation)) return "''";
        else if (elseCasesWhatToDo.equals(caseSpace)) return " ";
        else if (elseCasesWhatToDo.equals(caseZero)) return "0";
        else if (elseCasesWhatToDo.equals(caseNull)) return "";
        else if (elseCasesWhatToDo.equals(caseValueAssign)) return assignedValue;
        else if (elseCasesWhatToDo.equals(caseErrorMessage)) return errMsg;
        else if (elseCasesWhatToDo.equals(caseTaggingSuffix)) return searchStr + assignedValue;
        else if (elseCasesWhatToDo.equals(caseTaggingPrefix)) return assignedValue + searchStr;
        else if (elseCasesWhatToDo.equals(caseMakeAnError)) {}
        else if (elseCasesWhatToDo.equals("")) {}
        else throw new FunctionException("Invalid elseCasesWhatToDo tag : " + elseCasesWhatToDo + ":" + content, 799, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
        throw new FunctionException(errMsg, 705, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
    }

    public List<String> checkMappingFileAndGatheringDomainName(String fileNameSCR) throws FunctionException
    {
        String domain = "";
        String fileName = "";
        if (fileNameSCR.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR) > 0)
        {
            String[] arrayRes = extractDomainAndFileName(fileNameSCR);
            domain = arrayRes[0];
            fileName = arrayRes[1];
        }
        else fileName = fileNameSCR;

        if (type.equals(typeNamePossibleList[0]))
        {
            String mapData ="";
            try {  mapData = checkMappingFileAndExtractData(fileNameSCR); }
            catch(FunctionException fe)
            {
                if (fe.getErrorNumber() == 708) mapData = "";
                else throw fe;
            }

            if (mapData.equals(tagForXMLFormatVOMFile))
            {
                return getDomains(fileName);
            }
        }
        else if (type.equals(typeNamePossibleList[2]))
        {
            return getDomains(fileName);
        }


        List<String> listDomain = new ArrayList<String>();
        boolean domainTag = false;
        boolean findTag = false;

        if (fileName.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());

        File file = new File(fileName);
        if (file.exists())
        {
            if (file.isFile()) {}
            else throw new FunctionException("This vocabulary mapping path is not a file(2). : " + fileName, 702, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        else throw new FunctionException("Not exist vocabulary mapping file(2) : " + fileName, 701, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        String readLineOfFile = "";

        try
        {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            while((readLineOfFile=br.readLine())!=null)
            {
                readLineOfFile = readLineOfFile.trim();
                if (readLineOfFile.startsWith("#")) continue;
                String startDomain = "&StartDomain:";
                String endDomain = "&EndDomain";
                if (readLineOfFile.toUpperCase().startsWith(startDomain.toUpperCase()))
                {
                    readLineOfFile = readLineOfFile.substring(startDomain.length()).trim();
                    String domainName = "";
                    String achar = "";
                    for (int i=0;i<readLineOfFile.length();i++)
                    {
                        achar = readLineOfFile.substring(i, i+1);
                        if ((achar.equals(" "))||(achar.equals(";"))||(achar.equals("\t"))) break;
                        domainName = domainName + achar;
                    }
                    if (!domainTag) listDomain.add(domainName);
                    domainTag = true;
                }
                if (readLineOfFile.toUpperCase().startsWith(endDomain.toUpperCase()))
                {
                    domainTag = false;
                }
                int idx1 = readLineOfFile.indexOf(Config.VOCABULARY_MAP_FILE_VALUE_SEPARATOR);
                if (idx1 > 0) findTag = true;
            }
            fr.close();
            br.close();
        }
        catch(IOException e)
        {
            throw new FunctionException("Vocabulary mapping file reading error(2). : " + value, 703, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        if (!findTag) throw new FunctionException("This is not a Vocabulary Mapping file found. : ", 714, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        return listDomain;
    }


    public void checkFileName(String fileName) throws FunctionException
    {
        //if (fileName.startsWith(FileUtil.getWorkingDirPath())) fileName = fileName.replace(FileUtil.getWorkingDirPath(), Config.CAADAPTER_HOME_DIR_TAG);
        //checkMappingFileAndGatheringDomainName(fileName);

        if (type.equals(typeNamePossibleList[0]))
        {
            String mapData = checkMappingFileAndExtractData(fileName);
            if (mapData.equals(tagForXMLFormatVOMFile))
            {
                getDomains(fileName);
            }
            else checkMappingFileAndGatheringDomainName(fileName);
        }
        else if (type.equals(typeNamePossibleList[2]))
        {
            getDomains(fileName);
        }
    }

    public void setValue(String fileName) throws FunctionException
    {
        if (fileName.startsWith(FileUtil.getWorkingDirPath())) fileName = fileName.replace(FileUtil.getWorkingDirPath(), Config.CAADAPTER_HOME_DIR_TAG);

        if (type.equals(typeNamePossibleList[0]))
        {
            String mapData = checkMappingFileAndExtractData(fileName);
            if (mapData.equals(tagForXMLFormatVOMFile))
            {
                getDomains(fileName);
            }
            else checkMappingFileAndGatheringDomainName(fileName);
        }
        else if (type.equals(typeNamePossibleList[2]))
        {
            getDomains(fileName);
        }

        value = fileName;
    }
    public String searchXMLMappingFile(String input, boolean inversTag, String path) throws FunctionException
    {
        return searchXMLMappingURL(0, input, path, inversTag).get(0);
    }
    public List<String> getDomains(String path) throws FunctionException
    {
        validateVOMdataFile(path);
        return searchXMLMappingURL(1, "", path, false);
    }
    public int getDomainNumber(String path) throws FunctionException
    {
        return getDomains(path).size();
    }
    public String getFirstResult(String path) throws FunctionException
    {
        //validateVOMdataFile(path);
        return searchXMLMappingURL(2, "", path, false).get(0);
    }

    private void validateVOMdataFile(String path) throws FunctionException
    {
        if (path.equals(pathNameJustBeforeValidated)) return;
        if (path.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) path = path.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());
        String[] arrayRes = extractDomainAndFileName(path);
        String domain = arrayRes[0];
        String pathName = arrayRes[1];
        String targetPath = "";
        if (pathName.equals(pathNameJustBeforeValidated)) return;
        File file = new File(pathName);
        if (file.exists())
        {
            if (file.isFile()) targetPath = pathName;
        }
        if (targetPath.equals(""))
        {
            try
            {
                targetPath = FunctionUtil.downloadFromURLtoTempFile(pathName);
            }
            catch(IOException ie)
            {
                throw new FunctionException("Invalid URL address of vom file ("+pathName+"). : " + ie.getMessage());
            }
        }
        String xsdFilePath = "";
        File aFile = null;




        String xsdFileClassPath = Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION;
        ClassLoaderUtil loader = null;
        try
        {
            loader = new ClassLoaderUtil(xsdFileClassPath);
        }
        catch(IOException ie)
        {
            throw new FunctionException("Not Found xml schema file " + Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION + " for vom file ("+path+") : " + ie.getMessage());
        }
        if (loader.getFileNames().size() == 0)
        {
            throw new FunctionException("Not Found xml schema file () " + Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION + " for vom file ("+path+")");
        }
        aFile = new File(loader.getFileNames().get(0));
        xsdFilePath = aFile.getAbsolutePath();


        /*
        try
        {
        	URL fileURL= ClassLoader.getSystemResource(Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION);
        	String filePath=fileURL.getFile();
        	System.out.println("function spec file Path:"+filePath + " : " + Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION);

            aFile = new File(filePath);
//            new FileReader(aFile);
        }
        catch (Exception e)
        {
            throw new FunctionException("Not Found xml schema file " + Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION + " for vom file ("+path+") : " + e.getMessage());
        }
        xsdFilePath = aFile.getAbsolutePath();
        System.out.println("xsd absoultePath:"+xsdFilePath);
        System.out.println(" File exists :"+ aFile.exists());

        try
        {
            FileReader reader = new FileReader(aFile);
            BufferedReader br = new BufferedReader(reader);
            while(true)
            {
                String str = br.readLine();
                if (str == null) break;
                System.out.println("   %%% : " + str);
            }

        }
        catch(FileNotFoundException fe)
        {
            throw new FunctionException("Not Found xml schema file " + Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION + " for vom file ("+path+") : " + fe.getMessage());
        }
        catch(IOException fe)
        {
            throw new FunctionException("IO Exception " + Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION + " for vom file ("+path+") : " + fe.getMessage());
        }
        */
        XMLValidator xmlValidator = new XMLValidator(targetPath, xsdFilePath);
        ValidatorResults result = xmlValidator.validate();
        if (!result.isValid())
        {
            String messages = "";
            List<Message> liM = result.getAllMessages();
            for (int i=0;i<liM.size();i++)
            {
                Message mes = liM.get(i);
                messages = messages + "\n" + mes.toString();
            }
            throw new FunctionException("Invalid xml format vom file ("+path+") with " + xsdFilePath + messages);
        }
        pathNameJustBeforeValidated = path;
    }

    private List<String> searchXMLMappingURL(int jobTag, String searchStr, String path, boolean inversTag) throws FunctionException
    {
        String[] arrayRes = extractDomainAndFileName(path);
        String domain = arrayRes[0];
        String pathName = arrayRes[1];
        List<String> list = new ArrayList<String>();
        //if (domain.equals("")) domain = defaultDomainName;

        if (pathName.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) pathName = pathName.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());
        File file = new File(pathName);

        if (file.exists())
        {
            pathName = file.toURI().toString().replace("file:/", "file:///");
        }

        FunctionVocabularyXMLMappingEventHandler handler = null;
        int n = 0;
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            XMLReader producer = parser.getXMLReader();
            //ContentHandler handler = new FunctionVocabularyMappingEventHandler();

            handler = new FunctionVocabularyXMLMappingEventHandler();

            producer.setContentHandler(handler);

            producer.parse(new InputSource(pathName));

        }
        catch(SAXException e)
        {
            throw new FunctionException("XMLMappingEventHandler SAXException (XML) : " + e.getMessage(), 715, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(ConnectException e)
        {
            throw new FunctionException("XMLMappingEventHandler ConnectException (XML) : " + e.getMessage(), 715, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(IOException e)
        {
            throw new FunctionException("XMLMappingEventHandler IOException (XML) : " + e.getMessage(), 716, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(ParserConfigurationException e)
        {
            throw new FunctionException("XMLMappingEventHandler ParserConfigurationException (XML) : " + e.getMessage(), 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(Exception e)
        {
            throw new FunctionException("XMLMappingEventHandler Unknown Exception (XML) : " + e.getMessage(), 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        //System.out.println("DDDD : " + message + " : " +messageLevel + " : " + mappingValue + " : " + handler.getMappingDomain()+ " : " + handler.getMappingSource());
        if (jobTag == 0)
        {
            String lines = handler.getLinesSearchValue(domain);
            list.add(searchMappingFile(searchStr, inversTag, lines));
        }
        else if (jobTag == 1)
        {
            list = handler.getDomains();
        }
        else if (jobTag == 2)
        {
            list.add(handler.getFirstValue(""));
        }
        return list;
    }

    public String searchMappingURL(String searchStr) throws FunctionException
    {
        String message = "";
        String messageLevel = "";
        String mappingValue = "";

        FunctionVocabularyMappingEventHandler handler = null;
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            XMLReader producer = parser.getXMLReader();
            //ContentHandler handler = new FunctionVocabularyMappingEventHandler();
            handler = new FunctionVocabularyMappingEventHandler();
            producer.setContentHandler(handler);
            //System.out.println("E1");
            producer.parse(new InputSource(modifyURLForSearch(value, searchStr)));
            //System.out.println("E2");
            message = handler.getMessge();
            //System.out.println("E3");
            messageLevel = handler.getMessgeLevel();
            //System.out.println("E4");
            mappingValue = handler.getMappingResult();
            //System.out.println("EEEE : " + message + " : " +messageLevel + " : " + mappingValue);

        }
        catch(SAXException e)
        {
            throw new FunctionException("VocMappingEventHandler SAXException : " + e.getMessage(), 715, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(ConnectException e)
        {
            throw new FunctionException("VocMappingEventHandler ConnectException : " + e.getMessage(), 715, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(ParserConfigurationException e)
        {
            throw new FunctionException("VocMappingEventHandler ParserConfigurationException : " + e.getMessage(), 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(IOException e)
        {
            throw new FunctionException("VocMappingEventHandler IOException : " + e.getMessage(), 716, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(Exception e)
        {
            throw new FunctionException("VocMappingEventHandler Unknown Exception : " + e.getMessage(), 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        //System.out.println("DDDD : " + message + " : " +messageLevel + " : " + mappingValue + " : " + handler.getMappingDomain()+ " : " + handler.getMappingSource());
        if (messageLevel.equalsIgnoreCase("Error"))
            throw new FunctionException("MappingEventHandler (Invalid Data) : " + message, 718, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        else return mappingValue;
    }

    public void setType(String typ) throws FunctionException
    {
        boolean check = false;
        String msg = "";
        for (int i=0;i<typeNamePossibleList.length;i++)
        {
            String or = "";
            if (i == 0) or = " ";
            else if (i == (typeNamePossibleList.length-1)) or = " or ";
            else or = ", ";
            if (typeNamePossibleList[i].equals(typ)) check = true;
            msg = msg + or + "'" + typeNamePossibleList[i] + "'";
        }
        if (check)
        {
            type = typ;
        }
        else throw new FunctionException("Vocabulary mapping type must be either" + msg + ". : " + typ, 707, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
    }

    public String getValue()
    {
        return value;
    }
    public String getType()
    {
        return type;
    }
    public boolean getInverseTag()
    {
        return inverseTag;
    }
    public String getDefaultDomainName()
    {
        return defaultDomainName;
    }
    public String[] getTypeNamePossibleList()
    {
        return typeNamePossibleList;
    }
    public String[] getMethodNamePossibleList()
    {
        return methodNamePossibleList;
    }

    private String modifyURLForSearch(String val, String str)
    {
        String sharpChar = Config.VOCABULARY_MAP_URL_SEARCH_DATA_INPUT_POINT_CHARACTER;
        int idx = val.indexOf("=" + sharpChar);
        String val1 = val.substring(0, idx + sharpChar.length());
        String val2 = val.substring(idx + sharpChar.length() + 1);
        idx = val2.indexOf(sharpChar);
        if (idx < 0) return val.replace(sharpChar, str);
        else
        {
            String val3 = val1 + val2.substring(idx);
            //System.out.println("FFFF : " + val + " : " + val1 + " : "+val3);
            return val3.replace(sharpChar, str);
        }
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/06 18:54:55  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/10/03 21:54:31  umkis
 * HISTORY      : Removed the problem of calling vom.xsd file from the resource zip file.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 18:26:25  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/11/15 05:47:57  umkis
 * HISTORY      : Fixing Bugs item #3420
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/11/03 19:03:14  umkis
 * HISTORY      : error fixing related to Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/11/02 18:38:05  umkis
 * HISTORY      : XML format vom file must be validated before recorded into a map file with the xml schema file which is directed by Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/11/01 02:05:50  umkis
 * HISTORY      : Extending function of vocabulary mapping : URL XML vom file can use.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/10/30 17:02:32  umkis
 * HISTORY      : Change two function names transferMappedValue and transferInverseMappedValue to translateValue and inverseTranslateValue.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/10/11 19:17:57  umkis
 * HISTORY      : error fixing related to {caAdapter_Home} tag.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/10/11 18:37:40  umkis
 * HISTORY      : protect inputting 'URL' type when inverse mapping.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/10/02 20:17:47  umkis
 * HISTORY      : error correction from 'tranferMappedValue' to 'transferMappedValue'
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/10/02 18:05:08  umkis
 * HISTORY      : Vocabulary mapping function upgrade which allow to mapping through a URL and domained .vom file.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/09/08 20:19:12  umkis
 * HISTORY      : When searching is failure, error message is differentiated whether inverse searching or forward.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/09/06 23:17:05  umkis
 * HISTORY      : add Inverse Else case
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/09/06 21:34:21  umkis
 * HISTORY      : The file path of source file change expression from absolute path to relative such as {caAdapter_Home}\workingspace.....
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/09/06 19:50:09  umkis
 * HISTORY      : The file path of source file change expression from absolute path to relative such as {caAdapter_Home}\workingspace.....
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/09/06 18:52:31  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/09/06 18:20:35  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 */
