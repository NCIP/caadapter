package gov.nih.nci.cbiit.cmts.ui.dnd;

import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.ui.common.UIHelper;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionTypeNodeLoader;
import gov.nih.nci.cbiit.cmts.ui.mapping.ElementMetaLoader;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeTransferableNode implements Transferable, Serializable {

	public static final DataFlavor mutableTreeNodeFlavor = new DataFlavor(DefaultMutableTreeNode.class, "DefaultMutableTreeNode");
	public static final DataFlavor localStringFlavor = DataFlavor.stringFlavor;

	public static final DataFlavor[] flavors = {
		mutableTreeNodeFlavor,
		DataFlavor.stringFlavor
	  };
	 private static final List flavorList = Arrays.asList( flavors );
	 private DefaultMutableTreeNode nodeTransferable;
	 
	 public TreeTransferableNode(DefaultMutableTreeNode node)
	 {
		 nodeTransferable=node;
	 }
	 public synchronized Object getTransferData(DataFlavor flavor)
//	 public Object getTransferData(DataFlavor flavor)
	 throws UnsupportedFlavorException, IOException {
		 if (flavor.getRepresentationClass().equals(mutableTreeNodeFlavor.getRepresentationClass())) 
		 {
			 if(nodeTransferable.getUserObject() instanceof ElementMetaLoader.MyTreeObject)
			 {		
				 //return the node path for mapping tree
				 String pathString = UIHelper.getPathStringForNode(nodeTransferable);
				 return pathString;
			 }
			 else if (nodeTransferable.getUserObject() instanceof FunctionTypeNodeLoader.MyTreeObject)
			 {
				//return the FunctionDef object for tree node of function meta
				 FunctionDef f =null;	
				 f=((FunctionDef)((FunctionTypeNodeLoader.MyTreeObject)nodeTransferable.getUserObject()).getObj()); 
				 return f;
			 }
			 else 
				 return nodeTransferable.getUserObject();
		 }
		 else
			 throw new UnsupportedFlavorException(flavor);
 
//	    else {
//	    System.out.println("returning latin-1 charset");    
//	    return new ByteArrayInputStream(this.string.getBytes("iso8859-1"));
//	      }
//	    } else if (StringTransferable.localStringFlavor.equals(flavor)) {
//	      return this.string;
//	    } 

	  }


	public synchronized DataFlavor[] getTransferDataFlavors() {
	    return flavors;
	  }
	public boolean isDataFlavorSupported( DataFlavor flavor ) {
		return (flavorList.contains(flavor));
	}
}
