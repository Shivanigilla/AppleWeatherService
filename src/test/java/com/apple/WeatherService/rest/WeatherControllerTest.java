package com.apple.WeatherService.rest;

import com.apple.WeatherService.model.WeatherData;
import com.apple.WeatherService.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @Test
    void testGetWeatherByZipcode_CachedWeather() {
        // Mock data
        String zipcode = "12345";
        WeatherData cachedWeatherData = WeatherData.builder().currentTemperature(1).highTemperature(1).lowTemperature(1).build();

        cachedWeatherData.setCached(true);

        // Mock weatherService.getCachedWeather to return cachedWeatherData
        when(weatherService.getCachedWeather(zipcode)).thenReturn(cachedWeatherData);

        // Call the method
        ResponseEntity<Object> responseEntity = weatherController.getWeatherByZipcode(zipcode);

        // Verify ResponseEntity contains cachedWeatherData
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(cachedWeatherData, responseEntity.getBody());
    }

    @Test
    void testGetWeatherByZipcode_PreciseWeather() {
        // Mock data
        String zipcode = "12345";
        WeatherData preciseWeatherData = WeatherData.builder().currentTemperature(1).highTemperature(1).lowTemperature(1).build();

        preciseWeatherData.setCached(false);

        // Mock weatherService.getCachedWeather to return null
        when(weatherService.getCachedWeather(zipcode)).thenReturn(null);
        // Mock weatherService.getWeatherByZipcode to return preciseWeatherData
        when(weatherService.getWeatherByZipcode(zipcode)).thenReturn(preciseWeatherData);

        // Call the method
        ResponseEntity<Object> responseEntity = weatherController.getWeatherByZipcode(zipcode);

        // Verify ResponseEntity contains preciseWeatherData
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(preciseWeatherData, responseEntity.getBody());
    }

    @Test
    void testGetWeatherByZipcode_Exception() {
        // Mock data
        String zipcode = "12345";

        // Mock weatherService.getCachedWeather to throw exception
        when(weatherService.getCachedWeather(zipcode)).thenThrow(new RuntimeException("Test Exception"));

        // Call the method
        ResponseEntity<Object> responseEntity = weatherController.getWeatherByZipcode(zipcode);

        // Verify ResponseEntity contains internal server error message
        assertNotNull(responseEntity);
        assertEquals(500, responseEntity.getStatusCodeValue());
        assertEquals("Error retrieving weather data for zipcode 12345", responseEntity.getBody());
    }
}
