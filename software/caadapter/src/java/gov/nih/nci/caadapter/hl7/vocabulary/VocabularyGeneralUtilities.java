/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.hl7.vocabulary;

import gov.nih.nci.caadapter.common.ApplicationException;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.5 $
 *          date        Apr 25, 2008
 *          Time:       12:31:37 AM $
 */
public class VocabularyGeneralUtilities
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": VocabularyGeneralUtilities.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/VocabularyGeneralUtilities.java,v 1.00 Apr 25, 2008 12:31:37 AM umkis Exp $";

    public static V3VocabularySeeker vocabulrarySeeker = null;

    private VocabularyGeneralUtilities()
	{//never instantiable
        getV3VocabularySeeker();
    }

    /**
	 * Return V3VocabularySeeker object.
	 * @return V3VocabularySeeker.
	 */
	public static final V3VocabularySeeker getV3VocabularySeeker()
	{
        if (vocabulrarySeeker == null)
        {
            try
            {
                vocabulrarySeeker = new V3VocabularySeeker();
            }
            catch(ApplicationException ae)
            {
                System.err.println("V3 Vocabulary tree building failure... : " + ae.getMessage());
            }
        }
        return vocabulrarySeeker;
	}

}

/**
 * HISTORY : $Log: not supported by cvs2svn $
 */
