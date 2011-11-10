declare variable $docName as xs:string external;
document
{
   element PORR_MT049006UV01.InvestigationEvent
   {
      element id
      {
         attribute root{data(doc($docName)/BAER/aer__profile_id_valueset)},
         attribute extension{data(doc($docName)/BAER/REPORT_INFORMATION/REPORT_IDENTIFYING_INFORMATION/aer__case_report_id_str)},
         attribute 
             assigningAuthorityName
             {
                 data(doc(replace('http://137.187.205.75:8080/caadapter-dvts/restful/context/SampleContext01/domain/maritalStatus/value/9999', '9999', doc($docName)/BAER/aer__sender))/VocabularyMappingData/MappingResults/Result)
             },
         ""
      },
      element id
      {
         attribute root{string("2.16.840.1.113883.3.965")},
         attribute extension{data(doc($docName)/BAER/REPORT_INFORMATION/REPORT_IDENTIFYING_INFORMATION/aer__icsr_number)},
         ""
      },
      element code
      {
         attribute code{data(doc($docName)/BAER/REPORT_INFORMATION/REPORT_IDENTIFYING_INFORMATION/aer__report_type_cd_code)},
         attribute codeSystem{data(doc($docName)/BAER/REPORT_INFORMATION/REPORT_IDENTIFYING_INFORMATION/aer__report_type_cd_valueset)},
         attribute displayName{data(doc($docName)/BAER/REPORT_INFORMATION/REPORT_IDENTIFYING_INFORMATION/aer__report_type_cd)},
         ""
      },
      element text
      {
         attribute mediaType{"text/plain"},
         concat(doc($docName)/BAER/REPORT_INFORMATION/client__ip,doc($docName)/BAER/REPORT_INFORMATION/client__agent)
      },
      for $item_temp1 in doc($docName)/BAER/aer__sender return
      element senderName
      {
         attribute PUBLICID{"2222323"},
         attribute VERSION{"1.0"},
         $item_temp1/text()
      },
      element statusCode
      {
         attribute code{string("New")},
         ""
      },
      element activityTime
      {
         attribute operator{"I"},
         attribute value{data(doc($docName)/BAER/REPORT_INFORMATION/REPORT_IDENTIFYING_INFORMATION/aer__aer_dt)},
         ""
      },
      element subject
      {
         element investigativeSubject
         {
            element subjectOf
            {
               attribute contextControlCode{"ON"},
                  element observation
                  {
                     ""
                  },
                  element substanceAdministration
                  {
                     element consumable
                     {
                        element administerableMaterial
                        {
                           element administerableMaterialKind
                           {
                              element code
                              {
                                 attribute code{data(doc($docName)/BAER/RELEVANT_PRODUCTS/PRODUCT_INFORMATION/ae_product__product_cd)},
                                 attribute displayName{data(doc($docName)/BAER/RELEVANT_PRODUCTS/PRODUCT_INFORMATION/ae_product__product_name)},
                                 for $item_temp1 in doc($docName)/BAER/RELEVANT_PRODUCTS/PRODUCT_INFORMATION/ae_product__product_name return
                                 element originalText
                                 {
                                    attribute mediaType{"text/plain"},
                                    attribute integrityCheckAlgorithm{"SHA-1"},
                                    attribute representation{"TXT"},
                                    $item_temp1/text()
                                 },
                                 ""
                              },
                              for $item_temp1 in doc($docName)/BAER/REPORT_INFORMATION/PRODUCT_ADULTERATION_AND_DISPOSITION/PRODUCT_ADULTERATION_AND_DISPOSITION_DETAILS/adulteration__animal_ae_description return
                              element desc
                              {
                                 attribute representation{"TXT"},
                                 attribute mediaType{"text/plain"},
                                 $item_temp1/text()
                              },
                              element materialPart
                              {
                                 element partMaterialKind
                                 {
                                    element code
                                    {
                                       attribute code{data(doc($docName)/BAER/REPORT_INFORMATION/PRODUCT_ADULTERATION_AND_DISPOSITION/PRODUCT_ADULTERATION_AND_DISPOSITION_DETAILS/adulteration__adulteration_cd_code)},
                                       attribute codeSystem{data(doc($docName)/BAER/REPORT_INFORMATION/PRODUCT_ADULTERATION_AND_DISPOSITION/PRODUCT_ADULTERATION_AND_DISPOSITION_DETAILS/adulteration__adulteration_cd_valueset)},
                                       attribute displayName{data(doc($docName)/BAER/REPORT_INFORMATION/PRODUCT_ADULTERATION_AND_DISPOSITION/PRODUCT_ADULTERATION_AND_DISPOSITION_DETAILS/adulteration__adulteration_cd)},
                                       ""
                                    },
                                    for $item_temp1 in doc($docName)/BAER/REPORT_INFORMATION/PRODUCT_ADULTERATION_AND_DISPOSITION/PRODUCT_ADULTERATION_AND_DISPOSITION_DETAILS/adulteration__adulteration_description return
                                    element desc
                                    {
                                       attribute representation{"TXT"},
                                       attribute mediaType{"text/plain"},
                                       $item_temp1/text()
                                    },
                                    ""
                                 },
                                 ""
                              },
                              ""
                           },
                           ""
                        },
                        ""
                     },
                     ""
                  },
                  element supply
                  {
                     ""
                  },
                  element procedure
                  {
                     ""
                  },
                  element encounter
                  {
                     ""
                  },
                  element act
                  {
                     ""
                  },
                  element organizer
                  {
                     ""
                  },
                  ""
               
               
            },
            ""
         },
         ""
      },
      ""
   }
   
}

