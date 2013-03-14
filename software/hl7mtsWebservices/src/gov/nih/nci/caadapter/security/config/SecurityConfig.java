/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.config;

/**
 * This file configures the xmlRepository document settings, castor mapping file etc.
 *
 * @author OWNER: Sandeep Phadke
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.1 $
 * @date $$Date: 2008-11-07 23:17:04 $
 *
 */

public class SecurityConfig {

    public static String SECURITY_XML_SOURCE_DIR="caAdapter/security/config";
    public static String SECURITY_XML_SECURITYOBJECT="caAdapter/security/config/dataxmls/SecurityObject.xml";
    public static String SECURITY_XML_USER="caAdapter/security/config/dataxmls/User.xml";
    public static String SECURITY_XML_USERGROUP="caAdapter/security/config/dataxmls/UserGroup.xml";
    public static String SECURITY_XML_USERSECURITYOBJECTMAPPING="caAdapter/security/config/dataxmls/UserSecurityObjectMapping.xml";
    public static String SECURITY_XML_WEBSERVICEMAPPING="caAdapter/security/config/dataxmls/WebServiceMapping.xml";

    public static String SECURITY_XML_MAPPING="caAdapter/security/config/Mapping.xml";

    public static String SECURITY_DATA_ACCESS_OBJECT="gov.nih.nci.caadapter.security.dao.xmlDao.XmlDaoImpl";

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/