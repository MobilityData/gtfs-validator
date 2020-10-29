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

import React from 'react';

/**
 * Table in which the execution parameters from the configuration file are displayed
 * @param data {JSON} the JSON data to format as a table
 * @returns {JSX.Element} the DOM element to be rendered
 * @constructor
 */
const JsonRenderer = ({data}) => {

    return (
        <div>
            <table id="json-table">
                <th>Execution parameter</th>
                <th>Value</th>
                {
                    Object.keys(data).map((key, i) => (
                        <tr key={i}>
                            <td>{key}</td>
                            <td>
                                {data[key].toString()}
                            </td>
                        </tr>
                    ))
                }
            </table>
        </div>
    )
}

export default JsonRenderer;
