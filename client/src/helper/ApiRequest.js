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

import {GetReportContentCommand, InitConfigCommand, Port, RunValidatorCommand} from "./Constants";
import CircularIndeterminate from "../components/CircularProgress";

export async function initConfig(port, execParamConfigFileAsString) {
    axios.post('http://localhost:' + Port() + InitConfigCommand(), execParamConfigFileAsString)
        .then((res) => {
            console.log(res.data)
        }).catch((error) => {
        console.log(error)
    });
}

export async function validateFeed() {
    ReactDOM.render(
        <CircularIndeterminate/>,
        document.getElementById("progress-circles"));
    document.getElementById("progress-circles").style.visibility = "visible";
    axios.get('http://localhost:' + Port() + RunValidatorCommand())
        .then((response) => {
            document.getElementById("display-result-button").style.visibility = "visible";
            document.getElementById("progress-circles").style.visibility = "hidden";
            document.getElementById("validation-status").style.visibility = "visible";
            document.getElementById("display-result-button").style.visibility = "visible";
            ReactDOM.render(
                <p>{response.data}</p>,
                document.getElementById("validation-status"));
        }).catch((error) => {
        console.log(error)
    });
}

export async function displayValidationReport() {
    axios.get('http://localhost:' + Port() + GetReportContentCommand())
        .then((res) => {
            console.log("report opened in text edit");
        }).catch((error) => {
        console.log(error)
    });
}
