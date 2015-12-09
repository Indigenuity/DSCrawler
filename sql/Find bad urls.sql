select given_url, INTERMEDIATE_URL from siteinformation
where INTERMEDIATE_URL not like '%index.htm'
    and INTERMEDIATE_URL not like '%index.html'
    and INTERMEDIATE_URL not like '%index.cfm'
    and INTERMEDIATE_URL not like '%home.aspx'
    and INTERMEDIATE_URL not like '%Default.aspx'
    and INTERMEDIATE_URL not like '%default.htm'
    and INTERMEDIATE_URL not like '%default.html'
	and INTERMEDIATE_URL not like '%.com'
    and INTERMEDIATE_URL not like '%.net'
    and INTERMEDIATE_URL not like '%.net/'
    and INTERMEDIATE_URL not like '%.com/'

