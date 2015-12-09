select pd.website, c from placesdealer pd
join (SELECT website, COUNT(*) c FROM placesdealer 
GROUP BY website 
HAVING c > 1
order by c desc) dup on pd.website = dup.website
order by c desc