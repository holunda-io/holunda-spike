package io.holunda.spike;

import io.holunda.spike.example.notification.client.NotificationService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableProcessApplication
@EnableScheduling
public class NotificationCamundaJobHandlerApplication {

  public static void main(String[] args) {
    SpringApplication.run(NotificationCamundaJobHandlerApplication.class, args);
  }

//    @Bean
//    @Lazy
//    public CommandExecutor commandExecutor(final ProcessEngineConfigurationImpl config) {
//      return config.getCommandExecutorTxRequired();
//    }

  @Autowired
  @Lazy
  private NotificationService notificationService;

  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  public void send() {
    notificationService.sendNotificationInaAJob("kermit@muppetshow.biz", "Miss Piggy is lost!", "Find her");
  }

}
