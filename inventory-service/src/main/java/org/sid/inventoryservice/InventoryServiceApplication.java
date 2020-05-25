package org.sid.inventoryservice;

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
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//class product
//http://localhost:8082/h2-console to access to H2-database (jdbc url = jdbc:h2:mem:testdb, username= sa, password="")
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @ToString
class Product{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private double price;
}

@RepositoryRestResource
interface ProductRepository extends JpaRepository<Product, Long>{

}
@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(InventoryServiceApplication.class, args);
	}
	@Bean
	CommandLineRunner start(ProductRepository productRepository, RepositoryRestConfiguration repositoryRestConfiguration){
		return args ->{
			repositoryRestConfiguration.exposeIdsFor(Product.class);
			productRepository.save(new Product(null, "Ordi HP 878", 87000));
			productRepository.save(new Product(null, "Ordi Mac bokk", 120000));
			productRepository.save(new Product(null, "Imprimante Epson", 25000));
		};
	}

}
