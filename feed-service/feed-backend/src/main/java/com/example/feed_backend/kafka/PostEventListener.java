package com.example.feed_backend.kafka;

import com.example.feed_backend.model.FeedPost;
import com.example.feed_backend.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.messaging.handler.annotation.Payload;


@Component
public class PostEventListener {

    @Autowired
    private FeedService feedService;

    @KafkaListener(topics = "post.created", groupId = "feed-group")
    public void listen(@Payload PostCreatedEvent event) {
        System.out.println(" Received post.created event: " + event.getPostId());

        FeedPost post = new FeedPost(
                event.getPostId(),
                event.getUserId(),
                event.getImageUrl(),
                event.getCaption(),
                event.getTimestamp()
        );

        feedService.distributePostToFollowers(post);
    }
}
