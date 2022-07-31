package pl.adamsiedlecki.oms.exo.otm.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.oms.exo.pojo.StationTemperatureMessage;
import pl.adamsiedlecki.oms.exo.pojo.TemperatureMessage;
import pl.adamsiedlecki.otm.model.SendTemperatureInput;
import pl.adamsiedlecki.otm.model.SendTemperatureInputStationMessage;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtmApiService {

    private final OtmApiClient otmApiClient;

    public void importIntoOtm(TemperatureMessage message) {
        SendTemperatureInput input = new SendTemperatureInput()
                .locationPlaceId(message.getLpid())
                .time(message.getTime())
                .town(message.getTown())
                .stationMessage(new SendTemperatureInputStationMessage()
                        .temperature(BigDecimal.valueOf(message.getStationMessage().getTp()))
                        .address(message.getStationMessage().getA())
                );

        ResponseEntity<Void> responseEntity = otmApiClient.importTemperatureWithHttpInfo(input);
        log.info("Message sent to OTM with result: {}", responseEntity);
    }
}
