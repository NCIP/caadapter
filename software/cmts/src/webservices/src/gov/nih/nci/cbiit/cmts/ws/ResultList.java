/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
 
@XmlRootElement(name = "ReulstData")
public class ResultList {
 
    @XmlElement(name = "result", required = true)
    private List <String> result;
 
    public List<String> getResultData() {
        if (result == null) {
        	result = new ArrayList<String>();
        }
        return this.result;
    }
}