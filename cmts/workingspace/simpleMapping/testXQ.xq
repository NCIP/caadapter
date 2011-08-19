declare variable $docName as xs:string external;
document
{
   element printorder
   {
      attribute orderid{concat(doc($docName)/shiporder/@orderid,string("localPrintType"))},
      attribute printType{"text/html"},
      element buyer
      {
         concat(doc($docName)/shiporder/orderperson,current-date())
      },
      element address
      {
         for $item_temp1 in doc($docName)/shiporder/shipto/name return
         element name
         {
            attribute PUBLICID{"2222323"},
            attribute VERSION{"1.0"},
            $item_temp1/text()
         },
         for $item_temp1 in doc($docName)/shiporder/shipto/address return
         element street
         {
            $item_temp1/text()
         },
         for $item_temp1 in doc($docName)/shiporder/shipto/city return
         element city
         {
            $item_temp1/text()
         },
         for $item_temp1 in doc($docName)/shiporder/shipto/country return
         element country
         {
            $item_temp1/text()
         },
         ""
      },
      for $item_temp1 in doc($docName)/shiporder/item return
      element item
      {
         element name
         {
            current-dateTime()
         },
         element description
         {
            concat($item_temp1/title,$item_temp1/note)
         },
         for $item_temp2 in $item_temp1/quantity return
         element quantity
         {
            $item_temp2/text()
         },
         element price
         {
            day-from-date(xs:date(string("2010-01-25"))) - day-from-date(xs:date(string("2010-01-01")))
         },
         $item_temp1/text()
      },
      ""
   }
   
}

