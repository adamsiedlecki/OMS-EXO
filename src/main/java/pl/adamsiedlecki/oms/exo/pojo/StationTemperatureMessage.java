package pl.adamsiedlecki.oms.exo.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StationTemperatureMessage {

    @JsonCreator
    public StationTemperatureMessage(long a, float tp) {
        this.a = a;
        this.tp = tp;
    }

    @JsonProperty(required = true)
    private long a;
    @JsonProperty(required = true)
    private float tp;
}
