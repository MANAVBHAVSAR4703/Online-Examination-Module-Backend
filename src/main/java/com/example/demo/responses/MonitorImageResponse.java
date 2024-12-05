package com.example.demo.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MonitorImageResponse {
    private Long id;
    private byte[] image;
    private Date captureTime;
}
