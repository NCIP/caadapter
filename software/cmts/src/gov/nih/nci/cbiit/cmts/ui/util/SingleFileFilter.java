/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * This class is a basic utility class.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
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
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

