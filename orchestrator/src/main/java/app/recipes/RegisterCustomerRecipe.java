package app.recipes;

import java.util.concurrent.ExecutionException;

import com.ing.baker.recipe.common.InteractionFailureStrategy;
import org.springframework.stereotype.Component;
import com.ing.baker.compiler.RecipeCompiler;
import com.ing.baker.recipe.javadsl.InteractionDescriptor;
import com.ing.baker.recipe.javadsl.Recipe;
import app.components.BakerComponent;
import app.ingredients.RegisterCustomerRequested;
import app.interactions.OpenAccount;
import app.interactions.RegisterCustomer;
import app.interactions.SendRegisterEmail;
import jakarta.annotation.PostConstruct; 

@Component
public class RegisterCustomerRecipe {
    private static final Recipe _recipe = new Recipe("register-customer-recipe")
                        .withDefaultFailureStrategy(new InteractionFailureStrategy.BlockInteraction())
                        .withSensoryEvent(RegisterCustomerRequested.class)
                        .withInteractions(
                            InteractionDescriptor.of(RegisterCustomer.class),
                            InteractionDescriptor.of(OpenAccount.class).withRequiredEvent(RegisterCustomer.CustomerRegistered.class),
                            InteractionDescriptor.of(SendRegisterEmail.class).withRequiredEvent(OpenAccount.AccountOpened.class)
                        );
                        
    private final BakerComponent _baker;
    private String _id;

    public RegisterCustomerRecipe(BakerComponent baker) {
        _baker = baker;
    }
    
    @PostConstruct
    public void instance() {
        try {
            var compiledRecipe = RecipeCompiler.compileRecipe(_recipe);
            _id = _baker.instance().addRecipe(compiledRecipe, true).get();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println(e);
        }
    }

    public String ID() {
        return _id;
    }
}
