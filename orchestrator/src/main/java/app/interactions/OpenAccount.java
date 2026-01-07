package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

import app.ingredients.CustomerDTO; 

public interface OpenAccount extends Interaction {
    interface OpenAccountResult {}
    record AccountOpened(String accountId) implements OpenAccountResult {}
    record AccountFailed(String reason) implements OpenAccountResult {}

    @FiresEvent(oneOf = { AccountOpened.class, AccountFailed.class })
    OpenAccountResult apply(@RequiresIngredient("customerId") String customerId, 
                            @RequiresIngredient("customer") CustomerDTO customer);
}
