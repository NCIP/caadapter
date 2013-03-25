/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.KindType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.core.TagType;
import gov.nih.nci.cbiit.cmts.mapping.MappingAnnotationUtil;
import gov.nih.nci.cbiit.cmts.ui.common.UIHelper;
import gov.nih.nci.cbiit.cmts.ui.mapping.ElementMetaLoader;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;
import gov.nih.nci.cbiit.cmts.ui.tree.MappingTargetTree;
import gov.nih.nci.cbiit.cmts.ui.tree.MappingBaseTree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.math.BigInteger;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * @author wangeug
 *
 */
public class ElementAnnotationAction extends AbstractContextAction {

	public final static int CLONE_ADD_ACTION=0;
	public final static int CLONE_REMOVE_ACTION=1;
	public final static int CHOICE_SELECT_ACTION=2;
	public final static int CHOICE_DESELECT_ACTION=3;
	public final static int RECURSION_ENABLE_ACTION=4;
	public final static int RECURSION_DISABLE_ACTION=5;

    public final static int ACTIVATE_ACTION=6;
	public final static int DEACTIVATE_ACTION=7;

    public final static int CONCRETE_ACTION=8;

    private static final long serialVersionUID = -8974807457591325892L;
	private int annotationType;
	private Mapping mappingData;
	private MappingBaseTree treeAnnotate;
    private MappingMainPanel parentPanel;

    public JTree getTreeAnnotate() {
		return treeAnnotate;
	}

	public void setTreeAnnotate(MappingBaseTree treeAnnotate) {
		this.treeAnnotate = treeAnnotate;
	}

	public ElementAnnotationAction(String name) {
		this(name, CHOICE_SELECT_ACTION);
	}

	public ElementAnnotationAction(String name, int actionType) {
		super(name);
		annotationType=actionType;
		setEnabled(false);
	}
	@Override
	protected boolean doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		TreePath slctedPath=getTreeAnnotate().getSelectionPath();
		if (slctedPath==null)
			return false;
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) slctedPath.getLastPathComponent();
		
		ElementMetaLoader.MyTreeObject treeNodeObj=(ElementMetaLoader.MyTreeObject)treeNode.getUserObject();
		if (!(treeNodeObj.getUserObject() instanceof ElementMeta))
			return false;
		ElementMeta annotateElement=(ElementMeta)treeNodeObj.getUserObject();
		gov.nih.nci.cbiit.cmts.core.Component rootComponent=(gov.nih.nci.cbiit.cmts.core.Component) treeNodeObj.getRootObject();
		DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode)treeNode.getParent();
		DefaultTreeModel treeModel=(DefaultTreeModel)treeAnnotate.getModel();
		ComponentType compType=ComponentType.SOURCE;
		int newNodeType=ElementMetaLoader.SOURCE_MODE;
		if (getTreeAnnotate() instanceof MappingTargetTree)
		{
			compType=ComponentType.TARGET;
			newNodeType=ElementMetaLoader.TARGET_MODE;
		}
		String nodePath=UIHelper.getPathStringForNode(treeNode);
		ElementMetaLoader.MyTreeObject parentNodeObj=(ElementMetaLoader.MyTreeObject)parentNode.getUserObject();
		ElementMeta parentElement=(ElementMeta)parentNodeObj.getUserObject();
		switch (getAnnotationType())
		{
		case CLONE_ADD_ACTION:
			int maxMultiple=MappingAnnotationUtil.findMaxMultiplicityIndex(parentElement, annotateElement.getName());
			ElementMeta clonedMeta=(ElementMeta)annotateElement.clone();
			clonedMeta.setMultiplicityIndex(BigInteger.valueOf(maxMultiple+1));
			parentElement.getChildElement().add(clonedMeta);
			DefaultMutableTreeNode clonedNode = (DefaultMutableTreeNode)new ElementMetaLoader(newNodeType).loadDataForRoot(clonedMeta, rootComponent);

			String lastClonedName=annotateElement.getName();
			if (maxMultiple>0)
				lastClonedName=lastClonedName+"["+maxMultiple+"]";
			int treeNodeIndx=parentNode.getIndex(treeNode);
			
			//find the position of the last cloned meta
			for (int siblingIndx=0;siblingIndx<parentNode.getChildCount();siblingIndx++)
			{
				DefaultMutableTreeNode siblingNode=(DefaultMutableTreeNode)parentNode.getChildAt(siblingIndx);
				ElementMetaLoader.MyTreeObject siblingNodeObj=(ElementMetaLoader.MyTreeObject)siblingNode.getUserObject();
				if (siblingNodeObj.getUserObject() instanceof ElementMeta )
				{
					ElementMeta siblingElement=(ElementMeta)siblingNodeObj.getUserObject();
					if (siblingElement.getName().equals(lastClonedName))
						treeNodeIndx=parentNode.getIndex(siblingNode);
				}
			}
			parentNode.insert(clonedNode, treeNodeIndx+1);		
			clonedNode.setParent(parentNode);
			treeModel.reload(parentNode);
			MappingAnnotationUtil.addTag(mappingData, compType, KindType.CLONE, nodePath, clonedMeta.getMultiplicityIndex().toString());
			break;
		case CLONE_REMOVE_ACTION:	
			treeModel.removeNodeFromParent(treeNode);
			parentElement.getChildElement().remove(annotateElement);
			//adjust the multiplicity of other cloned element
			String clonedName=annotateElement.getName().substring(0,annotateElement.getName().lastIndexOf("["));	
			for (ElementMeta childMeta:parentElement.getChildElement())
			{
				if(childMeta.getName().startsWith(clonedName+"["))
				{
					//decrease one for all other cloned if index >current index
					int chldIndex=childMeta.getMultiplicityIndex().intValue();
					if (chldIndex>annotateElement.getMultiplicityIndex().intValue())
						childMeta.setMultiplicityIndex(BigInteger.valueOf(chldIndex-1));
 				}
			}
			treeModel.reload(parentNode);
			//remove "[indx]" from the cloned node path
			String cloneKey=nodePath.substring(0, nodePath.lastIndexOf("["));
			MappingAnnotationUtil.removeTag(mappingData, compType, KindType.CLONE, cloneKey, ""+annotateElement.getMultiplicityIndex());
			//adjust index value of mapping annotation tags
			for (TagType tag:mappingData.getTags().getTag())
			{
				if (tag.getComponentType().equals(compType)
						&&tag.getKind().equals(KindType.CLONE)
						&&tag.getKey().equals(cloneKey))
				{
					int tagIndx=Integer.valueOf(tag.getValue());
					if (tagIndx>annotateElement.getMultiplicityIndex().intValue())
					{
						tag.setValue(""+(tagIndx-1));
					}
				}
			}
			break;		
		case CHOICE_SELECT_ACTION:
			//de-select all other sibling nodes
			for (int siblingIndx=0;siblingIndx<parentNode.getChildCount();siblingIndx++)
			{
				DefaultMutableTreeNode siblingNode=(DefaultMutableTreeNode)parentNode.getChildAt(siblingIndx);
				if(siblingNode.getChildCount()>0)
				{
					ElementMetaLoader.MyTreeObject siblingNodeObj=(ElementMetaLoader.MyTreeObject)siblingNode.getUserObject();
					ElementMeta siblingElement=(ElementMeta)siblingNodeObj.getUserObject();
					siblingElement.setIsChosen(false);
					siblingNode.removeAllChildren();
					treeModel.reload(siblingNode);
					MappingAnnotationUtil.removeTag(mappingData, compType, KindType.CHOICE, UIHelper.getPathStringForNode(siblingNode), "true");
				}
			}
			annotateElement.setIsChosen(true);

            DefaultMutableTreeNode newChoiceNode = null;
            if (annotateElement.getChildElement().size() > 0)
            {
                //System.out.println("CCCX CVCV annotateElement.getChildElement().size() > 0");
                newChoiceNode = (DefaultMutableTreeNode)new ElementMetaLoader(newNodeType).loadDataForRoot(annotateElement, rootComponent);
            }
            else
            {
                //System.out.println("CCCX CECE annotateElement.getChildElement().size() = 0");
                newChoiceNode = treeAnnotate.getSchemaParser().expandNodeWithLazyLoad(annotateElement, newNodeType, rootComponent);
            }
            for (int childIndx=newChoiceNode.getChildCount();childIndx>0;childIndx--)
            {
                treeNode.insert((DefaultMutableTreeNode)newChoiceNode.getChildAt(childIndx-1),0);
            }

            treeModel.reload(treeNode);
			MappingAnnotationUtil.addTag(mappingData, compType, KindType.CHOICE, nodePath, "true");
			break;
			
		case CHOICE_DESELECT_ACTION:
			annotateElement.setIsChosen(false);
			treeNode.removeAllChildren();
			treeModel.reload(treeNode);
			MappingAnnotationUtil.removeTag(mappingData, compType, KindType.CHOICE, nodePath, "true");
			break;
		case RECURSION_ENABLE_ACTION:
			DefaultMutableTreeNode recursiveNode=parentNode;

            ElementMeta recursiveMeta=parentElement;
            System.out.println("$$$$0" + recursiveNode.toString());
            while (!recursiveMeta.getType().equals(annotateElement.getType()))
			{

                recursiveNode=(DefaultMutableTreeNode)recursiveNode.getParent();

                if (recursiveNode == null)
                {
                    //recursiveNode=parentNode;
                    System.out.println("$$$$9" + parentNode.toString());
                    break;
                }
                else System.out.println("$$$$1" + recursiveNode.toString());
                ElementMetaLoader.MyTreeObject rpNodeObj=(ElementMetaLoader.MyTreeObject)recursiveNode.getUserObject();
				recursiveMeta=(ElementMeta)rpNodeObj.getUserObject();
			}
			String oldElementId=annotateElement.getId();
			annotateElement=(ElementMeta)recursiveMeta.clone();
			annotateElement.setId(oldElementId);
			MappingAnnotationUtil.enableRecursiveElementMeta(annotateElement);
			DefaultMutableTreeNode newRecursiveNode = (DefaultMutableTreeNode)new ElementMetaLoader(newNodeType).loadDataForRoot(annotateElement, rootComponent);

			//replace the old tree node with new recursive node
			int oldNodeIndx=parentNode.getIndex(treeNode);
			parentNode.insert(newRecursiveNode, oldNodeIndx);
			newRecursiveNode.setParent(parentNode);
			parentNode.remove(treeNode);
			treeModel.reload(parentNode);
			annotateElement.setIsRecursive(true);
			annotateElement.setIsEnabled(true);
			
			MappingAnnotationUtil.addTag(mappingData, compType, KindType.RECURSION, nodePath, "true");
			break;
		case RECURSION_DISABLE_ACTION:
			annotateElement.setIsEnabled(false);
			treeNode.removeAllChildren();
			annotateElement.getAttrData().clear();
			annotateElement.getChildElement().clear();
			treeModel.reload(treeNode);
			MappingAnnotationUtil.removeTag(mappingData, compType, KindType.RECURSION, nodePath, "true");
			break;
        case CONCRETE_ACTION:
            //todo
            break;
        }
		return true;
	}

 
	public int getAnnotationType() {
		return annotationType;
	}

	@Override
	protected Component getAssociatedUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setAnnotationType(int annotationType) {
		this.annotationType = annotationType;
	}

	public void setMappingData(Mapping mappingData) {
		this.mappingData = mappingData;
	}
    public void setParentPanel(MappingMainPanel panel)
    {
        this.parentPanel = panel;
    }
}
