import {createRoot} from "react-dom/client";

import React, {useRef, useState} from "react";
import {QueryClient, QueryClientProvider, useQuery, useQueryClient} from "react-query";
import {Toast} from "primereact/toast";
import axios from "axios";
import {Customer, Order, OrderRequestItem, Product, Shop} from "./api/classes";
import {format} from "date-fns";
import {Button} from "primereact/button";

import './style.less'

createRoot(document.getElementById("root")).render(
    <QueryClientProvider client={new QueryClient()}>
        <AppCashier/>
    </QueryClientProvider>
);

export function AppCashier() {
    const queryClient = useQueryClient();
    const toast = useRef(null);
    const [customerCode, setCustomerCode] = useState<string>("");

    const customerQuery = useQuery(
        ['/api/customer/findByQrCode/' + customerCode],
        async () => (await axios.get('/api/customer/findByQrCode/' + customerCode)).data,
        {
            enabled: customerCode?.length == 6
        });
    const customer: Customer = customerQuery?.data ?? null;

    const shopQuery = useQuery(
        ['/api/shop/findAll'],
        async () => (await axios.get('/api/shop/findAll')).data,
        {
            enabled: true
        });
    const shop: Shop = shopQuery?.data?.[0] ?? null;

    const productQuery = useQuery(
        ['product'],
        async () => (await axios.get("/api/product/findAll")).data,
        {
            enabled: true
        });
    const productList: Array<Product> = productQuery?.data ?? [];

    const orderQuery = useQuery(
        ['/api/order/findAllActiveByShopId', shopQuery],
        async () => (await axios.get("/api/order/findAllActiveByShopId/" + shop.id)).data,
        {
            enabled: shop != null
        });
    const orderList: Array<Order> = orderQuery?.data ?? [];

    const [showClientDialog, setShowClientDialog] = useState<boolean>(false);
    const [newOrderItemList, setNewOrderItemList] = useState<OrderRequestItem[]>([]);

    const totalPrice = newOrderItemList.length > 0 ? newOrderItemList.map(item => item.quantity * productList.find(product => product.id == item.productId).price).reduce((a, b) => a + b) : 0;

    if (shop == null) return <div></div>

    return (
        <div className="gh-container">
            <div className="gh-control">
                <div className="gh-orders">
                    {orderList.map(order => <div
                        key={order.id}
                        className="order">
                        <div className="title">{order.customer?.name} {format(order.createdAt, "hh:mm:ss dd.MM")} </div>
                        <div className="items">
                            {order.orderItemList.map(orderItem => <div
                                key={orderItem.id}>
                                {orderItem?.product?.name}
                                {orderItem?.quantity > 1 ? " - " + orderItem.quantity + " шт." : ""}</div>)}
                        </div>
                    </div>)}
                </div>
                <div className="gh-neworder">
                    <div className="result">
                        {newOrderItemList.map(productQuantity =>
                            <div
                                key={productQuantity.productId}>
                                {productList.find(p => p.id == productQuantity.productId).name}{" - "}
                                {productList.find(p => p.id == productQuantity.productId).price}Р
                                {productQuantity.quantity > 1 ? " - " + productQuantity.quantity + " шт." : ""}
                                {productQuantity.quantity > 1 ? " - " + productList.find(p => p.id == productQuantity.productId).price * productQuantity.quantity + "Р" : ""}
                            </div>
                        )}
                        {totalPrice > 0 ? "Всего: " + totalPrice + "Р" : ""}
                    </div>
                    <div className="gh-products">
                        {productList.map(product =>
                            <div
                                key={product.id}
                                onClick={() => {
                                    const item = newOrderItemList.find(item => item.productId == product.id);
                                    if (item) {
                                        setNewOrderItemList(newOrderItemList.map(i => {
                                            if (i.productId == product.id) {
                                                i.quantity += 1;
                                            }
                                            return i;
                                        }));
                                    } else {
                                        setNewOrderItemList([...newOrderItemList, {
                                            productId: product.id,
                                            quantity: 1
                                        }]);
                                    }
                                }}
                                className="product">
                                <div>{product.name}</div>
                                <div>{product.price}Р</div>
                            </div>)}
                    </div>
                    <div className="customer">
                        {customer
                            ?
                            <div className="info">Гость: {customer.name} Телефон: {customer.phoneNumber} Баллы: {customer.points - customer.pointsReserved}</div>
                            : <div className="input">
                                <label>Код гостя: </label>
                                <input value={customerCode} onChange={(e) => setCustomerCode(e.target.value)}/>
                            </div>}
                    </div>
                    <Button
                        disabled={totalPrice == 0}
                        onClick={() => {
                            axios.post("/api/order/create",
                                {
                                    usePoints: true,
                                    customerId: customer?.id,
                                    shopId: shop.id,
                                    orderRequestItemList: newOrderItemList
                                })
                                .then(() => {
                                    setCustomerCode("");
                                    setNewOrderItemList([]);
                                    toast.current.show({
                                        severity: 'secondary',
                                        detail: "Заказ создан",
                                        closable: false,
                                        life: 2000
                                    });
                                    setTimeout(() => queryClient.invalidateQueries(
                                        {queryKey: ['order']}), 1000);
                                }).catch((e) =>  {
                                toast.current.show({
                                    severity: 'warning',
                                    detail: `Ошибка: ${e}`,
                                    closable: false,
                                    life: 3000
                                });
                            });
                        }}
                        label={"Создать: " + totalPrice + "Р"}
                        className="finish">
                    </Button>
                </div>
            </div>

            <Toast className="toast" ref={toast} position='center'/>
        </div>
    )
}