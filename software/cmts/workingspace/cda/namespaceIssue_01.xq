declare default element namespace "urn:hl7-org:v2xml";
declare variable $docName as xs:string external;
document
{
   element ClinicalDocument
   {
      attribute classCode{"DOCCLIN"},
      attribute moodCode{"EVN"},
      element typeId
      {
         attribute root{"2.16.840.1.113883.1.3"},
         attribute extension{string("A123")},
         ""
      },
      element id
      {
         attribute root{string("9988")},
         attribute extension{data(doc($docName)/ADT_A03/MSH/MSH.10)},
         ""
      },
      element code
      {
         attribute code{data(doc($docName)/ADT_A03/MSH/MSH.11/PT.1)},
         ""
      },
      element title
      {
         attribute representation{"TXT"},
         attribute mediaType{"text/plain"},
         string("Discharge Sumary")
      },
      element effectiveTime
      {
         attribute value{data(doc($docName)/ADT_A03/MSH/MSH.7/TS.1)},
         ""
      },
      element confidentialityCode
      {
         attribute code{data(doc($docName)/ADT_A03/MSH/MSH.11/PT.2)},
         ""
      },
      element languageCode
      {
         attribute code{data(doc($docName)/ADT_A03/MSH/MSH.19/CE.2)},
         ""
      },
      element recordTarget
      {
         attribute typeCode{"RCT"},
         attribute contextControlCode{"OP"},
         element patientRole
         {
            attribute classCode{"PAT"},
            element id
            {
               attribute extension{data(doc($docName)/ADT_A03/PID/PID.19)},
               attribute assigningAuthorityName{data(doc($docName)/ADT_A03/PID/PID.19/@LongName)},
               ""
            },
            element addr
            {
               for $item_temp1 in doc($docName)/ADT_A03/PID/PID.11/XAD.1/SAD.1 return
               element state
               {
                  attribute partType{"STA"},
                  attribute representation{"TXT"},
                  attribute mediaType{"text/plain"},
                  $item_temp1/text()
               },
               ""
            },
            element telecom
            {
               attribute use{string("H")},
               attribute value{concat(string("tel:"),doc($docName)/ADT_A03/PID/PID.13/XTN.1)},
               ""
            },
            for $item_temp1 in doc($docName)/ADT_A03/PID return
            element patient
            {
               attribute classCode{"PSN"},
               attribute determinerCode{"INSTANCE"},
               element typeId
               {
                  attribute root{"2.16.840.1.113883.1.3"},
                  attribute extension{data($item_temp1/PID.1)},
                  ""
               },
               element id
               {
                  attribute extension{data($item_temp1/PID.3/CX.1)},
                  ""
               },
               element name
               {
                  attribute use{string("L")},
                  for $item_temp2 in $item_temp1/PID.5/XPN.1/FN.1 return
                  element family
                  {
                     attribute partType{"FAM"},
                     attribute representation{"TXT"},
                     attribute mediaType{"text/plain"},
                     $item_temp2/text()
                  },
                  for $item_temp2 in $item_temp1/PID.5/XPN.2 return
                  element given
                  {
                     attribute partType{"GIV"},
                     attribute representation{"TXT"},
                     attribute mediaType{"text/plain"},
                     $item_temp2/text()
                  },
                  ""
               },
               element administrativeGenderCode
               {
                  attribute code{data($item_temp1/PID.8)},
                  ""
               },
               element birthTime
               {
                  attribute value{data($item_temp1/PID.7/TS.1)},
                  ""
               },
               element maritalStatusCode
               {
                  attribute code{data($item_temp1/PID.16/CE.1)},
                  attribute codeSystem{data($item_temp1/PID.16/CE.3)},
                  attribute displayName{data($item_temp1/PID.16/CE.2)},
                  ""
               },
               element religiousAffiliationCode
               {
                  attribute code{data($item_temp1/PID.17/CE.1)},
                  attribute codeSystem{data($item_temp1/PID.17/CE.3)},
                  attribute displayName{data($item_temp1/PID.17/CE.2)},
                  ""
               },
               $item_temp1/text()
            },
            ""
         },
         ""
      },
      element author
      {
         attribute typeCode{"AUT"},
         attribute contextControlCode{"OP"},
         element time
         {
            attribute value{data(doc($docName)/ADT_A03/MSH/MSH.7/TS.1)},
            ""
         },
         element assignedAuthor
         {
            attribute classCode{"ASSIGNED"},
            element id
            {
               attribute extension{data(doc($docName)/ADT_A03/MSH/MSH.4/HD.1)},
               attribute assigningAuthorityName{data(doc($docName)/ADT_A03/MSH/MSH.4/HD.2)},
               ""
            },
            ""
         },
         ""
      },
      element componentOf
      {
         attribute typeCode{"COMP"},
         for $item_temp1 in doc($docName)/ADT_A03/PV1 return
         element encompassingEncounter
         {
            attribute classCode{"ENC"},
            attribute moodCode{"EVN"},
            element id
            {
               attribute extension{data($item_temp1/PV1.1)},
               ""
            },
            element code
            {
               attribute code{data($item_temp1/PV1.2)},
               ""
            },
            element effectiveTime
            {
               attribute operator{"I"},
               attribute value{data($item_temp1/PV1.44/TS.1)},
               ""
            },
            element dischargeDispositionCode
            {
               attribute code{data($item_temp1/PV1.36)},
               ""
            },
            element responsibleParty
            {
               attribute typeCode{"RESP"},
               element assignedEntity
               {
                  attribute classCode{"ASSIGNED"},
                  element id
                  {
                     attribute extension{data($item_temp1/PV1.7/XCN.1)},
                     attribute assigningAuthorityName{data($item_temp1/PV1.7/XCN.14/HD.2)},
                     ""
                  },
                  element code
                  {
                     attribute code{data($item_temp1/PV1.10)},
                     ""
                  },
                  element assignedPerson
                  {
                     attribute classCode{"PSN"},
                     attribute determinerCode{"INSTANCE"},
                     element name
                     {
                        for $item_temp2 in $item_temp1/PV1.7/XCN.2/FN.1 return
                        element family
                        {
                           attribute partType{"FAM"},
                           attribute representation{"TXT"},
                           attribute mediaType{"text/plain"},
                           $item_temp2/text()
                        },
                        ""
                     },
                     ""
                  },
                  ""
               },
               ""
            },
            element location
            {
               attribute typeCode{"LOC"},
               element healthCareFacility
               {
                  attribute classCode{"SDLOC"},
                  element id
                  {
                     attribute extension{data($item_temp1/PV1.3/PL.3)},
                     ""
                  },
                  element id
                  {
                     attribute extension{data($item_temp1/PV1.3/PL.2)},
                     ""
                  },
                  element id
                  {
                     attribute extension{data($item_temp1/PV1.3/PL.2)},
                     ""
                  },
                  ""
               },
               ""
            },
            $item_temp1/text()
         },
         ""
      },
      element component
      {
         attribute typeCode{"COMP"},
         attribute contextConductionInd{"true"},
         element typeId
         {
            attribute root{"2.16.840.1.113883.1.3"},
            attribute extension{string("COMP")},
            ""
         },
         element structuredBody
         {
            attribute classCode{"DOCBODY"},
            attribute moodCode{"EVN"},
            element component
            {
               attribute typeCode{"COMP"},
               attribute contextConductionInd{"true"},
               element typeId
               {
                  attribute root{"2.16.840.1.113883.1.3"},
                  attribute extension{string("PRCS")},
                  ""
               },
               element section
               {
                  attribute classCode{"DOCSECT"},
                  attribute moodCode{"EVN"},
                  element title
                  {
                     attribute representation{"TXT"},
                     attribute mediaType{"text/plain"},
                     string("CDA Record")
                  },
                  for $item_temp1 in doc($docName)/ADT_A03/ADT_A03.PROCEDURE/PR1 return
                  element entry
                  {
                     attribute typeCode{"COMP"},
                     attribute contextConductionInd{"true"},
                     element procedure
                     {
                        attribute classCode{string("ACCM")},
                        attribute moodCode{string("APT")},
                        element id
                        {
                           attribute root{string("2.56.34.2345.5434.444")},
                           attribute extension{data($item_temp1/PR1.1)},
                           ""
                        },
                        element code
                        {
                           attribute code{data($item_temp1/PR1.3/CE.1)},
                           attribute codeSystem{data($item_temp1/PR1.3/CE.3)},
                           attribute displayName{data($item_temp1/PR1.3/CE.2)},
                           ""
                        },
                        element effectiveTime
                        {
                           attribute operator{"I"},
                           attribute value{data($item_temp1/PR1.5/TS.1)},
                           ""
                        },
                        ""
                     },
                     $item_temp1/text()
                  },
                  ""
               },
               ""
            },
            element component
            {
               attribute typeCode{"COMP"},
               attribute contextConductionInd{"true"},
               element typeId
               {
                  attribute root{"2.16.840.1.113883.1.3"},
                  attribute extension{string("OBSR")},
                  ""
               },
               element section
               {
                  attribute classCode{"DOCSECT"},
                  attribute moodCode{"EVN"},
                  element title
                  {
                     attribute representation{"TXT"},
                     attribute mediaType{"text/plain"},
                     string("Observation results")
                  },
                  for $item_temp1 in doc($docName)/ADT_A03/OBX return
                  element entry
                  {
                     attribute typeCode{"COMP"},
                     attribute contextConductionInd{"true"},
                     element observation
                     {
                        attribute classCode{string("ALRT")},
                        attribute moodCode{string("DEF")},
                        element id
                        {
                           attribute root{string("2.56.34.2345.5434.555")},
                           attribute extension{data($item_temp1/OBX.1)},
                           ""
                        },
                        element code
                        {
                           attribute code{data($item_temp1/OBX.3/CE.1)},
                           attribute displayName{data($item_temp1/OBX.3/CE.2)},
                           ""
                        },
                        for $item_temp2 in $item_temp1/OBX.4 return
                        element derivationExpr
                        {
                           attribute representation{"TXT"},
                           attribute mediaType{"text/plain"},
                           $item_temp2/text()
                        },
                        for $item_temp2 in $item_temp1/OBX.5 return
                        element text
                        {
                           attribute mediaType{"text/plain"},
                           attribute integrityCheckAlgorithm{"SHA-1"},
                           attribute representation{"TXT"},
                           $item_temp2/text()
                        },
                        element statusCode
                        {
                           attribute code{string("completed")},
                           ""
                        },
                        element effectiveTime
                        {
                           attribute operator{"I"},
                           attribute value{data($item_temp1/OBX.14/TS.1)},
                           ""
                        },
                        ""
                     },
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
   }
   
}

