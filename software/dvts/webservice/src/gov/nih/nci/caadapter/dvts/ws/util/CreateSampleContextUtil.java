/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws.util;

import gov.nih.nci.caadapter.dvts.common.util.FileUtil;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 1, 2011
 * Time: 12:19:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateSampleContextUtil
{
    private File dir = null;
    public CreateSampleContextUtil(File f)
    {
        dir = f;
    }

    public boolean createSampleVOMFile()
    {
        try
        {
            String path = dir.getAbsolutePath();
            if (!path.endsWith(File.separator)) path = path + File.separator;
            FileUtil.saveStringIntoTemporaryFile(path + "example01.xml", test01Case(), false);
            FileUtil.saveStringIntoTemporaryFile(path + "example02.xml", test02Case(), false);
        }
        catch(Exception ee)
        {
            return false;
        }
        return true;
    }
    private String test01Case()
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<VocabularyMapping name=\"Test_Example01\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"C:\\projects\\hl7sdk\\data\\vom.xsd\">\n" +
                "\t<comment>\n" +
                "This vom file is mapping between; \n" +
                "Source : HL7 0007 definition table (marital status)  \n" +
                "Target : HL7 version 3.0 vocabulary domain 2.16.840.1.113883.11.16899 (ActUncertainty)\n" +
                "   </comment>\n" +
                "\t<domain name=\"maritalStatus\">\n" +
                "\t\t<comment>This is Example domain about marrital status.</comment>\n" +
                "\t\t<translation name=\"single\">\n" +
                "\t\t\t<comment>this means single.</comment>\n" +
                "\t\t\t<source value=\"S\"/>\n" +
                "\t\t\t<target value=\"SGL\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"single\">\n" +
                "\t\t\t<comment>this means married.</comment>\n" +
                "\t\t\t<source value=\"M\"/>\n" +
                "\t\t\t<target value=\"MRD\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"widow\">\n" +
                "\t\t\t<comment>this means widow.</comment>\n" +
                "\t\t\t<source value=\"W\" remark=\"black widow\"/>\n" +
                "\t\t\t<target value=\"WID\" remark=\"white widow\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"unknown\">\n" +
                "\t\t\t<comment>this means unknown.</comment>\n" +
                "\t\t\t<source value=\"U\" remark=\"totoally unknown\"/>\n" +
                "\t\t\t<target value=\"UNK\" remark=\"partly unknown\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<elseCase type=\"keepValue\"/>\n" +
                "\t\t<inverseElseCase type=\"assignValue\" value=\"Asigned\"/> \n" +
                "\t</domain>\n" +
                "\t<domain name=\"OtherCategory\">\n" +
                "\t\t<comment>This is OtherCategory.</comment>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"aa1\"/>\n" +
                "\t\t\t<target value=\"bb1\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"aa2\"/>\n" +
                "\t\t\t<target value=\"bb2\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"aa3\"/>\n" +
                "\t\t\t<target value=\"bb3\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"aa4\"/>\n" +
                "\t\t\t<target value=\"bb4\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"aa5\"/>\n" +
                "\t\t\t<target value=\"bb5\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<elseCase type=\"assignValue\" value=\"AsignedForward\"/> \n" +
                "\t\t<inverseElseCase type=\"keepValue\"/>\n" +
                "\t</domain>\n" +
                "</VocabularyMapping>";
    }
    private String test02Case()
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<VocabularyMapping name=\"Test_Example01\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"C:\\projects\\hl7sdk\\data\\vom.xsd\">\n" +
                "\t<comment>\n" +
                "This vom file was made for test instance of V2-V3 mapping which is between ADT^A03 and PRPA_MT402003. It works as a template for update or adding new domain/value\n" +
                "   </comment>\n" +
                "\t<domain name=\"AdministrativeGender\">\n" +
                "\t\t<comment>Source:HL70001(Administrative Sex), Target:2.16.840.1.113883.11.1(AdministrativeGender)</comment>\n" +
                "\t\t<translation name=\"Male\">\n" +
                "\t\t\t<source value=\"M\" remark=\"Male\"/>\n" +
                "\t\t\t<target value=\"M\" remark=\"Male\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"Female\">\n" +
                "\t\t\t<source value=\"F\" remark=\"Female\"/>\n" +
                "\t\t\t<target value=\"F\" remark=\"Female\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"unknown1\">\n" +
                "\t\t\t<source value=\"U\" remark=\"Unknown\"/>\n" +
                "\t\t\t<target value=\"UN\" remark=\"Undifferentiated\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"unknown2\">\n" +
                "\t\t\t<source value=\"O\" remark=\"Other\"/>\n" +
                "\t\t\t<target value=\"UN\" remark=\"Undifferentiated\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"unknown3\">\n" +
                "\t\t\t<source value=\"A\" remark=\"Ambiguous\"/>\n" +
                "\t\t\t<target value=\"UN\" remark=\"Undifferentiated\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"unknown4\">\n" +
                "\t\t\t<source value=\"N\" remark=\"Not applicable\"/>\n" +
                "\t\t\t<target value=\"UN\" remark=\"Undifferentiated\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<elseCase type=\"keepValue\"/>\n" +
                "\t\t<inverseElseCase type=\"assignValue\" value=\"Asigned\"/> \n" +
                "\t\t<!-- <inverseElseCase type=\"taggingPrefix\"/> -->\n" +
                "\t</domain>\n" +
                "\t<domain name=\"DiseaseCodingSystemOID\">\n" +
                "\t\t<translation name=\"ICD-10\">\n" +
                "\t\t\t<source value=\"I10\"/>\n" +
                "\t\t\t<target value=\"2.16.840.1.113883.6.3\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"ICD-9CM\">\n" +
                "\t\t\t<source value=\"I9C\"/>\n" +
                "\t\t\t<target value=\"2.16.840.1.113883.6.2\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"SNOMED\">\n" +
                "\t\t\t<source value=\"SNM\"/>\n" +
                "\t\t\t<target value=\"2.16.840.1.113883.6.5\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"SNOMED-International\">\n" +
                "\t\t\t<source value=\"SNM3\"/>\n" +
                "\t\t\t<target value=\"2.16.840.1.113883.6.51\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"SNOMED-DICOM\">\n" +
                "\t\t\t<source value=\"SDM\"/>\n" +
                "\t\t\t<target value=\"2.16.840.1.113883.6.53\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"LOINC\">\n" +
                "\t\t\t<source value=\"LN\"/>\n" +
                "\t\t\t<target value=\"2.16.840.1.113883.6.1\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<elseCase type=\"keepValue\"/> \n" +
                "\t\t<inverseElseCase type=\"keepValue\"/>\n" +
                "\t</domain>\n" +
                "\t<domain name=\"DiseaseCodingSystemCode\">\n" +
                "\t\t<translation name=\"ICD10\">\n" +
                "\t\t\t<source value=\"I10\"/>\n" +
                "\t\t\t<target value=\"ICD10\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"I9C\"/>\n" +
                "\t\t\t<target value=\"ICD9CM\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"SNM\"/>\n" +
                "\t\t\t<target value=\"SNM\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"SNM3\"/>\n" +
                "\t\t\t<target value=\"SNM3\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"SDM\"/>\n" +
                "\t\t\t<target value=\"SDM\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"LN\"/>\n" +
                "\t\t\t<target value=\"LN\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<elseCase type=\"keepValue\"/> \n" +
                "\t\t<inverseElseCase type=\"keepValue\"/>\n" +
                "\t</domain>\n" +
                "\t<domain name=\"DiseaseCodingSystemCodeName\">\n" +
                "\t\t<translation name=\"ICD10\">\n" +
                "\t\t\t<source value=\"I10\"/>\n" +
                "\t\t\t<target value=\"ICD-10\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"I9C\"/>\n" +
                "\t\t\t<target value=\"ICD-9CM\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"SNM\"/>\n" +
                "\t\t\t<target value=\"SNOMED\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"SNM3\"/>\n" +
                "\t\t\t<target value=\"SNOMED-International\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"SDM\"/>\n" +
                "\t\t\t<target value=\"SNOMED-DICOM\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation>\n" +
                "\t\t\t<source value=\"LN\"/>\n" +
                "\t\t\t<target value=\"LOINC\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<elseCase type=\"keepValue\"/> \n" +
                "\t\t<inverseElseCase type=\"keepValue\"/>\n" +
                "\t</domain>\n" +
                "\t<domain name=\"DiagnosisType\">\n" +
                "\t\t<comment>Source:HL70052(DiagnosisType), Target:2.16.840.1.113883.11.16228(ObservationDiagnosisTypes)</comment>\n" +
                "\t\t<translation name=\"Admitting\">\n" +
                "\t\t\t<source value=\"A\" remark=\"Admitting\"/>\n" +
                "\t\t\t<target value=\"ADMDX\" remark=\"admitting diagnosis\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"Final\">\n" +
                "\t\t\t<source value=\"F\" remark=\"Final\"/>\n" +
                "\t\t\t<target value=\"DISDX\" remark=\"discharge diagnosis\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"Working\">\n" +
                "\t\t\t<source value=\"W\" remark=\"Working\"/>\n" +
                "\t\t\t<target value=\"INTDX\" remark=\"intermediate diagnosis\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<elseCase type=\"keepValue\"/> \n" +
                "\t\t<inverseElseCase type=\"keepValue\"/>\n" +
                "\t</domain>\n" +
                "\t<domain name=\"AdmissionType\">\n" +
                "\t\t<comment>Source:HL70007(AdmissionType), Target:2.16.840.1.113883.11.19457(x_EncounterAdmissionUrgency)</comment>\n" +
                "\t\t<translation name=\"Accident\">\n" +
                "\t\t\t<source value=\"A\" remark=\"Accident\"/>\n" +
                "\t\t\t<target value=\"A\" remark=\"ASAP\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"Elective\">\n" +
                "\t\t\t<source value=\"C\" remark=\"Elective\"/>\n" +
                "\t\t\t<target value=\"EL\" remark=\"elective\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"Emergency\">\n" +
                "\t\t\t<source value=\"E\" remark=\"Emergency\"/>\n" +
                "\t\t\t<target value=\"EM\" remark=\"emergency\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"Labor and Delivery\">\n" +
                "\t\t\t<source value=\"L\" remark=\"Labor and Delivery\"/>\n" +
                "\t\t\t<target value=\"UR\" remark=\"urgent\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"Newborn\">\n" +
                "\t\t\t<source value=\"N\" remark=\"Newborn\"/>\n" +
                "\t\t\t<target value=\"UR\" remark=\"urgent\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"Routine\">\n" +
                "\t\t\t<source value=\"R\" remark=\"Routine\"/>\n" +
                "\t\t\t<target value=\"R\" remark=\"routine\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<translation name=\"Urgent\">\n" +
                "\t\t\t<source value=\"U\" remark=\"Urgent\"/>\n" +
                "\t\t\t<target value=\"UR\" remark=\"urgent\"/>\n" +
                "\t\t</translation>\n" +
                "\t\t<elseCase type=\"keepValue\"/> \n" +
                "\t\t<inverseElseCase type=\"keepValue\"/>\n" +
                "\t</domain>\n" +
                "</VocabularyMapping>";
    }
}
