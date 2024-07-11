import {createRoot} from "react-dom/client";

import './style.less'
import React, {useRef, useState} from "react";
import {QueryClient, QueryClientProvider} from "react-query";
import {Toast} from "primereact/toast";

createRoot(document.getElementById("root")).render(
    <QueryClientProvider client={new QueryClient()}>
        <App/>
    </QueryClientProvider>
);

const products = [
    {id: "coffee", name: "Кофе", price: 200},
    {id: "tea", name: "Чай", price: 150},
    {id: "tea1", name: "Чай 1", price: 150},
    {id: "tea2", name: "Чай 2", price: 150},
    {id: "tea3", name: "Чай 3", price: 150}
]

export function App() {
    const toast = useRef(null);

    const [orders, setOrders] = useState([
        {id: 0, time: "12:00:00", text: "Заказ №1"},
        {id: 1, time: "12:00:00", text: "Заказ №2"},
        {id: 2, time: "12:00:00", text: "Заказ №3"},
        {id: 3, time: "12:00:00", text: "Заказ №4"},
        {id: 4, time: "12:00:00", text: "Заказ №5"},
    ]);
    const [messages, setMessages] = useState([
        {id: 0, time: "12:00:00", text: "Добавлен заказ №1"},
        {id: 1, time: "12:00:00", text: "Добавлен заказ №2"},
        {id: 2, time: "12:00:00", text: "Добавлен заказ №3"},
        {id: 3, time: "12:00:00", text: "Добавлен заказ №4"},
        {id: 4, time: "12:00:00", text: "Добавлен заказ №5"},
    ]);
    const [status, setStatus] = useState("OK");

    return (
        <div className="gh-container">
            <div className="gh-control">
                <div className="gh-orders">
                    {orders.map(o => <div className="order">{o.time} {o.text}</div>)}
                </div>
                <div className="gh-neworder">
                    <div className="result">
                        Заказ №1
                        Кофе
                        Кофе
                    </div>
                    <div className="gh-products">
                        {products.map(p => <div className="product">{p.name} {p.price}</div>)}
                    </div>
                    <div className="finish">
                        Создать
                    </div>
                </div>
            </div>
            <div className="gh-messages">
                <div className="list">
                    {messages.map(m => <div className="message">{m.time}: {m.text}</div>)}
                </div>
                <div className="gh-status">
                    <div className="message">Статус: {status}</div>
                </div>
            </div>

            <Toast className="toast" ref={toast} position='center'/>
        </div>
    )
}