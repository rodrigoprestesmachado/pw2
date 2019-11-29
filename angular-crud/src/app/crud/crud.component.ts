import {
  Component,
  OnInit,
  ViewChild,
  ElementRef,
  Inject
} from "@angular/core";

import { HttpClient } from "@angular/common/http";
import { HttpParams } from "@angular/common/http";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
  MatDialogConfig
} from "@angular/material/dialog";

@Component({
  selector: "app-crud",
  templateUrl: "./crud.component.html",
  styleUrls: ["./crud.component.css"]
})
export class CrudComponent implements OnInit {
  //Web Service
  private WS_URL: string = "http://code.inf.poa.ifrs.edu.br/CrudWS/api/v1/";

  // CRUD data
  public clients: Client[];

  // controls the update form
  public enableForm: number = -1;

  public deleteClient: boolean = true;

  // References to the interface
  @ViewChild("formCreateName", { static: false }) formCreateName: ElementRef;
  @ViewChild("formCreateEmail", { static: false }) formCreateEmail: ElementRef;
  @ViewChild("formName", { static: false }) formName: ElementRef;
  @ViewChild("formEmail", { static: false }) formEmail: ElementRef;

  constructor(private http: HttpClient, public dialog: MatDialog) {}

  /**
   *
   */
  btnCreate() {
    // Creates the parameter to the body
    const httpOptions = new HttpParams()
      .set("name", this.formCreateName.nativeElement.value)
      .set("email", this.formCreateEmail.nativeElement.value);
    this.http
      .post<Client>(this.WS_URL + "create/", httpOptions)
      .subscribe(client => {
        this.clients.push(client);
      });
  }

  /**
   * Read operation on init
   */
  ngOnInit() {
    this.http.get<Client[]>(this.WS_URL + "read").subscribe(json => {
      this.clients = json;
    });
  }

  /**
   * Enables the update form
   * @param id
   */
  btnUpdateForm(id) {
    this.enableForm = id;
  }

  /**
   * Updates the client object on server
   * @param id
   */
  btnUpdate(id) {
    // Creates the parameter to the body
    const httpOptions = new HttpParams()
      .set("id", id)
      .set("name", this.formName.nativeElement.value)
      .set("email", this.formEmail.nativeElement.value);

    this.http
      .post<Client>(this.WS_URL + "update/", httpOptions)
      .subscribe(client => {
        // disabe the forms
        this.enableForm = -1;
        // Updating the clients array and the interface
        for (const key in this.clients) {
          if (this.clients[key].id == client.id) {
            this.clients[key] = client;
          }
        }
      });
  }
  /**
   * Implements the delete button action
   * @param id
   */
  btnDelete(id) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.position = {
      top: "10%",
      left: "35%"
    };
    dialogConfig.data = { result: this.deleteClient };
    dialogConfig.width = "200px";
    dialogConfig.height = "200px";
    const dialogRef = this.dialog.open(DialogDelete, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.http
          .get<Result>(this.WS_URL + "delete/" + id)
          .subscribe(client => {
            for (const key in this.clients) {
              if (this.clients[key].id == client.id) {
                this.clients.splice(Number(key), 1);
              }
            }
          });
      }
    });
  }
}

/**
 * Client WS model
 */
export class Client {
  id: number;
  name: string;
  email: string;
}

/**
 * Result WS model
 */
export class Result {
  id: number;
  result: boolean;
}

export interface DialogData {
  result: boolean;
}

@Component({
  selector: "dialog-delete",
  templateUrl: "dialog-delete.html"
})
export class DialogDelete {
  constructor(
    public dialogDelete: MatDialogRef<DialogDelete>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {}

  onNoClick(): void {
    this.dialogDelete.close();
  }
}
