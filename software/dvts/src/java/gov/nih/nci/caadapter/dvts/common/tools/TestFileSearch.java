/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.tools;

import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.util.FileSearchUtil;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 26, 2011
 * Time: 12:11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestFileSearch
{
    public static void main(String[] args)
    {
        String path = (new FileSearchUtil()).searchFile("vomTranslation.xsd");
        System.out.println("CCCC " + path);
    }
}
