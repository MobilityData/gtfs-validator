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
import JsonBeautyfier from "../components/JsonBeautyfier";
import React from "react";
import {GetReportContentCommand, InitConfigCommand, Port, RunValidatorCommand} from "./Constants";

export async function initConfig(port, execParamConfigFileAsString) {
    await fetch('http://localhost:' + Port() + InitConfigCommand() + execParamConfigFileAsString);
}

export async function validateFeed() {
    let response = await fetch('http://localhost:' + Port() + RunValidatorCommand());
    let body = await response.text();
    ReactDOM.render(
        <p>{body}</p>,
        document.getElementById("validation-status"))
    let reportResponse = await fetch('http://localhost:' + Port() + GetReportContentCommand());
    let reportBody = await reportResponse.text();
    ReactDOM.render(
        <JsonBeautyfier data={JSON.parse(reportBody)}/>,
        document.getElementById("report"))
}
