package spring.authentication.product;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final  ProductRepository productRepo;

    @GetMapping("/all")
    public List<Product> findAll() throws Exception{
        return productRepo.findAll();
    }

    @PostMapping("/addProcuts")
    public void addProductsList(@RequestBody List<Product> productList) throws Exception{
         productRepo.saveAll(productList);
    }
}
