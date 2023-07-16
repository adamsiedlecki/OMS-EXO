package pl.adamsiedlecki.oms.exo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TraceableInput(String evId) {
}
