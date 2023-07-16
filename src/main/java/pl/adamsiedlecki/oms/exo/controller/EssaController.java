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
import pl.adamsiedlecki.oms.exo.pojo.TraceableInput;

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
                               @RequestParam(defaultValue = "2000") int responseOnWebsocketMillis) {
        log.info("essa request: {}", message.replaceAll("\\s", ""));

        try {
            TraceableInput traceableInput = objectMapper.readValue(message, TraceableInput.class);
            if (traceableInput.evId() == null || traceableInput.evId().isEmpty()) {
                log.error("missing or blank evId in message!");
                return "missing or blank evId in message!";
            }
            log.info("Essa target: {}, eventId: {}", essaId, traceableInput.evId());

            essaWebsocketsController.send(message, "essa" + essaId);
            var start = LocalDateTime.now();
            while (Duration.between(start, LocalDateTime.now()).toMillis() < responseOnWebsocketMillis) {
                var response = essaWebsocketsController.getResponse(traceableInput.evId());
                if (response != null) {
                    return response;
                }
            }
            var error = essaWebsocketsController.getResponse("essa" + essaId + "-error");
            if (error != null) {
                log.info("Error message was found: " + error);
                return error;
            }
        } catch (JsonProcessingException e) {
            String errorMessage = "Cannot convert message to Traceable: " + e.getMessage();
            log.info(errorMessage);
            return errorMessage;
        }

        log.error("No response in desired time: {} millis", responseOnWebsocketMillis);
        return "No response in desired time";
    }
}
