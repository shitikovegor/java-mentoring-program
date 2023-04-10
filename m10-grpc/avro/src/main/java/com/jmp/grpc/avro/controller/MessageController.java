package com.jmp.grpc.avro.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jmp.grpc.avro.dto.MessageRequestDto;
import com.jmp.grpc.avro.producer.KafkaMessageProducer;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/messages")
public class MessageController {

    private final KafkaMessageProducer producer;

    @PostMapping
    public void sendMessage(@RequestBody final MessageRequestDto request) {
        producer.send(request)
                .subscribe();
    }

}
