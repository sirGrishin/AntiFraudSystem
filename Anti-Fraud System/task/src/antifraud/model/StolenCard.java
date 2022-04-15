package antifraud.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.LuhnCheck;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * @author Ilya Grishin
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StolenCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @LuhnCheck
    @Column(name = "number")
    private String number;
}
