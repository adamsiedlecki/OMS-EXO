package pl.adamsiedlecki.oms.exo.pojo;

public record EssaCompleteRequest(String type, String evId, int maxWaitMillis, LoraCompleteRequest messForLora) {
}
