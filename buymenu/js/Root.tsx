import {createRoot} from "react-dom/client";
import {App} from "./App";

import './style.less'
import React from "react";
import {QueryClient, QueryClientProvider} from "react-query";

createRoot(document.getElementById("root")).render(
    <QueryClientProvider client={new QueryClient()}>
        <App/>
    </QueryClientProvider>
);