package buiducnhan.hutech.Online_Shopping_Store.repository;


import buiducnhan.hutech.Online_Shopping_Store.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
