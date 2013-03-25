/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.domain;
/**
 * The EmailType object define the types of email address for a user to access the data from security xmls.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-10-24 20:59:25 $
 *  * @created 24-Oct-2008 
 */
public class EmailType
{

        private String emailType;
        private String emailTypeName;
        private int emailTypeId;
        
        public EmailType()
        {}
    /**   Sets the value for variable emailType.
         * @param String setValue
         */
       public void setEmailType(String setValue){
            this.emailType = setValue;
        }
       /**   gets the value for variable emailType.
        * @return String emailType
        */
       public String getEmailType(){
           return this.emailType ;
       }
       /**   Sets the value for variable emailTypeName.
        * @param String setValue
        */
       public void setEmailTypeName(String setValue){
            this.emailTypeName = setValue;
       }
       /**   Gets the value for variable emailTypeName.
        * @return String emailTypeName
        */
       public String getEmailTypeName(){
           return this.emailTypeName;
      }
       
       /**   Sets the value for variable emailTypeId.
        * @param int setValue
        */
       public void setEmailTypeId(int setValue){
           this.emailTypeId = setValue;
       }

       /** Returns the int for the variable emailTypeId.
        * @return String
           */
          public int getEmailTypeId(){
              return this.emailTypeId;
          }
    }    

