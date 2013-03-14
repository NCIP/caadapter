/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.hl7.mif.NormativeVersionUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFIndex;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Apr 2, 2009
 * Time: 1:59:19 AM
 * To change this template use File | Settings | File Templates.
 */

public class SchemaDirUtil
{
    MIFIndex mifIndex = null;
    public SchemaDirUtil()
    {

    }
    public SchemaDirUtil(MIFIndex mifIndex2) throws IOException
    {
        setMIFIndex(mifIndex2);
    }
    public void setMIFIndex(MIFIndex mifIndex2) throws IOException
    {
        if (mifIndex2 == null) throw new IOException("Not found mif index object.");
        mifIndex = mifIndex2;
    }
    public String getV3XsdFilePath(String copyRightYear)
    {
        MIFIndex mifIndex2 =  NormativeVersionUtil.loadMIFIndex(copyRightYear);
        if (mifIndex2 == null) return null;
        mifIndex = mifIndex2;
        return getMifIndex(mifIndex2);
    }
    public String getV3XsdFilePath() throws IOException
    {
        MIFIndex mifIndex2 = mifIndex;
        if (mifIndex2 == null)
        {
            mifIndex2 = NormativeVersionUtil.getCurrentMIFIndex();
            if (mifIndex2 == null) throw new IOException("Not found mif index object.");
        }
        return getMifIndex(mifIndex2);
    }
    private String getMifIndex(MIFIndex mifIndex)
    {
        String schemaPath= mifIndex.getSchemaPath();
        //System.out.println("WWWW search...(0) : " + schemaPath);
        File f = new File(schemaPath);
        if (f.exists())
        {
            if (f.isFile()) return f.getParentFile().getAbsolutePath();
            if (f.isDirectory()) return f.getAbsolutePath();
        }

        //if (!f.exists())
        //{
        //    System.err.println("Not Found V3 XSD Directory...(1) : " + schemaPath);
        //    return null;
        //}

        //if (f.isDirectory()) return f.getAbsolutePath();

        String parent = f.getParent();
        if (!parent.endsWith(File.separator)) parent = parent + File.separator;

        File sdir = new File(parent + "schemas");
        if ((sdir.exists())&&(sdir.isDirectory())) return sdir.getAbsolutePath();

        File sdir2 = new File(parent + "schemas.zip");
        if ((sdir2.exists())&&(sdir2.isFile())) return sdir.getParentFile().getAbsolutePath();

        System.err.println("Not Found V3 XSD Directory...(2) : " + parent + "schemas");
        return null;
    }
}
