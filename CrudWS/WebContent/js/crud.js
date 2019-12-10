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

/*jshint esversion: 6 */

/**
 * Create
 */
function fnCreate() {
  var name = document.getElementById("name").value;
  var email = document.getElementById("email").value;
  let wsClient = new WsClient();
  wsClient
    .post("api/v1/create", "name=" + name + "&email=" + email)
    .then(function(json) {
      var table = document.getElementById("tableData");
      createRow(table, json);
    })
    .catch(function() {
      return console.log("Create fail");
    });
}

/**
 * Read
 */
function fnRead() {
  let wsClient = new WsClient();
  wsClient
    .get("api/v1/read")
    .then(function(json) {
      var table = document.getElementById("tableData");
      for (var key in json) {
        createRow(table, json[key]);
      }
    })
    .catch(function() {
      return console.log("Read fail");
    });
}

/**
 * Update
 *
 * @param {*} id
 */
function fnUpdate(id) {
  var tr = document.getElementById(id);
  var trChildNodes = tr.childNodes;
  var textName = trChildNodes[0].firstChild.value;
  var textEmail = trChildNodes[1].firstChild.value;

  let data = "id=" + id + "&name=" + textName + "&email=" + textEmail;
  let wsClient = new WsClient();
  wsClient
    .post("api/v1/update", data)
    .then(function(json) {
      var tr = document.getElementById(json.id);
      var trChildNodes = tr.childNodes;
      trChildNodes[0].innerText = json.name;
      trChildNodes[1].innerText = json.email;
    })
    .catch(function() {
      return console.log("Update fail");
    });
}

/**
 * Delete
 *
 * @param {*} id
 */
function fnDelete(id) {
  let wsClient = new WsClient();
  wsClient
    .get("api/v1/delete/" + id)
    .then(function(json) {
      // If returns true of the server then remove table row
      if (json.result === "true") {
        //gets the table row
        var tr = document.getElementById(json.id);
        // get table row parent
        var trParent = tr.parentNode;
        // remove table row
        trParent.removeChild(tr);
      }
    })
    .catch(function() {
      return console.log("Delete fail");
    });
}

/**
 * Enables the form to update the data
 *
 * @param {*} id
 */
function enableUpdate(id) {
  var tr = document.getElementById(id);
  var trChildNodes = tr.childNodes;

  for (var index = 0; index <= 1; index++) {
    // Get the table value
    var text = trChildNodes[index].innerText;
    trChildNodes[index].innerText = "";

    //Creating the input element
    var input = document.createElement("input");
    input.setAttribute("onblur", "fnUpdate(" + id + ");");
    // seting the old value
    input.value = text;
    // append to the column
    trChildNodes[index].appendChild(input);
  }
}

/**
 * Create a row in the HTML table using DOM
 *
 * @param {*} table
 * @param {*} json
 */
function createRow(table, json) {
  // Creating table row with the data base id
  var tr = document.createElement("tr");
  tr.setAttribute("id", json.id);

  // Creating table columns
  var tdName = document.createElement("td");
  var tdEmail = document.createElement("td");
  var tdDelete = document.createElement("td");
  var tdUpdate = document.createElement("td");

  // adding data in the columns
  tdName.innerText = json.name;
  tdEmail.innerText = json.email;

  // adding the delete image to the column
  var imgDelete = document.createElement("img");
  imgDelete.setAttribute("src", "img/delete.png");
  imgDelete.setAttribute("onclick", "fnDelete(" + json.id + ");");
  tdDelete.appendChild(imgDelete);

  // adding the update image to the column
  var imgUpdate = document.createElement("img");
  imgUpdate.setAttribute("src", "img/update.png");
  imgUpdate.setAttribute("onclick", "enableUpdate(" + json.id + ");");
  tdUpdate.appendChild(imgUpdate);

  // adding the columns in the row
  tr.appendChild(tdName);
  tr.appendChild(tdEmail);
  tr.appendChild(tdDelete);
  tr.appendChild(tdUpdate);

  // adding the row in the table
  table.appendChild(tr);
}
