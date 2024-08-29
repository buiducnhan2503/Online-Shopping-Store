package buiducnhan.hutech.Online_Shopping_Store.repository;

import buiducnhan.hutech.Online_Shopping_Store.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    default List<Product> findAllProducts(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Product> pagedResult = findAll(pageable);
        return pagedResult.getContent();
    }

    @Query(""" 
            SELECT p FROM Product p
            WHERE p.name LIKE %?1%
            OR p.description LIKE %?1%
            OR p.category.name LIKE %?1%
            """)
    List<Product> searchProduct(String keyword);
}
