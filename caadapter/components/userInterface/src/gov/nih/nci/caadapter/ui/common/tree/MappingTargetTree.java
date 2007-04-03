/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/MappingTargetTree.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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
 * This class provides a customized tree to support the "target" in the mapping.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
public class MappingTargetTree extends MappingBaseTree
{
	public MappingTargetTree(JPanel m, TreeNode root)
	{
		super(m, root);
//		this.addTreeExpansionListener(this);
//		if (root != null)
//		{
//			DefaultTreeModel dtm = new DefaultTreeModel(root);
//			setModel(dtm);
//		}
//		else
//		{//load default dummy data
//			loadData();
//		}
	}

	protected void loadData()
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("SubstanceAdministrationEvent");
		DefaultMutableTreeNode clone;
		DefaultMutableTreeNode clone_child;
		DefaultMutableTreeNode dt;
		DefaultMutableTreeNode att;

		// set the root.
		DefaultTreeModel dtm = new DefaultTreeModel(root);
		setModel(dtm);

		// setup datatypes off root.
		dt = new DefaultMutableTreeNode("code");
		root.add(dt);
		att = new DefaultMutableTreeNode("code");
		dt.add(att);
		att = new DefaultMutableTreeNode("codeSystemName");
		dt.add(att);
		att = new DefaultMutableTreeNode("displayName");
		dt.add(att);
		att = new DefaultMutableTreeNode("codeSystemVersion");
		dt.add(att);

		dt = new DefaultMutableTreeNode("classcode");
		root.add(dt);
		att = new DefaultMutableTreeNode("code");
		dt.add(att);

		dt = new DefaultMutableTreeNode("moodcode");
		root.add(dt);
		att = new DefaultMutableTreeNode("code");
		dt.add(att);

		dt = new DefaultMutableTreeNode("effectiveTime");
		root.add(dt);
		att = new DefaultMutableTreeNode("value");
		dt.add(att);

		dt = new DefaultMutableTreeNode("routeCode");
		root.add(dt);
		att = new DefaultMutableTreeNode("code");
		dt.add(att);

		dt = new DefaultMutableTreeNode("doseQuantity");
		root.add(dt);
		att = new DefaultMutableTreeNode("value");
		dt.add(att);
		att = new DefaultMutableTreeNode("unit");
		dt.add(att);

		// set up a clone off the root.
		clone = new DefaultMutableTreeNode("directTarget");
		root.add(clone);
		dt = new DefaultMutableTreeNode("typeCode", false);
		clone.add(dt);

		dt = new DefaultMutableTreeNode("manufacturedProduct");
		clone.add(dt);

		clone = new DefaultMutableTreeNode("manufacturedDrugInstance");
		dt.add(clone);

		dt = new DefaultMutableTreeNode("name");
		clone.add(dt);

		att = new DefaultMutableTreeNode("inlineText");
		dt.add(att);

		dt = new DefaultMutableTreeNode("expirationTime");
		clone.add(dt);

		att = new DefaultMutableTreeNode("high");
		dt.add(att);

		// set up a clone off the root.
		clone = new DefaultMutableTreeNode("pertinentInformation1");
		root.add(clone);
		dt = new DefaultMutableTreeNode("typeCode");
		clone.add(dt);
		att = new DefaultMutableTreeNode("code");
		dt.add(att);

		clone_child = new DefaultMutableTreeNode("pertinentObservationDx");
		clone.add(clone_child);


		clone = new DefaultMutableTreeNode("authorOrPerformer");
		root.add(clone);
		dt = new DefaultMutableTreeNode("or more clones", false);
		clone.add(dt);

	}


}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.14  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/04 22:22:15  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */

