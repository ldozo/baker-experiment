package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;

import app.ingredients.OpenAccountRequest; 

public interface OpenAccount {
    interface Outcome {}
    record AccountOpened(String accountId) implements Outcome {}
    record AccountFailed(String reason) implements Outcome {}

    @FiresEvent(oneOf = { AccountOpened.class, AccountFailed.class })
    Outcome apply(@RequiresIngredient("accountRequest") OpenAccountRequest request);
}
