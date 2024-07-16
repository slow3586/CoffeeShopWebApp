import {createRoot} from "react-dom/client";

import React, {useState} from "react";
import {QueryClient, QueryClientProvider} from "react-query";
import {InputText} from "primereact/inputtext";
import axios from "axios";
import {Button} from "primereact/button";

import './style.less'

createRoot(document.getElementById("root")).render(
    <QueryClientProvider client={new QueryClient()}>
        <AppLogin/>
    </QueryClientProvider>
);

export function AppLogin() {
    const [login, setLogin] = useState("");
    const [password, setPassword] = useState("");
    const [code, setCode] = useState("");

    return <div>
        <InputText value={login} onChange={(e) => setLogin(e.target.value)}></InputText>
        <InputText value={password} onChange={(e) => setPassword(e.target.value)}></InputText>
        <InputText value={code} onChange={(e) => setCode(e.target.value)}></InputText>
        <Button onClick={async () => {
            const axiosResponse = await axios.post("api/login", {
                login,
                password,
                code
            });
        }}></Button>
    </div>
}