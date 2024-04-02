package com.apple.WeatherService.rest;


import com.apple.WeatherService.model.WeatherData;
import com.apple.WeatherService.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.apple.WeatherService.constant.AppConstants.WEATHER_RETRIEVE_ERROR;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/weather")
@EnableCaching
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<Object> getWeatherByZipcode(@RequestParam String zipcode) {
        try {
            WeatherData weatherData = weatherService.getCachedWeather(zipcode);
            if(weatherData!=null){
                weatherData.setCached(true);
                log.info("Retrieving Cached weather for zipcode {}",zipcode);
                return ResponseEntity.ok(weatherData);
            }
            weatherData= weatherService.getWeatherByZipcode(zipcode);
            log.info("Retrieving precise weather for zipcode {}",zipcode);

            weatherData.setCached(false);
            return ResponseEntity.ok(weatherData);
        }catch (Exception exception){
            log.info(String.format(WEATHER_RETRIEVE_ERROR,zipcode),exception);
            return ResponseEntity.internalServerError().body(String.format(WEATHER_RETRIEVE_ERROR,zipcode));
        }


    }
}

