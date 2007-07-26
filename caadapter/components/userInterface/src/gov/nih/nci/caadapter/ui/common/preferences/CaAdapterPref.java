package gov.nih.nci.caadapter.ui.common.preferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jul 26, 2007
 * Time: 10:29:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class CaAdapterPref
{

    private static CaAdapterPref caAdapterPref;

    private HashMap caAdapterPropHashMap = null;

    private HashMap getcaAdapterPref()
    {
        return caAdapterPropHashMap;
    }

    public static synchronized HashMap getCaAdapterPreferences()
    {
        if (caAdapterPref == null)
        {
            caAdapterPref = new CaAdapterPref();
            return caAdapterPref.getcaAdapterPref();
        } else
        {
            return caAdapterPref.getcaAdapterPref();
        }
    }

    private CaAdapterPref()
    {
        try
        {
            FileInputStream f_out = new FileInputStream(System.getProperty("user.home") + "\\.caadapter");
            ObjectInputStream obj_out = new ObjectInputStream(f_out);
            caAdapterPropHashMap = (HashMap) obj_out.readObject();
        } catch (Exception e)
        {
            caAdapterPropHashMap = null;
            e.printStackTrace();
        }
    }

    public static synchronized void setCaAdapterPreferences(HashMap mapFromApplication)
    {
        try
        {
            FileOutputStream f_out = new FileOutputStream(System.getProperty("user.home") + "\\.caadapter");
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(mapFromApplication);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String args[])
    {
        //setCaAdapterPreferences(new HashMap());
        System.out.println(getCaAdapterPreferences());
    }
}
