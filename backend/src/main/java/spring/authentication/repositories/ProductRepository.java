package spring.authentication.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import spring.authentication.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
