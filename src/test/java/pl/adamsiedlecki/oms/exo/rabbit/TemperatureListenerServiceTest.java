package pl.adamsiedlecki.oms.exo.rabbit;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.adamsiedlecki.oms.exo.config.Config;
import pl.adamsiedlecki.oms.exo.otm.client.OtmApiService;
import pl.adamsiedlecki.oms.exo.pojo.StationTemperatureMessage;
import pl.adamsiedlecki.oms.exo.pojo.TemperatureMessage;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureListenerServiceTest {

    private Config config;
    private OtmApiService otmApiService;
    private TemperatureListenerService sut;

    @BeforeEach
    void setUp() {
        config = new Config();
        otmApiService = Mockito.mock(OtmApiService.class);
        sut = new TemperatureListenerService(config.objectMapper(), otmApiService);
    }

    @Test
    void receiveTemperatureMessage() {
        // given
        String message = "{lpid:2,town:\"Skurów\",time:1659283210,stationMessage:{a:2,tp:16.49}}";

        StationTemperatureMessage temperatureMessage = new StationTemperatureMessage();
        temperatureMessage.setA(2);
        temperatureMessage.setTp(16.49f);

        TemperatureMessage expectedMessage = new TemperatureMessage();
        expectedMessage.setLpid(2);
        expectedMessage.setTown("Skurów");
        expectedMessage.setTime(1659283210);
        expectedMessage.setStationMessage(temperatureMessage);

        // when
        sut.receiveTemperatureMessage(message);

        // then
        Mockito.verify(otmApiService).importIntoOtm(expectedMessage);
    }
}