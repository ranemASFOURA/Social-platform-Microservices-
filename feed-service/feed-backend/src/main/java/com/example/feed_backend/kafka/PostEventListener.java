package com.example.feed_backend.kafka;

import com.example.feed_backend.model.FeedPost;
import com.example.feed_backend.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.tracing.Tracer;

@Component
public class PostEventListener  {

    @Autowired
    private FeedService feedService;
    private static final Logger logger = LoggerFactory.getLogger(PostEventListener.class);


    @KafkaListener(topics = "post.created", groupId = "feed-group")
public void listen(
    @Payload PostCreatedEvent event,
    @Header(name = "X-B3-TraceId", required = false) String traceId,
    @Header(name = "X-B3-SpanId", required = false) String spanId
) {
    try {
        logger.info("Received post.created event. traceId={}, spanId={}", traceId, spanId);
        System.out.println("Received post.created event: " + event.getPostId());

        FeedPost post = new FeedPost(
                event.getPostId(),
                event.getUserId(),
                event.getImageUrl(),
                event.getCaption(),
                event.getTimestamp()
        );

        feedService.distributePostToFollowers(post);
    } catch (Exception e) {
        System.err.println("Kafka listener failed: " + e.getMessage());
        e.printStackTrace();
    }
}

}
