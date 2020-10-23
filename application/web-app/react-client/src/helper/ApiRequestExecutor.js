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
import React from "react";
import axios from 'axios';

import {OpenReportContentCommand, Port, RunValidatorCommand} from "./Constants";
import CircularIndeterminate from "../components/CircularProgress";
import DomInteractionExecutor from "./DomInteractionsExecutor";
require("regenerator-runtime");

/**
 * This class defines methods to send API requests
 */
class ApiRequestExecutor {
    /**
     * Sends a request to server to initialize gtfs-validator DefaultConfig
     * @param axios {AxiosStatic}                   promise based HTTP client for the browser and node.js
     * @param port {string}                         port to navigate to
     * @param initConfigCommand {string}            constant that defines the command to
     * @param execParamConfigFileAsString {string}  content of the execution parameters file provided in the dropzone,
     * as String
     * @returns {Promise<void>}
     */
    static async initializeConfig(axios, port, initConfigCommand, execParamConfigFileAsString) {
        axios.post('http://localhost:' + port + initConfigCommand, execParamConfigFileAsString)
            .then((res) => {
                return res.data;
            }).catch((error) => {
                console.log(error)
        });
    }

    /**
     * Sends a request to server to validate GTFS feed
     * @returns {Promise<void>}
     */
    static async runValidator() {
        ReactDOM.render(
            <CircularIndeterminate/>,
            document.getElementById("progress-circles"));
        DomInteractionExecutor.showItem("progress-circles");
        DomInteractionExecutor.disableElement("validate-button");
        DomInteractionExecutor.disableElement("json-config-file");
            axios.get('http://localhost:' + Port() + RunValidatorCommand())
            .then((response) => {
                DomInteractionExecutor.hideItem("progress-circles");
                DomInteractionExecutor.showItem("display-result-button");
                DomInteractionExecutor.showItem("validation-status");
                DomInteractionExecutor.showItem("display-result-button");
                ReactDOM.render(
                    <p>{response.data}</p>,
                    document.getElementById("validation-status")
                );
                DomInteractionExecutor.enableElement("json-config-file")
            }).catch((error) => {
            console.log(error)
        });
    }

    /**
     * Sends a request to server to display the validation report in the default text editor of user
     * @returns {Promise<void>}
     */
    static async openReport() {
        axios.get('http://localhost:' + Port() + OpenReportContentCommand())
            .then((res) => {
                ReactDOM.render(
                    res.data,
                    document.getElementById("open-validation-dom-element"));
                DomInteractionExecutor.showItem("open-validation-dom-element");
            }).catch((error) => {
            console.log(error)
        });
    }
}

export default ApiRequestExecutor;
