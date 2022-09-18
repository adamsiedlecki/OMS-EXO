package pl.adamsiedlecki.oms.exo.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StationHumidityMessage {

    @JsonCreator
    public StationHumidityMessage(long a, float hu) {
        this.a = a;
        this.hu = hu;
    }

    @JsonProperty(required = true)
    private long a;
    @JsonProperty(required = true)
    private float hu;
}
