package antifraud.controller;

import antifraud.model.Ip;
import antifraud.model.Payment;
import antifraud.model.util.PaymentFeedback;
import antifraud.model.util.Status;
import antifraud.service.IpService;
import antifraud.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.LuhnCheck;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author Ilya Grishin
 */
@Validated
@RestController
@RequestMapping("/api/antifraud")
@RequiredArgsConstructor
public class AntifraudController {

    private final IpService ipService;
    private final PaymentService paymentService;

    @PreAuthorize("hasAuthority('SUPPORT')")
    @PostMapping("/suspicious-ip")
    public ResponseEntity<Ip> newSuspIp(@RequestBody @Valid Ip ip) {
        ipService.saveIp(ip);
        return ResponseEntity.ok().body(ip);
    }

    @PreAuthorize("hasAuthority('SUPPORT')")
    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<Status> deleteIp(@PathVariable @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$") String ip) {
        Status status = ipService.deleteByIp(ip);
        return ResponseEntity.ok().body(status);
    }

    @PreAuthorize("hasAuthority('SUPPORT')")
    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<Ip>> getAllIp() {
        return ResponseEntity.ok().body(ipService.getAll());
    }

    @PreAuthorize("hasAuthority('SUPPORT')")
    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getAllPayment() {
        return ResponseEntity.ok().body(paymentService.getAll());
    }

    @PreAuthorize("hasAuthority('SUPPORT')")
    @GetMapping("/history/{number}")
    public ResponseEntity<List<Payment>> getAllPaymentByNumber(@PathVariable @LuhnCheck String number) {
        return ResponseEntity.ok().body(paymentService.getAllByNumber(number));
    }

    @PutMapping("/transaction")
    @PreAuthorize("hasAuthority('SUPPORT')")
    public ResponseEntity<Payment> updatePayment(@RequestBody PaymentFeedback paymentFeedback) {
        return ResponseEntity.ok().body(paymentService.updatePayment(paymentFeedback));
    }

}
