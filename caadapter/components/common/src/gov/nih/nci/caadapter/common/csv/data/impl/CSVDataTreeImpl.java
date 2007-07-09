/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/CSVDataTreeImpl.java,v 1.1 2007-07-09 15:36:26 umkis Exp $
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
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/CSVDataTreeImpl.java,v 1.1 2007-07-09 15:36:26 umkis Exp $";



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
 */
