select count(*) from dealer d
join site ds on d.DEALER_MAIN_SITE_ID = ds.siteId
where domain is not null
and datasource = 'GooglePlacesAPI'
and not exists (select domain from Site s where s.domain = ds.domain and s.datasource = 'CapDB')
order by domain