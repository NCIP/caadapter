/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dataviewer.util;

import java.util.Hashtable;

/**
 * This object serves as a lookup for all the SDTM domain names
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class SDTMDomainLookUp
{

    Hashtable<String, String> tab = new Hashtable<String, String>();

    public SDTMDomainLookUp()
    {
        tab.put("DM", "Demography");
        tab.put("CO", "Comments");
        tab.put("CM", "Concomitant Medications");
        tab.put("EX", "Exposure");
        tab.put("SU", "Substance Use");
        tab.put("AE", "Adverse Events");
        tab.put("DS", "Disposition");
        tab.put("MH", "Medical History");
        tab.put("EG", "ECG Test Results");
        tab.put("IE", "Inclusion/Exclusion Exception");
        tab.put("LB", "Laboratory Test Results");
        tab.put("PE", "Physical Examinations");
        tab.put("QS", "Questionnaires");
        tab.put("SC", "Subject Characteristics");
        tab.put("VS", "Vital Signs");
        tab.put("TE", "Trial Elements");
        tab.put("TA", "Trial Arms");
        tab.put("TV", "Trial Visits");
        tab.put("SE", "Subject Elements");
        tab.put("SV", "Subject Visits");
    }

    public String getDescription(String val)
    {
        return tab.get(val);
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/08/16 18:53:55  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */