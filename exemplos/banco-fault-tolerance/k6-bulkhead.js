import exec from 'k6/execution';
import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 8,
    duration: '8s',
};

export default function () {
    http.get('http://localhost:8080/banco/transferencia-lenta/' + exec.vu.idInTest);
    sleep(0.5);
}
