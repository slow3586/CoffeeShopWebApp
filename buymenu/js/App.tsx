import {Button} from "primereact/button";
import React, {useRef, useState} from "react";
import {Card} from "primereact/card";
import {Dialog} from "primereact/dialog";
import {Toast} from "primereact/toast";

const branches = [
    {id: "start", children: ["type_hot", "type_cold"]},
    {
        id: "type_hot",
        name: "Горячий",
        text: "Погрузитесь в уют с нашими горячими напитками! Восхитительный аромат свежесваренного кофе и нежное тепло чая подарят вам моменты истинного наслаждения и комфорта.",
        children: ["type_coffee", "type_tea", "type_cacao"],
        background: "10197130_4346596.jpg",
        image: "img3.webp",
        borderColor: "#854e36",
        shadowColor: "rgb(147 106 106)"
    },
    {
        id: "type_cold",
        name: "Холодный",
        text: "Освежитесь в жаркий день с нашими холодными напитками! От прохладных лимонадов до ледяного кофе — у нас найдется что-то для каждого, чтобы утолить жажду и поднять настроение.",
        children: ["type_ice_cream_latte", "type_milkshake"],
        background: "15628337_5651878.jpg",
        image: "img3.webp",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
    },
    {
        id: "type_coffee",
        name: "Кофе",
        children: ["coffee_cappucino", "coffee_latte_makiato"],
        text: "Погрузитесь в мир ароматного кофе, который подарит вам заряд бодрости и вдохновения. От классического эспрессо до нежного латте — у нас найдётся идеальный напиток для каждого любителя кофе.",
        image: "img3.webp"
    },
    {
        id: "type_tea",
        name: "Чай",
        image: "img3.webp",
        text: "Откройте для себя разнообразие вкусов с нашими изысканными чаями! От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["tea_english_breakfast", "tea_earl_grey"]
    },
    {
        id: "type_cacao",
        name: "Какао",
        image: "img3.webp",
        text: "Откройте для себя разнообразие вкусов с нашими изысканными чаями! От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["cacao_classic", "cacao_caramel"]
    },
    {
        id: "type_ice_cream_latte",
        name: "Айс Крим Латте",
        image: "img3.webp",
        text: "Откройте для себя наш освежающий айс крим латте! Сочетание крепкого эспрессо и сливочного мороженого создаёт идеальный напиток, который подарит вам прохладу и наслаждение в каждом глотке.",
        children: ["ice_cream_latte_classic", "ice_cream_latte_caramel"]
    },
    {
        id: "type_milkshake",
        name: "Милкшейк",
        image: "img3.webp",
        text: "Попробуйте наш восхитительный молочный коктейль! Приготовленный из свежего молока и натурального мороженого, он подарит вам истинное наслаждение и сладкое удовольствие в каждом глотке.",
        children: ["milkshake_classic", "milkshake_caramel", "milkshake_test"]
    },
    {
        id: "coffee_cappucino",
        name: "Капучино",
        image: "img3.webp",
        text: "Идеальное сочетание крепкого эспрессо и нежной пенки из взбитого молока. Каждый глоток этого классического итальянского напитка подарит вам уют и наслаждение, создавая гармонию вкусов и ароматов.",
        contains: "эспрессо / молоко (147 / 174 ккал: белки 7,7 / жиры 7,8 / углеводы 11,3 | белки 8,2 / жиры 10,7 / углеводы 13,2)",
        children: ["coffee_cappucino_350", "coffee_cappucino_450"]
    },
    {
        id: "coffee_cappucino_350",
        name: "Капучино 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "coffee_cappucino_450",
        name: "Капучино 450мл",
        image: "img3.webp",
        price: 450
    },
    {
        id: "coffee_latte_makiato",
        name: "Латте Макиато",
        image: "img3.webp",
        text: "Откройте для себя разнообразие вкусов с нашими изысканными чаями! От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["coffee_latte_makiato_350", "coffee_latte_makiato_450"]
    },
    {
        id: "coffee_latte_makiato_350",
        name: "Латте Макиато 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "coffee_latte_makiato_450",
        name: "Латте Макиато 450мл",
        image: "img3.webp",
        price: 450
    },
    {
        id: "tea_english_breakfast",
        name: "Английский Завтрак",
        image: "img3.webp",
        text: "Откройте для себя разнообразие вкусов с нашими изысканными чаями! От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["tea_english_breakfast_350", "tea_english_breakfast_450"]
    },
    {
        id: "tea_english_breakfast_350",
        name: "Английский Завтрак 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "tea_english_breakfast_450",
        name: "Английский Завтрак 450мл",
        image: "img3.webp",
        price: 450
    },
    {
        id: "tea_earl_grey",
        name: "Чарльз Эрл Грей",
        image: "img3.webp",
        text: "Откройте для себя разнообразие вкусов с нашими изысканными чаями! От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["tea_earl_grey_350", "tea_earl_grey_450"]
    },
    {
        id: "tea_earl_grey_350",
        name: "Чарльз Эрл Грей 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "tea_earl_grey_450",
        name: "Чарльз Эрл Грей 450мл",
        image: "img3.webp",
        price: 450
    },
    {
        id: "cacao_classic",
        name: "Классический",
        image: "img3.webp",
        text: "Откройте для себя разнообразие вкусов с нашими изысканными чаями! От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["cacao_classic_350", "cacao_classic_450"]
    },
    {
        id: "cacao_classic_350",
        name: "Какао Классический 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "cacao_classic_450",
        name: "Какао Классический 450мл",
        image: "img3.webp",
        price: 450
    },
    {
        id: "cacao_caramel",
        name: "Солёная Карамель",
        image: "img3.webp",
        text: "Откройте для себя разнообразие вкусов с нашими изысканными чаями! От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["cacao_caramel_350", "cacao_caramel_450"]
    },
    {
        id: "cacao_caramel_350",
        name: "Какао Солёная Карамель 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "cacao_caramel_450",
        name: "Какао Солёная Карамель 450мл",
        image: "img3.webp",
        price: 450
    },
    {
        id: "ice_cream_latte_classic",
        name: "Классический",
        image: "img3.webp",
        text: "Освежитесь нашим классическим айс крим латте! Это идеальное сочетание охлажденного эспрессо и сливочного мороженого подарит вам неповторимое наслаждение и заряд бодрости.",
        children: ["ice_cream_latte_classic_350", "ice_cream_latte_classic_450"]
    },
    {
        id: "ice_cream_latte_classic_350",
        name: "Айс Крим Латте Классический 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "ice_cream_latte_classic_450",
        name: "Айс Крим Латте Классический 450мл",
        image: "img3.webp",
        price: 450
    },
    {
        id: "ice_cream_latte_caramel",
        name: "Солёная Карамель",
        image: "img3.webp",
        text: "Попробуйте наш айс крим латте со вкусом солёной карамели! Этот изысканный напиток сочетает в себе насыщенный эспрессо, сливочное мороженое и тонкую солоновато-сладкую карамель, создавая непревзойденное наслаждение.",
        children: ["ice_cream_latte_caramel_350", "ice_cream_latte_caramel_450"]
    },
    {
        id: "ice_cream_latte_caramel_350",
        name: "Айс Крим Латте Классический 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "ice_cream_latte_caramel_450",
        name: "Айс Крим Латте Классический 450мл",
        image: "img3.webp",
        price: 450
    },
    {
        id: "milkshake_classic",
        name: "Классический",
        image: "img3.webp",
        text: "Насладитесь нежным и насыщенным вкусом нашего классического молочного коктейля! Этот любимый напиток, приготовленный из свежего молока и натурального мороженого, подарит вам истинное удовольствие и мгновения радости.",
        children: ["milkshake_classic_350", "milkshake_classic_450"]
    },
    {
        id: "milkshake_classic_350",
        name: "Милкшейк Классический 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "milkshake_classic_450",
        name: "Милкшейк Классический 450мл",
        image: "img3.webp",
        price: 450
    },
    {
        id: "milkshake_caramel",
        name: "Солёная Карамель",
        image: "img3.webp",
        text: "Побалуйте себя нашим молочным коктейлем со вкусом солёной карамели! Этот восхитительный напиток сочетает в себе сладость и лёгкую солёность, создавая уникальное вкусовое удовольствие, которое вы не забудете.",
        children: ["milkshake_caramel_350", "milkshake_caramel_450"]
    },
    {
        id: "milkshake_caramel_350",
        name: "Милкшейк Солёная Карамель 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "milkshake_caramel_450",
        name: "Милкшейк Солёная Карамель 450мл",
        image: "img3.webp",
        price: 450
    },
]

export function App() {
    const toast = useRef(null);

    const [trail, setTrail] = useState([]);
    const [checkout, setCheckout] = useState([]);
    const [showCheckout, setShowCheckout] = useState(false);

    const currentBranchId = trail.length > 0 ? trail[trail.length - 1] : null;
    const currentBranch = branches.find(v => v.id === currentBranchId) ?? branches[0];

    return (
        <div className="gh-container">
            <Toast className="toast" ref={toast} position='center'/>
            <div className="gh-toppart"
                 style={{
                     height: checkout.length == 0 ? "100%" : ""
                 }}>
                <div className="gh-bar">
                    <img src="logo.svg" alt=""></img>
                    <div>Сеть кофеен</div>
                </div>
                <div className="gh-trail">
                    {trail.length == 0 && <div
                        className="but">
                        {checkout.length == 0 ? "Я хочу..." : "А ещё..."}
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
                <div className="gh-select">
                    {currentBranch.children.map((childId, i) => {
                            const buttonBranch = branches.find(t => t.id == childId);
                            if (buttonBranch !== undefined) {
                                const backgroundImage = buttonBranch.background != undefined ? "url(\"" + buttonBranch.background + "\")" : "none";
                                return <div
                                    key={currentBranch.id + "_" + childId}
                                    style={{
                                        backgroundImage,
                                        borderColor: buttonBranch.borderColor,
                                        boxShadow: "0px 0px 5px 5px " + buttonBranch.shadowColor,
                                    }}
                                    onClick={() => {
                                        if (buttonBranch.children != null && buttonBranch.children.length > 0) {
                                            setTrail([...trail, childId]);
                                            return;
                                        }

                                        if (buttonBranch.price > 0) {
                                            setCheckout([...checkout, childId]);
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
            </div>
            {checkout.length > 0 &&
                <div className="gh-checkout"
                     onClick={() => setShowCheckout(true)}>
                    <div className="content">
                        <div className="item">
                            <div className="name">Капучино</div>
                            <div className="price">1 x 200р</div>
                        </div>
                        <div className="item">
                            <div className="name">Капучино</div>
                            <div className="price">1 x 200р</div>
                        </div>
                        <div className="item">Ещё 1шт...</div>
                    </div>
                    <div className="but">
                        <div className="label">500р</div>
                        <div className="icon pi pi-shopping-cart"></div>
                    </div>
                </div>
            }
            <Dialog header="Корзина"
                    visible={showCheckout}
                    className="gh-checkout-dialog"
                    draggable={false}
                    resizable={false}
                    onHide={() => {
                        if (!showCheckout) return;
                        setShowCheckout(false);
                    }}>
                <div className="content">
                    <div className="items">
                        <div className="item">Кофе №1</div>
                        <div className="item">Кофе №2</div>
                        <div className="item">Кофе №3</div>
                        <div className="item">Кофе №4</div>
                        <div className="item">Кофе №5</div>
                    </div>
                    <div className="footer">
                        <div>Оплатить через QR-код</div>
                    </div>
                </div>
            </Dialog>
        </div>
    )
}