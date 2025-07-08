package com.ambient.performance.model;

import lombok.Data;

@Data
public class AppointmentRequest {
    private String client_id;
    private Item item;

    @Data
    public static class Item {
        private String scheduled_start;
        private String scheduled_end;
        private String patient_name;
        private String notes;
    }
}
