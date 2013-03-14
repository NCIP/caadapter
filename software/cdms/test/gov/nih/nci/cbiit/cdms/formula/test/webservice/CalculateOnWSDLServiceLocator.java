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
 * Time: 11:19:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class CalculateOnWSDLServiceLocator extends org.apache.axis.client.Service implements CalculateOnWSDLService {

    public CalculateOnWSDLServiceLocator() {
    }


    public CalculateOnWSDLServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CalculateOnWSDLServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CalculateOnWSDL
    private java.lang.String CalculateOnWSDL_address = "http://165.112.133.125:8080/caAdapterWS_cdms/ws/CalculateOnWSDL";

    public java.lang.String getCalculateOnWSDLAddress() {
        return CalculateOnWSDL_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CalculateOnWSDLWSDDServiceName = "CalculateOnWSDL";

    public java.lang.String getCalculateOnWSDLWSDDServiceName() {
        return CalculateOnWSDLWSDDServiceName;
    }

    public void setCalculateOnWSDLWSDDServiceName(java.lang.String name) {
        CalculateOnWSDLWSDDServiceName = name;
    }

    public gov.nih.nci.cbiit.cdms.formula.test.webservice.CalculateOnWSDL getCalculateOnWSDL() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CalculateOnWSDL_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCalculateOnWSDL(endpoint);
    }

    public gov.nih.nci.cbiit.cdms.formula.test.webservice.CalculateOnWSDL getCalculateOnWSDL(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            gov.nih.nci.cbiit.cdms.formula.test.webservice.CalculateOnWSDLSoapBindingStub _stub = new gov.nih.nci.cbiit.cdms.formula.test.webservice.CalculateOnWSDLSoapBindingStub(portAddress, this);
            _stub.setPortName(getCalculateOnWSDLWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCalculateOnWSDLEndpointAddress(java.lang.String address) {
        CalculateOnWSDL_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (gov.nih.nci.cbiit.cdms.formula.test.webservice.CalculateOnWSDL.class.isAssignableFrom(serviceEndpointInterface)) {
                gov.nih.nci.cbiit.cdms.formula.test.webservice.CalculateOnWSDLSoapBindingStub _stub = new gov.nih.nci.cbiit.cdms.formula.test.webservice.CalculateOnWSDLSoapBindingStub(new java.net.URL(CalculateOnWSDL_address), this);
                _stub.setPortName(getCalculateOnWSDLWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CalculateOnWSDL".equals(inputPortName)) {
            return getCalculateOnWSDL();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://165.112.133.125:8080/caAdapterWS_cdms/ws/CalculateOnWSDL", "CalculateOnWSDLService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://165.112.133.125:8080/caAdapterWS_cdms/ws/CalculateOnWSDL", "CalculateOnWSDL"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

if ("CalculateOnWSDL".equals(portName)) {
            setCalculateOnWSDLEndpointAddress(address);
        }
        else
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
