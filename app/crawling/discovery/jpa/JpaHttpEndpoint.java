package crawling.discovery.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import crawling.discovery.HttpEndpoint;

@Entity
public class JpaHttpEndpoint {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long jpaHttpEndpointId;

	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String urlString;
	
	public JpaHttpEndpoint(HttpEndpoint endpoint) {
		this.urlString = endpoint.getUrl().toString();
	}
	
	@SuppressWarnings("unused")
	private JpaHttpEndpoint() {}
}
