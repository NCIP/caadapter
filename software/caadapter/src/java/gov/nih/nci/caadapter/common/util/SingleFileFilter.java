/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * This class is a basic utility class.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:50 $
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/08/10 16:46:03  wangeug
 * HISTORY      : make File choose work with multiple extensions
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/16 22:33:50  jiangsc
 * HISTORY      : Update License Information
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/10 20:49:02  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/04 22:22:24  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
