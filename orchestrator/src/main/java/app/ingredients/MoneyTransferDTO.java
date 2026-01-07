package app.ingredients;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferDTO {
    private String sourceAccountId;
    private String targetAccountId;
    private BigDecimal amount;
    private String currency;
}
