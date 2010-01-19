package gov.nih.nci.cbiit.cmts.ui.dnd;

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
	 private static final List<DataFlavor> flavorList = Arrays.asList( flavors );
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
			 return nodeTransferable;
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
