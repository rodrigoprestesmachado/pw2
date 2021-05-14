/**
 * PW2 by Rodrigo Prestes Machado
 * 
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 *
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
