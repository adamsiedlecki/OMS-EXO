package pl.adamsiedlecki.oms.exo.otm.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.adamsiedlecki.oms.exo.config.Config;
import pl.adamsiedlecki.otm.api.OtmApi;
import pl.adamsiedlecki.otm.invoker.ApiClient;

@Service
public class OtmApiClient extends OtmApi {

    public OtmApiClient(RestTemplate restTemplate, Config config) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(config.getOtmAddress());
        setApiClient(apiClient);
    }
}
