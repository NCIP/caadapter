/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.security.domain;

/**
 * The WebServiceMapping object defines all the security applied to Webservice scenarios.
 * @author : Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-10-24 21:11:59 $
 *  * @created 24-Oct-2008 
 */

public class WebServiceMapping
{

            private String h3sFile;
            private String mappingFile;
            private boolean activeStatus;
            private String scenarioName;
            private String scsFile;
            private String userId;
            private UserDateStamp userDateStamp;
            private int scenarioId;
            
            public WebServiceMapping()
            {}
        /**   Sets the value for variable h3sFile.
             * @param String setValue
             */
           public void setH3sFile(String setValue){
                this.h3sFile = setValue;
            }
           /**   gets the value for variable h3sFile.
            * @return String h3sFile
            */
           public String getH3sFile(){
               return this.h3sFile ;
           }
           /**   Sets the value for variable mappingFile.
            * @param String setValue
            */
           public void setMappingFile(String setValue){
                this.mappingFile = setValue;
           }
           /**   Gets the value for variable mappingFile.
            * @return String mappingFile
            */
           public String getMappingFile(){
               return this.mappingFile;
          }
           
           /**   Sets the value for variable scenarioName.
            * @param String setValue
            */
           public void setScenarioName(String setValue){
                this.scenarioName = setValue;
           }
           /**   Gets the value for variable scenarioName.
            * @return String scenarioName
            */
           public String getScenarioName(){
               return this.scenarioName;
          }
           /**   Sets the value for variable scsFile.
            * @param String setValue
            */
           public void setScsFile(String setValue){
                this.scsFile = setValue;
           }
           /**   Gets the value for variable scsFile.
            * @return String scsFile
            */
           public String getScsFile(){
               return this.scsFile;
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
           /**   Sets the value for variable activeStatus.
            * @param String setValue
            */
           public void setActiveStatus(boolean setValue){
                this.activeStatus = setValue;
           }
           /**   Gets the value for variable activeStatus.
            * @return boolean activeStatus
            */
           public boolean getActiveStatus(){
               return this.activeStatus;
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
           /**   Sets the value for variable scenarioId.
            * @param int setValue
            */
           public void setScenarioId(int setValue){
               this.scenarioId = setValue;
           }

           /** Returns the int for the variable scenarioId.
            * @return int scenarioId
               */
              public int getScenarioId(){
                  return this.scenarioId;
              }

}
