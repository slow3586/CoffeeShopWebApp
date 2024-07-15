import {createRoot} from "react-dom/client";

import {components, paths} from "./api/api";
import './style.less'
import React, {useRef, useState} from "react";
import {QueryClient, QueryClientProvider, useQuery} from "react-query";
import {Toast} from "primereact/toast";
import axios from "axios";

createRoot(document.getElementById("root")).render(
    <QueryClientProvider client={new QueryClient()}>
        <App/>
    </QueryClientProvider>
);

export function App() {
    const toast = useRef(null);

    const productQuery = useQuery(
        ["/api/product/all"],
        async () => (await axios.get("/api/product/all")).data,
        {
            enabled: true
        });
    const shopQuery = useQuery(
        ['/api/shop/all'],
        async () => (await axios.get('/api/shop/all')).data,
        {
            enabled: true
        });
    const orderQuery = useQuery(
        ['/api/order/query'],
        async () => (await axios.get("/api/order/query")).data,
        {
            enabled: true
        });

    const shop = shopQuery?.data;
    const productList: Array<components['schemas']['Product']> = productQuery?.data ?? [];
    const orderList: Array<components['schemas']['CustomerOrder']> = orderQuery?.data ?? [];

    const [newOrder, setNewOrder] = useState<components['schemas']['OrderRequest']>({productQuantityList: []})

    return (
        <div className="gh-container">
            <div className="gh-control">
                <div className="gh-orders">
                    {orderList.map(o => <div className="order">{o.time} {o.text}</div>)}
                </div>
                <div className="gh-neworder">
                    <div className="result">
                        {newOrder.productQuantityList.map(p =>
                            <div key={p.productId}>{p.productId}</div>
                        )}
                    </div>
                    <div className="gh-products">
                        {products.map(p => <div
                            onClick={() => {
                                const orderRequestItem = new OrderRequestItem();
                                orderRequestItem.productId = p.id;
                                newOrder.productQuantityList.push(orderRequestItem);
                                setNewOrder(newOrder);
                            }}
                            className="product">{p.name} {p.price}</div>)}
                    </div>
                    <div className="finish">
                        Создать
                    </div>
                </div>
            </div>
            <div className="gh-messages">
                <div className="list">

                </div>
                <div className="gh-status">
                    <div className="message">Статус: {status}</div>
                </div>
            </div>

            <Toast className="toast" ref={toast} position='center'/>
        </div>
    )
}