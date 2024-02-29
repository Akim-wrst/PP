package com.example.pp.sheduler;

import com.example.pp.service.ClientServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SchedulerTask {
    private final ClientServiceImpl clientService;

    @Scheduled(cron = "${myapp.cron}")
    public void scheduleTaskWithCronExpression() {
        clientService.getClientUniqueInfo();
    }
}
