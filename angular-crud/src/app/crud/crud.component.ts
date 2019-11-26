import { Component, OnInit } from "@angular/core";

import { HttpClient } from "@angular/common/http";

@Component({
  selector: "app-crud",
  templateUrl: "./crud.component.html",
  styleUrls: ["./crud.component.css"]
})
export class CrudComponent implements OnInit {
  public clients: Client[];

  WS_URL: string = "http://localhost:8080/CrudWS-0.0.1/ws/v1/";

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get<Client[]>(this.WS_URL + "read").subscribe(json => {
      this.clients = json;
    });
  }

  btnDelete(id) {
    this.http.get<Result>(this.WS_URL + "delete/" + id).subscribe(json => {
      for (const key in this.clients) {
        if (this.clients[key].id == json.id) {
          this.clients.splice(Number(key), 1);
        }
      }
    });
  }
}

export class Client {
  id: number;
  name: string;
  email: string;
}

export class Result {
  id: number;
  result: boolean;
}
