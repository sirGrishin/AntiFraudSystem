package antifraud.controller;

import antifraud.model.Payment;
import antifraud.model.util.PaymentAnswer;
import antifraud.service.PaymentService;
import antifraud.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Ilya Grishin
 */
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping("/api/antifraud/transaction")
    @PreAuthorize("hasAuthority('MERCHANT')")
    public ResponseEntity<PaymentAnswer> getResult(@RequestBody @Valid Payment payment) {
        PaymentAnswer result = paymentService.getResult(payment);
        return ResponseEntity.ok().body(result);
    }
}
