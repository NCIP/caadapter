package gov.nih.nci.caadapter.security.domain;

/**
 * The UserDateStamp object define the update/create time stamp and user info for security xmls.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-10-24 20:59:25 $
 *  * @created 24-Oct-2008 
 */

import java.util.Date;

public class UserDateStamp
{

        private Date createDate;
        private String createdBy;
        private Date updateDate;
        private String updatedBy;
        
        
        public UserDateStamp()
        {}
    /**   Sets the value for variable createDate.
         * @param Date setValue
         */
       public void setCreateDate(Date setValue){
            this.createDate = setValue;
        }
       /**   gets the value for variable createDate.
        * @return Date createDate
        */
       public Date getCreateDate(){
           return this.createDate ;
       }
       /**   Sets the value for variable createdBy.
        * @param String setValue
        */
       public void setCreatedBy(String setValue){
            this.createdBy = setValue;
       }
       /**   Gets the value for variable createdBy.
        * @return String createdBy
        */
       public String getCreatedBy(){
           return this.createdBy;
      }
       
       /**   Sets the value for variable updateDate.
        * @param int setValue
        */
       public void setUpdateDate(Date setValue){
           this.updateDate = setValue;
       }

       /** Returns the int for the variable emailTypeId.
        * @return String
           */
          public Date getUpdateDate(){
              return this.updateDate;
          }
          /**   Sets the value for variable updateBy.
           * @param String setValue
           */
          public void setUpdateBy(String setValue){
               this.updatedBy = setValue;
          }
          /**   Gets the value for variable updateBy.
           * @return String updateBy
           */
          public String getUpdateBy(){
              return this.updatedBy;
         }          
}
