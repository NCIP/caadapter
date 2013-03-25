/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.common.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 17, 2010
 * Time: 1:04:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleFileFilter extends FileFilter
{
    private String extension = null;

    public SingleFileFilter(String extension)
    {
        this.extension = extension;
    }

    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }
        return f.getName().toLowerCase().endsWith(extension);
    }

    public String getDescription()
    {
        return extension;
    }

    /**
     * Override so as to provide enhanced equal() comparison.
     *
     * @param o
     * @return true if given object is equal to this object; false, otherwise.
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final SingleFileFilter that = (SingleFileFilter) o;

        if (extension != null ? !extension.equals(that.extension) : that.extension != null)
        {
            return false;
        }

        return true;
    }

    /**
     * Return hash code representation of this object.
     *
     * @return hash code representation of this object.
     */
    public int hashCode()
    {
        return (extension != null ? extension.hashCode() : 0);
    }

    public String toString()
    {
        return this.extension;
    }

    public String getExtension() {
        return extension;
    }
}
