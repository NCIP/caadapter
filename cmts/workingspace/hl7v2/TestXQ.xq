declare default element namespace "urn:hl7-org:v2xml";
declare variable $docName as xs:string external;
document
{
   for $item_temp1 in doc($docName)/ADT_A03 return
   element PRPA_IN402003UV02
   {
      attribute ITSVersion{"XML_1.0"},
      for $item_temp2 in $item_temp1/MSH/MSH.3 return
      element templateId
      {
         attribute root{data($item_temp2/HD.1)},
         attribute extension{data($item_temp2/HD.2)},
         attribute assigningAuthorityName{data($item_temp2/HD.3)},
         $item_temp2/text()
      },
      for $item_temp2 in $item_temp1/MSH/MSH.4 return
      element id
      {
         attribute root{data($item_temp2/HD.1)},
         attribute extension{data($item_temp2/HD.2)},
         attribute assigningAuthorityName{data($item_temp2/HD.3)},
         $item_temp2/text()
      },
      for $item_temp2 in $item_temp1/MSH/MSH.7 return
      element creationTime
      {
         attribute value{data($item_temp2/TS.1)},
         attribute nullFlavor{data($item_temp2/TS.2)},
         $item_temp2/text()
      },
      for $item_temp2 in $item_temp1/MSH/MSH.2 return
      element versionCode
      {
         $item_temp2/text()
      },
      element interactionId
      {
         ""
      },
      for $item_temp2 in $item_temp1/MSH/MSH.5/HD.1 return
      element processingCode
      {
         $item_temp2/text()
      },
      for $item_temp2 in $item_temp1/MSH/MSH.5/HD.2 return
      element processingModeCode
      {
         $item_temp2/text()
      },
      element acceptAckCode
      {
         ""
      },
      element receiver
      {
         ""
      },
      for $item_temp2 in $item_temp1/MSH/MSH.6 return
      element sender
      {
         element typeId
         {
            attribute root{data($item_temp2/HD.1)},
            attribute extension{data($item_temp2/HD.2)},
            ""
         },
         element device
         {
            ""
         },
         $item_temp2/text()
      },
      for $item_temp2 in $item_temp1/MSH return
      element controlActProcess
      {
         for $item_temp3 in $item_temp2/MSH.9 return
         element typeId
         {
            attribute root{data($item_temp3/MSG.1)},
            attribute extension{data($item_temp3/MSG.2)},
            attribute assigningAuthorityName{data($item_temp3/MSG.3)},
            $item_temp3/text()
         },
         for $item_temp3 in $item_temp2/MSH.9 return
         element templateId
         {
            attribute root{data($item_temp3/MSG.1)},
            attribute extension{data($item_temp3/MSG.2)},
            attribute assigningAuthorityName{data($item_temp3/MSG.3)},
            $item_temp3/text()
         },
         for $item_temp3 in $item_temp2/MSH.10 return
         element id
         {
            $item_temp3/text()
         },
         for $item_temp3 in $item_temp2/MSH.12 return
         element code
         {
            attribute codeSystem{data($item_temp3/VID.1)},
            attribute codeSystemName{data($item_temp3/VID.2)},
            $item_temp3/text()
         },
         for $item_temp3 in $item_temp2/MSH.19 return
         element languageCode
         {
            attribute codeSystem{data($item_temp3/CE.1)},
            attribute codeSystemName{data($item_temp3/CE.2)},
            $item_temp3/text()
         },
         element subject
         {
            attribute contextConductionInd{"false"},
            element inpatientEncounterEvent
            {
               element id
               {
                  ""
               },
               element code
               {
                  ""
               },
               element statusCode
               {
                  ""
               },
               element effectiveTime
               {
                  attribute operator{"I"},
                  ""
               },
               for $item_temp3 in $item_temp2/../DG1 return
               element subject
               {
                  attribute contextControlCode{"OP"},
                  element patient
                  {
                     ""
                  },
                  $item_temp3/text()
               },
               ""
            },
            ""
         },
         $item_temp2/text()
      },
      $item_temp1/text()
   }
   
}

