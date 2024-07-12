package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.OrderTransaction;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductTypeRepository;
import com.slow3586.drinkshop.mainservice.repository.PromoRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProductService {
    CustomerRepository customerRepository;
    PromoRepository promoRepository;
    TelegramPublishRepository telegramPublishRepository;
    ProductRepository productRepository;
    ProductTypeRepository productTypeRepository;
    ProductInventoryRepository productInventoryRepository;
    StreamsBuilder streamsBuilder;

    @PostConstruct
    public void orderTransactionStream() {
        JsonSerde<OrderTransaction> orderTransactionJsonSerde = new JsonSerde<>();
        orderTransactionJsonSerde.deserializer().trustedPackages("*");

        KStream<String, OrderTransaction> stream = streamsBuilder
            .stream("order_transaction", Consumed.with(Serdes.String(), orderTransactionJsonSerde));

        stream.filter((k, op) -> op.getProduct() == null)
            .mapValues((k, operation) -> operation.setProduct(
                productRepository.findById(operation.getOrderRequest().getProductId()).orElseThrow()))
            .to("order_transaction");

        stream.filter((k, op) -> op.getProduct() != null && op.getProductInventoryList() == null)
            .mapValues((k, operation) -> operation.setProductInventoryList(
                productInventoryRepository.findByProductId(operation.getProduct().getId())))
            .to("order_transaction");
    }
}
