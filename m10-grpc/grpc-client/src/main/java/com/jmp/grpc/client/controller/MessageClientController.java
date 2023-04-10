package com.jmp.grpc.client.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jmp.grpc.client.dto.MessageRequestDto;
import com.jmp.grpc.client.service.MessageClientService;
import com.jmp.grpc.proto.MessageRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/messages")
public class MessageClientController {

    private final MessageClientService service;

    @PostMapping
    public void sendMessage(@RequestBody final MessageRequestDto request) {
        final var messageRequest = MessageRequest.newBuilder()
                .setMessage(request.getMessage())
                .build();
        service.sendMessage(messageRequest);
    }

}
