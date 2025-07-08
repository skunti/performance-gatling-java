package com.ambient.performance.model;

import lombok.Data;

@Data
public class Appointment {
    private String id;
    private String etag;
    private String patient_name;
    private String scheduled_start;
    private String scheduled_end;
    private String notes;

    // For Jackson deserialization
    public Appointment() {}
}
