package antifraud.repo;

import antifraud.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p where p.number = ?1 and p.date between ?2 and ?3")
    List<Payment> findAllByNumberAndDateBetween(String number, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    Optional<List<Payment>> findAllByNumber(String number);

    List<Payment> findAll();


}