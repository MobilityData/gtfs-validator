/*
 * Copyright 2023 Jarvus Innovations LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mobilitydata.gtfsvalidator.web.service.controller;

/*
 * Body.Message is the payload of a Pub/Sub event. Please refer to the docs for additional information regarding Pub/Sub events.
 * @see <a href="https://cloud.google.com/pubsub/docs/reference/rest/v1/PubsubMessage">Google Cloud PubsubMessage</a>
 */
public class GoogleCloudPubsubMessage {

  private Message message;

  public GoogleCloudPubsubMessage() {}

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  public class Message {

    private String messageId;
    private String publishTime;
    private String data;

    public Message() {}

    public Message(String messageId, String publishTime, String data) {
      this.messageId = messageId;
      this.publishTime = publishTime;
      this.data = data;
    }

    public String getMessageId() {
      return messageId;
    }

    public void setMessageId(String messageId) {
      this.messageId = messageId;
    }

    public String getPublishTime() {
      return publishTime;
    }

    public void setPublishTime(String publishTime) {
      this.publishTime = publishTime;
    }

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }
  }
}
