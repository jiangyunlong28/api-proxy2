package test;

import com.hnttg.cibcredit.api.service.WeatherService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WeatherTest.class)
@ComponentScan({"com.hnttg","com.hq"})
public class WeatherTest {

    private final  static Logger logger= LoggerFactory.getLogger(WeatherTest.class);


    @Autowired
    private WeatherService weatherService;


    @Test
    public void getWeatherTest() {
        weatherService.getWeather();
    }

}
