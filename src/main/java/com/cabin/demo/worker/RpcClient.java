package com.cabin.demo.worker;

import com.cabin.express.config.Environment;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class RpcClient implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(RpcClient.class.getName());
    private final Channel channel;
    private final Connection connection;
    private final String requestQueue = "photo.convert.rpc";
    private final String replyQueue;
    private final Map<String, CompletableFuture<String>> futures = new ConcurrentHashMap<>();

    public RpcClient() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Environment.getString("RABBITMQ_HOST"));
        factory.setUsername(Environment.getString("RABBITMQ_USERNAME"));
        factory.setPassword(Environment.getString("RABBITMQ_PASSWORD"));
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();

        channel.queueDeclare(requestQueue, /*durable*/ true, /*exclusive*/ false, /*autoDelete*/ false, null);

        // Declare a private, exclusive callback queue
        this.replyQueue = channel.queueDeclare("", false, true, true, null).getQueue();
        channel.basicConsume(replyQueue, true, this::handleResponse, consumerTag -> {
        });
    }

    private void handleResponse(String consumerTag, Delivery delivery) {
        String corrId = delivery.getProperties().getCorrelationId();
        CompletableFuture<String> future = futures.remove(corrId);
        if (future != null) {
            future.complete(new String(delivery.getBody(), StandardCharsets.UTF_8));
        }
    }

    public CompletableFuture<String> callConvert(long photoId, String rawKey) throws IOException {
        String corrId = UUID.randomUUID().toString();
        // Build request payload
        String msg = String.format("{\"photoId\":%d,\"rawKey\":\"%s\"}", photoId, rawKey);

        // Prepare properties with reply_to and correlation_id
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueue)
                .build();

        // Publish to the RPC queue
        channel.basicPublish("", requestQueue, props, msg.getBytes(StandardCharsets.UTF_8));
        // Store future to be completed upon reply
        CompletableFuture<String> future = new CompletableFuture<>();
        futures.put(corrId, future);
        return future;
    }

    @Override
    public void close() throws Exception {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }
}