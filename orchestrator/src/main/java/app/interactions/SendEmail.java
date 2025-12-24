package app.interactions;
 
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

import app.ingredients.SendEmailRequest;

public interface SendEmail extends Interaction {
    record EmailResult(String status) {}

    EmailResult apply(@RequiresIngredient("emailRequst") SendEmailRequest request);
}
