package backAgil.example.back.repositories;

import backAgil.example.back.models.OrderDetail;
import org.hibernate.query.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT o FROM OrderDetail o LEFT JOIN FETCH o.produit")
    List<OrderDetail> findAll();

    @Query("SELECT o FROM OrderDetail o LEFT JOIN FETCH o.produit WHERE o.orderId = :orderId")
    Optional<OrderDetail> findById(@Param("orderId") Long orderId);
}
