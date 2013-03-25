/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.map.BaseComponent;

/**
 * A component that contains function information.
 *
 * @author OWNER: Eric Chen  Date: Jun 8, 2005
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.7 $
 * @date $$Date: 2008-11-21 16:16:49 $
 * @since caAdapter v1.2
 */

public class FunctionComponent extends BaseComponent {
    private static final String LOGID = "$RCSfile: FunctionComponent.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/FunctionComponent.java,v 1.7 2008-11-21 16:16:49 wangeug Exp $";

    private FunctionConstant functionConstant = null;
    private FunctionVocabularyMapping functionVocabularyMapping = null;
    private String id="";
    private static int idCount=0;


	// constuctors. singleton
    private FunctionComponent() {
    	super();
    	setId( idCount+"");
    	idCount++;
    }
    public static FunctionComponent getFunctionComponent() {
        return new FunctionComponent();
    }
//    public FunctionComponent(FunctionMeta meta) {
//        super(meta);
//    }

    /**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}



    // setters and getters.
    public FunctionConstant getFunctionConstant() {
        return functionConstant;
    }

    public void setFunctionConstant(FunctionConstant functionConstant) {
        this.functionConstant = functionConstant;
    }

    public FunctionVocabularyMapping getFunctionVocabularyMapping() {
        return functionVocabularyMapping;
    }

    public void setFunctionVocabularyMapping(FunctionVocabularyMapping vm) {
        this.functionVocabularyMapping = vm;
    }


    public FunctionMeta getMeta() {
        return (FunctionMeta)meta;
    }

    public void setMeta(FunctionMeta meta) {
        super.setMeta(meta);
    }

    public boolean isInputComplete() {
        return false;
    }

    public boolean isOutputComplete() {
        return false;
    }

    public Object compute() {
        return null;
    }

    public String getXmlPath()
    {
    	StringBuffer rtnSb=new StringBuffer("function");
    	rtnSb.append("."+getId());
    	return rtnSb.toString();
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2008/11/17 20:07:31  wangeug
 * HISTORY      : Move from HL7 module to common module
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/09/29 15:47:18  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/06/09 19:53:50  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/18 20:37:50  wangeug
 * HISTORY      : create CSV-H7L mapping with mapppingV4.0.xsd
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/17 20:47:19  wuye
 * HISTORY      : added id field
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 18:26:25  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/09/06 18:22:24  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/22 20:17:26  jiangsc
 * HISTORY      : Minor change
 * HISTORY      :
 */
