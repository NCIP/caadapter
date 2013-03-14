/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.dao.xmlDao;

import gov.nih.nci.caadapter.security.domain.SecurityObject;
import gov.nih.nci.caadapter.security.domain.User;
import gov.nih.nci.caadapter.security.domain.UserAddress;
import gov.nih.nci.caadapter.security.domain.UserEmail;
import gov.nih.nci.caadapter.security.domain.UserGroup;
import gov.nih.nci.caadapter.security.domain.UserSecurityObjectMapping;
import gov.nih.nci.caadapter.security.domain.WebServiceMapping;

import java.io.Serializable;
import java.util.Vector;

public class SecurityXmlDatasource implements Serializable
{


    /**
     *
     * @author phadkes
     */
       private Vector<UserSecurityObjectMapping> userSecurityObjectMappingList;
       private Vector<SecurityObject> securityObjectList;
       private Vector<User> userList;
       private Vector<UserAddress> userAddressList;
       private Vector<UserEmail> userEmailList;
       private Vector<WebServiceMapping> webServiceMappingList;
       private Vector<UserGroup> userGroupList;


    /**   Sets the Vector for this UserSecurityObjectMapping.
         * @param Vector setValue
         */
          public void setUserSecurityObjectMapping(Vector<UserSecurityObjectMapping> setValue){
              userSecurityObjectMappingList=setValue;
        }
    /** Returns a Vector for UserSecurityObjectMapping.
      * @return Vector
         */
        public Vector<UserSecurityObjectMapping> getUserSecurityObjectMapping(){
            return this.userSecurityObjectMappingList;
        }

    /**   Sets the Vector for this SecurityObject.
         * @param Vector setValue
         */
          public void setSecurityObject(Vector<SecurityObject> setValue){
            this.securityObjectList=setValue;
        }

          /** Returns a Vector for SecurityObject.
           * @return Vector
         */
        public Vector<SecurityObject> getSecurityObject(){
            return this.securityObjectList;
        }

        /**   Sets the Vector for this User.
         * @param Vector setValue
         */
          public void setUser(Vector<User> setValue){
            this.userList=setValue;
        }
          /** Returns a Vector for User.
           * @return Vector
         */
        public Vector<User> getUser(){
            return this.userList;
        }
        /**   Sets the Vector for this UserAddress.
         * @param Vector setValue
         */
          public void setUserAddress(Vector<UserAddress> setValue){
              userAddressList=setValue;
        }
    /** Returns a Vector for UserAddress.
      * @return Vector
         */
        public Vector<UserAddress> getUserAddress(){
            return this.userAddressList;
        }
        /**   Sets the Vector for this UserEmail.
         * @param Vector setValue
         */
          public void setUserEmail(Vector<UserEmail> setValue){
              userEmailList=setValue;
        }
    /** Returns a Vector for UserEmail.
      * @return Vector
         */
        public Vector<UserEmail> getUserEmail(){
            return this.userEmailList;
        }
        /**   Sets the Vector for this WebServiceMapping.
         * @param Vector setValue
         */
          public void setWebServiceMapping(Vector<WebServiceMapping> setValue){
              webServiceMappingList=setValue;
        }
    /** Returns a Vector for WebServiceMapping.
      * @return Vector
         */
        public Vector<WebServiceMapping> getWebServiceMapping(){
            return this.webServiceMappingList;
        }
        /**   Sets the Vector for this UserGroup.
         * @param Vector setValue
         */
          public void setUserGroup(Vector<UserGroup> setValue){
              userGroupList=setValue;
        }
    /** Returns a Vector for UserGroup.
      * @return Vector
         */
        public Vector<UserGroup> getUserGroup(){
            return this.userGroupList;
        }
}
