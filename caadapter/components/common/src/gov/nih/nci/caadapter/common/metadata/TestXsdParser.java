/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.metadata;

import java.util.Iterator;
import junit.framework.TestCase;

public class TestXsdParser extends TestCase {

    public void testGeneration() throws Exception {
        XsdModelMetadata xsdModel = new XsdModelMetadata();
        String xmlSchema="workingspace/GME_Example/example.com.xsd";
        xsdModel.parseSchema(xmlSchema);

        //list class mapping
        Iterator objKeys=xsdModel.getObjectMap().keySet().iterator();
        System.out.println("TestXsdParser.testGeneration() .. \nclassMapping:");
        while(objKeys.hasNext())
        {
        	String objKey=(String)objKeys.next();
        	System.out.println(XsdUtil.writeXsdObjectString((ObjectMetadata)xsdModel.getObjectMap().get(objKey)));
        }

//      list attribute mapping
        Iterator attrKeys=xsdModel.getAttributeMap().keySet().iterator();
        System.out.println("TestXsdParser.testGeneration() .. \nAttributeMapping:");
        while(attrKeys.hasNext())
        {
        	String objKey=(String)attrKeys.next();
        	System.out.println(objKey+"="+ xsdModel.getAttributeMap().get(objKey));
        }

//      list association mapping
        Iterator asscKeys=xsdModel.getAssociationMap().keySet().iterator();
        System.out.println("TestXsdParser.testGeneration() .. \nAssociationMapping:");
        while(asscKeys.hasNext())
        {
        	String objKey=(String)asscKeys.next();
        	System.out.println(objKey+"="+ xsdModel.getAssociationMap().get(objKey));
        }
    }

}
