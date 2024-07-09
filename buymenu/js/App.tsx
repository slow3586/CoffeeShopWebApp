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
        text: "Восхитительный аромат свежесваренного кофе и нежное тепло чая подарят вам моменты истинного наслаждения и комфорта.",
        children: ["type_coffee", "type_tea", "type_cacao", "type_hot_chocolate"],
        image: "img3.webp",
        background: "10197130_4346596.jpg",
        borderColor: "#854e36",
        shadowColor: "rgb(147 106 106)"
    },
    {
        id: "type_cold",
        name: "Холодный",
        text: "От прохладных лимонадов до ледяного кофе — у нас найдется что-то для каждого, чтобы утолить жажду и поднять настроение.",
        children: ["type_ice_cream_latte", "type_milkshake"],
        image: "img3.webp",
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
    },
    {
        id: "type_coffee",
        name: "Кофе",
        children: ["coffee_cappucino", "coffee_latte_makiato"],
        text: "От классического эспрессо до нежного латте — у нас найдётся идеальный напиток для каждого любителя кофе.",
        image: "img3.webp",
        background: "15628337_5651878.jpg",
        borderColor: "rgb(111,73,23)",
        shadowColor: "rgb(126 84 29)"
    },
    {
        id: "type_tea",
        name: "Чай",
        image: "img3.webp",
        text: "От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["tea_english_breakfast", "tea_earl_grey"],
        background: "15628337_5651878.jpg",
        borderColor: "rgb(67,43,11)",
        shadowColor: "rgb(52,33,8)"
    },
    {
        id: "type_cacao",
        name: "Какао",
        image: "img3.webp",
        text: "От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["cacao_classic", "cacao_caramel"],
        background: "15628337_5651878.jpg",
        borderColor: "rgb(172,119,48)",
        shadowColor: "rgb(156,102,31)"
    },
    {
        id: "type_hot_chocolate",
        name: "Горячий Шоколад",
        image: "img3.webp",
        text: "От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["hot_chocolate_200"],
        background: "15628337_5651878.jpg",
        borderColor: "rgb(172,119,48)",
        shadowColor: "rgb(156,102,31)"
    },
    {
        id: "hot_chocolate_200",
        name: "Горячий Шоколад 200мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "type_ice_cream_latte",
        name: "Айс Крим Латте",
        image: "img3.webp",
        text: "Сочетание крепкого эспрессо и сливочного мороженого создаёт идеальный напиток, который подарит вам прохладу и наслаждение в каждом глотке.",
        children: ["ice_cream_latte_classic", "ice_cream_latte_caramel"],
        background: "15628337_5651878.jpg",
        borderColor: "rgb(207,200,200)",
        shadowColor: "rgb(249,223,212)"
    },
    {
        id: "type_milkshake",
        name: "Милкшейк",
        image: "img3.webp",
        text: "Свежее молоко и натуральное мороженое обещают тебе непередаваемое удовольствие от каждого глотка благодаря насыщенному вкусу.",
        children: ["milkshake_classic", "milkshake_caramel", "milkshake_test"],
        background: "15628337_5651878.jpg",
        borderColor: "rgb(255 255 255)",
        shadowColor: "rgb(220,219,219)"
    },
    {
        id: "coffee_cappucino",
        name: "Капучино",
        image: "img3.webp",
        text: "Каждый глоток этого классического итальянского напитка подарит вам уют и наслаждение, создавая гармонию вкусов и ароматов.",
        contains: "эспрессо / молоко (147 / 174 ккал: белки 7,7 / жиры 7,8 / углеводы 11,3 | белки 8,2 / жиры 10,7 / углеводы 13,2)",
        children: ["coffee_cappucino_350", "coffee_cappucino_450"],
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
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
        text: "От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["coffee_latte_makiato_350", "coffee_latte_makiato_450"],
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
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
        text: "От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["tea_english_breakfast_350", "tea_english_breakfast_450"],
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
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
        text: "От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["tea_earl_grey_350", "tea_earl_grey_450"],
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
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
        text: "От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["cacao_classic_350", "cacao_classic_450"],
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
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
        text: "От классических черных и зеленых сортов до ароматных травяных сборов — каждый глоток принесет вам наслаждение и умиротворение.",
        children: ["cacao_caramel_350", "cacao_caramel_450"],
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
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
        text: "Это идеальное сочетание охлажденного эспрессо и сливочного мороженого подарит вам неповторимое наслаждение и заряд бодрости.",
        children: ["ice_cream_latte_classic_350", "ice_cream_latte_classic_450"],
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
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
        text: "Этот изысканный напиток сочетает в себе насыщенный эспрессо, сливочное мороженое и тонкую солоновато-сладкую карамель, создавая непревзойденное наслаждение.",
        children: ["ice_cream_latte_caramel_350", "ice_cream_latte_caramel_450"],
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
    },
    {
        id: "ice_cream_latte_caramel_350",
        name: "Айс Крим Латте Солёная Карамель 350мл",
        image: "img3.webp",
        price: 350
    },
    {
        id: "ice_cream_latte_caramel_450",
        name: "Айс Крим Латте Солёная Карамель 450мл",
        image: "img3.webp",
        price: 450
    },
    {
        id: "milkshake_classic",
        name: "Классический",
        image: "img3.webp",
        text: "Волшебное сочетание свежего молока и насыщенного натурального мороженого, которое дарует напитку неповторимую кремовую гладкость и богатый, непередаваемый вкус.",
        children: ["milkshake_classic_350", "milkshake_classic_450"],
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
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
        text: "Этот восхитительный напиток сочетает в себе сладость и лёгкую солёность, создавая уникальное вкусовое удовольствие, которое вы не забудете.",
        children: ["milkshake_caramel_350", "milkshake_caramel_450"],
        background: "15628337_5651878.jpg",
        borderColor: "#5095a3",
        shadowColor: "#4e78a1"
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
    const dialog = useRef(null);

    const [trail, setTrail] = useState([]);
    const [checkout, setCheckout] = useState([]);
    const [showCheckout, setShowCheckout] = useState(false);

    if (showCheckout && checkout.length == 0) {
        setShowCheckout(false);
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
                                        if (checkout.length > 6) {
                                            toast.current.show({
                                                severity: 'warning',
                                                summary: 'Ой!',
                                                detail: "Слишком большой заказ!",
                                                closable: false,
                                                life: 3000
                                            });
                                            setTimeout(() => toast.current.clear(), 3000)
                                        } else {
                                            setCheckout([
                                                ...checkout,
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
            {checkout.length > 0 &&
                <div className="gh-checkout"
                     onClick={() => setShowCheckout(true)}>
                    <div className="content">
                        {[...checkout].reverse().slice(0, 2).map((item, i) => {
                            return <div className="item">
                                <div className="name">{item.name}</div>
                                <div className="price">{item.price}Р</div>
                            </div>
                        })}
                        {checkout.length > 2 && <div className="item more">{"Ещё " + (checkout.length - 2) + " шт."}</div>}
                    </div>
                    <div className="but">
                        <div className="label">{checkout.map(item => item.price).reduce((a, b) => a + b)}Р</div>
                        <div className="icon pi pi-shopping-cart"></div>
                    </div>
                </div>
            }
            <Dialog header="Корзина"
                    visible={showCheckout}
                    className="gh-checkout-dialog"
                    draggable={false}
                    resizable={false}
                    ref={dialog}
                    onHide={() => {
                        if (!showCheckout) return;
                        setShowCheckout(false);
                    }}>
                <div className="content">
                    <div className="items">
                        {checkout.map((item, i) => <div
                            onClick={() => {
                                setCheckout(checkout.filter(c => c.__id !== item.__id));
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