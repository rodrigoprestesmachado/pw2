# Fault Tolerance (PW2)

Exemplos do topico de tolerancia a falhas com Quarkus 3.40.1.2 LTS.

## Como rodar

```sh
./mvnw quarkus:dev
```

## Endpoints

* `GET /fault/{name}`: `@Retry`, `@Fallback`, `@Timeout`
* `GET /fault/bulkhead/{name}`: `@Bulkhead`
* `GET /circuit/{id}/availability`: `@CircuitBreaker` via `CoffeeRepositoryService`

## Bulkhead com k6

```sh
k6 run k6.js
```
