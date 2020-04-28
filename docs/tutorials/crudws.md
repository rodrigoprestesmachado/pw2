---
layout: default
title: Instalação do projeto CrudWS
parent: Tutoriais
nav_order: 1
---

# Instalação do projeto CrudWS

* **Nota:** Antes de iniciar, instale o [Docker](https://www.docker.com) e o [Maven](https://maven.apache.org) na sua máquina

Para executar todo o ambiente de execução do projeto (MySQL, PhpMyAdmin e Wildfly), inicialmente faça um clone por meio do git:

    git clone https://github.com/rodrigoprestesmachado/pw2

Entre no diretório do projeto:

    cd pw2/CrudWS/

Para compilar e empacotar com o Maven:

    mvn package -f "pom.xml"

Execute o docker-compose para baixar as imagens e executar os *containers*:

    docker-compose up -d

# Utilização do projeto

O docker-compose irá colocar em execução três container um MySQL (porta 3306), um PhpMyAdmin (porta 80) e a aplicação CrudWS rodando em cima do Wildfly (porta 8080 e 9990). Todos os usuários e senhas foram configurados como "*pw2*", assim, se você desejar, por exemplo, entrar no console de administração do wildfly acesse *localhost:9990* como o usuário *pw2* e senha *pw2*. Finalmente, para acessar o CrudWS digite no seu navegador:

    http://localhost:8080/CrudWS/

# CrudWS com interface em Angular

**Nota:** necessário instalar o [Angular CLI](https://angular.io).

Para rodar o projeto CrudWS com uma interface em Angular primeiramente faça um clone do projeto:

    git clone https://github.com/rodrigoprestesmachado/pw2

Para fazer a compilação e empacotamento do projeto temos que inicialmente compilar a interface angular e depois compilar/empacotar as classes em Java. Existe um script em shell (linux/unix) para realizar essa tarefa, dessa maneira, execute os seguintes comandos:

    cd pw2
    sh ./angularBuild.sh

Uma vez empacotado, execute o docker-compose para baixar as imagens e construir os *containers*:

    cd CrudWS/
    docker-compose up -d

**Nota:** Para voltar para a interface simples faça novamente um clone do projeto, pois, a compilação da interface em angular acaba criando alguns arquivos no WebContent e também apagando o index.html anterior da interface simples em JS.
