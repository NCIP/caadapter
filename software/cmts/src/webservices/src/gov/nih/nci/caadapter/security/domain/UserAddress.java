/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.domain;

/**
 * The UserAddress object define the all the addresses for a user in security xmls.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-10-24 20:59:25 $
 *  * @created 24-Oct-2008 
 */


public class UserAddress
{
    
    private String address1;
    private String address2;
    private boolean addressStatus;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String userId;
    private AddressType addressType;
    private UserDateStamp userDateStamp;
    private int userAddressId;
    
    public UserAddress()
    {}
/**   Sets the value for variable addressType.
     * @param String setValue
     */
   public void setAddressType(AddressType setValue){
        this.addressType = setValue;
    }
   /**   gets the value for variable addressType.
    * @return String addressType
    */
   public AddressType getAddressType(){
       return this.addressType ;
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
   /**   Sets the value for variable city.
    * @param String setValue
    */
   public void setCity(String setValue){
        this.city = setValue;
   }
   /**   Gets the value for variable city.
    * @return String city
    */
   public String getCity(){
       return this.city;
  }
   /**   Sets the value for variable country.
    * @param String setValue
    */
   public void setCountry(String setValue){
        this.country = setValue;
   }
   /**   Gets the value for variable country.
    * @return String country
    */
   public String getCountry(){
       return this.country;
  }
   /**   Sets the value for variable address1.
    * @param String setValue
    */
   public void setAddress1(String setValue){
        this.address1 = setValue;
   }
   /**   Gets the value for variable address1.
    * @return String address1
    */
   public String getAddress1(){
       return this.address1;
  }
   /**   Sets the value for variable address2.
    * @param String setValue
    */
   public void setAddress2(String setValue){
        this.address2 = setValue;
   }
   /**   Gets the value for variable address2.
    * @return String address2
    */
   public String getAddress2(){
       return this.address2;
  }
   /**   Sets the value for variable state.
    * @param String setValue
    */
   public void setState(String setValue){
        this.state = setValue;
   }
   /**   Gets the value for variable state.
    * @return String state
    */
   public String getState(){
       return this.state;
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
   /**   Sets the value for variable addressStatus.
    * @param String setValue
    */
   public void setAddressStatus(boolean setValue){
        this.addressStatus = setValue;
   }
   /**   Gets the value for variable addressStatus.
    * @return boolean addressStatus
    */
   public boolean getAddressStatus(){
       return this.addressStatus;
  }
   /**   Sets the value for variable zipCode.
    * @param String setValue
    */
   public void setZipCode(String setValue){
        this.zipCode = setValue;
   }
   /**   Gets the value for variable zipCode.
    * @return String zipCode
    */
   public String getZipCode(){
       return this.zipCode;
  }   
   /**   Sets the value for variable userAddressId.
    * @param int setValue
    */
   public void setUserAddressId(int setValue){
       this.userAddressId = setValue;
   }

   /** Returns the int for the variable UserAddressId.
    * @return String
       */
      public int getUserAddressId(){
          return this.userAddressId;
      }

}
