/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/nodeloader/DBMBasicTreeNodeLoader.java,v 1.1 2007-04-03 16:17:13 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.nodeloader;

import gov.nih.nci.caadapter.hl7.database.meta.ColumnMeta;
import gov.nih.nci.caadapter.hl7.database.meta.DatabaseMeta;
import gov.nih.nci.caadapter.hl7.database.meta.ForeignKeyMeta;
import gov.nih.nci.caadapter.hl7.database.meta.TableMeta;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.List;

/**
 * This class defines the node loader designated to DBM tree loading.
 *
 * @author OWNER: Eric Chen  Date: Sep 15, 2005
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.1 $
 * @date $$Date: 2007-04-03 16:17:13 $
 * @since caAdapter v1.2
 */


public class DBMBasicTreeNodeLoader extends DefaultNodeLoader
{
    public TreeNode loadData(Object o) throws NodeLoader.MetaDataloadException
    {
        try
		{
			if (o instanceof DatabaseMeta)
			{
				//Create the nodes.
				return processDatabase((DatabaseMeta)o);
			}
			else
			{
				throw new RuntimeException("DBMBasicTreeNodeLoader.loadData() input " +
						"not recognized. " + o);
			}
		}
		catch (Exception e)
		{
			throw new NodeLoader.MetaDataloadException(e.getMessage(), e);
		}
    }

    /**
     * Called by loadData()
     *
     * @param database
     * @return a tree node wrapping the given database meta data
     */
    private DefaultMutableTreeNode processDatabase(DatabaseMeta database)
    {
        DefaultMutableTreeNode node = constructTreeNode(database);
        List<TableMeta> tables = database.getTables();
        for (int i = 0; i < tables.size(); i++)
        {
            TableMeta table = tables.get(i);
            DefaultMutableTreeNode subNode = processTable(table);
            node.add(subNode);
        }
        return node;
    }


    /**
     * Called by processDatabase()
     *
     * @param table
     * @return a tree node wrapping the given database meta data
     */
    private DefaultMutableTreeNode processTable(TableMeta table)
    {
        DefaultMutableTreeNode node = constructTreeNode(table);
        List<ColumnMeta> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++)
        {
            ColumnMeta column = columns.get(i);
            node.add(constructTreeNode(column, false));
        }


        List<ForeignKeyMeta> foreignKey = table.getForeignKeys();
        for (int i = 0; i < foreignKey.size(); i++)
        {
            ForeignKeyMeta foreignKeyMeta = foreignKey.get(i);
            node.add(constructTreeNode(foreignKeyMeta, false));
        }
        return node;
    }


}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.9  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/09/19 20:57:14  chene
 * HISTORY      : Add the foreign key java beans
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/16 23:18:59  chene
 * HISTORY      : Database prototype GUI support, but can not be loaded
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/09/16 15:35:20  chene
 * HISTORY      : Database prototype
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/16 02:34:53  chene
 * HISTORY      : Database prototype
 * HISTORY      :
 */
