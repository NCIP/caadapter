/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.map;

import gov.nih.nci.caadapter.common.BaseObjectImpl;
import gov.nih.nci.caadapter.common.MetaObject;

import java.io.File;

/**
 * A component represents a meta information that is mapped to/from.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.3 $
 * @date $Date: 2008-09-25 19:15:46 $
 * @since caAdapter v1.2
 */

public class BaseComponent extends BaseObjectImpl
{

	private View view = null;
	protected MetaObject meta = null;
	//type will tell whether it is source, target, or function component.
	protected String type = null;
	//kind will tell whether it is a scs, h3s, etc.
	protected String kind = null;
	private File file = null;
	private String fileName = null;

	public BaseComponent()
	{
	}

	protected BaseComponent(MetaObject meta)
	{
		this.meta = meta;
	}

	public View getView()
	{
		return view;
	}

	public void setView(View view)
	{
		this.view = view;
	}

	public MetaObject getMeta()
	{
		return meta;
	}

	public void setMeta(MetaObject meta)
	{
		this.meta = meta;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public String getFileAbsolutePath()
	{
		if (file == null)
		{
			return null;
		}
		else
		{
			return getFile().getAbsolutePath();
		}
		//        return fileName;
	}

	public String getFileName()
	{
		if (file == null)
		{
			return null;
		}
		else
		{
			return getFile().getName();
		}
	}

	//    public void setFileName(String fileName) {
	//        this.fileName = fileName;
	//    }

	public String getKind()
	{
		return kind;
	}

	public void setKind(String kind)
	{
		this.kind = kind;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
