package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.hl7.mif.NormativeVersionUtil;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Apr 2, 2009
 * Time: 1:59:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaDirUtil
{
    public SchemaDirUtil()
    {

    }
    public String getV3XsdFilePath()
    {
        String schemaPath= NormativeVersionUtil.getCurrentMIFIndex().getSchemaPath();

        File f = new File(schemaPath);
        if (!f.exists()) f = new File("../" + schemaPath);

        if (!f.exists())
        {
            System.err.println("Not Found V3 XSD Directory...");
            return null;
        }

        if (f.isDirectory()) return f.getAbsolutePath();

        String parent = f.getParent();
        if (!parent.endsWith(File.separator)) parent = parent + File.separator;
        File sdir = new File(parent + "schemas");
        if ((sdir.exists())&&(sdir.isDirectory())) return sdir.getAbsolutePath();

        System.err.println("Not Found V3 XSD Directory...");
        return null;
    }
}
