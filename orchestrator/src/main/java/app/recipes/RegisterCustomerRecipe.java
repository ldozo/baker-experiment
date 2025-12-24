package app.recipes;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ing.baker.compiler.RecipeCompiler;
import com.ing.baker.recipe.javadsl.InteractionDescriptor;
import com.ing.baker.recipe.javadsl.Recipe;
import com.ing.baker.runtime.javadsl.Baker;

import app.ingredients.RegisterCustomerRequested;
import app.interactions.OpenAccount;
import app.interactions.RegisterCustomer;
import app.interactions.SendEmail;
import jakarta.annotation.PostConstruct; 

@Component
public class RegisterCustomerRecipe {
    private static final Recipe _recipe = new Recipe("register-customer-recipe")
                        .withSensoryEvent(RegisterCustomerRequested.class)
                        .withInteractions(
                            InteractionDescriptor.of(RegisterCustomer.class),
                            InteractionDescriptor.of(OpenAccount.class).withRequiredEvent(RegisterCustomer.CustomerRegistered.class),
                            InteractionDescriptor.of(SendEmail.class).withRequiredEvent(OpenAccount.AccountOpened.class)
                        );

    @Autowired
    Baker _baker;

    private String _id;
    
    @PostConstruct
    public void instance() {
        try {
            var compiledRecipe = RecipeCompiler.compileRecipe(_recipe);
            _id = _baker.addRecipe(compiledRecipe, true).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public String ID() {
        return _id;
    }
}
