select p1.* as id, 
  pubtypes.cv_name as pub_type, 
  rp.property_value as submission_database, 
  (case when ira.datasource_id=9 then 'computed' else 'curated' end) as assignment_method,
  (select p.is_largescale from nextprot.view_paper_scale p where p.publication_id=p1.resource_id) as is_largescale         
from nextprot.publications p1 
	 inner join nextprot.cv_publication_types as pubtypes on p1.cv_publication_type_id = pubtypes.cv_id 
	 left outer join nextprot.cv_journals as j on (p1.cv_journal_id = j.cv_id) 
	 left outer join nextprot.resource_properties rp on (p1.resource_id = rp.resource_id and rp.property_name = 'database')
	 inner join nextprot.identifier_resource_assoc ira on (p1.resource_id = ira.resource_id and ira.identifier_id=:identifierId)
order by extract (year from p1.publication_date) desc,  
    p1.cv_publication_type_id,  
    j.journal_name asc,  
    p1.volume asc,  
    p1.first_page asc  		    