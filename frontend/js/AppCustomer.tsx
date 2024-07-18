import React, {useRef, useState} from "react";
import {Dialog} from "primereact/dialog";
import {Toast} from "primereact/toast";
import {QueryClient, QueryClientProvider, useQuery} from "react-query";
import axios from "axios";

import './style.less';
import {createRoot} from "react-dom/client";

createRoot(document.getElementById("root")).render(
    <QueryClientProvider client={new QueryClient()}>
        <AppCustomer/>
    </QueryClientProvider>
);

export function AppCustomer() {
    const toast = useRef(null);
    const dialog = useRef(null);

    const productsQuery = useQuery(
        ['/api/product/findAll/'],
        async () => (await axios.get('/api/product/findAll/')).data,
        {
            enabled: true
        });

    const [trail, setTrail] = useState([]);
    const [cart, setCart] = useState([]);
    const [showCart, setShowCart] = useState(false);

    if (showCart && cart.length == 0) {
        setShowCart(false);
    }

    const currentBranchId = trail.length > 0 ? trail[trail.length - 1] : null;
    const currentBranch = branches.find(v => v.id === currentBranchId) ?? branches[0];

    const isLong = currentBranch.children.length > 3;

    return (
        <div className="gh-container">
            <div className="gh-bar">
                <img src="logo.svg" alt=""></img>
                <div>Сеть кофеен</div>
            </div>
            <div className="gh-trail">
                {trail.length == 0 && <div
                    className="but but-msg">
                    {cart.length == 0 ? "Я хочу..." : "А ещё..."}
                </div>}
                {trail.map((item, i) => {
                    return <div
                        className="but"
                        key={"gh_trail" + i}
                        onClick={() => {
                            setTrail(trail.slice(0, i));
                        }}>
                        <div className="txt">{branches.find(i => i.id == item).name}</div>
                        <div className="icon pi pi-times"></div>
                    </div>
                })}
            </div>
            <div className={"gh-select" + (isLong == true ? " gh-select-long" : "")}>
                {currentBranch.children.map((childId, i) => {
                        const buttonBranch = branches.find(t => t.id == childId);
                        if (buttonBranch !== undefined) {
                            const backgroundImage = buttonBranch.background != undefined ? "url(\"" + buttonBranch.background + "\")" : "none";
                            return <div
                                key={currentBranch.id + "_" + childId}
                                style={{
                                    backgroundImage,
                                    borderColor: buttonBranch.borderColor,
                                    boxShadow:
                                        "rgba(0, 0, 0, 0.4) 0px 0px 7px 1px, " +
                                        "rgba(0, 0, 0, 0.4) 7px 7px 7px 1px, " +
                                        "inset " + buttonBranch.shadowColor + " 0px 0px 2px 0px",
                                }}
                                onClick={() => {
                                    if (buttonBranch.children != null && buttonBranch.children.length > 0) {
                                        setTrail([...trail, childId]);
                                        return;
                                    }

                                    if (buttonBranch.price > 0) {
                                        if (cart.length > 6) {
                                            toast.current.show({
                                                severity: 'warning',
                                                summary: 'Ой!',
                                                detail: "Слишком большой заказ!",
                                                closable: false,
                                                life: 3000
                                            });
                                            setTimeout(() => toast.current.clear(), 3000)
                                        } else {
                                            setCart([
                                                ...cart,
                                                {
                                                    ...buttonBranch,
                                                    __id: crypto.randomUUID()
                                                }
                                            ]);
                                            toast.current.show({
                                                severity: 'secondary',
                                                summary: 'Ура!',
                                                detail: buttonBranch.name + " добавлен в корзину!",
                                                closable: false,
                                                life: 2000
                                            });
                                            setTimeout(() => toast.current.clear(), 2000)
                                            setTrail([]);
                                        }
                                    }
                                }}
                                className="btn-branch">
                                <img src={buttonBranch.image} className="img" alt=""/>
                                <div className="desc">
                                    <div className="name">{buttonBranch.name}</div>
                                    <div className="text">{buttonBranch.text}</div>
                                </div>
                            </div>;
                        }
                        return;
                    }
                )}
            </div>
            {cart.length > 0 &&
                <div className="gh-checkout"
                     onClick={() => setShowCart(true)}>
                    <div className="content">
                        {[...cart].reverse().slice(0, 2).map((item, i) => {
                            return <div className="item">
                                <div className="name">{item.name}</div>
                                <div className="price">{item.price}Р</div>
                            </div>
                        })}
                        {cart.length > 2 && <div className="item more">{"Ещё " + (cart.length - 2) + " шт."}</div>}
                    </div>
                    <div className="but">
                        <div className="label">{cart.map(item => item.price).reduce((a, b) => a + b)}Р</div>
                        <div className="icon pi pi-shopping-cart"></div>
                    </div>
                </div>
            }
            <Dialog header="Корзина"
                    visible={showCart}
                    className="gh-checkout-dialog"
                    draggable={false}
                    resizable={false}
                    ref={dialog}
                    onHide={() => {
                        if (!showCart) return;
                        setShowCart(false);
                    }}>
                <div className="content">
                    <div className="items">
                        {cart.map((item, i) => <div
                            onClick={() => {
                                setCart(cart.filter(c => c.__id !== item.__id));
                            }}
                            className="item">
                            <p>{(i + 1) + ") " + item.name}</p>
                            <div className="right">{item.price + "Р"}<div className="icon pi pi-times"></div></div>
                        </div>)}
                    </div>
                    <div className="footer"
                        onClick={() => {alert("Спасибо за тестирование!")}}>
                        <div>Оплатить через QR-код</div>
                    </div>
                </div>
            </Dialog>
            <Toast className="toast" ref={toast} position='center'/>
        </div>
    )
}