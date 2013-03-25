/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.mif;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import gov.nih.nci.caadapter.common.util.FileUtil;


/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Dec 12, 2008
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.4 $
 * @date 	 DATE: $Date: 2009-06-22 23:05:57 $
 * @since caAdapter v4.2
 */

public class NormativeVersionSettingLoader {

    private SAXBuilder builder;
    private HashMap<String, MIFIndex> nomativeSetting;
    /**
     * @return the datatypeCoreAttributes
     */
    public HashMap<String, MIFIndex> getNormativeSettings() {
        return nomativeSetting;
    }

    public NormativeVersionSettingLoader()
    {
        builder = new SAXBuilder(false);
    }

    public void loadNomativeSetting(InputStream in)
    {
        Document document;
        try {
            document = builder.build(in);
            Element root = document.getRootElement();
            parseRootElement(root);
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void loadNomativeSetting(File f)
    {
        try
        {
            if (f == null) throw new FileNotFoundException("Null at NormativeVersionSettingLoader.loadNomativeSetting(file)");
            if (f.exists()) loadNomativeSetting(new FileInputStream(new File(f.getAbsolutePath())));
            else
            {
                String path = FileUtil.searchFile(f.getName());
                if (path != null ) loadNomativeSetting(new FileInputStream(new File(path)));
                else throw new FileNotFoundException("Not Found : " + f.getName());
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void parseRootElement(Element root)
    {
        nomativeSetting=new HashMap<String, MIFIndex>();
        Iterator itr=root.getChildren("normative").iterator();
        while (itr.hasNext())
        {
            Element normative= (Element)itr.next();
            String copyrightYear=normative.getAttributeValue("copyrightYear");
            String description=normative.getAttributeValue("description");
            String mifFilePath=normative.getChild("mifFile").getValue();
            String mifFilePathSearched = FileUtil.searchFile(mifFilePath);
            if (mifFilePathSearched != null) mifFilePath = mifFilePathSearched;
            String schemaFilePath=normative.getChild("schemaFile").getValue();
            String schemaFilePathSearched = FileUtil.searchFile(schemaFilePath);
            if (schemaFilePathSearched != null) schemaFilePath = schemaFilePathSearched;
            try {
                MIFIndex mifIndexInfos=MIFIndexParser.loadMIFIndexFromZipFile(mifFilePath);
                if (mifIndexInfos!=null)
                {
                    mifIndexInfos.setCopyrightYears(copyrightYear);
                    mifIndexInfos.setMifPath(mifFilePath);
                    mifIndexInfos.setSchemaPath(schemaFilePath);
                    mifIndexInfos.setNormativeDescription(description);
                    nomativeSetting.put(copyrightYear, mifIndexInfos);
                }
//				MIFIndexParser.printMIFIndex(mifIndexInfos);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//				nomativeSetting.put(nameKey, coreAttrList);
            }
        }

    public static void main(String[] args) throws Exception {
        String mifFilePath="conf/hl7-normative-setting.xml";
        NormativeVersionSettingLoader settingLoader=new NormativeVersionSettingLoader();
        settingLoader.loadNomativeSetting(new File(mifFilePath));

        HashMap<String, MIFIndex> normatives=settingLoader.getNormativeSettings();

    }
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.3  2009/03/18 15:50:36  wangeug
* HISTORY: enable wesstart to support multiple normatives
* HISTORY:
* HISTORY: Revision 1.2  2009/03/13 14:52:17  wangeug
* HISTORY: support multiple HL& normatives: reload a H3S/Map
* HISTORY:
* HISTORY: Revision 1.1  2009/03/12 15:00:46  wangeug
* HISTORY: support multiple HL& normatives
* HISTORY:
* HISTORY: Revision 1.1  2009/01/09 21:32:59  wangeug
* HISTORY: process core attribute seting with HL7 datatypes
* HISTORY:
**/