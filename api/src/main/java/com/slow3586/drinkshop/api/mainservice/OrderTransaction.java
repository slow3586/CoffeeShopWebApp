package com.slow3586.drinkshop.api.mainservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.slow3586.drinkshop.api.mainservice.entity.Product;
import com.slow3586.drinkshop.api.mainservice.entity.ProductInventory;
import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderTransaction {
    OrderRequest orderRequest;
    Product product;
    List<ProductInventory> productInventoryList;
    List<ShopInventory> shopInventoryList;
    Boolean inventoryReserved;
    Boolean paymentReceived;
    String cancelledReason;
}
