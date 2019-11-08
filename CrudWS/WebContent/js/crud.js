/**
 * Copyright 2019 Rodrigo Prestes Machado
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

// Global XMLHttpRequest
var xmlHttp;

function fnCreate() {
	var name = document.getElementById("name").value;
	var email = document.getElementById("email").value;
	xmlHttp.onreadystatechange = createCallback;
	xmlHttp.open("GET", "ws/v1/create/"+name+"/"+email, true);
	xmlHttp.send();
}

function createCallback() {
	if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
		var json = JSON.parse(xmlHttp.responseText);
		var table = document.getElementById("tableData");
		createRow(table, json);
	}
}

function fnRead() {
	xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = readCallback;
	xmlHttp.open("GET", "ws/v1/read", true);
	xmlHttp.send();
}

function readCallback() {
	if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
		// get json from the server
		var json = JSON.parse(xmlHttp.responseText);
		var table = document.getElementById("tableData");
		for ( var key in json) {
			createRow(table, json[key]);
		}
	}
}

function fnDelete(id) {
	xmlHttp.onreadystatechange = deleteCallback;
	xmlHttp.open("GET", "server.php?op=delete&&id=" + id, true);
	xmlHttp.send();
}

function deleteCallback() {
	if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
		var json = JSON.parse(xmlHttp.responseText);
		// If returns true of the server then remove table row
		if (json.result === "true") {
			// gets the table row
			var tr = document.getElementById(json.id);
			// get table row parent
			var trParent = tr.parentNode;
			// remove table row
			trParent.removeChild(tr);
		}
	}
}

/* DOM do create a table row */
function createRow(table, json) {
	// Creating table row with the data base id
	var tr = document.createElement("tr");
	tr.setAttribute("id", json.id);

	// Creating table columns
	var tdName = document.createElement("td");
	var tdEmail = document.createElement("td");
	var tdDelete = document.createElement("td");

	// adding data in the columns
	tdName.innerText = json.name;
	tdEmail.innerText = json.email;

	// adding the delete image to the column
	var imgDelete = document.createElement("img");
	imgDelete.setAttribute("src", "img/delete.png");
	imgDelete.setAttribute("onclick", "fnDelete(" + json.id + ");");
	tdDelete.appendChild(imgDelete);

	// adding the columns in the row
	tr.appendChild(tdName);
	tr.appendChild(tdEmail);
	tr.appendChild(tdDelete);

	// adding the row in the table
	table.appendChild(tr);
}