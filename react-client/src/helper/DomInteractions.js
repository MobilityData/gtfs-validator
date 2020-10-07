/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import ReactDOM from "react-dom";
import JsonRenderer from "../components/JsonRenderer";
import React from "react";

export function enableElement(htmlId) {
    document.getElementById(htmlId).removeAttribute("disabled");
}

export function disableElement(htmlId) {
    document.getElementById(htmlId).setAttribute("disabled", "true");
}

export function hideItem(htmlId) {
    document.getElementById(htmlId).style.visibility = "hidden";
}

export function showItem(htmlId) {
    document.getElementById(htmlId).style.visibility = "visible";
}

export function hasAttribute(htmlId, attribute) {
    return document.getElementById(htmlId).hasAttribute(attribute);
}

export function displayJsonData(jsonData, htmlDocumentId) {
    ReactDOM.render(
        <JsonRenderer data={jsonData} htmlId={htmlDocumentId}/>,
        document.getElementById(htmlDocumentId)
    );
}
