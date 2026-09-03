package trofimov.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YandexDiskFileData {
    private String name;
    private String path;
    private String type;
    private long size;
    private String created;
    private String modified;
}
