package io.holunda.spike.cughh.bottlesofbeer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootApplication
@EnableProcessApplication
@Slf4j
public class BeerOnTheWallApp {

  public static void main(String[] args) {
    SpringApplication.run(BeerOnTheWallApp.class, args);
  }

  @Autowired
  private BeerService beerService;

  @EventListener
  public void deployProcesses(PostDeployEvent event) {
    event.getProcessEngine().getRepositoryService().createDeployment()
      .addModelInstance(
        "ordernew.bpmn",
        Bpmn.createExecutableProcess("orderBeer")
          .startEvent().condition("${beers == 0}")
          .serviceTask().camundaDelegateExpression("${orderBeerDelegate}").camundaAsyncBefore()
          .endEvent()
          .done()
      )
      .addModelInstance(
        "takeone.bpmn",
        Bpmn.createExecutableProcess("drinkBeer")
          .startEvent().condition("${beers > 0}")
          .serviceTask().camundaDelegateExpression("${drinkBeerDelegate}").camundaAsyncBefore()
          .endEvent()
          .done()
      )
      .deploy();


    beerService.orderBeer(10);
  }

  @Bean
  JavaDelegate orderBeerDelegate() {
    return delegateExecution -> beerService.orderBeer(10);
  }

  @Bean
  JavaDelegate drinkBeerDelegate() {
    return delegateExecution -> beerService.drinkBeer();
  }

  @Service
  @RequiredArgsConstructor
  public static class BeerService {
    private final RuntimeService runtimeService;

    private final AtomicInteger beers = new AtomicInteger(0);


    public void drinkBeer() {
      log.info("{} bottles of beer on the wall. Take one down and pass it around, {} bootles of beer on the wall", beers.get(), beers.decrementAndGet());

      publishNumberOfBeers();
    }

    public void orderBeer(int num) {
      beers.set(num);
      log.info("Go to the store and buy some more, {} bottles of beer on the wall.", beers.get());

      publishNumberOfBeers();
    }

    private void publishNumberOfBeers() {
      sleepUninterruptibly(2, SECONDS);
      runtimeService.createConditionEvaluation()
        .setVariables(Variables.putValue("beers", this.beers.get()))
        .evaluateStartConditions();
    }
  }
}
