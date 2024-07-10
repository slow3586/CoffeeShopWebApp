package com.slow3586.drinkshop.mainservice.customer;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.slow3586.drinkshop.api.CustomerDto;
import com.slow3586.drinkshop.api.GetQrCodeResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
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
    QRCodeWriter qrCodeWriter = new QRCodeWriter();

    @GetMapping("/by_id/{id}")
    public CustomerDto get(@PathVariable UUID id) {
        return customerMapper.toDto(customerRepository.findById(id).orElseThrow());
    }

    @GetMapping("/by_tg/{telegramId}")
    public CustomerDto getByTelegramId(@PathVariable String telegramId) {
        return customerMapper.toDto(customerRepository.getByTelegramId(telegramId));
    }

    @GetMapping("/by_qr/{qrCode}")
    public CustomerDto getByQrCode(@PathVariable String qrCode) {
        return customerMapper.toDto(customerRepository.findByQrCode(qrCode));
    }

    @PostMapping
    public UUID save(CustomerDto customerDto) {
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

        final byte[] image = generateQRCodeImage(customerEntity.getQrCode());

        return GetQrCodeResponse.builder()
            .code(customerEntity.getQrCode())
            .duration(Duration.ofMinutes(5))
            .image(image)
            .build();
    }

    public byte[] generateQRCodeImage(String barcodeText) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(
                MatrixToImageWriter.toBufferedImage(
                    qrCodeWriter.encode(
                        barcodeText,
                        BarcodeFormat.QR_CODE,
                        200,
                        200)),
                "png",
                outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
