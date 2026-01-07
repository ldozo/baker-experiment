package app.recipes;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import com.ing.baker.compiler.RecipeCompiler;
import com.ing.baker.il.CompiledRecipe;
import com.ing.baker.recipe.javadsl.InteractionDescriptor;
import com.ing.baker.recipe.javadsl.Recipe;

import app.components.BakerComponent;
import app.ingredients.MoneyTransferDTO;
import app.ingredients.MoneyTransferEvent;
import app.interactions.CreditAccount;
import app.interactions.DebitAccount;
import app.interactions.ValidateSourceAccount;
import app.interactions.ValidateSourceCustomer;
import app.interactions.ValidateTargetAccount;
import app.interactions.ValidateTargetCustomer;
import app.interactions.ValidateTransaction;
import jakarta.annotation.PostConstruct;

@Component
public class MoneyTransferRecipe {

    private static final Recipe _recipe = new Recipe("money-transfer-recipe")
                        .withSensoryEvent(MoneyTransferEvent.class)
                        .withInteractions(
                            InteractionDescriptor.of(ValidateSourceAccount.class)
                                                     .withRequiredEvent(MoneyTransferEvent.class)
                                                     .withMaximumInteractionCount(1),
                            InteractionDescriptor.of(ValidateSourceCustomer.class)
                                                     .withRequiredEvent(ValidateSourceAccount.SourceAccountValidated.class)
                                                     .withMaximumInteractionCount(1),
                            InteractionDescriptor.of(ValidateTargetAccount.class)
                                                     .withRequiredEvent(MoneyTransferDTO.class)
                                                     .withMaximumInteractionCount(1),
                            InteractionDescriptor.of(ValidateTargetCustomer.class)
                                                     .withRequiredEvent(ValidateTargetAccount.TargetAccountValidated.class)
                                                     .withMaximumInteractionCount(1),
                            InteractionDescriptor.of(ValidateTransaction.class)
                                                     .withRequiredEvent(MoneyTransferEvent.class)
                                                     .withMaximumInteractionCount(1),
                            InteractionDescriptor.of(DebitAccount.class)
                                                     .withRequiredEvents(ValidateSourceCustomer.SourceCustomerValidated.class,
                                                                        ValidateTargetCustomer.TargetCustomerValidated.class,
                                                                        ValidateTransaction.TransactionValidated.class                                                     )
                                                     .withMaximumInteractionCount(1),
                            InteractionDescriptor.of(CreditAccount.class)
                                                     .withRequiredEvent(DebitAccount.DebitSucceed.class)
                                                     .withMaximumInteractionCount(1)

                        );
                        
    private final BakerComponent _baker;
    private String _id; 
    private CompiledRecipe _compileRecipe;
    
    public MoneyTransferRecipe(BakerComponent baker) {
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
