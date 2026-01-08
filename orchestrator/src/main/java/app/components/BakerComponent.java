package app.components;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ing.baker.runtime.inmemory.InMemoryBaker;
import com.ing.baker.runtime.javadsl.Baker;

import app.interactions.impl.CreditAccountImpl;
import app.interactions.impl.DebitAccountImpl;
import app.interactions.impl.OpenAccountImpl;
import app.interactions.impl.RegisterCustomerImpl;
import app.interactions.impl.RollbackDebitAccountImpl;
import app.interactions.impl.SendRegisterEmailImpl;
import app.interactions.impl.ValidateSourceAccountImpl;
import app.interactions.impl.ValidateSourceCustomerImpl;
import app.interactions.impl.ValidateTargetAccountImpl;
import app.interactions.impl.ValidateTargetCustomerImpl;
import app.interactions.impl.ValidateTransactionImpl;

@Component
public class BakerComponent {
    private final Baker _baker;

    public BakerComponent (RegisterCustomerImpl rci,
                            OpenAccountImpl oai,
                            SendRegisterEmailImpl srei,
                            ValidateSourceAccountImpl vsai,
                            ValidateSourceCustomerImpl vsaci,
                            ValidateTargetAccountImpl vtai,
                            ValidateTargetCustomerImpl vtci,
                            ValidateTransactionImpl vti,
                            DebitAccountImpl dai,
                            CreditAccountImpl cai,
                            RollbackDebitAccountImpl rdai
    ){
        _baker = InMemoryBaker.java(
            List.of(rci, oai, srei, vsai, vsaci, vtai, vtci, vti, dai, cai, rdai)
        );
    }

    public Baker instance() {
        return _baker;
    }
}
