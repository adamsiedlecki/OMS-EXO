package pl.adamsiedlecki.oms.exo.pojo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TemperatureMessage {

    private long lpid;
    private String town;
    private long time;
    private StationTemperatureMessage stationMessage;

}
