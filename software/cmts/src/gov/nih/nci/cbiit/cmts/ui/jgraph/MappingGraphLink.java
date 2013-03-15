/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.jgraph;

import gov.nih.nci.cbiit.cmts.common.PropertiesProvider;
import gov.nih.nci.cbiit.cmts.core.FunctionData;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxGraphCell;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxGraphPort;
import gov.nih.nci.cbiit.cmts.ui.properties.PropertiesResult;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;

public class MappingGraphLink extends DefaultEdge  implements  Serializable, PropertiesProvider {

	public PropertiesResult getPropertyDescriptors() throws Exception {
		// TODO Auto-generated method stub
		Class beanClass = this.getClass();
		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		PropertyDescriptor srcParent = new PropertyDescriptor("Source Parent", beanClass, "getSourcParentProperty", null);
		PropertyDescriptor srcName = new PropertyDescriptor("Source Name", beanClass,"getSourceNameProperty", null);
		propList.add(srcParent);
		propList.add(srcName);
		PropertyDescriptor trgtParent = new PropertyDescriptor("Target Parent", beanClass, "getTargetParentProperty", null);
		PropertyDescriptor trgtName = new PropertyDescriptor("Target Name", beanClass,"getTargetNameProperty", null);
		propList.add(trgtParent);
		propList.add(trgtName);

		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);
		return result;
	}
	
	public String getSourcParentProperty()
	{
		DefaultPort srcPort=(DefaultPort)this.getSource();
		if (srcPort==null)
			return null;
		if (srcPort instanceof FunctionBoxGraphPort)
		{
			FunctionBoxGraphPort fPort=(FunctionBoxGraphPort)srcPort;
			FunctionBoxGraphCell functionBox=(FunctionBoxGraphCell)fPort.getParent();
			return functionBox.getFunctionDef().getGroup()+":"+functionBox.getFunctionDef().getName();
		}
		else
		{
			DefaultMutableTreeNode treeNode=(DefaultMutableTreeNode)srcPort;
			DefaultMutableTreeNode userObjct=(DefaultMutableTreeNode )treeNode.getUserObject();
			DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode )userObjct.getParent();
			return parentNode.getUserObject().toString();
		}
	}
	public String getSourceNameProperty()
	{
		DefaultPort srcPort=(DefaultPort)this.getSource();
		if (srcPort==null)
			return null;
		if (srcPort instanceof FunctionBoxGraphPort)
		{
			FunctionBoxGraphPort fPort=(FunctionBoxGraphPort)srcPort;
			FunctionData fData=(FunctionData)fPort.getUserObject();

			return fData.getName()+":"+fData.getValue();
		}
		else
		{
			DefaultMutableTreeNode treeNode=(DefaultMutableTreeNode)srcPort;
			return treeNode.getUserObject().toString();
		}
	}


	public String getTargetParentProperty()
	{
		DefaultPort trgtPort=(DefaultPort)this.getTarget();
		if (trgtPort==null)
			return null;
		if (trgtPort instanceof FunctionBoxGraphPort)
		{
			FunctionBoxGraphPort fPort=(FunctionBoxGraphPort)trgtPort;
			FunctionBoxGraphCell functionBox=(FunctionBoxGraphCell)fPort.getParent();
			return functionBox.getFunctionDef().getGroup()+":"+functionBox.getFunctionDef().getName();
		}
		else
		{
			DefaultMutableTreeNode treeNode=(DefaultMutableTreeNode)trgtPort;
			DefaultMutableTreeNode userObjct=(DefaultMutableTreeNode )treeNode.getUserObject();
			DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode )userObjct.getParent();
			return parentNode.getUserObject().toString();
		}
 
	}
	public String getTargetNameProperty()
	{
		DefaultPort trgtPort=(DefaultPort)this.getTarget();
		if (trgtPort==null)
			return null;
		if (trgtPort instanceof FunctionBoxGraphPort)
		{
			FunctionBoxGraphPort fPort=(FunctionBoxGraphPort)trgtPort;
			FunctionData fData=(FunctionData)fPort.getUserObject();

			return fData.getName()+":"+fData.getValue();
		}
		else
		{
			DefaultMutableTreeNode treeNode=(DefaultMutableTreeNode)trgtPort;
			return treeNode.getUserObject().toString();
		}
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "Link Properties";
	}

}
