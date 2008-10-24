package gov.nih.nci.caadapter.security.domain;
/**
 * The UserSecurityObjectMapping object ties User to the SecurityObjects for which security applies.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-10-24 20:59:25 $
 *  * @created 24-Oct-2008 
 */

public class UserSecurityObjectMapping
{
            private Permissions objectPermissions;
            private int objectId;
            private int securityMapId;
            private String userId;
            private UserDateStamp userDateStamp;
            
            public UserSecurityObjectMapping()
            {}
        /**   Sets the value for variable userId.
             * @param String setValue
             */
           public void setUserId(String setValue){
                this.userId = setValue;
            }
           /**   gets the value for variable userId.
            * @return String userId
            */
           public String getUserId(){
               return this.userId ;
           }
           /**   Sets the value for variable objectPermissions.
            * @param String setValue
            */
           public void setObjectPermissions(Permissions setValue){
                this.objectPermissions = setValue;
           }
           /**   Gets the value for variable objectPermissions.
            * @return String objectPermissions
            */
           public Permissions getObjectPermissions(){
               return this.objectPermissions;
          }
           /**   Sets the value for variable objectId.
            * @param int setValue
            */
           public void setObjectId(int setValue){
               this.objectId = setValue;
           }

           /** Returns the int for the variable objectId.
            * @return String objectId
               */
              public int getObjectId(){
                  return this.objectId;
              }          
           /**   Sets the value for variable securityMapId.
            * @param int setValue
            */
           public void setSecurityMapId(int setValue){
               this.securityMapId = setValue;
           }

           /** Returns the int for the variable securityMapId.
            * @return String securityMapId
               */
              public int getSecurityMapId(){
                  return this.securityMapId;
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
}
