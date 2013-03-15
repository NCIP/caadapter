/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common;


import java.io.File;
import java.io.OutputStream;

/**
 * An interface for building meta based XML from in memory objects.
 *
 * @author OWNER: Eric Chen  Date: Jun 3, 2004
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.2 $
 * @date $$Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */
public interface MetaBuilder {

    /**
     * @param outputStream
     * @param metaObject
     * @throws MetaException
     */
    public void build(OutputStream outputStream, MetaObject metaObject) throws MetaException;

    /**
     * Add the ability which will not earse the file content if exception is thrown
     *
     * @param file
     * @param metaObject
     * @throws MetaException
     */
    public void build(File file, MetaObject metaObject) throws MetaException;

    /**
     * HISTORY      : $Log: not supported by cvs2svn $
     * HISTORY      : Revision 1.4  2005/09/16 16:20:17  giordanm
     * HISTORY      : HL7V3 parser is not returning a result object not a just a meta object.
     * HISTORY      :
     * HISTORY      : Revision 1.3  2005/08/04 21:41:13  chene
     * HISTORY      : Support temporaly file saving
     * HISTORY      :
     * HISTORY      : Revision 1.2  2005/07/18 22:18:26  jiangsc
     * HISTORY      : First implementation of the Log functions.
     * HISTORY      :
     * HISTORY      : Revision 1.1  2005/06/03 22:43:03  chene
     * HISTORY      : Add HL7V3 Meta Builder
     * HISTORY      :
     */
}
