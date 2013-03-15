/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.dao.xmlDao;


import java.util.Properties;
import org.exolab.castor.mapping.ConfigurableFieldHandler;
import org.exolab.castor.mapping.ValidityException;

/**
 *
 * @author phadkes
 */
public class SecurityCastorFieldHandler implements ConfigurableFieldHandler {


    public SecurityCastorFieldHandler() {
        super();
    }

/**   Sets the configuation properties.  
     * @param Properties config
     */  
    public void setConfiguration(Properties config) throws ValidityException {
        String pattern = config.getProperty("MetricFlag");
        if (pattern == null) {
            throw new ValidityException("Required parameter \"MetricFlag\" is missing for CastorFieldHandler.");
        }
    }

  
    public void resetValue(Object arg0) throws IllegalStateException, IllegalArgumentException {
        throw new UnsupportedOperationException("resetValue Not supported yet.");
    }

    public void checkValidity(Object arg0) throws ValidityException, IllegalStateException {
        throw new UnsupportedOperationException("Validity Not supported yet.");
    }

    public Object newInstance(Object arg0) throws IllegalStateException {
        throw new UnsupportedOperationException("Instance Not supported yet.");
    }

    public Object getValue(Object arg0) throws IllegalStateException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setValue(Object arg0, Object arg1)
            throws IllegalStateException, IllegalArgumentException
    {
        // TODO Auto-generated method stub
        
    }

   
}
