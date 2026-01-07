package app.interactions;
 
import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

import app.ingredients.CustomerDTO;
 
public interface SendRegisterEmail extends Interaction {
    interface EmailResult {}
    record EmailSent(String id) implements EmailResult {};
    record EmailFailed(String reason) implements EmailResult {};

    @FiresEvent(oneOf = { EmailSent.class, EmailFailed.class })
    EmailResult apply(@RequiresIngredient("customer") CustomerDTO customer);
}
