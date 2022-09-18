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
public class HumidityMessage {

    @JsonCreator
    public HumidityMessage(long lpid, String town, long time, StationHumidityMessage stationMessage) {
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
    private StationHumidityMessage stationMessage;

}
