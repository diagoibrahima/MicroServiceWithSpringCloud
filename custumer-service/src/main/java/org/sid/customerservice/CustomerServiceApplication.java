package org.sid.customerservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// implementation de la classe customer
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @ToString
class Customer{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String email;
}

//interface de la classe customer
@RepositoryRestResource
interface CustomerRepository extends JpaRepository<Customer, Long>{
}

// Une projection qui permer de n'afficher que ces deux attributx specifier // http://localhost:8081/customers?projection=p1
@Projection(name = "p1", types = Customer.class)
interface CustomerProjection{
	public Long getId();
	public String getName();
}
@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner start(CustomerRepository customerRepository, RepositoryRestConfiguration repositoryRestConfiguration){
		return args ->{
			repositoryRestConfiguration.exposeIdsFor(Customer.class);
			customerRepository.save(new Customer(null, "ENSET", "enset@gmail.com"));
			customerRepository.save(new Customer(null, "IBM", "ibm@gmail.com"));
			customerRepository.save(new Customer(null, "HP", "hp@hp.com"));
			customerRepository.findAll().forEach(System.out::println);
		};
	}

}
