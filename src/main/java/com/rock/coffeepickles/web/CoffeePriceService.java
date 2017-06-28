package com.rock.coffeepickles.web;

import com.rock.coffeepickles.domain.Coffee;
import com.rock.coffeepickles.domain.Customer;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class CoffeePriceService {
    private final KieContainer kieContainer;

    @Autowired
    public CoffeePriceService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public Coffee getCoffeePrice(Customer user) {
        KieSession ksession = kieContainer.newKieSession("ksession");
        ksession.insert(user);
        ksession.fireAllRules();

        QueryResults results = ksession.getQueryResults("getCoffeeObjects");
        Coffee coffee = new Coffee(new BigDecimal("10000.00"), null);
        for ( QueryResultsRow row : results ) {
            Coffee queryCoffee = ( Coffee ) row.get("$result");
            if (queryCoffee.getUser().getUserName() == user.getUserName()) {
                coffee = queryCoffee;
            }
        }
        ksession.dispose();
        return coffee;
    }
}
