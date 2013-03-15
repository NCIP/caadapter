/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.jgraph;

import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.XMIAnnotationUtil;
import gov.nih.nci.caadapter.ui.common.Iso21090uiUtil;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingViewCommonComponent;
import gov.nih.nci.caadapter.ui.common.tree.MappingBaseTree;
import gov.nih.nci.caadapter.ui.common.tree.MappingSourceTree;
import gov.nih.nci.caadapter.ui.common.tree.MappingTargetTree;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.MappingTreeScrollPane;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.AssociationAnnotationAction;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.AttributeAnnotationAction;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.ColumnAnnotationAction;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.ObjectAnnotationAction;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.metadata.TableMetadata;
import gov.nih.nci.caadapter.common.util.Iso21090Util;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;

/**
 * This class defines a highlighter class for graph presentation.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.18 $
 *          date        $Date: 2009-07-30 17:37:05 $
 */
public class LinkSelectionHighlighter extends MouseAdapter implements GraphSelectionListener, TreeSelectionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: LinkSelectionHighlighter.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/LinkSelectionHighlighter.java,v 1.18 2009-07-30 17:37:05 wangeug Exp $";

	private AbstractMappingPanel mappingPanel;
	private JGraph graph;
	private MappingMiddlePanel middlePanel;
	private boolean graphInSelection = false;

	public LinkSelectionHighlighter(AbstractMappingPanel mappingPanel, JGraph graph, MappingMiddlePanel middlePanel)
	{
		this.middlePanel = middlePanel;
		this.mappingPanel = mappingPanel;
		this.graph = graph;
		initialize();
	}

	private void initialize()
	{
		if(mappingPanel!=null)
		{
			JTree tree = mappingPanel.getSourceTree();
			if(tree!=null)
			{
				tree.removeMouseListener(this);
				tree.addMouseListener(this);
			}
			tree = mappingPanel.getTargetTree();
			if(tree!=null)
			{
				tree.removeMouseListener(this);
				tree.addMouseListener(this);
			}
		}
		if(graph!=null)
		{
			graph.addMouseListener(this);
		}
	}

	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(GraphSelectionEvent e)
	{
		if (mappingPanel.isInDragDropMode())
			return;
		//clean tree highlight if remove graph highlight
	    if (!e.isAddedCell())
	    {
			if (mappingPanel.getTargetTree() == null)
			{
				JOptionPane.showMessageDialog(mappingPanel, "You should input the source file name first(2).", "No Source file", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else
				mappingPanel.getTargetTree().clearSelection();

			if (mappingPanel.getSourceTree() == null)
			{
				JOptionPane.showMessageDialog(mappingPanel, "You should input the target file name first(2).", "No Target file", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else
				mappingPanel.getSourceTree().clearSelection();
	    	return;
	    }
		if(graphInSelection)
			return;

		graphInSelection = true;
		Object obj = e.getCell();
		if (obj instanceof DefaultEdge)
		{//only handles edge, when graph is NOT in CLEAR selection mode.
			DefaultEdge edge = (DefaultEdge) obj;
			Object source = edge.getSource();
			Object target = edge.getTarget();
			Object sourceUserObject = getUserObject(source);
			highlightTreeNodeInTree(mappingPanel.getSourceTree(), sourceUserObject);

			Object targetUserObject = getUserObject(target);
			highlightTreeNodeInTree(mappingPanel.getTargetTree(), targetUserObject);
		}
		graphInSelection = false;
	}

	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		if (mappingPanel.isInDragDropMode())
			return;
		//ignore if it is a clear selection event
		if (!e.isAddedPath())
			return;

		if (graphInSelection)
			return;
		graph.clearSelection();
		Object eventSource = e.getSource();
		TreePath path = e.getPath();
		if (path==null)
			return;
		//set tree highlight being cleaned as clean graph highlight
		MappingBaseTree evntTree=(MappingBaseTree)eventSource;
		evntTree.setSelectionPath(path);

		String searchMode = null;
		if(eventSource instanceof MappingSourceTree)
			searchMode = MappingViewCommonComponent.SEARCH_BY_SOURCE_NODE;
		else if(eventSource instanceof MappingTargetTree)
			searchMode = MappingViewCommonComponent.SEARCH_BY_TARGET_NODE;

		Object node = (path==null)? null : path.getLastPathComponent();
		MappingDataManager dataManager = mappingPanel.getMappingDataManager();
		List<MappingViewCommonComponent> compList = dataManager.findMappingViewCommonComponentList(node, searchMode);
		int size = compList.size();
		for(int i=0; i<size; i++)
		{
			MappingViewCommonComponent comp = compList.get(i);
			DefaultEdge linkEdge = comp.getLinkEdge();
			if(graph!=null)
			{//highlight linkEdge
				graph.setSelectionCell(linkEdge);
			}
		}
	}

	private Object getUserObject(Object graphOrTreeNode)
	{
		if(graphOrTreeNode instanceof DefaultPort)
		{
			DefaultMutableTreeNode parentCell = (DefaultMutableTreeNode)((DefaultPort) graphOrTreeNode).getParent();
			return getUserObject(parentCell);
		}
		else if(graphOrTreeNode instanceof DefaultMutableTreeNode)
		{
			return ((DefaultMutableTreeNode)graphOrTreeNode).getUserObject();
		}
		else
		{
			return null;
		}
	}

	private void highlightTreeNodeInTree(JTree tree, Object object)
	{
		if ((!(object instanceof DefaultGraphCell) && (object instanceof DefaultMutableTreeNode)))
		{//screen out possible graph cell but just leave pure tree node to be highlighted
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) object;
			TreePath treePath = new TreePath(treeNode.getPath());
			tree.setSelectionPath(treePath);
		}
	}

	/**
	 * Invoked when the mouse has been clicked on a component.
	 */
	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e))
		{
			Container parentC = e.getComponent().getParent();

			while ( !(parentC instanceof JScrollPane))
			{
				parentC=parentC.getParent();
			}

			MappingTreeScrollPane mappingScroll=(MappingTreeScrollPane)parentC;

			if(mappingScroll.getPaneType().equals(MappingTreeScrollPane.DRAW_NODE_TO_LEFT))
			{
				// Create PopupMenu for the Cell
				JPopupMenu menu = createTargetPopupMenu();

				// Display PopupMenu
				menu.show(e.getComponent(), e.getX(), e.getY());
			}

			if(mappingScroll.getPaneType().equals(MappingTreeScrollPane.DRAW_NODE_TO_RIGHT))
			{
				// Create PopupMenu for the Cell
				JPopupMenu menu = createSourcePopupMenu();

				// Display PopupMenu
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		//if mouse clicked, it is definitely not in drag and drop.
        boolean previousValue;
        try
        {
            previousValue = mappingPanel.isInDragDropMode();
        }
        catch(NullPointerException ne)
        {
            JOptionPane.showMessageDialog(mappingPanel, "You should input the source and target file names first.", "No Source or Target file", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mappingPanel.setInDragDropMode(false);

        if(previousValue)
		{//previously in drag and drop mode, so to ensure the highlight back up, generate the corresponding tree or graph selection event.
			Object source = e.getSource();
//			String name = source == null ? "null" : source.getClass().getName();
//			System.out.println("Source is ' " + name + "'," + "LinkSelectionHighlighter's mouseClicked is called");
			//following code tries to trigger the valueChanged() methods above to mimic and restore the "highlight" command
			if(source instanceof JGraph)
			{
				JGraph mGraph = (JGraph) source;
				mGraph.setSelectionCells(mGraph.getSelectionCells());
//				GraphSelectionEvent event = new GraphSelectionEvent(source, new Object[]{}, )
			}
			else if(source instanceof JTree)
			{
				JTree mTree = (JTree) source;
				//following code does not trigger valueChanged(TreeSelectionEvent) above.
				//mTree.setSelectionPaths(mTree.getSelectionPaths());
				//mTree.setSelectionRows(mTree.getSelectionRows());
				TreePath[] paths = mTree.getSelectionPaths();
				int size = paths==null? 0 : paths.length;
				if(size>0)
				{
					boolean[] areNew = new boolean[size];
					for(int i=0; i<size; i++)
					{
						areNew[i] = true;
					}
					TreePath leadingPath = mTree.getLeadSelectionPath();
					TreeSelectionEvent event = new TreeSelectionEvent(source, paths, areNew, null, leadingPath);
					valueChanged(event);
				}
			}
		}
	}
	/**
	 * Set popup menu for the nodes of source tree
	 * @return sourceNodePopup
	 */
	private JPopupMenu createSourcePopupMenu()
	{
        JTree sourceTree = mappingPanel.getSourceTree();
        JPopupMenu popupMenu = new JPopupMenu();

        ModelMetadata modelMetadata = CumulativeMappingGenerator.getInstance().getMetaModel();//ModelMetadata.getInstance();

		TreePath leadingPath = sourceTree.getLeadSelectionPath();
        //Check to see if anything is selected
		if( sourceTree.getLeadSelectionPath() != null )
		{
			leadingPath = sourceTree.getLeadSelectionPath();
			//TreePath paths[] = sourceTree.getSelectionPaths();
			DefaultMutableTreeNode mutNode = (DefaultMutableTreeNode)leadingPath.getLastPathComponent();

			Object nodeObj=mutNode.getUserObject();
			if (!(nodeObj instanceof MetaObject))
				return popupMenu;
			MetaObject metaObj=(MetaObject)nodeObj;

			//the following four actions apply to object metadata
			ObjectAnnotationAction discValueSettingAction=new ObjectAnnotationAction("Set Discriminator Value", ObjectAnnotationAction.SET_DISCRIMINATOR_VALUE,middlePanel);
            popupMenu.add(new JMenuItem(discValueSettingAction));
            ObjectAnnotationAction discValueRemoveAction=new ObjectAnnotationAction("Remove Discriminator Value", ObjectAnnotationAction.REMOVE_DISCRIMINATOR_VALUE,middlePanel);
            popupMenu.add(new JMenuItem(discValueRemoveAction));
            ObjectAnnotationAction gNullflavorValueSettingAction=new ObjectAnnotationAction("Set Global NullFlavor Constant", ObjectAnnotationAction.SET_GLOBAL_NULLFLAVOR_CONTANT,middlePanel);
            popupMenu.add(new JMenuItem(gNullflavorValueSettingAction));
            ObjectAnnotationAction gNullflavorValueRemoveAction=new ObjectAnnotationAction("Remove Global NullFlavor Constant", ObjectAnnotationAction.REMOVE_GLOBAL_NULLFLAVOR_CONSTANT,middlePanel);
            popupMenu.add(new JMenuItem(gNullflavorValueRemoveAction));

            popupMenu.addSeparator();

            //add action for attribute
            AttributeAnnotationAction pkSetAction=new AttributeAnnotationAction("Set as Primary Key", AttributeAnnotationAction.SET_AS_PK,middlePanel);
            popupMenu.add(new JMenuItem(pkSetAction));
            pkSetAction.setAnnotationTagName("id-attribute");
            AttributeAnnotationAction pkUnsetAction=new AttributeAnnotationAction("Unset as Primary Key", AttributeAnnotationAction.REMOVE_ANNOTATION_TAG,middlePanel);
            popupMenu.add(new JMenuItem(pkUnsetAction));
            pkUnsetAction.setAnnotationTagName("id-attribute");

            AttributeAnnotationAction constantSetAction=new AttributeAnnotationAction("Set Local Constant Value", AttributeAnnotationAction.SET_CONSTANT_VALUE,middlePanel);
            popupMenu.add(new JMenuItem(constantSetAction));
            AttributeAnnotationAction constantRemoveAction=new AttributeAnnotationAction("Remove Local Constant Value", AttributeAnnotationAction.REMOVE_ANNOTATION_TAG,middlePanel);
            popupMenu.add(new JMenuItem(constantRemoveAction));

            AttributeAnnotationAction nullflavorSetAction=new AttributeAnnotationAction("Set Local NullFlavor Constant", AttributeAnnotationAction.SET_NULLFLAOVR_CONSTANT,middlePanel);
            popupMenu.add(new JMenuItem(nullflavorSetAction));
            AttributeAnnotationAction nullflavorRemoveAction=new AttributeAnnotationAction("Remove Local NullFlavor Constant", AttributeAnnotationAction.REMOVE_ANNOTATION_TAG,middlePanel);
            popupMenu.add(new JMenuItem(nullflavorRemoveAction));

            AttributeAnnotationAction elementTypeSetAction=new AttributeAnnotationAction("Set Collection Eelment Type", AttributeAnnotationAction.SET_COLLECTION_ELEMENT_TYPE,middlePanel);
            popupMenu.add(new JMenuItem(elementTypeSetAction));
            AttributeAnnotationAction elementTypeRemoveAction=new AttributeAnnotationAction("Remove Collection Eelment Type", AttributeAnnotationAction.REMOVE_ANNOTATION_TAG,middlePanel);
            popupMenu.add(new JMenuItem(elementTypeRemoveAction));
            popupMenu.addSeparator();

        	//The following actions apply for association metadata
        	AssociationAnnotationAction cascadeSettingAction=new AssociationAnnotationAction("Set Cascade Value", AssociationAnnotationAction.SET_CASCADE_SETTING,middlePanel);
            popupMenu.add(new JMenuItem(cascadeSettingAction));
            AssociationAnnotationAction cascadeRemoveAction=new AssociationAnnotationAction("Remove Cascade Value",  AssociationAnnotationAction.REMOVE_CASCADE_SETTING,middlePanel);
            popupMenu.add(new JMenuItem(cascadeRemoveAction));

            AssociationAnnotationAction inverseSettingAction=new AssociationAnnotationAction("Set as Inverse Side",  AssociationAnnotationAction.SET_INVERSEOF,middlePanel);
            popupMenu.add(new JMenuItem(inverseSettingAction));
            AssociationAnnotationAction inverseRemoveAction=new AssociationAnnotationAction("Unset as Inverse Side",  AssociationAnnotationAction.REMOVE_INVERSEOF, middlePanel);
            popupMenu.add(new JMenuItem(inverseRemoveAction));

            if ( metaObj instanceof AssociationMetadata )
            {
            	AssociationMetadata asscMetadata = (AssociationMetadata)mutNode.getUserObject();
            	if (!asscMetadata.isMapped())
            		return popupMenu;
            	//check if "inverse-of" exist
            	UMLAssociation umlAssc=asscMetadata.getUMLAssociation();
            	String pureAssciationEndPath=XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(),  asscMetadata.getXPath());
            	UMLTaggedValue correlationTagValue=umlAssc.getTaggedValue("correlation-table");

            	if (correlationTagValue!=null)
    			{
            		ColumnMetadata columnMapped=(ColumnMetadata)CumulativeMappingGenerator.getInstance().getCumulativeMapping().findMappedTarget(asscMetadata.getXPath());
                	UMLAttribute xpathAttr=ModelUtil.findAttribute(modelMetadata.getModel(),columnMapped.getXPath());
                	UMLTaggedValue inverseTagValue=xpathAttr.getTaggedValue("inverse-of");
                	if (inverseTagValue!=null)//&&inverseTagValue.getValue().equals(pureAssciationEndPath))
	    			{
	    				inverseRemoveAction.setMetaAnnoted(asscMetadata);
	    				inverseRemoveAction.setEnabled(true);
	    			}
	    			else
	    			{
	    				inverseSettingAction.setMetaAnnoted(asscMetadata);
	    				inverseSettingAction.setEnabled(true);
	    			}
    			}
    			//check if NCI_CASCADE_ASSOCIATION exist

    			String fullCascadeTagName="NCI_CASCADE_ASSOCIATION#"+pureAssciationEndPath;
    			UMLTaggedValue cascadeTagValue=umlAssc.getTaggedValue(fullCascadeTagName);
     			cascadeSettingAction.setMetaAnnoted(asscMetadata);
   				cascadeSettingAction.setEnabled(true);
    			if (cascadeTagValue!=null)
    			{
     	   			System.out.println("AssociationAnnotationAction.doAction()..existing value:"+cascadeTagValue.getName()+"="+cascadeTagValue.getValue());
   					cascadeRemoveAction.setMetaAnnoted(asscMetadata);
   					cascadeRemoveAction.setEnabled(true);
    			}
            }
            else 	if (metaObj instanceof AttributeMetadata)
            {
            	AttributeMetadata attrMetadata = (AttributeMetadata)mutNode.getUserObject();
            	AttributeMetadata annotationAttrMetadat=Iso21090uiUtil.findAnnotationAttribute(mutNode);
            	boolean isGlobal=false;
    			if (attrMetadata.getXPath().equals(annotationAttrMetadat.getXPath()))
    				isGlobal=true;
 				UMLAttribute xpathAttr= ModelUtil.findAttribute(modelMetadata.getModel(),annotationAttrMetadat.getXPath());
				String tagName="mapped-constant:"+XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(), annotationAttrMetadat.getXPath());

				if (xpathAttr==null&&annotationAttrMetadat.isDerived())
				{
					ObjectMetadata holderObject=(ObjectMetadata)modelMetadata.getModelMetadata().get(annotationAttrMetadat.getParentXPath());
					xpathAttr=Iso21090uiUtil.findInheritedAttributeDefinition(modelMetadata.getModel(), annotationAttrMetadat, holderObject.getUmlClass());
				}
				//add relative path for local attribute
				if (!isGlobal)
					tagName=tagName+"."+Iso21090uiUtil.findAttributeRelativePath(mutNode);

				if (mutNode.isLeaf())
            	{
        			if (isGlobal)
        			{
        				constantSetAction.setName("Set Global Constant");
        				constantRemoveAction.setName("Remove Global Constant");
        			}
            		constantSetAction.setEnabled(true);
            		constantSetAction.setMetaAnnoted(annotationAttrMetadat);
            		constantSetAction.setAnnotationTagName(tagName);
            		if (xpathAttr.getTaggedValue(tagName)!=null)
            		{
            			constantRemoveAction.setEnabled(true);
            			constantRemoveAction.setMetaAnnoted(annotationAttrMetadat);
            			constantRemoveAction.setAnnotationTagName(tagName);
            		}
            	}
				else if (attrMetadata.getDatatype().equalsIgnoreCase("SEQUENCE(ADXP)"))
//
//            	else if (annotationAttrMetadat.getDatatype().equalsIgnoreCase("DSET<AD>")
//            			&&attrMetadata.getDatatype().equalsIgnoreCase("SEQUENCE(ADXP)"))
            	{
            		tagName="mapped-collection-element-type:"+attrMetadata.getName();
            		elementTypeSetAction.setEnabled(true);
            		elementTypeSetAction.setMetaAnnoted(annotationAttrMetadat);
            		elementTypeSetAction.setAnnotationTagName(tagName);
            		if (xpathAttr.getTaggedValue(tagName)!=null)
            		{
            			elementTypeRemoveAction.setEnabled(true);
            			elementTypeRemoveAction.setMetaAnnoted(annotationAttrMetadat);
            			elementTypeRemoveAction.setAnnotationTagName(tagName);
            		}
            	}
            	else
            	{
            		tagName=tagName+".nullFlavor";
            		nullflavorSetAction.setEnabled(true);
            		nullflavorSetAction.setMetaAnnoted(annotationAttrMetadat);
            		nullflavorSetAction.setAnnotationTagName(tagName);
            		if (xpathAttr.getTaggedValue(tagName)!=null)
            		{
            			nullflavorRemoveAction.setEnabled(true);
            			nullflavorRemoveAction.setMetaAnnoted(annotationAttrMetadat);
            			nullflavorRemoveAction.setAnnotationTagName(tagName);
            		}
            	}
            	if (attrMetadata!=annotationAttrMetadat) //.isMapped())
            		return popupMenu;

            	tagName="id-attribute";
            	UMLTaggedValue pkTag=xpathAttr.getTaggedValue(tagName);
            	if (pkTag!=null)
            	{
            		pkUnsetAction.setMetaAnnoted(annotationAttrMetadat);
            		pkUnsetAction.setEnabled(true);
            	}
            	else
            	{
            		UMLClass parentClass=ModelUtil.findClass(modelMetadata.getModel(), annotationAttrMetadat.getParentXPath());
            		for (UMLAttribute sblAttribute:parentClass.getAttributes())
        			{
        				if (sblAttribute.getTaggedValue(tagName)!=null)
        					return popupMenu;
        			}
                	pkSetAction.setMetaAnnoted(annotationAttrMetadat);
                	pkSetAction.setEnabled(true);
            	}
            }
            else  if (metaObj instanceof ObjectMetadata) {

            	ObjectMetadata objectMetadata = (ObjectMetadata)mutNode.getUserObject();
            	UMLClass objClass=(UMLClass)ModelUtil.findClass(modelMetadata.getModel(), objectMetadata.getXPath());
            	if (objClass==null)
            		objClass=objectMetadata.getUmlClass();
            	//enable GLOBAL_NULLFLAVOR_CONSTANT actions
            	if (Iso21090Util.resolveAttributeDatatype(objectMetadata.getName())!=null)
            	{
            		gNullflavorValueSettingAction.setMetaAnnoted(objectMetadata);
            		gNullflavorValueSettingAction.setEnabled(true);
            		String objecttCleanpath=XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(), objectMetadata.getXPath());
            		UMLTaggedValue globalNullflavorTag=objClass.getTaggedValue("mapped-constant:"+objecttCleanpath+".nullFlavor");
            		if (globalNullflavorTag!=null)
                	{
            			gNullflavorValueRemoveAction.setMetaAnnoted(objectMetadata);
            			gNullflavorValueRemoveAction.setEnabled(true);
                	}
            	}
            	if (!objectMetadata.isMapped())
            		return popupMenu;

            	//check if this class is a sub-class
				List<UMLGeneralization> clazzGs = objClass.getGeneralizations();
				boolean isRoot=true;
                for (UMLGeneralization clazzG : clazzGs) {
                    UMLClass parent =(UMLClass) clazzG.getSupertype();
                    if (parent != objClass) {
                    	isRoot = false;
                        break;
                    }
                }
                if (isRoot)
            		return popupMenu;

            	//The class is a sub-class, continue
            	//check if discrimniator value exists
            	UMLTaggedValue discTag=objClass.getTaggedValue("discriminator");
            	if (discTag!=null)
            	{
            		discValueRemoveAction.setMetaAnnoted(objectMetadata);
            		discValueRemoveAction.setEnabled(true);
            	}
               	discValueSettingAction.setMetaAnnoted(objectMetadata);
               	discValueSettingAction.setEnabled(true);
            }
        }
		return popupMenu;
	}

	/**
	 * Create the Popup menu for a target tree node
	 * @return
	 */
	private JPopupMenu createTargetPopupMenu()
	{
		JPopupMenu popupMenu = new JPopupMenu();
        //Could change this depending on whether lazy/eager
    	ModelMetadata modelMetadata = CumulativeMappingGenerator.getInstance().getMetaModel();//ModelMetadata.getInstance();

        JTree targetTree = mappingPanel.getTargetTree();
		TreePath leadingPath = targetTree.getLeadSelectionPath();

		//the following two actions apply to table metadata
		ColumnAnnotationAction pkSettingAction=new ColumnAnnotationAction("Set Primary Key Generator", ColumnAnnotationAction.SET_PK_GENERATOR,middlePanel);
        popupMenu.add(new JMenuItem(pkSettingAction));
        ColumnAnnotationAction pkRemoveAction=new ColumnAnnotationAction("Remove Primary Key Generator", ColumnAnnotationAction.REMOVE_PK_GENERATOR,middlePanel);
        popupMenu.add(new JMenuItem(pkRemoveAction));

        popupMenu.addSeparator();

    	//The following actions apply for association column metadata
        ColumnAnnotationAction discKeySettingAction=new ColumnAnnotationAction("Set as Discriminator Key", ColumnAnnotationAction.SET_DISCRIMINATOR_KEY,middlePanel);
        popupMenu.add(new JMenuItem(discKeySettingAction));
        ColumnAnnotationAction discKeyRemoveAction=new ColumnAnnotationAction("Unset as Discriminator Key",  ColumnAnnotationAction.REMOVE_DISCRIMINATOR_KEY,middlePanel);
        popupMenu.add(new JMenuItem(discKeyRemoveAction));
        popupMenu.addSeparator();

      //The following actions apply for column metadata of table mapped to ISO data type
        ColumnAnnotationAction isoInverseSettingAction=new ColumnAnnotationAction("Set as Inverse Side", ColumnAnnotationAction.SET_INVERSE_KEY,middlePanel);
        popupMenu.add(new JMenuItem(isoInverseSettingAction));
        ColumnAnnotationAction isoInverseRemoveAction=new ColumnAnnotationAction("Unset as Inverse Side",  ColumnAnnotationAction.REMOVE_INVERSE_KEY,middlePanel);
        popupMenu.add(new JMenuItem(isoInverseRemoveAction));
        ColumnAnnotationAction isoImplicitKeySettingAction=new ColumnAnnotationAction("Map to Implicit Key", ColumnAnnotationAction.SET_IMPLICT_KEY,middlePanel);
        popupMenu.add(new JMenuItem(isoImplicitKeySettingAction));
        ColumnAnnotationAction isoImplicitKeyRemoveAction=new ColumnAnnotationAction("Unmap from Implicit Key",  ColumnAnnotationAction.REMOVE_IMPLICT_KEY,middlePanel);
        popupMenu.add(new JMenuItem(isoImplicitKeyRemoveAction));
        //Check to see if anything is selected
		if( targetTree.getLeadSelectionPath() != null )
		{
			leadingPath = targetTree.getLeadSelectionPath();
			DefaultMutableTreeNode mutNode = (DefaultMutableTreeNode)leadingPath.getLastPathComponent();
			MetaObject metaObj=(MetaObject)mutNode.getUserObject();
			if (metaObj instanceof ColumnMetadata)
			{
				ColumnMetadata columnMeta=(ColumnMetadata)metaObj;
				UMLAttribute xpathAttr=ModelUtil.findAttribute(modelMetadata.getModel(),columnMeta.getXPath());
				//check the column is mapped
				UMLTaggedValue mappingAsscTag=xpathAttr.getTaggedValue("implements-association");
				if (mappingAsscTag!=null)
					return popupMenu;

				UMLTaggedValue mappingAttrTag=xpathAttr.getTaggedValue("mapped-attributes");
 				if (mappingAttrTag!=null)
				{
					//check if primary key column
					HashMap<String, HashMap<String, String>> pkSetting=XMIAnnotationUtil.findPrimaryKeyGenerrator(xpathAttr);
					if (!pkSetting.isEmpty())
					{
						pkRemoveAction.setMetaAnnoted(columnMeta);
						pkRemoveAction.setEnabled(true);
						pkSettingAction.setMetaAnnoted(columnMeta);
						pkSettingAction.setEnabled(true);
					}
					else
					{
						//enable pkSettingAction if no attribute has "primaryKey Generator"
						boolean pkSet=false;
						TableMetadata tblMeta=columnMeta.getTableMetadata();
						for(ColumnMetadata anyCol:tblMeta.getColumns())
						{
							UMLAttribute anyUmlAttr=ModelUtil.findAttribute(modelMetadata.getModel(),anyCol.getXPath());
							HashMap<String, HashMap<String, String>> pkAnySetting=XMIAnnotationUtil.findPrimaryKeyGenerrator(anyUmlAttr);
							if (!pkAnySetting.isEmpty())
							{
								pkSet=true;
								break;
							}
						}
						if (!pkSet)
						{
							//enable pkSettingAction since no attribute has "primaryKey Generator"
							pkSettingAction.setMetaAnnoted(columnMeta);
							pkSettingAction.setEnabled(true);
						}
					}
					//check if the column is mapped as implicit primary key
					AttributeMetadata srcAttr=(AttributeMetadata)CumulativeMappingGenerator.getInstance().getCumulativeMapping().findMappedSource(columnMeta.getXPath());
					if (srcAttr==null)
					{
						isoImplicitKeyRemoveAction.setEnabled(true);
						isoImplicitKeyRemoveAction.setMetaAnnoted(columnMeta);
					}
				}
				else
				{
					//the discriminator key column will not map to any thing
					UMLTaggedValue discKeyTag=xpathAttr.getTaggedValue("discriminator");
					if (discKeyTag!=null)
					{
						discKeyRemoveAction.setMetaAnnoted(columnMeta);
						discKeyRemoveAction.setEnabled(true);
					}
					else
					{
						discKeySettingAction.setMetaAnnoted(columnMeta);
						discKeySettingAction.setEnabled(true);
					}
					//the inverse-of key column will not map to any thing
					UMLTaggedValue inverseOfTag=xpathAttr.getTaggedValue("inverse-of");
					if (inverseOfTag!=null)
					{
						isoInverseRemoveAction.setMetaAnnoted(columnMeta);
						isoInverseRemoveAction.setEnabled(true);
					}
					else
					{
						isoInverseSettingAction.setMetaAnnoted(columnMeta);
						isoInverseSettingAction.setEnabled(true);

						//the unmapped column always be a candidate of implicit primary key
						isoImplicitKeySettingAction.setMetaAnnoted(columnMeta);
						isoImplicitKeySettingAction.setEnabled(true);
					}
				}
			}
		}
		return popupMenu;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.17  2009/07/10 19:57:37  wangeug
 * HISTORY      : MMS re-engineering
 * HISTORY      :
 * HISTORY      : Revision 1.16  2009/06/12 15:54:08  wangeug
 * HISTORY      : clean code: caAdapter MMS 4.1.1
 * HISTORY      :
 * HISTORY      : Revision 1.15  2008/10/20 15:42:47  linc
 * HISTORY      : remove clob, inverse-of, lazy/eager functionality from mms.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2008/10/02 19:16:48  linc
 * HISTORY      : updated.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2008/10/02 18:59:27  linc
 * HISTORY      : remove "lazy" menu item when not applicable.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2008/06/09 19:54:06  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2007/12/13 21:09:11  wangeug
 * HISTORY      : resolve code dependence in compiling
 * HISTORY      :
 * HISTORY      : Revision 1.10  2007/10/02 14:52:25  schroedn
 * HISTORY      : Changed error message to warning message
 * HISTORY      :
 * HISTORY      : Revision 1.9  2007/09/21 04:41:21  wuye
 * HISTORY      : removed system.out
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/09/20 16:39:13  schroedn
 * HISTORY      : License text
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/09/17 15:08:05  wuye
 * HISTORY      : added modify discriminator value capability
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/09/14 15:06:57  wuye
 * HISTORY      : Added support for table per inheritence structure
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/09/11 20:27:03  schroedn
 * HISTORY      : CLob, Discriminator, Lazy/Eager
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/09/05 15:15:47  schroedn
 * HISTORY      : Added icons to PK and Lazy/Eager
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/07 20:50:39  schroedn
 * HISTORY      : New Feature, Primary Key and Lazy/Eager functions added to MMS
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/08/07 15:51:25  schroedn
 * HISTORY      : New Feature, Primary Key and Lazy/Eager functions added to MMS
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/12/19 22:48:30  umkis
 * HISTORY      : Null pointer error protetion
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/07 19:20:50  jiangsc
 * HISTORY      : With enhanced functions.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/23 20:03:59  jiangsc
 * HISTORY      : Enhancement on highlight functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/24 20:31:00  jiangsc
 * HISTORY      : Turned off auto-scroll feature to comprise mapping issue.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/21 15:12:07  jiangsc
 * HISTORY      : Removed some comments lines
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/07 22:20:58  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/03 19:33:59  jiangsc
 * HISTORY      : Implement highlighting tree nodes upon graph selection.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/27 21:47:58  jiangsc
 * HISTORY      : Customized edge rendering and initially added a link highlighter class.
 * HISTORY      :
 */
