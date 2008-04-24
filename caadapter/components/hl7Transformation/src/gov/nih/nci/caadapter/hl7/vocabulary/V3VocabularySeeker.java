/*
 *  : /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/V3VocabularySeeker.java,v 1.00 Apr 24, 2008 1:10:36 PM umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE  
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
 */

package gov.nih.nci.caadapter.hl7.vocabulary;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.standard.*;
import gov.nih.nci.caadapter.common.standard.impl.MetaTreeMetaImpl;
import gov.nih.nci.caadapter.common.util.ClassLoaderUtil;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
 *          date        Apr 24, 2008
 *          Time:       1:10:36 PM $
 */
public class V3VocabularySeeker
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": V3VocabularySeeker.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/V3VocabularySeeker.java,v 1.00 Apr 24, 2008 1:10:36 PM umkis Exp $";


    MetaTreeMeta h3sVocTree = null;
    List<String> codeList = null;
    List<String> displayList = null;
    //MetaSegment head = null;

    public V3VocabularySeeker() throws ApplicationException
    {
        if (h3sVocTree == null) buildTree();
    }
    public V3VocabularySeeker(MetaTreeMeta h3sVocTreeS) throws ApplicationException
    {
        setVocaularyTree(h3sVocTreeS);
    }
    public void setVocaularyTree(MetaTreeMeta h3sVocTreeS) throws ApplicationException
    {
        if (h3sVocTreeS == null)
        {
            throw new ApplicationException("Null vocabulary head segment");
        }
        h3sVocTree = h3sVocTreeS;
    }

    private void buildTree() throws ApplicationException
    {
        ClassLoaderUtil loaderUtil = null;

        try
        {
            loaderUtil = new ClassLoaderUtil("instanceGen/vocab.xml", false);
        }
        catch(IOException ie)
        {
            throw new ApplicationException(ie.getMessage());
        }

        if (loaderUtil.getSizeOfInputStreams() == 0) throw new ApplicationException("The V3 Vocabulary resource can not be found.");

        //String VocFileName = "C:\\projects\\javasig\\data\\vocab.xml";
        boolean res = false;
        V3VocabularyTreeBuildEventHandler handler = null;
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            XMLReader producer = parser.getXMLReader();
            handler = new V3VocabularyTreeBuildEventHandler();

            producer.setContentHandler(handler);

            //producer.parse(new InputSource("file:///" + h3sFileName.replaceAll("\\", "/")));
            //producer.parse(new InputSource("file:///" + VocFileName));
            producer.parse(new InputSource(loaderUtil.getInputStreams().get(0)));
            h3sVocTree = new MetaTreeMetaImpl(handler.getHeadSegment());
        }
        catch(IOException e)
        {
            throw new ApplicationException("H3SVocabTreeBuildEventHandler IOException 1 : " + e.getMessage());
        }
        catch(Exception e)
        {
            throw new ApplicationException("H3SVocabTreeBuildEventHandler IOException 2 : " + e.getMessage());

            //e.printStackTrace(System.err);
        }
    }

    public MetaTreeMeta getVocabularyTree()
    {
        return h3sVocTree;
    }

    public String seekDisplayNameWithCode(String domainName, String findCode)
    {
        if ((findCode == null)||(findCode.trim().equals(""))) return null;
        String[] result = getVocabularyDomainCodes(domainName, findCode);
        if ((result == null)||(result.length != 2)) return null;
        return result[1];
    }
    public String seekCodeWithDisplayName(String domainName, String displayName)
    {
        if ((displayName == null)||(displayName.trim().equals(""))) return null;
        displayName = displayName.trim();
        String[] result = getVocabularyDomainCodes(domainName);
        if ((result == null)||(result.length != 2)) return null;
        if (result[0].equals("NoData")) return null;
        for(int i=0;i<displayList.size();i++)
        {
            String display = displayList.get(i).trim();
            if (display.equals(displayName)) return codeList.get(i);
        }
        return null;
    }

    public String[] getVocabularyDomainCodes(String domainName)
    {
        return getVocabularyDomainCodes(domainName, null);
    }
    public String[] getVocabularyDomainCodes(String domainName, String findCode)
    {
        codeList = new ArrayList<String>();
        displayList = new ArrayList<String>();
        if (findCode == null) findCode = "";
        findCode = findCode.trim();
        if ((domainName == null)||(domainName.trim().equals(""))) return null;
        domainName = domainName.trim();
        CommonNode temp = h3sVocTree.getHeadSegment();
        MetaSegment post = null;

        boolean found = false;
        boolean success = false;
        while(temp!=null)
        {
            CommonAttribute attribute = temp.getAttributes();
            if (!found)
            {
                for (int i=0;i<attribute.getAttributeItems().size();i++)
                {
                    CommonAttributeItem item = attribute.getAttributeItems().get(i);
                    if (item.getItemValue().equals(domainName))
                    {
                        if (temp instanceof MetaSegment)
                        {
                            found = true;
                            success = true;
                            post = (MetaSegment)temp;
                            break;
                        }
                    }
                }
            }
            else
            {
                if (h3sVocTree.isAncestor(post, temp))
                {
                    if (temp.getName().equals("leafTerm"))
                    {
                        String code = "";
                        String display = "";
                        for (int i=0;i<attribute.getAttributeItems().size();i++)
                        {
                            CommonAttributeItem item = attribute.getAttributeItems().get(i);
                            if (item.getName().equals("Code"))
                            {
                                code = item.getItemValue();
                            }
                            if (item.getName().equals("printName"))
                            {
                                display = item.getItemValue();
                            }
                            if ((code.length() > 0)&&(display.length() > 0))
                            {
                                codeList.add(code);
                                displayList.add(display);
                            }
                        }
                    }
                }
                else
                {
                    found = false;
                    post = null;
                    continue;
                }
            }

            try
            {
                temp = h3sVocTree.nextTraverse(temp);
                 if (temp == h3sVocTree.getHeadSegment()) break;
            }
            catch(ApplicationException ae)
            {
                return null;
            }
        }
        if (!success)
        {
            return retrySearchCodeWithMif(domainName, findCode);
            //return null;
        }

        int idx = -1;
        if (findCode.equals(""))
        {
            if (codeList.size() == 0) return retrySearchCodeWithMif(domainName, null);//return new String[] {"NoData", "Domain was found but no Data implemented"};
            if (codeList.size() == 1) return new String[] {codeList.get(0), displayList.get(0)};

            idx = FileUtil.getRandomNumber(0, codeList.size());
        }
        else
        {
            for (int i=0;i<codeList.size();i++)
            {
                if (findCode.equals(codeList.get(i).trim())) idx = i;
            }
        }
        if (idx < 0) return retrySearchCodeWithMif(domainName, findCode);//return new String[] {findCode, "This default code cannot be found"};
        else
        {
            if (idx >= displayList.size()) return new String[] {codeList.get(idx), "No Display Name"};
            else return new String[] {codeList.get(idx), displayList.get(idx)};
        }
    }

    private String[] retrySearchCodeWithMif(String domainName, String findCode)
    {
        if (findCode == null) findCode = "";
        findCode = findCode.trim();

        Hashtable datatypes = new Hashtable();
        try
        {
            InputStream is = this.getClass().getResourceAsStream("/datatypes");
            if (is == null)
            {
                is = this.getClass().getClassLoader().getResource("datatypes").openStream();
            }
            ObjectInputStream ois = new ObjectInputStream(is);
            datatypes = (Hashtable)ois.readObject();
            ois.close();
            is.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();

            return null;
        }

        Datatype datatypeItem = null;
        if (domainName!= null && !domainName.equals(""))
            datatypeItem = (Datatype)datatypes.get(domainName);
        else
            datatypeItem = (Datatype)datatypes.get(domainName);
        if (datatypeItem != null)
        {
            HashSet predefinedValues = datatypeItem.getPredefinedValues();
            if (predefinedValues.size() > 0)
            {
                if (findCode.equals(""))
                {
                    Iterator iter = predefinedValues.iterator();
                    int idx = FileUtil.getRandomNumber(0, predefinedValues.size());
                    int n = 0;
                    while(iter.hasNext())
                    {
                        if (n == idx) return new String[] {(String)iter.next(), "Random code with No display name"};
                        n++;
                    }
                }
                else
                {
                    if (predefinedValues.contains(findCode))
                    {
                        return new String[] {findCode, "No display name"};
                    }
                    else
                    {
                        return new String[] {findCode, "Domain and Data exist, but Not found this data."};
                    }
                }

            }
            else
            {
                if (findCode.equals("")) findCode = "NoData";
                return new String[] {findCode, "Domain exists, but No Data."};
            }
        }
        //return null;
        if (findCode.equals("")) findCode = "NoData";
        return new String[] {findCode, "Domain not found"};

    }

}
/**
 * HISTORY      : : V3VocabularySeeker.java,v $
 */
