package pl.adamsiedlecki.oms.exo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import pl.adamsiedlecki.oms.exo.config.EssaDevice;
import pl.adamsiedlecki.oms.exo.pojo.Traceable;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/oms-exo/essa")
public class EssaController {

    private List<EssaDevice> essaDeviceList = List.of(new EssaDevice(4, "test"), new EssaDevice(5, "test"));

    private final RabbitTemplate rabbitTemplate;
    private final EssaWebsocketsController essaWebsocketsController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EssaController(RabbitTemplate rabbitTemplate, EssaWebsocketsController essaWebsocketsController) throws IOException {
        this.rabbitTemplate = rabbitTemplate;
        this.essaWebsocketsController = essaWebsocketsController;
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);

        for(EssaDevice essaDevice: essaDeviceList) {
            // responseFrom/essa
            // commandsFor/essa%s
            String queueName = "responseFrom/essa" + essaDevice.getEssaId();
            String routingKey = "responseFrom.essa" + essaDevice.getEssaId();
            rabbitAdmin.declareQueue(new Queue(queueName, true));
            rabbitAdmin.declareBinding(new Binding(queueName, Binding.DestinationType.QUEUE, "amq.topic", routingKey, null));
        }
    }

    @PostMapping(value = "/task", consumes = "application/json", produces = "application/json")
    public String essaResponse(@RequestParam int essaId,
                               @RequestBody String message,
                               @RequestParam(defaultValue = "2000") int responseRabbitTimeoutMillis) {
        log.info("essa{} - request: {}", essaId, message.replaceAll("\\s", ""));

        try {
            Traceable traceable = objectMapper.readValue(message, Traceable.class);
            if (traceable.evId() == null || traceable.evId().isEmpty()) {
                return "missing or blank evId in message!";
            }

            essaWebsocketsController.send(message);
            var start = LocalDateTime.now();
            while (Duration.between(start, LocalDateTime.now()).toMillis() < responseRabbitTimeoutMillis) {
                var response = essaWebsocketsController.getResponse(traceable.evId());
                if (response != null) {
                    return response;
                }
            }
        } catch (JsonProcessingException e) {
            log.info("no evId in message! {}", e.getMessage());
            return "no evId in message!";
        }




//        synchronized (String.valueOf(essaId)) {
//            Message oldMessageFromQueue;
//            do {
//                oldMessageFromQueue = rabbitTemplate.receive("responseFrom/essa" + essaId, 1);
//            } while (oldMessageFromQueue != null);
//            rabbitTemplate.send("amq.topic", "commandsFor.essa" + essaId, new Message(message.getBytes(StandardCharsets.UTF_8)));
//            Message receive = rabbitTemplate.receive("responseFrom/essa" + essaId, responseRabbitTimeoutMillis);
//            if (receive == null) {
//                log.error("essa{} - no response on queue", essaId);
//                return "{\"error\": \"No message on response queue within: " + responseRabbitTimeoutMillis + " millis\"}";
//            }
//            log.info("essa{} - response: {}", essaId, new String(receive.getBody()));
//            return new String(receive.getBody());
//        }
        log.error("No response in desired time: {} millis", responseRabbitTimeoutMillis);
        return "No response in desired time";
    }
}
