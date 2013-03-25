/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.domain;
/**
 * The SecurityObject object define all the objects for which security applies.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-10-24 20:59:24 $
 *  * @created 24-Oct-2008 
 */

public class SecurityObject
{
        private String description;
        private String objectName;
        private int objectId;
        private UserDateStamp userDateStamp;
        
        public SecurityObject()
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
           return this.description ;
       }
       /**   Sets the value for variable objectName.
        * @param String setValue
        */
       public void setObjectName(String setValue){
            this.objectName = setValue;
       }
       /**   Gets the value for variable objectName.
        * @return String objectName
        */
       public String getObjectName(){
           return this.objectName;
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
