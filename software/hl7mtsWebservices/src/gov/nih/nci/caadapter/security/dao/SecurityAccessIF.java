/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.dao;

import gov.nih.nci.caadapter.security.domain.Permissions;
import gov.nih.nci.caadapter.security.domain.SecurityObject;
import gov.nih.nci.caadapter.security.domain.User;
import gov.nih.nci.caadapter.security.domain.UserAddress;
import gov.nih.nci.caadapter.security.domain.UserEmail;
import gov.nih.nci.caadapter.security.domain.UserGroup;
import gov.nih.nci.caadapter.security.domain.UserSecurityObjectMapping;
import gov.nih.nci.caadapter.security.domain.WebServiceMapping;

import java.util.List;

/**
 * The SecurityAccessIF define the APIs to access the data from security xmls.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-11-10 22:40:04 $
 *  * @created 24-Oct-2008 
 */
//import gov.nih.nci.caadapter.security.DAO.xmldao.XmlDaoImpl;


public interface SecurityAccessIF  {

  
    /**
     * Retrieves all the SecurityObjects .
     */

    public List<SecurityObject> getAllSecurityObjects();
    /**
     * Retrieves all the UserGroups .
     */

    public List<UserGroup> getAllUserGroups();
    /**
     * Retrieves all the Users .
     */

    public List<User> getAllUsers();
    /**
     * Retrieves all the UserSecurityObjectMappings .
     */

    public List<UserSecurityObjectMapping> getAlluserSecObjMappings();
    /**
     * Retrieves all the WebSericeMappings .
     */
    public List <WebServiceMapping> getAllWebserviceMappings();

    /**
     * Retrieves all the address for a user.
     * @param userId
     */
    public List<UserAddress> getUserAddresses(String userId);

    /**
     * List all the email address for a user
     * @param userId
     */
    public List<UserEmail> getUserEmailAddresses(String userId);

    /**
     * List all the Security objects mapped for a user.
     * @param userId
     */
    public UserSecurityObjectMapping getuserSecObjMapping(String userId);

    /**
     * List alll the Webservicemapping for a user
     * @param User
     */
    public List <WebServiceMapping> getUserWebserviceMappings(String User);

    /**
     * validate if a user is valid user in the system.
     * @param userId
     * @param password
     */
    public boolean validateUser(String userId, String password);

    public Permissions getUserObjectPermssions(String userId, int i);
    
}