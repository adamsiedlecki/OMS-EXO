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

    private OtmApiService otmApiService;
    private TemperatureListenerService sut;

    @BeforeEach
    void setUp() {
        otmApiService = Mockito.mock(OtmApiService.class);
        var config = new Config();

        sut = new TemperatureListenerService(config.objectMapper(), otmApiService);
    }

    @Test
    void shouldImportIntoOtmRabbitMessage() {
        // given
        String message = "{lpid:2,town:\"Skurów\",time:1659283210,stationMessage:{a:2,tp:16.49}}";

        StationTemperatureMessage temperatureMessage = new StationTemperatureMessage(2, 16.49f);

        TemperatureMessage expectedMessage = new TemperatureMessage(2, "Skurów", 1659283210, temperatureMessage);

        // when
        sut.receiveTemperatureMessage(message);

        // then
        Mockito.verify(otmApiService).importIntoOtm(expectedMessage);
    }

    @Test
    void shouldNotImportIntoOtmIncompleteData() {
        // given
        String message = "{lpid:2,town:\"Skurów\",time:1659283210,stationMessage:{a:2}}";

        // when
        sut.receiveTemperatureMessage(message);

        // then
        Mockito.verify(otmApiService, Mockito.times(0)).importIntoOtm(Mockito.any());
    }

    @Test
    void shouldNotImportIntoOtmBadData() {
        // given
        String message = "{lpid:2,town:\"Skurów\",time:1659283210,stationMessage:{a:2,hu:99.99}}";

        // when
        sut.receiveTemperatureMessage(message);

        // then
        Mockito.verify(otmApiService, Mockito.times(0)).importIntoOtm(Mockito.any());
    }
}