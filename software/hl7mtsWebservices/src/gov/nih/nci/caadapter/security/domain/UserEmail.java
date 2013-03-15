/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.domain;
/**
 * The UserEmail object stores all the email addresses for a user in security.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-11-07 23:16:34 $
 *  * @created 24-Oct-2008 
 */

public class UserEmail
{

            private String emailAddress;
            private EmailType emailType;
            private boolean emailStatus;
            private String userId;
            private UserDateStamp userDateStamp;
            private int userEmailId;
            
            public UserEmail()
            {}
          
           /**   Sets the value for variable emailAddress.
            * @param String setValue
            */
           public void setEmailAddress(String setValue){
                this.emailAddress = setValue;
           }
           /**   Gets the value for variable emailAddress.
            * @return String emailAddress
            */
           public String getEmailAddress(){
               return this.emailAddress;
          }
           /**   Sets the value for variable emailType.
            * @param String setValue
            */
           public void setEmailType(EmailType setValue){
                this.emailType = setValue;
           }
           /**   Gets the value for variable emailType.
            * @return String emailType
            */
           public EmailType getEmailType(){
               return this.emailType;
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
           /**   Sets the value for variable emailStatus.
            * @param String setValue
            */
           public void setEmailStatus(boolean setValue){
                this.emailStatus = setValue;
           }
           /**   Gets the value for variable emailStatus.
            * @return boolean emailStatus
            */
           public boolean getEmailStatus(){
               return this.emailStatus;
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
           /**   Sets the value for variable userEmailId.
            * @param int setValue
            */
           public void setUseremailid(int setValue){
               this.userEmailId = setValue;
           }

           /** Returns the int for the variable userEmailId.
            * @return int userEmailId
               */
              public int getUseremailid(){
                  return this.userEmailId;
              }


}
