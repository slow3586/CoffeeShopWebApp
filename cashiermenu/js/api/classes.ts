export class Customer {
    id: string;
    telegramId: string;
    name: string;
    points: number;
    pointsReserved: number;
    phoneNumber: string;
    qrCode: string;
    qrCodeExpiresAt: Date;
    blockedReason: string;
}

export class CustomerOrder {
    id: string;
    customerId: string;
    shopId: string;
    status: string;
    createdAt: Date;
    paidAt: Date;
    completedAt: Date;
    customer: Customer;
    shop: Shop;
    customerOrderItemList: CustomerOrderItem[];
}

export class CustomerOrderItem {
    id: string;
    orderId: string;
    productId: string;
    quantity: number;
    product: Product;
}

export class Payment {
    id: string;
    orderId: string;
    value: number;
    paymentSystemId: string;
    sentToPaymentSystem: boolean;
    sentToPaymentSystemAt: Date;
    deniedReason: string;
    deniedAt: Date;
}

export class PaymentCheck {
    id: string;
    paymentId: string;
    paymentSystemStatus: string;
    status: string;
}

export class Product {
    id: string;
    name: string;
    price: number;
    productInventoryList: Array<ProductInventory>;
}

export class ProductGroup {
    id: string;
    name: string;
    color: string;
    productList: Array<Product>;
}

export class ProductInventory {
    id: string;
    productId: string;
    productInventoryTypeId: string;
    quantity: number;
    productInventoryType: ProductInventoryType;
}


export class ProductInventoryType {
    id: string;
    name: string;
}

export class Promo {
    id:string;
    code:string;
    name:string;
    text:string;
    image:Uint8Array;
    productTypeId:string;
    queuedForTelegram:boolean;
    startsAt:Date;
    endsAt:Date;
}

export class Shop {
    id: string;
    name: string;
    location: string;
    status: string;
    shopInventoryList: ShopInventory[];
}

export class ShopInventory {
    id: string;
    productInventoryTypeId: string;
    shopId: string;
    quantity: number;
    reserved: number;
}

export class Worker {
    id: string;
    name: string;
    status: string;
}

export class OrderRequest {
    customerId: string;
    shopId: string;
    usePoints: boolean;
    productQuantityList: Array<OrderRequestItem>;
}

export class OrderRequestItem {
    productId: string;
    quantity: number;
}