package pl.adamsiedlecki.oms.exo.pojo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class StationTemperatureMessage {
    private long a;
    private float tp;
}
