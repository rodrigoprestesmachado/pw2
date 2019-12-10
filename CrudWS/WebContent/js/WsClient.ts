/**
 * Copyright 2019 Rodrigo Prestes Machado
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
class WsClient {
  get(url: string) {
    return this.exec("GET", url, "");
  }

  post(url: string, data: string) {
    return this.exec("POST", url, data);
  }

  private exec(method: string, url: string, data: string) {
    let reponsePromise = new Promise((resolve, reject) => {
      setTimeout(() => {
        let xmlHttpRequest = new XMLHttpRequest();
        xmlHttpRequest.onreadystatechange = function() {
          if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status == 200) {
            let json = JSON.parse(xmlHttpRequest.responseText);
            return resolve(json);
          }
        };
        xmlHttpRequest.open(method, url, true);
        method === "POST"
          ? xmlHttpRequest.setRequestHeader(
              "Content-type",
              "application/x-www-form-urlencoded"
            )
          : "";
        xmlHttpRequest.send(data);
      }, 1500);
    });
    return reponsePromise;
  }
}
