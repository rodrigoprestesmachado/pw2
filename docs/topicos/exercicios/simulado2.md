---
layout: default
title: Segunda Avaliação
nav_order: 11
---

# Simulado: Segunda Avaliação


## Exemplos de Questões Práticas

1. Escreva o trecho de código correto que usa @Retry para tentar executar o método até 3 vezes antes de lançar uma exceção.

2. Imagine que você tem um serviço que consulta uma API externa, mas essa API às vezes demora para responder. Você quer limitar o tempo máximo de espera para 2 segundos, e caso o serviço demore demais ou falhe, retornar uma mensagem alternativa.

3. Você quer monitorar o número de vezes que um método é chamado e o tempo que ele leva para ser executado.
Escreva o trecho de código que usa anotações do Quarkus para coletar métricas automáticas sobre esse método.

4. Você quer monitorar em tempo real quantos usuários estão conectados à aplicação.
Crie o trecho de código que expõe uma métrica chamada usuarios_ativos que retorna esse valor atual.

5. Você precisa ler do arquivo application.properties o valor da propriedade app.mensagem.boasvindas e exibir essa mensagem em um endpoint REST. Escreva o trecho de código que faz isso corretamente.

6. Você tem uma entidade chamada Pessoa que estende PanacheEntity. Escreva o trecho de código necessário para salvar uma nova pessoa no banco de dados com nome “Maria” e idade 25.

7. Crie uma entidade JPA chamada Produto que será mapeada para uma tabela no banco de dados com os campos id, nome e preco.

8. Você quer criar um health check que verifica se o banco de dados está disponível.
Se o banco estiver acessível, deve retornar status UP; caso contrário, DOWN.
Escreva o trecho de código que implementa esse health check.


## Exemplos de Questões Teóricas

1. Sobre configurações em Quarkus e SmallRye Config: Qual das alternativas abaixo está correta?
* A) As configurações marcadas como “build time” (tempo de construção) no Quarkus
podem ser alteradas em tempo de execução sem recompilar a aplicação.
* B) Arquivos YAML não são suportados pelo Quarkus para configurações externas.
* C) O padrão para perfis no Quarkus inclui “dev”, “test” e “prod”, e essas
configurações podem ser separadas usando o prefixo % no arquivo
application.properties.
* D) O namespace quarkus pode ser usado livremente para variáveis de usuário
definidas pela aplicação sem conflito.

2. Sobre “Health checks” no Quarkus com a extensão SmallRye Health: Assinale a
alternativa correta.
A) O endpoint /q/health/live verifica se o serviço está pronto para receber
requisições.
B) Para uma classe que implementa verificação de prontidão (readiness), deve-se
usar a anotação @Liveness.
C) A extensão SmallRye Health não suporta interface web para visualização dos
health checks.
D) O endpoint /q/health retorna, entre outras, um campo "checks" com as
verificações individuais de saúde.

3. Sobre tolerância a falhas (Fault Tolerance) com SmallRye Fault Tolerance:
Qual afirmação é verdadeira?
* A) A anotação @Bulkhead limita o número de threads simultâneas e não tem nada
a ver com filas de espera (“waiting task queue”).
* B) A anotação @CircuitBreaker impede que o método seja chamado novamente mesmo
se todas as chamadas de teste sucessivas forem bem-sucedidas.
* C) @Fallback permite que um método alternativo seja invocado se o método
original falhar, e o método de fallback deve ter a mesma assinatura do método
original.
* D) @Timeout serve apenas para limitar o número de tentativas de um método, mas
não para definir tempo máximo de execução.

4. Sobre “Trace e Log” com as ferramentas Jaeger e Graylog: Assinale a
alternativa correta.
* A) Jaeger é uma ferramenta de gerenciamento de logs centralizados.
* B) Em Quarkus, para enviar logs no formato GELF para o Graylog, utiliza-se a extensão quarkus-logging-gelf.
* C) O formato GELF não permite incluir campos adicionais além da mensagem básica de log.
* D) A integração com Jaeger no Quarkus não exige configuração de quarkus.otel.service.name.

5. Sobre o uso do Panache no Quarkus, assinale a alternativa correta:
* A) O Panache elimina completamente a necessidade de usar anotações JPA como @Entity ou @Table.
* B) Quando se estende PanacheEntityBase em vez de PanacheEntity, é necessário definir manualmente o identificador com @Id e @GeneratedValue.
* C) O método estático findAll() retorna sempre uma List, não sendo possível criar consultas reativas ou paginadas.
* D) O Panache não permite escrever consultas usando parâmetros nomeados; apenas consultas com parâmetros posicionais são aceitas.

6. Em uma aplicação Quarkus com a extensão SmallRye Fault Tolerance, considere o código a seguir:

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
````

Com base nesse código e nas anotações de tolerância a falhas, assinale a alternativa correta:
* A) O método processarPagamentoAlternativo será executado somente se o método principal lançar uma exceção e não em casos de timeout.
* B) A anotação @Retry faz com que o método seja executado até 3 vezes no total (1 tentativa inicial + 2 novas).
* C) A anotação @Timeout(2000) define que o método deve aguardar 2 segundos entre as tentativas do retry.
* D) O fallback é chamado antes de todas as tentativas de @Retry, para verificar se o serviço está disponível.

7. Em uma aplicação Quarkus, considere o seguinte código:

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
````

Com base no funcionamento do @CircuitBreaker, assinale a alternativa correta:
* A) O @CircuitBreaker abre o circuito imediatamente na primeira exceção lançada, impedindo novas chamadas.
* B) O circuito abrirá quando, em um total de 4 chamadas, pelo menos metade resultar em falha.
* C) Após o circuito abrir, ele permanece aberto indefinidamente até o reinício da aplicação.
* D) A anotação @CircuitBreaker substitui automaticamente o uso de @Retry, tornando-o desnecessário.

8. Em uma aplicação Quarkus que utiliza rastreamento distribuído (Tracing) e logging estruturado, analise as afirmações a seguir:

    I. O Jaeger é utilizado para rastrear requisições entre múltiplos serviços, permitindo visualizar a cadeia de chamadas e tempos de resposta.

    II. O Graylog recebe e armazena logs estruturados, podendo receber mensagens no formato GELF (Graylog Extended Log Format).

    III. A configuração da propriedade quarkus.otel.service.name define o nome do serviço que aparecerá no Jaeger.

    IV. O Jaeger é responsável por coletar e armazenar os logs de aplicação no formato JSON.

Quais afirmações estão corretas?
* A) Apenas I e II
* B) Apenas I, II e III
* C) Apenas II e IV
* D) Todas as afirmativas estão corretas

9. Em uma aplicação Quarkus, deseja-se definir diferentes valores de configuração para os ambientes de desenvolvimento, teste e produção. Qual das opções abaixo representa corretamente essa configuração no arquivo application.properties?

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

10. Em uma aplicação Quarkus que utiliza a extensão SmallRye Metrics, qual das
alternativas abaixo descreve corretamente o funcionamento das métricas expostas
pela aplicação?

* A) O Quarkus expõe o endpoint /q/metrics, que disponibiliza métricas em formato Prometheus, podendo incluir tanto métricas da JVM quanto métricas personalizadas.
* B) As métricas são acessadas pelo endpoint /metrics, que fornece dados em formato JSON apenas.
* C) As métricas só podem ser coletadas manualmente, não sendo possível integrá-las a ferramentas externas como Prometheus ou Grafana.
* D) A extensão de métricas do Quarkus substitui automaticamente os endpoints de health (/q/health) e readiness (/q/health/ready).

11. Para que servem os logs em uma aplicação Quarkus?
* A) Apenas para armazenar dados de configuração da aplicação.
* B) Para compilar o código automaticamente quando há erros.
* C) Para registrar informações sobre o comportamento da aplicação, como erros e
eventos importantes.
* D) Para substituir o banco de dados em tempo de execução.

12. O que é o Panache no Quarkus?
* A) Uma ferramenta para desenhar interfaces gráficas.
* B) Um framework de autenticação de usuários.
* C) Uma camada que simplifica o uso do Hibernate ORM, facilitando o acesso ao banco de dados.
* D) Um servidor web integrado ao Quarkus.

13. Para que serve o recurso de Fault Tolerance no Quarkus?
* A) Para melhorar o desempenho da aplicação durante a compilação.
* B) Para garantir que a aplicação continue funcionando mesmo quando ocorrem falhas em chamadas de serviços.
* C) Para fazer backup automático do banco de dados.
* D) Para gerenciar usuários e permissões.

14. O que é programação reativa no Quarkus?
* A) Um estilo de programação que reage a eventos de forma assíncrona e não bloqueante, melhorando o desempenho da aplicação.
* B) Um modelo de programação que executa as tarefas de forma sequencial e bloqueante.
* C) Uma biblioteca usada apenas para criar interfaces gráficas.
* D) Um modo de executar consultas SQL de maneira síncrona.

15. Para que servem os health checks em uma aplicação Quarkus?
* A) Para verificar se o banco de dados está vazio.
* B) Para compilar automaticamente o código quando há erros.
* C) Para aumentar a performance da aplicação.
* D) Para monitorar se a aplicação e seus componentes estão funcionando corretamente.

16. Em uma aplicação Quarkus, o desenvolvedor configurou um método com @CircuitBreaker da seguinte forma:

```java
@CircuitBreaker(requestVolumeThreshold = 6, failureRatio = 0.5, delay = 3000)
public String consultarServicoExterno() {
    if (Math.random() < 0.7) {
        throw new RuntimeException("Falha ao consultar serviço externo!");
    }
    return "Sucesso na consulta!";
}
```

Após várias execuções, o serviço apresentou 4 falhas e 2 sucessos consecutivos.
Qual será o estado do Circuit Breaker e o que ocorrerá nas próximas chamadas?
* A) O circuito permanecerá fechado, e todas as chamadas continuarão sendo executadas normalmente.
* B) O circuito ficará permanentemente fechado, independentemente das falhas.
* C) O circuito será aberto, bloqueando novas chamadas por 3 segundos.
* D) O circuito abrirá apenas se todas as 6 chamadas falharem.

17. Considere o seguinte trecho de código em uma aplicação Quarkus:

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

Durante a execução, 6 requisições simultâneas chamam o método processarPagamento().
O que acontecerá com essas chamadas, considerando o comportamento do @Bulkhead?
* A) 3 chamadas serão executadas, 2 ficarão na fila aguardando, e a 6ª será rejeitada com exceção.
* B) As 6 chamadas serão processadas simultaneamente, pois o Quarkus ignora limites de concorrência.
* C) Apenas 3 chamadas serão executadas e as demais serão descartadas sem erro.
* D) Todas as chamadas serão bloqueadas até que o primeiro processamento termine.

18. Para que serve a anotação @Timeout em um método de uma aplicação Quarkus?
* A) Para definir o tempo máximo que o método pode demorar para executar antes de ser interrompido.
* B) Para repetir automaticamente uma operação quando ocorre erro.
* C) Para colocar o método em espera antes da execução.
* D) Para medir o tempo total de execução e registrar no log.

19. Qual é o objetivo principal do Tracing em uma aplicação Quarkus?
* A) Aumentar a performance da aplicação em tempo de execução.
* B) Armazenar logs de banco de dados em arquivos locais.
* C) Impedir que falhas sejam registradas nos logs.
* D) Registrar e acompanhar o caminho completo de uma requisição entre diferentes serviços.

20. Para que serve o arquivo application.properties em uma aplicação Quarkus?
* A) Para armazenar o código-fonte principal da aplicação.
* B) Para salvar os dados que o usuário insere no sistema.
* C) Para definir configurações da aplicação, como portas, logs e conexões com banco de dados.
* D) Para compilar automaticamente a aplicação.

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
