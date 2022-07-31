package pl.adamsiedlecki.oms.exo.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.oms.exo.otm.client.OtmApiService;
import pl.adamsiedlecki.oms.exo.pojo.TemperatureMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemperatureListenerService {

    private static final String QUEUE_NAME = "otm/temperature";

    private final ObjectMapper objectMapper;
    private final OtmApiService otmApiService;

    @RabbitListener(ackMode = "AUTO", queues = {QUEUE_NAME})
    public void receiveTemperatureMessage(String message) {
        log.info("Received from rabbitmq: {}", message);
        try {
            TemperatureMessage temperatureMessage = objectMapper.readValue(message, TemperatureMessage.class);
            otmApiService.importIntoOtm(temperatureMessage);
        } catch(Exception e) {
            log.error("Error while processing rabbit message", e);
        }
    }
}
