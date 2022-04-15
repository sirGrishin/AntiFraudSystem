package antifraud.repo;

import antifraud.model.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {
    StolenCard findByNumber(String number);

    List<StolenCard> findAllByOrderByIdAsc();
}