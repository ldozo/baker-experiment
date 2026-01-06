package app.ingredients;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferRequested {
    String sourceAccountId;
    String targetAccountId;
    BigDecimal amount;
    String currency;
}
