/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.test.webservice;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 6, 2010
 * Time: 11:15:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CalculateOnWSDLService extends javax.xml.rpc.Service {
    public java.lang.String getCalculateOnWSDLAddress();

    public gov.nih.nci.cbiit.cdms.formula.test.webservice.CalculateOnWSDL getCalculateOnWSDL() throws javax.xml.rpc.ServiceException;

    public gov.nih.nci.cbiit.cdms.formula.test.webservice.CalculateOnWSDL getCalculateOnWSDL(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

