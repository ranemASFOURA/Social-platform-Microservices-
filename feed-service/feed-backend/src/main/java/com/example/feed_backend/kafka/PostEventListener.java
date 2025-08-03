package com.example.feed_backend.kafka;

import com.example.feed_backend.model.FeedPost;
import com.example.feed_backend.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.messaging.handler.annotation.Payload;
import java.util.Map;



@Component
public class PostEventListener {

    @Autowired
    private FeedService feedService;

    @KafkaListener(topics = "post.created", groupId = "feed-group")
public void listen(@Payload PostCreatedEvent event) {
    try {
        System.out.println("Received post.created event: " + event.getPostId());

        FeedPost post = new FeedPost(
                event.getPostId(),
                event.getUserId(),
                event.getImageUrl(),
                event.getCaption(),
                event.getTimestamp(),
                event.getUserType()
        );

        feedService.distributePostToFollowers(post);
    } catch (Exception e) {
        System.err.println("Kafka listener failed: " + e.getMessage());
        e.printStackTrace();
    }
}

@KafkaListener(topics = "post.deleted", groupId = "feed-deleted-group")
    public void listenDeleted(@Payload Map<String, Object> event) {
        System.out.println("Received post.deleted event: " + event);

        String postId = (String) event.get("postId");
        String userId = (String) event.get("userId");

        if (postId != null && userId != null) {
            feedService.removePostFromFeeds(postId, userId);
        } else {
            System.err.println("Invalid post.deleted event: missing postId or userId");
        }
    }
}
