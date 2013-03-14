/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.domain;

/**
 * The AddressType object define the types of addresses for a user to access the data from security xmls.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-10-24 20:59:26 $
 *  * @created 24-Oct-2008 
 */

public class AddressType
{
    private String addressType;
    private String addressTypeName;
    private int addressTypeId;
    
    public AddressType()
    {}
/**   Sets the value for variable addressType.
     * @param String setValue
     */
   public void setAddressType(String setValue){
        this.addressType = setValue;
    }
   /**   gets the value for variable addressType.
    * @return String addressType
    */
   public String getAddressType(){
       return this.addressType ;
   }
   /**   Sets the value for variable addressTypeName.
    * @param String setValue
    */
   public void setAddressTypeName(String setValue){
        this.addressTypeName = setValue;
   }
   /**   Gets the value for variable addressTypeName.
    * @return String addressTypeName
    */
   public String getAddressTypeName(){
       return this.addressTypeName;
  }
   
   /**   Sets the value for variable addressTypeId.
    * @param int setValue
    */
   public void setAddressTypeId(int setValue){
       this.addressTypeId = setValue;
   }

   /** Returns the int for the variable addressTypeId.
    * @return String
       */
      public int getAddressTypeId(){
          return this.addressTypeId;
      }
}