package antifraud.model;

import antifraud.model.util.Region;
import antifraud.model.util.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Ilya Grishin
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long transactionId;

    @Min(1)
    @NotNull
    @Column
    private Long amount;

    @NotBlank
    @Column
    private String number;

    @NotBlank
    @Column
    private String ip;

    @Column
    private Region region;

    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    @Column
    private LocalDateTime date;

    @Column
    private Result result;

    @Column
    private String feedback = "";

}
