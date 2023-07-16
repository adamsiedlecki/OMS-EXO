package pl.adamsiedlecki.oms.exo.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.messaging.saaj.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pl.adamsiedlecki.oms.exo.pojo.Traceable;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
public class EssaWebsocketsController {

    private final SimpMessagingTemplate template;

    private static final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    public EssaWebsocketsController(SimpMessagingTemplate template) {
        this.template = template;
        objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    }

    @MessageMapping("/essaResponse")
    public void receive(Message<String> message) {
        log.info("WS Received: " + message.getPayload());
        String messageDecoded = Base64.base64Decode(message.getPayload());
        log.info("Decoded to: " + messageDecoded);
        try {
            Traceable traceable = objectMapper.readValue(messageDecoded, Traceable.class);
            map.put(traceable.evId(), messageDecoded);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public void send(String message) {
        String messageWithoutWhiteSpace = message.replaceAll("\\s", "");
        log.info("WS Sending: {}", messageWithoutWhiteSpace);
        byte[] encoded = Base64.encode(messageWithoutWhiteSpace.getBytes(StandardCharsets.UTF_8));
        template.convertAndSend("/topic/messageToEssa", new String(encoded,StandardCharsets.UTF_8));
    }

    public String getResponse(String eventId) {
        var temp = map.get(eventId);
        map.remove(eventId);
        return temp;
    }
}