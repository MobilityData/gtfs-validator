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

import React from 'react'
import {Button} from 'semantic-ui-react'

/**
 * Customized button
 * @param id {string} id of the DOM element
 * @param method {function} method to be triggered by `onclick` event
 * @param description {string} text to be displayed inside the button
 * @returns {JSX.Element} the DOM element to be rendered
 * @constructor
 */
const NiceButton = ({id, method, description}) => <Button fluid id={id} onClick={method}>{description}</Button>

export default NiceButton
