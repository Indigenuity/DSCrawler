SELECT BasicDealerId,
	bd.name as "Account Name", 
    s.homepage as "Website", 
    pz.indyPod,
    pz.franchisePod,
    pz.primaryAreaCode as "Primary Area Code",
    bd.street as 'Dealership Street',
    bd.city as 'Dealership City',
    bd.stdState as 'Dealership State/Province',
    bd.stdPostal as 'Dealership Zip/Postal Code',
    bd.stdCountry as 'Dealership Country',
    bd.stdPhone as 'Phone', 
    salesforcematchstring
FROM ds_new.basicdealer bd
left join site s on bd.site_siteid = s.siteid
left join podzip pz on pz.postalcode = bd.stdpostal
where projectidentifier like '%finished%'
order by salesforcematchstring desc, bd.name