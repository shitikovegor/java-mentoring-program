package com.jmp.kafka.palmetto.service;

import java.util.List;

import com.jmp.kafka.palmetto.TestMockData;
import com.jmp.kafka.palmetto.dto.ProductAddRequestDto;
import com.jmp.kafka.palmetto.exception.PalmettoServiceException;
import com.jmp.kafka.palmetto.mapper.ProductMapper;
import com.jmp.kafka.palmetto.model.Product;
import com.jmp.kafka.palmetto.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository repository;

    private ProductService service;

    @BeforeEach
    void setUp() {
        final var mapper = Mappers.getMapper(ProductMapper.class);
        service = new ProductService(repository, mapper);
    }

    @Test
    void add() {
        // given
        final var productDto = TestMockData.buildProductDto(TestMockData.PRODUCT_NAME);
        final var request = new ProductAddRequestDto(TestMockData.PRODUCT_NAME);
        when(repository.existsByName(TestMockData.PRODUCT_NAME)).thenReturn(Mono.just(Boolean.FALSE));
        doAnswer(invocation -> {
            final var argument = invocation.<Product>getArgument(0);
            productDto.setId(argument.getId());
            return Mono.just(argument);
        }).when(repository).save(any());
        // when
        StepVerifier.create(service.add(request))
                // then
                .expectNext(productDto)
                .verifyComplete();
    }

    @Test
    void add_BadRequest() {
        // given
        final var request = TestMockData.buildProductRequest();
        when(repository.existsByName(TestMockData.PRODUCT_NAME)).thenReturn(Mono.just(Boolean.TRUE));
        // when
        StepVerifier.create(service.add(request))
                // then
                .expectError(PalmettoServiceException.class)
                .verify();
    }

    @Test
    void findByProductNames() {
        // given
        final var product1 = TestMockData.buildProduct(TestMockData.PRODUCT_NAME);
        final var productName1 = "Product1";
        final var product2 = TestMockData.buildProduct(productName1);
        final var productDto1 = TestMockData.buildProductDto(TestMockData.PRODUCT_NAME);
        final var productDto2 = TestMockData.buildProductDto(productName1);
        when(repository.findByName(TestMockData.PRODUCT_NAME)).thenReturn(Mono.just(product1));
        when(repository.findByName(productName1)).thenReturn(Mono.just(product2));
        // when
        StepVerifier.create(service.findByProductNames(List.of(TestMockData.PRODUCT_NAME, productName1)))
                // then
                .expectNextSequence(List.of(productDto1, productDto2))
                .verifyComplete();
    }

}
