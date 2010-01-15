package gov.nih.nci.cbiit.cmts.ui.dnd;

import gov.nih.nci.cbiit.cmts.core.FunctionDef;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

public class FunctionDefTransferable implements Transferable, Serializable {

	private FunctionDef object;
	public static final DataFlavor functionDefinitionFlavor = new DataFlavor(DefaultMutableTreeNode.class, "DefaultMutableTreeNode");
	
	private static DataFlavor[] flavorSupport=new DataFlavor[]{functionDefinitionFlavor};
	private static final List flavorList = Arrays.asList( flavorSupport );
	public  FunctionDefTransferable(FunctionDef transfered)
	{
		object=transfered;
	}
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		// TODO Auto-generated method stub
		if (flavor.equals(functionDefinitionFlavor))
			return object;
		else
			throw new UnsupportedFlavorException(flavor);
	}

	public DataFlavor[] getTransferDataFlavors() {
		// TODO Auto-generated method stub
		return flavorSupport;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		// TODO Auto-generated method stub
		return (flavorList.contains(flavor));
	}

}
