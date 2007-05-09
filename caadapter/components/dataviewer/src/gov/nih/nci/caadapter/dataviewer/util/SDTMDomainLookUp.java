package gov.nih.nci.caadapter.dataviewer.util;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: May 3, 2007
 * Time: 10:38:04 AM
 * To change this template use File | Settings | File Templates.
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
