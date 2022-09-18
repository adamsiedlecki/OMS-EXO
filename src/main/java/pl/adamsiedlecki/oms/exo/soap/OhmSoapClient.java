package pl.adamsiedlecki.oms.exo.soap;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import pl.adamsiedlecki.oms.exo.pojo.HumidityMessage;
import pl.adamsiedlecki.oms.exo.soap.humidity.client.ImportHumidityRequest;
import pl.adamsiedlecki.oms.exo.soap.humidity.client.ImportHumidityResponse;

/**
 * https://www.baeldung.com/spring-soap-web-service
 */
public class OhmSoapClient extends WebServiceGatewaySupport {

    public ImportHumidityResponse importHumidityIntoOhm(HumidityMessage message) {
        ImportHumidityRequest request = new ImportHumidityRequest();
        request.setHumidity(message.getStationMessage().getHu());
        request.setTime(message.getTime());
        request.setTown(message.getTown());
        request.setStationId(message.getStationMessage().getA());
        request.setLocationPlaceId(message.getLpid());
        return (ImportHumidityResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    }
}
