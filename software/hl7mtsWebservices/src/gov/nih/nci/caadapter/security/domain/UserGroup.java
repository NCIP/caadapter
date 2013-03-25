/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.domain;
/**
 * The UserGroup object define the UserGroups  for security.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-10-24 20:59:25 $
 *  * @created 24-Oct-2008 
 */
public class UserGroup
{


    private String description;
    private String groupName;
    private int groupId;
    private UserDateStamp userDateStamp;
    
    public UserGroup()
    {}
/**   Sets the value for variable description.
     * @param String setValue
     */
   public void setDescription(String setValue){
        this.description = setValue;
    }
   /**   gets the value for variable description.
    * @return String description
    */
   public String getDescription(){
       return this.description;
   }
   
   /**   Sets the value for variable groupName.
    * @param String setValue
    */
   public void setGroupName(String setValue){
        this.groupName = setValue;
   }
   /**   Gets the value for variable groupName.
    * @return String groupName
    */
   public String getGroupName(){
       return this.groupName;
  }
   
   /**   Sets the value for variable groupId.
    * @param int setValue
    */
   public void setGroupId(int setValue){
       this.groupId = setValue;
   }

   /** Returns the int for the variable groupId.
    * @return String
       */
      public int getGroupId(){
          return this.groupId;
      }
      /**   Sets the value for variable groupId.
       * @param int setValue
       */
      public void setUserDateStamp(UserDateStamp setValue){
          this.userDateStamp = setValue;
      }

      /** Returns the int for the variable userDateStamp.
       * @return UserDateStamp
          */
         public UserDateStamp getUserDateStamp(){
             return this.userDateStamp;
         }

}
