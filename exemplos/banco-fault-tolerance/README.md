# Banco Fault Tolerance

Projeto de apoio aos exercicios de fixacao do topico Fault Tolerance (PW2).

## Como rodar

```sh
./mvnw quarkus:dev
```

## Endpoints

* `GET /banco/saldo/{conta}`: `@Retry`
* `GET /banco/saldo-cache/{conta}`: `@Retry` + `@Fallback`
* `GET /banco/saldo-lento/{conta}`: `@Timeout`
* `GET /banco/transferencia-lenta/{id}`: `@Bulkhead`
* `GET /banco/saldo-circuito/{conta}`: `@CircuitBreaker`
* `GET /banco/transferir?origem=&destino=&valor=`: combinacao

## Bulkhead com k6

```sh
k6 run k6-bulkhead.js
```
