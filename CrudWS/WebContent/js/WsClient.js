/**
 * PW2 by Rodrigo Prestes Machado
 * 
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 *
*/
var WsClient = /** @class */ (function () {
    function WsClient() {
    }
    WsClient.prototype.get = function (url) {
        return this.exec("GET", url, "");
    };
    WsClient.prototype.post = function (url, data) {
        return this.exec("POST", url, data);
    };
    WsClient.prototype.exec = function (method, url, data) {
        var reponsePromise = new Promise(function (resolve, reject) {
            setTimeout(function () {
                var xmlHttpRequest = new XMLHttpRequest();
                xmlHttpRequest.onreadystatechange = function () {
                    if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status == 200) {
                        var json = JSON.parse(xmlHttpRequest.responseText);
                        return resolve(json);
                    }
                };
                xmlHttpRequest.open(method, url, true);
                method === "POST"
                    ? xmlHttpRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded")
                    : "";
                xmlHttpRequest.send(data);
            }, 1500);
        });
        return reponsePromise;
    };
    return WsClient;
}());
