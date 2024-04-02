package com.apple.WeatherService.service;

import com.apple.WeatherService.model.WeatherData;

public interface WeatherService {
    WeatherData getWeatherByZipcode(String zipcode);
    public WeatherData getCachedWeather(String zipcode);

}
