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

import React from "react";
import ReactJson from 'react-json-view'

/**
 * A component to view the content of the validation report
 * @param id {string} id of the DOM element to be rendered
 * @param jsonData {JSON}  JSON data to be formatted
 * @param theme {string} theme of component
 * @returns {JSX.Element}
 * @constructor
 */
const ValidationReportViewer = ({id, jsonData, theme}) => {
    return (
        <ReactJson
            id={id}
            src={jsonData}
            theme={theme}
            indentWidth={8}
            collapsed={2}
            iconStyle="triangle"
            displayDataTypes={false}/>
    );
};

export default ValidationReportViewer;
