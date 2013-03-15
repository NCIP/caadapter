/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.domain;
/**
 * The User object defines all the user in security.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-10-24 21:30:06 $
 *  * @created 24-Oct-2008 
 */

public class User
{
        private String firstName;
        private String lastName;
        private boolean active;
        private String middleName;
        private String homePhoneNumber;
        private String mobilePhoneNumber;
        private String workPhoneNumber;
        private String organization;
        private UserEmail userEmail;
        private String userId;
        private String password;
        private UserDateStamp userDateStamp;
        private int groupId;
        
        public User()
        {}
        /**   Sets the value for variable UserEmail.
         * @param String setValue
         */
       public void setUserEmail(UserEmail setValue){
            this.userEmail = setValue;
        }
       /**   gets the value for variable userEmail.
        * @return String userEmail
        */
       public UserEmail getUserEmail(){
           return this.userEmail ;
       }
       /**   Sets the value for variable firstName.
        * @param String setValue
        */
      public void setFirstName(String setValue){
           this.firstName = setValue;
       }
      /**   gets the value for variable firstName.
       * @return String firstName
       */
      public String getFirstName(){
          return this.firstName ;
      }
       
       /**   Sets the value for variable lastName.
        * @param String setValue
        */
       public void setLastName(String setValue){
            this.lastName = setValue;
       }
       /**   Gets the value for variable lastName.
        * @return String lastName
        */
       public String getLastName(){
           return this.lastName;
      }
       
       /**   Sets the value for variable middleName.
        * @param String setValue
        */
       public void setMiddleName(String setValue){
            this.middleName = setValue;
       }
       /**   Gets the value for variable middleName.
        * @return String middleName
        */
       public String getMiddleName(){
           return this.middleName;
      }
       /**   Sets the value for variable homePhoneNumber.
        * @param String setValue
        */
       public void setHomePhoneNumber(String setValue){
            this.homePhoneNumber = setValue;
       }
       /**   Gets the value for variable homePhoneNumber.
        * @return String homePhoneNumber
        */
       public String getHomePhoneNumber(){
           return this.homePhoneNumber;
      }
       /**   Sets the value for variable mobilePhoneNumber.
        * @param String setValue
        */
       public void setMobilePhoneNumber(String setValue){
            this.mobilePhoneNumber = setValue;
       }
       /**   Gets the value for variable mobilePhoneNumber.
        * @return String mobilePhoneNumber
        */
       public String getMobilePhoneNumber(){
           return this.mobilePhoneNumber;
      }
       /**   Sets the value for variable workPhoneNumber.
        * @param String setValue
        */
       public void setWorkPhoneNumber(String setValue){
            this.workPhoneNumber = setValue;
       }
       /**   Gets the value for variable workPhoneNumber.
        * @return String workPhoneNumber
        */
       public String getWorkPhoneNumber(){
           return this.workPhoneNumber;
      }
       /**   Sets the value for variable organization.
        * @param String setValue
        */
       public void setOrganization(String setValue){
            this.organization = setValue;
       }
       /**   Gets the value for variable organization.
        * @return String organization
        */
       public String getOrganization(){
           return this.organization;
      }
       /**   Sets the value for variable UserDateStamp.
        * @param String setValue
        */
       public void setUserDateStamp(UserDateStamp setValue){
            this.userDateStamp = setValue;
       }
       /**   Gets the value for variable UserDateStamp.
        * @return String UserDateStamp
        */
       public UserDateStamp getUserDateStamp(){
           return this.userDateStamp;
      }
       /**   Sets the value for variable active.
        * @param String setValue
        */
       public void setActive(boolean setValue){
            this.active = setValue;
       }
       /**   Gets the value for variable active.
        * @return boolean active
        */
       public boolean getActive(){
           return this.active;
      }
       /**   Sets the value for variable userId.
        * @param String setValue
        */
       public void setUserId(String setValue){
            this.userId = setValue;
       }
       /**   Gets the value for variable userId.
        * @return String userId
        */
       public String getUserId(){
           return this.userId;
      }   
       /**   Sets the value for variable password.
        * @param String setValue
        */
       public void setPassword(String setValue){
            this.password = setValue;
       }
       /**   Gets the value for variable password.
        * @return String password
        */
       public String getPassword(){
           return this.password;
      }       
       /**   Sets the value for variable groupId.
        * @param int setValue
        */
       public void setGroupId(int setValue){
           this.groupId = setValue;
       }

       /** Returns the int for the variable groupId.
        * @return int groupId
           */
          public int getGroupId(){
              return this.groupId;
          }

}
