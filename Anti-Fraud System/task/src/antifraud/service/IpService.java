package antifraud.service;

import antifraud.exeption.IpExistException;
import antifraud.model.Ip;
import antifraud.model.StolenCard;
import antifraud.model.User;
import antifraud.model.util.Status;
import antifraud.model.util.UserStatus;
import antifraud.repo.IpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ilya Grishin
 */
@Service
@RequiredArgsConstructor
public class IpService {
    private final IpRepository ipRepository;


    public void saveIp(Ip ip) {
        if (ipNotExist(ip.getIp())) {
            ipRepository.save(ip);
        } else throw new IpExistException();

    }

    public Status deleteByIp(String ip){
        Ip byIp = ipRepository.findByIp(ip);
        if (byIp == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Status status = new Status();
        ipRepository.delete(byIp);
        status.setStatus("IP "+ ip +" successfully removed!");
        return status;
    }

    public List<Ip> getAll(){
        List<Ip> allByOrderByIdAsc = ipRepository.findAllByOrderByIdAsc();
        if(allByOrderByIdAsc.isEmpty()){
            return new ArrayList<>();
        }
        else return allByOrderByIdAsc;
    }

    /**
     * true если ip не существует
     */
    private boolean ipNotExist(String ip) {
        return ipRepository.findByIp(ip) == null;
    }
}
