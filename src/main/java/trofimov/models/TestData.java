package trofimov.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TestData {
    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("apiUrl")
    private String apiUrl;

    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("actualExtension")
    private String actualExtension;

    @JsonProperty("newExtension")
    private String newExtension;
}