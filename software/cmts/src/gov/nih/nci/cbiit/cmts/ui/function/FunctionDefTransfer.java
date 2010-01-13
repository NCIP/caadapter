package gov.nih.nci.cbiit.cmts.ui.function;

import gov.nih.nci.cbiit.cmts.core.FunctionDef;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

public class FunctionDefTransfer implements Transferable, Serializable {

	private FunctionDef object;
	public static String FUNCTION_DEFINITION_FLAVOR="FunctionDefinitionFlavor";
	private static DataFlavor[] flavorSupport=new DataFlavor[]{new DataFlavor(FunctionDef.class, FUNCTION_DEFINITION_FLAVOR)};
	public  FunctionDefTransfer(FunctionDef transfered)
	{
		object=transfered;
	}
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		// TODO Auto-generated method stub
		return object;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		// TODO Auto-generated method stub
		return flavorSupport;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		// TODO Auto-generated method stub
		System.out.println("FunctionDefTransfer.isDataFlavorSupported()..flavor"+flavor);
		return false;
	}

}
