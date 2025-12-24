package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient; 

public interface OpenAccount {
    interface OpenAccountResult {}
    record AccountOpened(String accountId) implements OpenAccountResult {}
    record AccountFailed(String reason) implements OpenAccountResult {}

    @FiresEvent(oneOf = { AccountOpened.class, AccountFailed.class })
    OpenAccountResult apply(@RequiresIngredient("customerId") String customerId,
                  @RequiresIngredient("name") String name,
                  @RequiresIngredient("currency") String currency);
}
