select * from ds.placesdealer pd
inner join (select website from ds.placesdealer
group by website
having count(*) > 1) dup on pd.website = dup.website