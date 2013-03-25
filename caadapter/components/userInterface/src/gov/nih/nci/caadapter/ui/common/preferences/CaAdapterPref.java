/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.common.preferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * This class implements preferences in caAdapter
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.5 $
 *          $Date: 2008-06-09 19:53:51 $
 */
public class CaAdapterPref {
    private static CaAdapterPref caAdapterPref=null;
    private HashMap caAdapterPropHashMap = null;

    public static String VALIDATION_PERFORMANCE_LEVLE_0="level 0";
    public static String VALIDATION_PERFORMANCE_LEVLE_1="leve 1";
    public static String VALIDATION_PERFORMANCE_LEVLE_2="level 2";

    private HashMap getcaAdapterPref() {
        return caAdapterPropHashMap;
    }

    public static synchronized HashMap getCaAdapterPreferences() {
        if (caAdapterPref == null) {
            caAdapterPref = new CaAdapterPref();
            return caAdapterPref.getcaAdapterPref();
        } else {
            return caAdapterPref.getcaAdapterPref();
        }
    }

    private CaAdapterPref() {
        try {
            FileInputStream f_out = new FileInputStream(System.getProperty("user.home") + "\\.caadapter");
            ObjectInputStream obj_out = new ObjectInputStream(f_out);
            caAdapterPropHashMap = (HashMap) obj_out.readObject();
        } catch (Exception e) {
            caAdapterPropHashMap = null;
            e.printStackTrace();
        }
    }

    public static synchronized void setCaAdapterPreferences(HashMap mapFromApplication) {
        try {
            FileOutputStream f_out = new FileOutputStream(System.getProperty("user.home") + "\\.caadapter");
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(mapFromApplication);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2007/08/24 21:15:14  wangeug
 * add validation level constants
 *
 * Revision 1.3  2007/08/17 15:15:25  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
