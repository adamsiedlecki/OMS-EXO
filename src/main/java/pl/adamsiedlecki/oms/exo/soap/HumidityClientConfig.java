package pl.adamsiedlecki.oms.exo.soap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class HumidityClientConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("pl.adamsiedlecki.oms.exo.soap.humidity.client");
        return marshaller;
    }
    @Bean
    public OhmSoapClient humidityClient(Jaxb2Marshaller marshaller) {
        var client = new OhmSoapClient();
        client.setDefaultUri("http://10.0.0.20:8089/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
