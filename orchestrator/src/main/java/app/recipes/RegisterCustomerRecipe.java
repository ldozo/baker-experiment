package app.recipes;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;
import com.ing.baker.compiler.RecipeCompiler;
import com.ing.baker.il.CompiledRecipe;
import com.ing.baker.recipe.javadsl.InteractionDescriptor;
import com.ing.baker.recipe.javadsl.Recipe;
import app.components.BakerComponent;
import app.ingredients.CustomerRegisterEvent;
import app.interactions.OpenAccount;
import app.interactions.RegisterCustomer;
import app.interactions.SendRegisterEmail;
import jakarta.annotation.PostConstruct; 

@Component
public class RegisterCustomerRecipe {
    private static final Recipe _recipe = new Recipe("register-customer-recipe")
                        .withSensoryEvent(CustomerRegisterEvent.class)
                        .withInteractions(
                            InteractionDescriptor.of(RegisterCustomer.class)
                                                     .withMaximumInteractionCount(1),
                            InteractionDescriptor.of(OpenAccount.class)
                                                     .withRequiredEvent(RegisterCustomer.CustomerRegistered.class)
                                                     .withMaximumInteractionCount(1),
                            InteractionDescriptor.of(SendRegisterEmail.class)
                                                     .withRequiredEvent(OpenAccount.AccountOpened.class)
                                                     .withMaximumInteractionCount(1)
                        );
                        
    private final BakerComponent _baker;
    private String _id; 
    private CompiledRecipe _compileRecipe;
    
    public RegisterCustomerRecipe(BakerComponent baker) {
        _baker = baker;
    }
    
    @PostConstruct
    public void init() {
        try {
            _compileRecipe = RecipeCompiler.compileRecipe(_recipe);
            _id = _baker.instance().addRecipe(_compileRecipe, true).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public String ID() {
        return _id;
    }

    public CompiledRecipe compiled() {
        return _compileRecipe;
    }
}
