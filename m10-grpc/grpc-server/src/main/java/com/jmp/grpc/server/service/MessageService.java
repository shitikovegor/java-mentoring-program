package com.jmp.grpc.server.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import com.jmp.grpc.proto.MessageRequest;
import com.jmp.grpc.proto.MessageResponse;
import com.jmp.grpc.proto.MessageServiceGrpc;

@GrpcService
@Slf4j
public class MessageService extends MessageServiceGrpc.MessageServiceImplBase {

    @Override
    public void sendMessage(final MessageRequest request, final StreamObserver<MessageResponse> responseObserver) {
        log.info("Request is - {}", request.getMessage());
        final var messageResponse = MessageResponse.newBuilder()
                .setMessage("Pong")
                .build();
        responseObserver.onNext(messageResponse);
        responseObserver.onCompleted();
        log.info("Message sent");
    }

}
