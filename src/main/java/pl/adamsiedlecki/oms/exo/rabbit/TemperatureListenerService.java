package pl.adamsiedlecki.oms.exo.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.oms.exo.otm.client.OtmApiService;
import pl.adamsiedlecki.oms.exo.pojo.TemperatureMessage;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
            otmApiService.importTemperatureIntoOtm(temperatureMessage);
        } catch(Exception e) {
            log.debug("This is not classic temperature message - importing as generic");
            otmApiService.genericImportIntoOtm(Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8)));
        }
    }
}
