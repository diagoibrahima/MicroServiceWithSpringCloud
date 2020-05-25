package org.sid.billingservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

//Création de la classe Bill
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
class Bill{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Date billingDate;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // rend invisible ce champ dans l'affiche du json
	private Long customerID;
	@Transient // pour dire que l'attribut costumer ne sera pas enregistrer dans la base il servira juste pour lire des infos sur le customer
	private Customer customer;
	@OneToMany(mappedBy = "bill")
	private Collection<ProductItem> productItems;
}

//Création de l'interface BillRepository
@RepositoryRestResource
interface BillRepository extends JpaRepository<Bill, Long>{
}

//Creation de la projection fullBill pour afficher les infos du customer et des deatils du product
@Projection(name = "fullBill", types = Bill.class)
interface BillProjection{
	public Long getId();
	public Date getBillingDate();
	public Long getCustomerID();
	public Collection<ProductItem> getProductItems();
}

// Creation de l'entité ProductItem
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
class ProductItem{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // rend invisible ce champ dans l'affiche du json
	private Long productID;
	@Transient // pour dire que l'attribut costumer ne sera pas enregistrer dans la base il servira juste pour lire des infos sur le customer
	private Product product;
	private double price ;
	private double quantity;
	@ManyToOne
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // rend invisible ce champ dans l'affiche du json
	private Bill bill;
}

//Creation de l'interface ProductItemRepository
@RepositoryRestResource
interface ProductItemRepository extends JpaRepository<ProductItem, Long>{

}

//Creation de la classe Customer qui est non persistante car elle se trouve reelement dans un autre srvice (CUSTOMER-SERVICE)
@Data
class Customer{
	private Long id; private String name; private String email;
}

// Interogation du service CUSTOMER-SERVICE a traver l'annotation feign sur l'interface de service
@FeignClient(name = "CUSTOMER-SERVICE")
interface CustomerService{
	@GetMapping("/customers/{id}")
	public Customer findCustomerById(@PathVariable(name = "id") Long id);
}

//Creation de la classe Product qui est non persistante car elle se trouve reelement dans un autre srvice (INVENTORY-SERVICE)
@Data
class Product{
	private Long id; private String name; private double price;
}
// Interogation du service INVENTORY-SERVICE a traver l'annotation feign sur l'interface de service
@FeignClient(name = "INVENTORY-SERVICE")
interface InventoryService{
	@GetMapping("/products/{id}")
	public Product findProductById(@PathVariable(name = "id") Long id);

	//PageModel est un element de la bibliotheque hateos il permet de deserialiser le json
	@GetMapping("/products")
	public PagedModel<Product> findAllProducts();
}

@SpringBootApplication
@EnableFeignClients //Activation de feign
public class BillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner start(
			BillRepository billRepository, ProductItemRepository productItemRepository, CustomerService customerService,
			InventoryService inventoryService){
		return args -> {

			Customer c1 = customerService.findCustomerById(1L);
			System.out.printf("\n*****************");
			System.out.printf("\nID = " +c1.getId());
			System.out.printf("\n = " +c1.getName());
			System.out.printf("\nEmail = " +c1.getEmail());
			System.out.printf("\n*****************");

			Bill bill1 = billRepository.save(new Bill(null, new Date(), c1.getId(), null, null));

			PagedModel<Product> products = inventoryService.findAllProducts();
			products.getContent().forEach(p -> {
				productItemRepository.save(new ProductItem(null, p.getId(), null, p.getPrice(), 30, bill1));
			});

		};
	}

}

@RestController
class BillRestController{
	@Autowired
	private  BillRepository billRepository;
	@Autowired
	private ProductItemRepository productItemRepository;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private InventoryService inventoryService;

	@GetMapping("fullBill/{id}")
	public Bill getBill(@PathVariable(name = "id") Long id){
		Bill bill = billRepository.findById(id).get();
		bill.setCustomer(customerService.findCustomerById(bill.getCustomerID()));
		bill.getProductItems().forEach(pi->{
			pi.setProduct(inventoryService.findProductById(pi.getProductID()));
		});
		return bill;

	}

}
