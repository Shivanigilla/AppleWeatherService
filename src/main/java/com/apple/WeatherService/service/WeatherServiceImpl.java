package com.apple.WeatherService.service;

import com.apple.WeatherService.exception.WeatherServiceException;
import com.apple.WeatherService.model.OpenWeatherMapResponse;
import com.apple.WeatherService.model.WeatherData;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.apple.WeatherService.constant.AppConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService{
    @Value("${openweathermap.api.key}")
    public  String apiKey = "apiKey";

    @Value("${openweathermap.api.url}")
    public  String apiUrl ="apiUrl";

    private final RestTemplate restTemplate;
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "weatherCache")
    public WeatherData getWeatherByZipcode(String zipcode) {
        String url = apiUrl + "?zip=" + zipcode + "&appid=" + apiKey + "&units=metric"; // Assuming metric units for temperature

        try {
            OpenWeatherMapResponse response = restTemplate.getForObject(url, OpenWeatherMapResponse.class);

            if (response != null && response.getMain() != null) {
                double currentTemperature = response.getMain().getTemp();
                double highTemperature = response.getMain().getTempMax();
                double lowTemperature = response.getMain().getTempMin();

                return WeatherData.builder()
                        .cached(false) // Indicate data is fetched from API
                        .currentTemperature(currentTemperature)
                        .highTemperature(highTemperature)
                        .lowTemperature(lowTemperature)
                        .build();
            }
        }catch (Exception e) {
            log.info(EXCEPTION_ERROR_MSG,e);
            throw new WeatherServiceException(WEATHER_RETRIEVE_ERROR, e);
        }

        return null; // Return null if weather data couldn't be fetched
    }



    public WeatherData getCachedWeather(String zipcode) {
        Cache weatherCache = cacheManager.getCache(CACHE_NAME); // Get the cache instance
        if (weatherCache != null) {
            Cache.ValueWrapper valueWrapper = weatherCache.get(zipcode); // Check if the data for the given zipcode is cached
            return (valueWrapper == null )?null: (WeatherData) valueWrapper.get();
        }
        return null;
    }
}
