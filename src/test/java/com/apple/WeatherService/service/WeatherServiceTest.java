package com.apple.WeatherService.service;

import com.apple.WeatherService.exception.WeatherServiceException;
import com.apple.WeatherService.model.OpenWeatherMapResponse;
import com.apple.WeatherService.model.WeatherData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class WeatherServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private WeatherServiceImpl weatherService;


    @Test
    void testGetWeatherByZipcode_Success() {
        // Mock data
        String zipcode = "12345";
        double currentTemperature = 20.0;
        double highTemperature = 25.0;
        double lowTemperature = 15.0;
        String apiUrl = "apiUrl";
        String apiKey = "apiKey";
        String url = apiUrl + "?zip=" + zipcode + "&appid=" + apiKey + "&units=metric";
        OpenWeatherMapResponse mockResponse = new OpenWeatherMapResponse();
        OpenWeatherMapResponse.Main main = new OpenWeatherMapResponse.Main();
        main.setTemp(currentTemperature);
        main.setTempMax(highTemperature);
        main.setTempMin(lowTemperature);
        mockResponse.setMain(main);

        // Mock restTemplate.getForObject
        when(restTemplate.getForObject(url, OpenWeatherMapResponse.class)).thenReturn(mockResponse);

        // Call the method
        WeatherData weatherData = weatherService.getWeatherByZipcode(zipcode);

        // Verify the returned WeatherData object
        assertNotNull(weatherData);
        assertFalse(weatherData.isCached());
        assertEquals(currentTemperature, weatherData.getCurrentTemperature());
        assertEquals(highTemperature, weatherData.getHighTemperature());
        assertEquals(lowTemperature, weatherData.getLowTemperature());
    }

    @Test
    void testGetWeatherByZipcode_NoResponse() {
        // Mock data
        String zipcode = "12345";
        String apiUrl = "apiUrl";
        String apiKey = "apiKey";
        String url = apiUrl + "?zip=" + zipcode + "&appid=" + apiKey + "&units=metric";

        // Mock restTemplate.getForObject to return null
        when(restTemplate.getForObject(url, OpenWeatherMapResponse.class)).thenReturn(null);

        // Call the method
        WeatherData weatherData = weatherService.getWeatherByZipcode(zipcode);

        // Verify null response
        assertNull(weatherData);
    }

    @Test
    void testGetWeatherByZipcode_Exception() {
        // Mock data
        String zipcode = "12345";
        String apiUrl = "apiUrl";
        String apiKey = "apiKey";
        String url = apiUrl + "?zip=" + zipcode + "&appid=" + apiKey + "&units=metric";

        // Mock restTemplate.getForObject to throw exception
        when(restTemplate.getForObject(url, OpenWeatherMapResponse.class)).thenThrow(new WeatherServiceException("Test Exception"));

        // Call the method
        assertThrows(WeatherServiceException.class, () -> weatherService.getWeatherByZipcode(zipcode));
    }

    @Test
    void testGetCachedWeather_Cached() {
        // Mock data
        String zipcode = "12345";
        WeatherData expectedWeatherData = WeatherData.builder().currentTemperature(1).highTemperature(1).lowTemperature(1).build();
        expectedWeatherData.setCached(true);

        // Mock cacheManager.getCache to return cache
        when(cacheManager.getCache("weatherCache")).thenReturn(cache);
        // Mock cache.get to return value wrapper with WeatherData
        when(cache.get(zipcode)).thenReturn(mock(Cache.ValueWrapper.class));
        when(cache.get(zipcode).get()).thenReturn(expectedWeatherData);

        // Call the method
        WeatherData weatherData = weatherService.getCachedWeather(zipcode);

        // Verify the returned WeatherData object
        assertNotNull(weatherData);
        assertTrue(weatherData.isCached());
    }

    @Test
    void testGetCachedWeather_NotCached() {
        // Mock data
        String zipcode = "12345";

        // Mock cacheManager.getCache to return null
        when(cacheManager.getCache("weatherCache")).thenReturn(null);

        // Call the method
        WeatherData weatherData = weatherService.getCachedWeather(zipcode);

        // Verify null response
        assertNull(weatherData);
    }
}
