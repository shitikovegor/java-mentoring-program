package com.jmp.grpc.client.service;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import com.jmp.grpc.proto.MessageRequest;
import com.jmp.grpc.proto.MessageServiceGrpc;

@Service
@Slf4j
public class MessageClientService {

    @GrpcClient("local-grpc-service")
    private MessageServiceGrpc.MessageServiceBlockingStub messageClient;

    public void sendMessage(final MessageRequest request) {
        final var response = messageClient.sendMessage(request);
        log.info("Response for request - {} - is - {}", request.getMessage(), response.getMessage());
    }

}
