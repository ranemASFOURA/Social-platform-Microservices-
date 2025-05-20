package com.example.search_backend;

import com.example.search_backend.model.UserDocument;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

@SpringBootApplication
public class SearchBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchBackendApplication.class, args);
	}

	@Bean
	public ConsumerFactory<String, UserDocument> consumerFactory() {
		JsonDeserializer<UserDocument> deserializer = new JsonDeserializer<>(UserDocument.class);
		deserializer.setRemoveTypeHeaders(false);
		deserializer.addTrustedPackages("*");
		deserializer.setUseTypeMapperForKey(true);

		return new DefaultKafkaConsumerFactory<>(
				Map.of(
						ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
						ConsumerConfig.GROUP_ID_CONFIG, "search-service-group",
						ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
						ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer),
				new StringDeserializer(),
				deserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, UserDocument> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, UserDocument> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}
