package pl.adamsiedlecki.oms.exo.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TemperatureMessage {

    @JsonCreator
    public TemperatureMessage(long lpid, String town, long time, StationTemperatureMessage stationMessage) {
        this.lpid = lpid;
        this.town = town;
        this.time = time;
        this.stationMessage = stationMessage;
    }

    @JsonProperty(required = true)
    private long lpid;
    @JsonProperty(required = true)
    private String town;
    @JsonProperty(required = true)
    private long time;
    @JsonProperty(required = true)
    private StationTemperatureMessage stationMessage;

}
