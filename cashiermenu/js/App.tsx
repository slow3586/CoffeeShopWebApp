import React, {useRef, useState} from "react";
import {useQuery} from "react-query";
import axios from "axios";
import {Nullable} from "primereact/ts-helpers";
import {Button} from "primereact/button";
import {DataTable} from "primereact/datatable";
import {Column} from "primereact/column";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Calendar} from "primereact/calendar";
import {InputTextarea} from "primereact/inputtextarea";

class Promo {
    id: number;
    name: string;
    code: string;
    startsAt: Date;
    endsAt: Date;
    text: string;
}

export function App() {
    const toast = useRef(null);

    const promosQuery = useQuery(
        ['getFrontendConfig'],
        async () => (await axios.get("/api/promo")).data,
        {
            staleTime: Infinity,
            enabled: true
        });

    const promos = promosQuery?.data?.configs ?? [];
    const [showDialog, setShowDialog] = useState(false);

    const [newPromo, setNewPromo] = useState<Nullable<Promo>>(new Promo());

    const header = (
        <div className="flex flex-wrap align-items-center justify-content-between gap-2">
            <span className="text-xl text-900 font-bold">Акции</span>
            <Button onClick={() => setShowDialog(true)} icon="pi pi-plus" rounded raised/>
        </div>
    );

    return (
        <div className="card">
            <DataTable value={promos} header={header} footer={() => promos.length} tableStyle={{minWidth: '60rem'}}>
                <Column field="id" header="Ключ" body={(promo: Promo) => promo.id}></Column>
                <Column field="text" header="Название" body={(promo: Promo) => promo.name}></Column>
                <Column field="text" header="Промокод" body={(promo: Promo) => promo.code}></Column>
                <Column field="text" header="Текст" body={(promo: Promo) => promo.text}></Column>
                <Column field="text" header="Начинается" body={(promo: Promo) => promo.startsAt.toDateString()}></Column>
                <Column field="text" header="Кончается" body={(promo: Promo) => promo.endsAt.toDateString()}></Column>
            </DataTable>
            <Dialog header="Создать"
                    visible={showDialog}
                    className="gh-dialog"
                    draggable={false}
                    resizable={false}
                    footer={<div>
                        <Button
                            onClick={() => axios.post("/api/promo/save", newPromo)}
                            icon="pi pi-plus" rounded raised/>
                    </div>}
                    onHide={() => {
                        setShowDialog(false);
                    }}>
                <div className="content">
                    <div className="flex-auto">
                        <label className="font-bold block mb-2">Название</label>
                        <InputText value={newPromo.text} onChange={(e) => {
                            newPromo.name = e.target.value;
                            setNewPromo(newPromo);
                        }}/>
                    </div>
                    <div className="flex-auto">
                        <label className="font-bold block mb-2">Начало</label>
                        <Calendar value={newPromo.startsAt} onChange={(e: { value: Date; }) => {
                            newPromo.startsAt = e.value;
                            setNewPromo(newPromo);
                        }}/>
                    </div>
                    <div className="flex-auto">
                        <label className="font-bold block mb-2">Конец</label>
                        <Calendar
                            value={newPromo.endsAt}
                            onChange={(e: { value: Date; }) => {
                                newPromo.endsAt = e.value;
                                setNewPromo(newPromo);
                            }}/>
                    </div>
                    <div className="flex-auto">
                        <label className="font-bold block mb-2">Промокод</label>
                        <InputText
                            value={newPromo.code}
                            onChange={(e) => {
                                newPromo.code = e.target.value;
                                setNewPromo(newPromo);
                            }}/>
                    </div>
                    <div className="flex-auto">
                        <label className="font-bold block mb-2">Текст</label>
                        <InputTextarea
                            value={newPromo.text}
                            rows={5}
                            cols={30}
                            onChange={(e) => {
                                newPromo.text = e.target.value;
                                setNewPromo(newPromo);
                            }}/>
                    </div>
                </div>
            </Dialog>
        </div>
    );
}