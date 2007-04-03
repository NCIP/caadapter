/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/MappingSourceTree.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.common.tree;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.JPanel;

/**
 * This class provides a customized tree to support the "source" in the mapping.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
public class MappingSourceTree extends MappingBaseTree
{

	public MappingSourceTree(JPanel m, TreeNode root)
	{
		super(m,root);
	}

	protected void loadData()
	{
		DefaultMutableTreeNode root =
				new DefaultMutableTreeNode("SUBADEV");
		DefaultMutableTreeNode parent;
		DefaultMutableTreeNode child;
		DefaultMutableTreeNode field;

		// set the root.
		DefaultTreeModel dtm = new DefaultTreeModel(root);
		setModel(dtm);

		// setup parent off root.
		field = new DefaultMutableTreeNode("eventTime");
		root.add(field);

		field = new DefaultMutableTreeNode("routeCode");
		root.add(field);

		field = new DefaultMutableTreeNode("doseQuantity");
		root.add(field);

		field = new DefaultMutableTreeNode("actionTakenCode");
		root.add(field);

		field = new DefaultMutableTreeNode("actionTakenValue");
		root.add(field);

		field = new DefaultMutableTreeNode("drugCode");
		root.add(field);

		field = new DefaultMutableTreeNode("drugInstanceCode");
		root.add(field);

		field = new DefaultMutableTreeNode("drugInstanceName");
		root.add(field);

		field = new DefaultMutableTreeNode("lotNumber");
		root.add(field);

		field = new DefaultMutableTreeNode("drugExpireTime");
		root.add(field);

		field = new DefaultMutableTreeNode("manufactCode");
		root.add(field);

		field = new DefaultMutableTreeNode("manufactName");
		root.add(field);

		/******      INGREDIENT Child         ********/
		parent = new DefaultMutableTreeNode("INGREDIENT");
		root.add(parent);

		field = new DefaultMutableTreeNode("drugcode");
		parent.add(field);

		field = new DefaultMutableTreeNode("drugFormCode");
		parent.add(field);

		/******      PATIENT Child         ********/
		parent = new DefaultMutableTreeNode("PATIENT");
		root.add(parent);

		field = new DefaultMutableTreeNode("id");
		parent.add(field);

		field = new DefaultMutableTreeNode("name");
		parent.add(field);

		field = new DefaultMutableTreeNode("addres");
		parent.add(field);

		field = new DefaultMutableTreeNode("phone");
		parent.add(field);

		/******      PROVIDER Child         ********/
		parent = new DefaultMutableTreeNode("PROVIDER");
		root.add(parent);

		field = new DefaultMutableTreeNode("id");
		parent.add(field);
		field = new DefaultMutableTreeNode("type");
		parent.add(field);
		field = new DefaultMutableTreeNode("name");
		parent.add(field);
		field = new DefaultMutableTreeNode("address");
		parent.add(field);
		field = new DefaultMutableTreeNode("orgId");
		parent.add(field);
		field = new DefaultMutableTreeNode("orgCode");
		parent.add(field);
		field = new DefaultMutableTreeNode("orgName");
		parent.add(field);
		field = new DefaultMutableTreeNode("orgAddress");
		parent.add(field);


		/******      OBSDX Child         ********/
		parent = new DefaultMutableTreeNode("OBSDX");
		root.add(parent);

		field = new DefaultMutableTreeNode("id");
		parent.add(field);
		field = new DefaultMutableTreeNode("code");
		parent.add(field);
		field = new DefaultMutableTreeNode("statusCode");
		parent.add(field);
		field = new DefaultMutableTreeNode("time");
		parent.add(field);
		field = new DefaultMutableTreeNode("value");
		parent.add(field);
		field = new DefaultMutableTreeNode("organ");
		parent.add(field);

		child = new DefaultMutableTreeNode("DXPROVIDER");
		parent.add(child);

		field = new DefaultMutableTreeNode("id");
		child.add(field);
		field = new DefaultMutableTreeNode("type");
		child.add(field);
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.12  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/04 22:22:15  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
