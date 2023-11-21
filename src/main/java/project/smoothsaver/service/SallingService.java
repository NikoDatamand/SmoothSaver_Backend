package project.smoothsaver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import project.smoothsaver.dtos.SallingResponse;

import java.net.URI;
import java.util.List;

@Service
public class SallingService {

    @Value("${app.api-key-salling}")
    private String API_KEY;

    private final static String URL = "https://api.sallinggroup.com/v1/food-waste/";

    public static final Logger logger = LoggerFactory.getLogger(SallingService.class);
    private final WebClient client;
    public SallingService() {
        this.client = WebClient.create();
    }
    //Use this constructor for testing, to inject a mock client
    public SallingService(WebClient client) {
        this.client = client;
    }


    public List<SallingResponse> getItemsOnSaleZip(String zip) {
        String err;
        try {
            List<SallingResponse> response =  client.get()
                        .uri(new URI(URL + "?zip=" +  zip))
                    .header("Authorization", "Bearer " + API_KEY)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
          return response;

        }  catch (WebClientResponseException e){
            //This is how you can get the status code and message reported back by the remote API
            logger.error("Error response body: " + e.getResponseBodyAsString());
            logger.error("WebClientResponseException", e);
            err = "Internal Server Error, due to a failed request to external service. You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
        catch (Exception e) {
            logger.error("Exception", e);
            err = "Internal Server Error - You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
    }

//

    public Page<SallingResponse.ItemOnSale> getItemOnSaleById(String id, Pageable pageable) {
        String err;
        try {
            SallingResponse response = client.get()
                    .uri(new URI(URL + id))
                    .header("Authorization", "Bearer " + API_KEY)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(SallingResponse.class)
                    .block();

            int totalElements = response.getClearances().size();
            // Calculate the indices for the sublist
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), totalElements);

            // Return a sublist of clearances (ItemOnSale)
            return new PageImpl<>(response.getClearances().subList(start, end), pageable, totalElements);
        }  catch (WebClientResponseException e){
            //This is how you can get the status code and message reported back by the remote API
            logger.error("Error response body: " + e.getResponseBodyAsString());
            logger.error("WebClientResponseException", e);
            err = "Internal Server Error, due to a failed request to external service. You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
        catch (Exception e) {
            logger.error("Exception", e);
            err = "Internal Server Error - You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
    }

    public List<SallingResponse> getStoresCity(String city) {
        String url = "https://api.sallinggroup.com/v2/stores/?city=";
        String err;
        try {
            List<SallingResponse> response =  client.get()
                    .uri(new URI(url + city))
                    .header("Authorization", "Bearer " + API_KEY)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
            return response;

        }  catch (WebClientResponseException e){
            //This is how you can get the status code and message reported back by the remote API
            logger.error("Error response body: " + e.getResponseBodyAsString());
            logger.error("WebClientResponseException", e);
            err = "Internal Server Error, due to a failed request to external service. You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
        catch (Exception e) {
            logger.error("Exception", e);
            err = "Internal Server Error - You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
    }
}
