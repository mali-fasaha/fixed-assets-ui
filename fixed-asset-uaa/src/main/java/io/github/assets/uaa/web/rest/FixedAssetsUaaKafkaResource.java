package io.github.assets.uaa.web.rest;

import io.github.assets.uaa.service.FixedAssetsUaaKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fixed-assets-uaa-kafka")
public class FixedAssetsUaaKafkaResource {

    private final Logger log = LoggerFactory.getLogger(FixedAssetsUaaKafkaResource.class);

    private FixedAssetsUaaKafkaProducer kafkaProducer;

    public FixedAssetsUaaKafkaResource(FixedAssetsUaaKafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/publish")
    public void sendMessageToKafkaTopic(@RequestParam("message") String message) {
        log.debug("REST request to send to Kafka topic the message : {}", message);
        this.kafkaProducer.send(message);
    }
}
