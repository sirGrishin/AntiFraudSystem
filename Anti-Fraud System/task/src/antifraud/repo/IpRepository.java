package antifraud.repo;

import antifraud.model.Ip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpRepository extends JpaRepository<Ip, Long> {
    Ip findByIp(String ip);

    List<Ip> findAllByOrderByIdAsc();

}