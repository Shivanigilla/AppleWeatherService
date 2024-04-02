package com.apple.WeatherService.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherData {

    private double currentTemperature;
    private double highTemperature;
    private double lowTemperature;

    @Builder.Default
    private boolean cached=true;
}
