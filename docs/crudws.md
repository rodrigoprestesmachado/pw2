# Instalação do projeto CrudWS

* **Nota:** Antes de iniciar, instale o Docker e o Maven na sua máquina

Para executar todo o ambiente de execução do projeto (MySQL, PhpMyAdmin e Wildfly), inicialmente faça um clone por meio do git:

    git clone https://github.com/rodrigoprestesmachado/pw2

Entre no diretório do projeto:

    cd pw2/CrudWS/

Para compilar e empacotar com o Maven:

    mvn package -f "pom.xml"

Execute o docker-compose para baixar as imagens e executar os serviços:

    docker-compose up -d

# Utilização do projeto