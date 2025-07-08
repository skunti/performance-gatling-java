package com.ambient.performance.model;

import lombok.Data;
import java.util.List;

@Data
public class AppointmentList {
    private List<Appointment> appointments;
}
