/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*
* <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:

 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.common.csv.data.impl;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.standard.impl.DataTreeImpl;
import gov.nih.nci.caadapter.common.standard.DataSegment;
import gov.nih.nci.caadapter.common.csv.CsvCache;
import gov.nih.nci.caadapter.common.csv.meta.CSVMetaTree;
import gov.nih.nci.caadapter.common.csv.data.CSVDataTree;

import java.io.File;
import java.io.IOException;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 3, 2007
 *          Time:       11:12:50 AM $
 */
public class CSVDataTreeImpl extends DataTreeImpl implements CSVDataTree
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CSVDataTreeImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/CSVDataTreeImpl.java,v 1.3 2008-06-09 19:53:49 phadkes Exp $";



    public CSVDataTreeImpl()
    {
        super();
    }
    public CSVDataTreeImpl(DataSegment seg, CSVMetaTree meta) throws ApplicationException
    {
        super(seg, meta);
    }

    public void setCSVFileName(String fileName) throws ApplicationException
    {
        setDataFileName(fileName);
    }
    public String getCSVFileName()
    {
        return getDataFileName();
    }
    public void inputValuesFromFile(String fileName) throws ApplicationException
    {
        if (getHeadSegment() == null) throw new ApplicationException("Data tree is not built yet : " + fileName);

        if ((fileName == null)||(fileName.trim().equals(""))) throw new ApplicationException("Null meta file name : ");

        File file = new File(fileName);

        if (!file.exists()) throw new ApplicationException("This data file is not exists. : " + fileName);
        if (!file.isFile()) throw new ApplicationException("This data file name is not a file. : " + fileName);

        String[][] data = null;
        setCSVFileName(fileName);
        try
        {
            data = CsvCache.getCsv(fileName);
        }
        catch (IOException e)
        {
            throw new ApplicationException("Using '" + fileName + "' file, data array input failure : " + e.getMessage());
        }
        inputValuesFromArray(data);
        setCSVFileName(fileName);
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/06 18:53:56  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:36:26  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 */
