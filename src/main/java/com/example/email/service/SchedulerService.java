package com.example.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SchedulerService {

    @Autowired
    private OTCSClientService otcsClientService;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "${scheduler.cron}")
    public void run() {
        List<Map<String, Object>> data = otcsClientService.runLiveReport();

        for (Map<String, Object> row : data) {
            String nama = (String) row.get("Nama");
            String tanggal = String.valueOf(row.get("TanggalKadaluarsa"));

            emailService.sendNotification(
                "user@example.com",
                "Dokumen Kadaluarsa",
                "Dokumen " + nama +  " akan kadaluarsa pada " + tanggal
            );
        }
    }
}
