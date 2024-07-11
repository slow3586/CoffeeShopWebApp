package com.slow3586.drinkshop.mainservice.customer;

import com.slow3586.drinkshop.api.CustomerEntityDto;
import com.slow3586.drinkshop.api.GetQrCodeResponse;
import com.slow3586.drinkshop.mainservice.utils.QrCodeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.UUID;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CustomerService {
    CustomerRepository customerRepository;
    CustomerMapper customerMapper;
    QrCodeUtils qrCodeUtils;

    @PostMapping("find")
    public CustomerEntityDto findOne(CustomerEntityDto customerDto) {
        Example<CustomerEntity> customerEntityExample = Example.of(
            customerMapper.toEntity(customerDto),
            ExampleMatcher.matching().withIgnoreNullValues());
        return customerRepository
            .findOne(customerEntityExample)
            .map(customerMapper::toDto)
            .orElse(null);
    }

    @PostMapping("save")
    public UUID save(CustomerEntityDto customerDto) {
        return customerRepository.save(customerMapper.toEntity(customerDto)).getId();
    }

    @GetMapping("/qr/{telegramId}")
    public GetQrCodeResponse getQrCode(@PathVariable String telegramId) {
        CustomerEntity customerEntity = customerRepository.getByTelegramId(telegramId);

        if (customerEntity.getQrCode() == null || customerEntity.getQrCodeExpiresAt().isBefore(Instant.now())) {
            final String code = String.valueOf(
                    LocalTime.now(ZoneId.of("UTC"))
                        .get(ChronoField.MILLI_OF_DAY) + 10_000_000)
                .substring(0, 6);

            customerEntity.setQrCode(code);
            customerEntity.setQrCodeExpiresAt(Instant.now().plusSeconds(300));
            customerEntity = customerRepository.save(customerEntity);
        }

        final byte[] image = qrCodeUtils.generateQRCodeImage(customerEntity.getQrCode());

        return GetQrCodeResponse.builder()
            .code(customerEntity.getQrCode())
            .duration(Duration.ofMinutes(5))
            .image(image)
            .build();
    }
}
