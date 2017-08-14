SELECT 
	pd.name as "Account Name", 
    s.homepage as "Website", 
    pz.indyPod,
    pz.franchisePod,
    pd.street as 'Dealership Street',
    pd.city as 'Dealership City',
    pd.stdProvince as 'Dealership State/Province',
    pd.stdPostal as 'Dealership Zip/Postal Code',
    pd.stdCountry as 'Dealership Country',
    pd.stdPhone as 'Phone', 
    recordType,
    formattedaddress as "Google's formatted address",
    salesforcematchstring, 
    concat(pd.street, ", ", pd.city, ", ", pd.province, " ", pd.postal, ", ", pd.country) as "Dealership Location",
    placesdealerid
FROM ds_new.placesdealer pd
join site s on s.siteId = pd.site_siteId
left join podzip pz on pz.postalcode = pd.stdpostal
where s.defunctDomain = false 
and s.defunctPath = false 
and s.badUrlStructure = false
and s.httperror = false
and (pd.country = "United States" or pd.country = "Canada")
and salesforcematchstring is null
order by recordtype