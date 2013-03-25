/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.domain;

/**
 * The UserDateStamp object define the update/create time stamp and user info for security xmls.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-11-07 23:16:34 $
 *  * @created 24-Oct-2008 
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserDateStamp 
{

        private String createDate;
        private String createdBy;
        private String updateDate;
        private String updatedBy;
        
        
        public UserDateStamp()
        {}
    /**   Sets the value for variable createDate.
         * @param Date setValue
         */
       public void setCreateDate(String setValue){
//           SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
//            try
//            {
//                this.createDate = formatter.parse((String)setValue);
//            } catch (ParseException e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            };
           this.createDate = setValue;
        }
       /**   gets the value for variable createDate.
        * @return Date createDate
        */
       public String getCreateDate(){
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
       public void setUpdatedate(String setValue){
//           SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy HH:mm");
//           try
//           {
//               this.updateDate = formatter.parse((String)setValue);
//           } catch (ParseException e)
//           {
//               // TODO Auto-generated catch block
//               e.printStackTrace();
//           };
           
           this.updateDate = setValue;
       }

       /** Returns the int for the variable emailTypeId.
        * @return String
           */
          public String getUpdateDate(){
              return this.updateDate;
          }
          /**   Sets the value for variable updateBy.
           * @param String setValue
           */
          public void setUpdatedBy(String setValue){
               this.updatedBy = setValue;
          }
          /**   Gets the value for variable updateBy.
           * @return String updateBy
           */
          public String getUpdatedBy(){
              return this.updatedBy;
         }          
}
