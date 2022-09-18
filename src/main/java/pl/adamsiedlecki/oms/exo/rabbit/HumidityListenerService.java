package pl.adamsiedlecki.oms.exo.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pl.adamsiedlecki.oms.exo.pojo.HumidityMessage;
import pl.adamsiedlecki.oms.exo.soap.OhmSoapClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class HumidityListenerService {

    private static final String QUEUE_NAME = "ohm/humidity";

    private final ObjectMapper objectMapper;
    private final OhmSoapClient ohmSoapClient;

    @RabbitListener(ackMode = "AUTO", queues = {QUEUE_NAME})
    public void receiveHumidityMessage(String message) {
        log.info("Received from rabbitmq: {}", message);
        try {
            HumidityMessage humidityMessage = objectMapper.readValue(message, HumidityMessage.class);
            var result = ohmSoapClient.importHumidityIntoOhm(humidityMessage);
            log.info("Message imported into OHM with result: {} | {}", result.getResult().getCode(), result.getResult().getDescription());
        } catch(Exception e) {
            log.error("Error while processing rabbit message: {}", e.getMessage());
        }
    }
}
