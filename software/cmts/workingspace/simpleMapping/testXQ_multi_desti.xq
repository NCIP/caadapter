declare variable $docName as xs:string external;
document
{
   element printorder
   {
      attribute orderid{data(doc($docName)/shiporder/@orderid)},
      attribute printType{"text/html"},
      for $item_temp1 in doc($docName)/shiporder/orderperson return
      element buyer
      {
         $item_temp1/text()
      },
      element item
      {
         for $item_temp1 in doc($docName)/shiporder/item/title return
         element name
         {
            $item_temp1/text()
         },
         for $item_temp1 in doc($docName)/shiporder/item/note return
         element description
         {
            $item_temp1/text()
         },
         for $item_temp1 in doc($docName)/shiporder/item/quantity return
         element quantity
         {
            $item_temp1/text()
         },
         element price
         {
            number(doc($docName)/shiporder/item/price)* number(string("1000"))
         },
         ""
      },
      for $item_temp1 in doc($docName)/shiporder/shipto return
      element addressee
      {
         element name
         {
            for $item_temp2 in $item_temp1/name/firstname return
            element given_name
            {
               attribute PUBLICID{"2179589"},
               attribute VERSION{"2.0"},
               $item_temp2/text()
            },
            for $item_temp2 in $item_temp1/name/lastname return
            element family_name
            {
               attribute PUBLICID{"2179591"},
               attribute VERSION{"2.0"},
               $item_temp2/text()
            },
            ""
         },
         for $item_temp2 in $item_temp1/address return
         element street
         {
            $item_temp2/text()
         },
         for $item_temp2 in $item_temp1/city return
         element city
         {
            $item_temp2/text()
         },
         for $item_temp2 in $item_temp1/country return
         element country
         {
            $item_temp2/text()
         },
         $item_temp1/text()
      },
      ""
   }
   
}

