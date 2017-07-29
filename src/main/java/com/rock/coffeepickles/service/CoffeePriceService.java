package com.rock.coffeepickles.service;

import com.rock.coffeepickles.domain.Coffee;
import com.rock.coffeepickles.domain.Customer;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class CoffeePriceService {

    @Autowired
    public CoffeePriceService() {
    }

    public Coffee getCoffeePrice(Customer user, KieContainer container) {


        KieSession ksession = container.newKieSession("ksession");
        ksession.insert(user);
        ksession.fireAllRules();

        QueryResults results = ksession.getQueryResults("getCoffeeObjects");
        Coffee coffee = new Coffee(new BigDecimal("10000.00"), null);
        for ( QueryResultsRow row : results ) {
            Coffee queryCoffee = ( Coffee ) row.get("$result");
            if (queryCoffee.getUser().getUserName().equals(user.getUserName())) {
                coffee = queryCoffee;
            }
        }
        ksession.dispose();
        return coffee;
    }
}
