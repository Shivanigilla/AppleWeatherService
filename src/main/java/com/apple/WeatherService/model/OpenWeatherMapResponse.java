package com.apple.WeatherService.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenWeatherMapResponse {
    private Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public static class Main {
        @JsonProperty("temp")
        private double temp;

        @JsonProperty("temp_max")
        private double tempMax;

        @JsonProperty("temp_min")
        private double tempMin;

        // Getters and setters
        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public double getTempMax() {
            return tempMax;
        }

        public void setTempMax(double tempMax) {
            this.tempMax = tempMax;
        }

        public double getTempMin() {
            return tempMin;
        }

        public void setTempMin(double tempMin) {
            this.tempMin = tempMin;
        }
    }
}
