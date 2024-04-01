package com.example.pp.sheduler;

import com.example.pp.service.ClientServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerTask {
    private final ClientServiceImpl clientService;

    @Scheduled(cron = "${myapp.cron}")
    public void scheduleTaskWithCronExpression() {
        clientService.sendUniqueClientMessagesBasedOnTime();
    }
}
