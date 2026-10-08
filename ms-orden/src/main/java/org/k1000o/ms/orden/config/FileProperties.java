package org.k1000o.ms.orden.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "uploads")
@Data
public class FileProperties {
    private String path;
}