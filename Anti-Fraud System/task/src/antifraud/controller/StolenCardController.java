package antifraud.controller;

import antifraud.model.StolenCard;
import antifraud.model.util.Status;
import antifraud.service.StolenCardService;
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
public class StolenCardController {

    private final StolenCardService stolenCardService;

    @PreAuthorize("hasAuthority('SUPPORT')")
    @PostMapping("/stolencard")
    public ResponseEntity<StolenCard> newStolenCard(@RequestBody @Valid StolenCard stolenCard) {
        stolenCardService.saveStolenCard(stolenCard);
        return ResponseEntity.ok().body(stolenCard);
    }

    @PreAuthorize("hasAuthority('SUPPORT')")
    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<Status> deleteStolenCard(@PathVariable @LuhnCheck String number) {
        return ResponseEntity.ok().body(stolenCardService.deleteByNumber(number));
    }

    @PreAuthorize("hasAuthority('SUPPORT')")
    @GetMapping("/stolencard")
    public ResponseEntity<List<StolenCard>> getAll() {
        return ResponseEntity.ok().body(stolenCardService.getAll());
    }
}
