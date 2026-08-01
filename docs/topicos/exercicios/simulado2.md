---
layout: default
title: Segunda Avaliação
parent: Simulados
nav_order: 2
---

# Simulado: Segunda Avaliação

## Exemplos de Questões Práticas

**1.** Escreva o trecho de código que usa `@Retry` para executar o método até três vezes antes de lançar uma exceção.
{: .fs-3 }

**2.** Imagine que você tem um serviço que consulta uma API externa, mas essa API às vezes demora para responder. Você quer limitar o tempo máximo de espera a dois segundos e, caso o serviço demore demais ou falhe, retornar uma mensagem alternativa.
{: .fs-3 }

**3.** Você quer monitorar quantas vezes um método é chamado e quanto tempo ele leva para ser executado. Escreva o trecho de código que usa anotações do Quarkus para coletar métricas automáticas sobre esse método.
{: .fs-3 }

**4.** Você quer monitorar, em tempo real, quantos usuários estão conectados à aplicação. Crie o trecho de código que expõe uma métrica chamada `usuarios_ativos` com esse valor atual.
{: .fs-3 }

**5.** Você precisa ler, no arquivo `application.properties`, o valor da propriedade `app.mensagem.boasvindas` e exibir essa mensagem em um endpoint REST. Escreva o trecho de código que faz isso corretamente.
{: .fs-3 }

**6.** Você tem uma entidade chamada `Pessoa` que estende `PanacheEntity`. Escreva o trecho de código necessário para salvar uma nova pessoa no banco de dados com nome "Maria" e idade 25.
{: .fs-3 }

**7.** Crie uma entidade JPA chamada `Produto`, mapeada para uma tabela no banco de dados com os campos `id`, `nome` e `preco`.
{: .fs-3 }

**8.** Você quer criar um health check que verifica se o banco de dados está disponível. Se o banco estiver acessível, deve retornar status `UP`; caso contrário, `DOWN`. Escreva o trecho de código que implementa esse health check.
{: .fs-3 }

## Exemplos de Questões Teóricas

**1.** Sobre configurações em Quarkus e SmallRye Config: qual das alternativas abaixo está correta?
{: .fs-3 }

* A) As configurações marcadas como "build time" (tempo de compilação) no Quarkus podem ser alteradas em tempo de execução sem recompilar a aplicação.
* B) Arquivos YAML não são suportados pelo Quarkus para configurações externas.
* C) O padrão para perfis no Quarkus inclui `dev`, `test` e `prod`, e essas configurações podem ser separadas usando o prefixo `%` no arquivo `application.properties`.
* D) O namespace `quarkus` pode ser usado livremente para variáveis de usuário definidas pela aplicação, sem conflito.
{: .fs-3 }

**2.** Sobre health checks no Quarkus com a extensão SmallRye Health: assinale a alternativa correta.
{: .fs-3 }

* A) O endpoint `/q/health/live` verifica se o serviço está pronto para receber requisições.
* B) Para uma classe que implementa verificação de prontidão (readiness), deve-se usar a anotação `@Liveness`.
* C) A extensão SmallRye Health não suporta interface web para visualização dos health checks.
* D) O endpoint `/q/health` retorna, entre outros campos, um campo `checks` com as verificações individuais de saúde.
{: .fs-3 }

**3.** Sobre tolerância a falhas (Fault Tolerance) com SmallRye Fault Tolerance: qual afirmação é verdadeira?
{: .fs-3 }

* A) A anotação `@Bulkhead` limita o número de threads simultâneas e não está relacionada a filas de espera (*waiting task queue*).
* B) A anotação `@CircuitBreaker` impede que o método seja chamado novamente, mesmo que todas as chamadas subsequentes de teste sejam bem-sucedidas.
* C) `@Fallback` permite que um método alternativo seja invocado se o método original falhar, e o método de fallback deve ter a mesma assinatura do método original.
* D) `@Timeout` serve apenas para limitar o número de tentativas de um método, mas não para definir o tempo máximo de execução.
{: .fs-3 }

**4.** Sobre rastreamento (tracing) e log com as ferramentas Jaeger e Graylog: assinale a alternativa correta.
{: .fs-3 }

* A) Jaeger é uma ferramenta de gerenciamento de logs centralizados.
* B) Em Quarkus, para enviar logs no formato GELF para o Graylog, utiliza-se a extensão `quarkus-logging-gelf`.
* C) O formato GELF não permite incluir campos adicionais além da mensagem básica de log.
* D) A integração com Jaeger no Quarkus não exige a configuração de `quarkus.otel.service.name`.
{: .fs-3 }

**5.** Sobre o uso do Panache no Quarkus, assinale a alternativa correta:
{: .fs-3 }

* A) O Panache elimina completamente a necessidade de usar anotações JPA como `@Entity` ou `@Table`.
* B) Quando se estende `PanacheEntityBase` em vez de `PanacheEntity`, é necessário definir manualmente o identificador com `@Id` e `@GeneratedValue`.
* C) O método estático `findAll()` sempre retorna uma `List`, não sendo possível criar consultas reativas ou paginadas.
* D) O Panache não permite escrever consultas usando parâmetros nomeados; apenas consultas com parâmetros posicionais são aceitas.
{: .fs-3 }

**6.** Em uma aplicação Quarkus com a extensão SmallRye Fault Tolerance, considere o código a seguir:
{: .fs-3 }

```java
@ApplicationScoped
public class ServicoPagamento {

    @Retry(maxRetries = 3, delay = 500)
    @Timeout(2000)
    @Fallback(fallbackMethod = "processarPagamentoAlternativo")
    public String processarPagamento() {
        // chamada para um serviço externo
        return chamadaExterna();
    }

    public String processarPagamentoAlternativo() {
        return "Pagamento processado via fallback.";
    }
}
```

Com base nesse código e nas anotações de tolerância a falhas, assinale a alternativa correta:
{: .fs-3 }

* A) O método `processarPagamentoAlternativo` será executado somente se o método principal lançar uma exceção, e não em casos de timeout.
* B) A anotação `@Retry` faz com que o método seja executado até três vezes no total (uma tentativa inicial + duas novas).
* C) A anotação `@Timeout(2000)` define que o método deve aguardar dois segundos entre as tentativas do retry.
* D) O fallback é chamado antes de todas as tentativas de `@Retry`, para verificar se o serviço está disponível.
{: .fs-3 }

**7.** Em uma aplicação Quarkus, considere o seguinte código:
{: .fs-3 }

```java
@ApplicationScoped
public class ServicoEmail {

    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    public void enviarEmail(String destinatario) {
        // Simula uma falha eventual
        if (Math.random() < 0.7) {
            throw new RuntimeException("Falha ao enviar e-mail");
        }
        System.out.println("E-mail enviado para " + destinatario);
    }
}
```

Com base no funcionamento do `@CircuitBreaker`, assinale a alternativa correta:
{: .fs-3 }

* A) O `@CircuitBreaker` abre o circuito imediatamente na primeira exceção lançada, impedindo novas chamadas.
* B) O circuito abrirá quando, em um total de quatro chamadas, pelo menos metade resultar em falha.
* C) Após o circuito abrir, ele permanece aberto indefinidamente até o reinício da aplicação.
* D) A anotação `@CircuitBreaker` substitui automaticamente o uso de `@Retry`, tornando-o desnecessário.
{: .fs-3 }

**8.** Em uma aplicação Quarkus que utiliza rastreamento distribuído (tracing) e logging estruturado, analise as afirmações a seguir:
{: .fs-3 }

I. O Jaeger é utilizado para rastrear requisições entre múltiplos serviços, permitindo visualizar a cadeia de chamadas e os tempos de resposta.

II. O Graylog recebe e armazena logs estruturados, podendo receber mensagens no formato GELF (*Graylog Extended Log Format*).

III. A configuração da propriedade `quarkus.otel.service.name` define o nome do serviço que aparecerá no Jaeger.

IV. O Jaeger é responsável por coletar e armazenar os logs de aplicação no formato JSON.

Quais afirmações estão corretas?
{: .fs-3 }

* A) Apenas I e II
* B) Apenas I, II e III
* C) Apenas II e IV
* D) Todas as afirmativas estão corretas
{: .fs-3 }

**9.** Em uma aplicação Quarkus, deseja-se definir diferentes valores de configuração para os ambientes de desenvolvimento, teste e produção. Qual das opções abaixo representa corretamente essa configuração no arquivo `application.properties`?
{: .fs-3 }

* A)

```
profile.dev.database.url=jdbc:postgresql://localhost/devdb
profile.test.database.url=jdbc:postgresql://localhost/testdb
profile.prod.database.url=jdbc:postgresql://localhost/proddb
```

* B)

```
%dev.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost/devdb
%test.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost/testdb
%prod.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost/proddb
```

* C)

```
dev.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost/devdb
test.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost/testdb
prod.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost/proddb
```

* D)

```
quarkus.profile.dev.datasource.url=jdbc:postgresql://localhost/devdb
```
{: .fs-3 }

**10.** Em uma aplicação Quarkus que utiliza a extensão SmallRye Metrics, qual das alternativas abaixo descreve corretamente o funcionamento das métricas expostas pela aplicação?
{: .fs-3 }

* A) O Quarkus expõe o endpoint `/q/metrics`, que disponibiliza métricas em formato Prometheus, podendo incluir tanto métricas da JVM quanto métricas personalizadas.
* B) As métricas são acessadas pelo endpoint `/metrics`, que fornece dados em formato JSON apenas.
* C) As métricas só podem ser coletadas manualmente, não sendo possível integrá-las a ferramentas externas como Prometheus ou Grafana.
* D) A extensão de métricas do Quarkus substitui automaticamente os endpoints de health (`/q/health`) e readiness (`/q/health/ready`).
{: .fs-3 }

**11.** Para que servem os logs em uma aplicação Quarkus?
{: .fs-3 }

* A) Apenas para armazenar dados de configuração da aplicação.
* B) Para compilar o código automaticamente quando há erros.
* C) Para registrar informações sobre o comportamento da aplicação, como erros e eventos importantes.
* D) Para substituir o banco de dados em tempo de execução.
{: .fs-3 }

**12.** O que é o Panache no Quarkus?
{: .fs-3 }

* A) Uma ferramenta para desenhar interfaces gráficas.
* B) Um framework de autenticação de usuários.
* C) Uma camada que simplifica o uso do Hibernate ORM, facilitando o acesso ao banco de dados.
* D) Um servidor web integrado ao Quarkus.
{: .fs-3 }

**13.** Para que serve o recurso de Fault Tolerance no Quarkus?
{: .fs-3 }

* A) Para melhorar o desempenho da aplicação durante a compilação.
* B) Para garantir que a aplicação continue funcionando mesmo quando ocorrem falhas em chamadas de serviços.
* C) Para fazer backup automático do banco de dados.
* D) Para gerenciar usuários e permissões.
{: .fs-3 }

**14.** Para que servem os health checks em uma aplicação Quarkus?
{: .fs-3 }

* A) Para verificar se o banco de dados está vazio.
* B) Para compilar automaticamente o código quando há erros.
* C) Para aumentar o desempenho da aplicação.
* D) Para monitorar se a aplicação e seus componentes estão funcionando corretamente.
{: .fs-3 }

**15.** Em uma aplicação Quarkus, o desenvolvedor configurou um método com `@CircuitBreaker` da seguinte forma:
{: .fs-3 }

```java
@CircuitBreaker(requestVolumeThreshold = 6, failureRatio = 0.5, delay = 3000)
public String consultarServicoExterno() {
    if (Math.random() < 0.7) {
        throw new RuntimeException("Falha ao consultar serviço externo!");
    }
    return "Sucesso na consulta!";
}
```

Após várias execuções, o serviço apresentou quatro falhas e dois sucessos consecutivos. Qual será o estado do circuit breaker e o que ocorrerá nas próximas chamadas?
{: .fs-3 }

* A) O circuito permanecerá fechado, e todas as chamadas continuarão sendo executadas normalmente.
* B) O circuito ficará permanentemente fechado, independentemente das falhas.
* C) O circuito será aberto, bloqueando novas chamadas por três segundos.
* D) O circuito abrirá apenas se todas as seis chamadas falharem.
{: .fs-3 }

**16.** Considere o seguinte trecho de código em uma aplicação Quarkus:
{: .fs-3 }

```java
@ApplicationScoped
public class ServicoPagamento {

    @Bulkhead(value = 3, waitingTaskQueue = 2)
    public String processarPagamento() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Pagamento processado com sucesso!";
    }
}
```

Durante a execução, seis requisições simultâneas chamam o método `processarPagamento()`. O que acontecerá com essas chamadas, considerando o comportamento do `@Bulkhead`?
{: .fs-3 }

* A) Três chamadas serão executadas, duas ficarão na fila aguardando e a sexta será rejeitada com exceção.
* B) As seis chamadas serão processadas simultaneamente, pois o Quarkus ignora limites de concorrência.
* C) Apenas três chamadas serão executadas, e as demais serão descartadas sem erro.
* D) Todas as chamadas serão bloqueadas até que o primeiro processamento termine.
{: .fs-3 }

**17.** Para que serve a anotação `@Timeout` em um método de uma aplicação Quarkus?
{: .fs-3 }

* A) Para definir o tempo máximo que o método pode demorar para executar antes de ser interrompido.
* B) Para repetir automaticamente uma operação quando ocorre erro.
* C) Para colocar o método em espera antes da execução.
* D) Para medir o tempo total de execução e registrá-lo no log.
{: .fs-3 }

**18.** Qual é o objetivo principal do tracing em uma aplicação Quarkus?
{: .fs-3 }

* A) Aumentar o desempenho da aplicação em tempo de execução.
* B) Armazenar logs de banco de dados em arquivos locais.
* C) Impedir que falhas sejam registradas nos logs.
* D) Registrar e acompanhar o caminho completo de uma requisição entre diferentes serviços.
{: .fs-3 }

**19.** Para que serve o arquivo `application.properties` em uma aplicação Quarkus?
{: .fs-3 }

* A) Para armazenar o código-fonte principal da aplicação.
* B) Para salvar os dados que o usuário insere no sistema.
* C) Para definir configurações da aplicação, como portas, logs e conexões com banco de dados.
* D) Para compilar automaticamente a aplicação.
{: .fs-3 }

<center>
<a href="https://rpmhub.dev" target="_blank"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
