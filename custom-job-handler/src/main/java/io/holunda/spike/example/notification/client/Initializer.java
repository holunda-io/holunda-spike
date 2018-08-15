package io.holunda.spike.example.notification.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class Initializer implements ApplicationRunner {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void run(final ApplicationArguments args) throws Exception {
        notificationService.sendNotificationInaAJob("kermit@muppetshow.biz", "Miss Piggy is lost!", "Find her");
    }
}
