package gov.nih.nci.cbiit.cmts.transform;

import gov.nih.nci.cbiit.cmts.common.ApplicationResult;

import java.util.List;

import gov.nih.nci.cbiit.cmts.transform.TransformationService;

public class DefaultTransformer implements TransformationService {

	private boolean presentable;
	@Override
	public boolean isPresentable() {
		// TODO Auto-generated method stub
		return presentable;
	}

	@Override
	public void setPresentable(boolean value) {
		// TODO Auto-generated method stub
		presentable=value;
	}

	@Override
	public String transfer(String sourceFile, String processInstruction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ApplicationResult> validateXmlData(Object validator,
			String xmlData) {
		// TODO Auto-generated method stub
		return null;
	}

}
