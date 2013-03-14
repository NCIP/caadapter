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
 * Time: 11:14:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CalculateOnWSDL extends java.rmi.Remote {
    public java.lang.String execute(java.lang.String formula, java.util.HashMap param) throws java.rmi.RemoteException;
    public java.lang.String execute(java.lang.String formula, java.lang.String[] paramNames, java.lang.String[] values) throws java.rmi.RemoteException;
}