package gov.nih.nci.caadapter.sdtm;

import gov.nih.nci.caadapter.ui.common.MappableNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v3.2 revision
 *          $Revision: 1.4 $
 */
public class SDTMMappingGenerator {
    public ArrayList<String> results = null;

    int counter;

    String scsSDTMFile;

    String scsDefineXMLFIle;


    private HashMap linkSelectionHelper = null;


    public static SDTMMappingGenerator _sdtmMappingGeneratorReference;

    // new ArrayList();
    public SDTMMappingGenerator() {
        results = new ArrayList<String>();
        linkSelectionHelper = new HashMap();
        counter = 0;
    }

    public HashMap getLinkSelectionHelper() {
        return linkSelectionHelper;
    }

    public void removeObject(String source, String target) {
        results.remove(source + "~" + target);
    }

    public void putNodes(MappableNode source, MappableNode target){
        linkSelectionHelper.put(source, target);
    }

    public boolean put(String source, String target) throws Exception {
        //add a filter to remove the <choice> this causes parsing issues
        source = source.replaceAll("<", "[");
        source = source.replaceAll(">", "]");
        try {
            results.add(counter++, source + "~" + target);
//            try {
//                linkSelectionHelper.put(source, target);
//            } catch (Exception e) {
//
//            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            results.add(source + "~" + target);
        }
        return true;
    }

    public String getScsSDTMFile() {
        return scsSDTMFile;
    }

    public void setScsSDTMFile(String scsSDTMFile) {
        this.scsSDTMFile = scsSDTMFile;
    }

    public String getScsDefineXMLFIle() {
        return scsDefineXMLFIle;
    }

    public void setScsDefineXMLFIle(String scsDefineXMLFIle) {
        this.scsDefineXMLFIle = scsDefineXMLFIle;
    }

    public static SDTMMappingGenerator get_sdtmMappingGeneratorReference() {
        return _sdtmMappingGeneratorReference;
    }

    public void set_sdtmMappingGeneratorReference(SDTMMappingGenerator mappingGeneratorReference) {
        _sdtmMappingGeneratorReference = mappingGeneratorReference;
    }


}
