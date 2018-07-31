package pl.gregorius.onlineshop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class OnlineShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineShopApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(ProductRepository productRepository, CategoryRepository categoryRepository) {
        return args -> {

            Category cars = new Category("Cars");
            categoryRepository.save(cars);


            Product audi = new Product("Audi", cars);
            Product merc = new Product("Mercedes Benz", cars);
            Product fiat1 = new Product("Fiat", cars);
            Product fiat2 = new Product("Fiat", cars);
            productRepository.save(audi);
            productRepository.save(merc);
            productRepository.save(fiat1);
            productRepository.save(fiat2);
        };
    }
}

@EnableWebSecurity
class SecurityManagement extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest()
                .permitAll();
    }
}


@Entity
@Data
class Product {
    @Id
    @GeneratedValue
    Long id;

    String name;

    @ManyToOne
    Category category;

    private Product() {
    }

    public Product(String name) {
        this.name = name;
    }

    public Product(String name, Category category) {
        this.name = name;
        this.category = category;
    }
}


@Entity
@Data
class Category {
    @Id
    @GeneratedValue
    Long id;

    String name;

    @OneToMany(mappedBy = "category")
    List<Product> products;

    private Category() {
    }

    public Category(String name, List<Product> products) {
        this.name = name;
        this.products = products;
    }

    public Category(String name) {
        this.name = name;
    }
}

interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findDistinctByNameContains(String name);
}

interface CategoryRepository extends CrudRepository<Category, Long> {
}

@RestController
class ProductRest {

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/product")
    public List<Product> getProducts(@RequestParam(name = "q") String name) {
        return productRepository.findDistinctByNameContains(name);
    }

    @GetMapping("/product/{id}")
    public Optional<Product> getProduct(@PathVariable(name = "id") Long id) {
        return productRepository.findById(id);
    }

}
