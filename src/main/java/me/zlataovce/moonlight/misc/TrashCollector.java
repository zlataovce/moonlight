package me.zlataovce.moonlight.misc;

import lombok.RequiredArgsConstructor;
import me.zlataovce.moonlight.storage.PasteRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TrashCollector {
    private final PasteRepository pasteRepository;

    @Scheduled(fixedDelay = 7200000)  // 2 hours
    public void expireLogs() {
        this.pasteRepository.findAll().forEach(log -> {
            // retention 14 days
            if ((TimeUnit.MILLISECONDS.toDays(new Date().getTime() - log.getCreated().getTime()) % 365) >= 14) {
                this.pasteRepository.delete(log);
            }
        });
    }
}
