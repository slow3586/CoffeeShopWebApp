import {createRoot} from "react-dom/client";

import './style.less'
import React from "react";
import {QueryClient, QueryClientProvider} from "react-query";
import {App} from "./App";

createRoot(document.getElementById("root")).render(
    <QueryClientProvider client={new QueryClient()}>
        <App/>
    </QueryClientProvider>
);

