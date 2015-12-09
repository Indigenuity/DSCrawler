select * from dealer dx
join capentry cex on dx.capdb = cex.lead_no
where concat(address, ', ', city, ', ', state, ' ', zip, ', United States') in 
(select dealerAddress from 
(select dealerId, formattedAddress as dealerAddress from dealer d
join placesdealer pd on d.placesId = pd.placesId
union all
select dealerId, concat(address, ', ', city, ', ', state, ' ', zip, ', United States') as dealerAddress from dealer d2
join capentry ce on d2.capdb = ce.lead_no) theUnion
group by dealerAddress
having count(*)  > 1)
