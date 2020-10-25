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
require("regenerator-runtime");

/**
 * This class provides methods to execute actions on DOM elements
 */
class DomInteractionExecutor {

    /**
     * Enables a DOM element, if it is a dropzone user can drop files, else if it is a button, user can click on it
     * @param htmlId {string} id of the DOM element to enable
     */
    static enableElement(htmlId) {
        document.getElementById(htmlId).removeAttribute("disabled");
    }

    /**
     * Disables a DOM element, if it is a dropzone user can no longer drop files, else if it is a button, user can no
     * longer click on it
     * @param htmlId {string} id of the DOM element to disable
     */
    static disableElement(htmlId) {
        document.getElementById(htmlId).setAttribute("disabled", "true");
    }

    /**
     * Hides a DOM element
     * @param htmlId {string} id of the DOM element to hide
     */
    static hideItem(htmlId) {
        document.getElementById(htmlId).style.visibility = "hidden";
    }

    /**
     * Shows a DOM element
     * @param htmlId {string} id of the DOM element to hide
     */
    static showItem(htmlId) {
        document.getElementById(htmlId).style.visibility = "visible";
    }

    /**
     * Returns true if DOM element has an attribute, else returns false
     * @param htmlId {string}      id of the DOM element
     * @param attribute {string}   attribute of the DOM element
     * @returns {boolean} true if DOM element has an attribute, else returns false
     */
    static hasAttribute(htmlId, attribute) {
        return document.getElementById(htmlId).hasAttribute(attribute);
    }

    /**
     * Displays Json file as a 2*n table
     * @param jsonData {JSON}        data of the .json file
     * @param htmlDocumentId {string}  DOM element id
     */
    static displayJsonData(jsonData, htmlDocumentId) {
        ReactDOM.render(
            <JsonRenderer data={jsonData} htmlId={htmlDocumentId}/>,
            document.getElementById(htmlDocumentId)
        );
    }
}

export default DomInteractionExecutor;
