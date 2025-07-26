# ECOMMERCE - API RestFul

## Aplicação com foco em Facilidade, Segurança e Organização para Lojas Online.

Venha conhecer um pouco mais do Lado de Servidor desse Projeto!

## 📑 Índice

* [Sobre o projeto](#sobre-o-projeto)

* [Funcionalidades Principais](#funcionalidades-principais)

* [Estrutura do Banco de Dados](#estrutura-do-banco-de-dados)

* [Dependências Principais](#dependências-principais)

* [Tecnologias utilizadas](#tecnologias-utilizadas)

## 📁 Sobre o projeto

### O que é?

Uma API para gerenciamento de e-commerce, desenvolvida em Java com Spring Boot para criar endpoints RESTful. O projeto utiliza Postman para testes e Swagger para documentação interativa, além de Database Client JDBC para consultas e administração do banco de dados.

### 🎯 Objetivo

O objetivo deste projeto é oferecer de forma intuitiva e simplificada uma solução robusta para gerenciar vendas, produtos, clientes e pedidos de uma loja online, garantindo segurança e organização dos dados.

## 📌 Funcionalidades Principais

* **Roles**: Criação das roles (Permissões) "ROLE_ADMIN" e/ou "ROLE_USER".

* **Cadastro de Usuários**: Criação com dados indispensáveis, visualização de todos ou pelo ID, atualização de informações e exclusão do cadastro.

* **Login**: Acessar uma conta de Usuário já registrada.

* **Gerenciamento de Produtos e Estoque**: Criação, armazenamento, atualização, visualização de todos os produtos e suas quantidades em estoque, e exclusão.

* **Processamento de Pedidos**: Criação, atualização, visualização e exclusão de pedidos de clientes. É permitido ao usuário anexar arquivos (como comprovantes de pagamento ou notas fiscais) para salvar o mesmo.

* **Validação de Dados**: Validação de dados de entrada para clientes, produtos e pedidos, garantindo a integridade e consistência das informações do sistema.

## 📊 Estrutura do Banco de Dados

O Banco de Dados possui como principais tabelas:

* **Customer**: Armazena informações dos usuários/clientes, incluindo nome, e-mail e senha.

* **Roles**: Define os papéis dos usuários no sistema (ADMIN e/ou USER).

* **Categories**: Categorias de produtos.

* **Products**: Informações detalhadas dos produtos disponíveis.

* **Orders**: Dados dos pedidos realizados pelos clientes.

* **Order-Items**: Detalhes dos itens dentro de cada pedido.

<img width="1388" height="927" alt="Untitled" src="https://github.com/user-attachments/assets/1b463db5-6999-4462-8947-2ef565c1ea8e" />

## 💪 Dependências Principais

O projeto foi desenvolvido com as seguintes dependências principais:

* **Spring Boot**: Framework para construção do backend.

* **Spring Data JPA**: Para interação com o Banco de Dados.

* **Spring Security**: Para autenticação e autorização de usuários.

* **PostgreSQL**: Banco de dados utilizado no projeto.

## 👩🏻‍💻 Tecnologias utilizadas

* **VsCode**

* **Java**

  * Orientação a Objetos (com classes como Produto, Cliente, Pedido)

  * Persistência de Dados: Leitura e escrita de arquivos (.txt)

* **Spring Boot**

  * Ferramenta que facilita a criação de APIs e aplicações Java com configuração mínima e suporte para persistência de dados e injeção de dependências.

* **Postman**

  * Plataforma para desenvolvimento e teste de APIs, permitindo simular requisições HTTP e analisar respostas, ideal para testar e documentar endpoints em ambientes de desenvolvimento.

* **Swagger**

  * Ferramenta para documentar e testar APIs, com interface interativa para visualização de endpoints.

* **Database Client JDBC - Extensão VsCode**

  * Ferramenta de gerenciamento de banco de dados, com suporte para diversas bases e uma interface intuitiva para consultas SQL e administração.

## 🎨 Autor

* [Erik Mello Guedes](https://github.com/erikmello589)

-----

# ⚙️ Manual de Configuração Local do Projeto Spring Boot

Este manual irá guiá-lo através dos passos necessários para configurar e rodar o projeto Spring Boot localmente em sua máquina.

-----

📑 **Índice**

  - [Pré-requisitos](https://www.google.com/search?q=%23pr%C3%A9-requisitos)
  - [Clonando o Repositório](https://www.google.com/search?q=%23clonando-o-reposit%C3%B3rio)
  - [Gerando as Chaves .pem (Privada e Pública)](https://www.google.com/search?q=%23gerando-as-chaves-pem-privada-e-p%C3%BAblica)
  - [Configurando o Banco de Dados (PostgreSQL)](https://www.google.com/search?q=%23configurando-o-banco-de-dados-postgresql)
  - [Configurando o `application.properties` ou `application.yml`](https://www.google.com/search?q=%23configurando-o-applicationproperties-ou-applicationyml)
  - [Construindo e Rodando o Projeto](https://www.google.com/search?q=%23construindo-e-rodando-o-projeto)
  - [Verificação](https://www.google.com/search?q=%23verifica%C3%A7%C3%A3o)

-----

## Pré-requisitos

Antes de começar, certifique-se de ter as seguintes ferramentas instaladas em sua máquina:

  - **Java Development Kit (JDK) 17 ou superior**: Você pode baixar a versão mais recente do OpenJDK ou da Oracle.

      - [Download OpenJDK](https://openjdk.org/install/)

  - **Apache Maven** (se o projeto usa Maven) ou **Gradle** (se o projeto usa Gradle):

      - [Download Maven](https://maven.apache.org/download.cgi)
      - [Download Gradle](https://gradle.org/install/)

  - **Git**: Para clonar o repositório.

      - [Download Git](https://git-scm.com/downloads)

  - **PostgreSQL**: O banco de dados utilizado pelo projeto.

      - [Download PostgreSQL](https://www.postgresql.org/download/)

  - **OpenSSL**: Ferramenta para gerar as chaves criptográficas. Geralmente já vem pré-instalado em sistemas Linux/macOS. Para Windows, você pode instalá-lo via Chocolatey (`choco install openssl`) ou baixar um instalador.

      - [Download OpenSSL (para Windows, procure por "Light" ou "Win64OpenSSL")](https://www.google.com/search?q=https://wiki.openssl.org/index.php/Binaries)

-----

## Clonando o Repositório

1.  Abra seu terminal ou prompt de comando.

2.  Navegue até o diretório onde deseja clonar o projeto.

3.  Clone o repositório do GitHub usando o seguinte comando:

    ```bash
    git clone <URL_DO_SEU_REPOSITORIO_GITHUB>
    ```

    Substitua `<URL_DO_SEU_REPOSITORIO_GITHUB>` pela URL real do seu projeto.

4.  Entre no diretório do projeto:

    ```bash
    cd <NOME_DO_DIRETORIO_DO_PROJETO>
    ```

-----

## Gerando as Chaves .pem (Privada e Pública)

As chaves `.pem` são essenciais para a segurança da sua aplicação (provavelmente para JWT ou outras operações criptográficas). Você precisará gerar um par de chaves (privada e pública) e colocá-las no diretório `src/main/resources/` do seu projeto.

Usaremos o **OpenSSL** para gerar as chaves.

1.  Navegue até o diretório `src/main/resources/` do seu projeto:

    ```bash
    cd <CAMINHO_PARA_SEU_PROJETO>/src/main/resources
    ```

    (Ex: `cd meu-projeto-ecommerce/src/main/resources`)

2.  **Gere a Chave Privada (`private.pem`):**

    Execute o seguinte comando no terminal:

    ```bash
    openssl genrsa -out private.pem 2048
    ```

    Este comando gera uma chave RSA privada de 2048 bits e a salva no arquivo `private.pem`.

3.  **Gere a Chave Pública (`public.pem`) a partir da Chave Privada:**

    Execute o seguinte comando no terminal:

    ```bash
    openssl rsa -in private.pem -pubout -out public.pem
    ```

    Este comando extrai a parte pública da chave `private.pem` e a salva no arquivo `public.pem`.

Após esses passos, você deverá ter dois arquivos, `private.pem` e `public.pem`, dentro do diretório `src/main/resources/` do seu projeto.

-----

## Configurando o Banco de Dados (PostgreSQL)

1.  **Instale e Inicie o PostgreSQL**: Certifique-se de que o servidor PostgreSQL esteja rodando em sua máquina.

2.  **Crie um Banco de Dados**: Crie um novo banco de dados para o projeto. Você pode usar uma ferramenta como `psql` (linha de comando) ou `pgAdmin` (interface gráfica).

    Exemplo via `psql`:

    ```sql
    CREATE DATABASE ecommerce_db;
    ```

    (Você pode usar um nome de banco de dados diferente, se preferir, mas lembre-se de atualizá-lo nas configurações do projeto.)

3.  **Crie um Usuário (Opcional, mas recomendado)**: É uma boa prática criar um usuário específico para a aplicação com as permissões necessárias.

    Exemplo via `psql`:

    ```sql
    CREATE USER ecommerce_user WITH PASSWORD 'sua_senha_segura';
    GRANT ALL PRIVILEGES ON DATABASE ecommerce_db TO ecommerce_user;
    ```

    Substitua `sua_senha_segura` por uma senha forte.

-----

## Configurando o `application.properties` ou `application.yml`

O projeto provavelmente possui um arquivo de configuração (`application.properties` ou `application.yml`) localizado em `src/main/resources/`. Você precisará configurar as credenciais do banco de dados e outras propriedades.

Abra o arquivo (`application.properties` ou `application.yml`) e ajuste as seguintes propriedades (se já existirem, ou adicione-as):

**Exemplo para `application.properties`:**

```properties
# Configurações do Banco de Dados PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=ecommerce_user
spring.datasource.password=sua_senha_segura
spring.datasource.driver-class-name=org.postgresql.Driver

# Configurações JPA/Hibernate (ajuste conforme necessário)
spring.jpa.hibernate.ddl-auto=update # ou create, validate, none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Configurações do Servidor
server.port=8080

# Outras configurações específicas do seu projeto, se houver
# Por exemplo, configurações de JWT, se aplicável
# security.jwt.public-key=classpath:public.pem
# security.jwt.private-key=classpath:private.pem
```

**Exemplo para `application.yml`:**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce_db
    username: ecommerce_user
    password: sua_senha_segura
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update # ou create, validate, none
    show-sql: true
    properties:
      hibernate:
        format_sql: true
server:
  port: 8080

# Outras configurações específicas do seu projeto, se houver
# Por exemplo, configurações de JWT, se aplicável
# security:
#   jwt:
#     public-key: classpath:public.pem
#     private-key: classpath:private.pem
```

**Importante:**

  - Substitua `ecommerce_db`, `ecommerce_user` e `sua_senha_segura` pelos valores que você configurou para o seu banco de dados.
  - A propriedade `spring.jpa.hibernate.ddl-auto` deve ser definida com cautela. `update` é útil para desenvolvimento, mas `none` ou `validate` são mais seguros para ambientes de produção. Se o seu banco de dados não for populado automaticamente, você pode precisar de scripts SQL para criar as tabelas iniciais.

-----

## Construindo e Rodando o Projeto

Após configurar as chaves e o banco de dados, você pode construir e rodar o projeto.

1.  Navegue de volta para o diretório raiz do seu projeto (onde está o `pom.xml` ou `build.gradle`).

    ```bash
    cd <CAMINHO_PARA_SEU_PROJETO>
    ```

2.  **Construa o Projeto:**

      - **Se usa Maven:**

        ```bash
        mvn clean install
        ```

        Este comando irá baixar as dependências, compilar o código e empacotar a aplicação em um arquivo `.jar`.

      - **Se usa Gradle:**

        ```bash
        ./gradlew clean build
        ```

        Este comando fará o mesmo para projetos Gradle.

3.  **Rode o Projeto:**

    Após a construção bem-sucedida, você pode rodar a aplicação Spring Boot.

      - **Usando o JAR gerado (recomendado):**

        ```bash
        java -jar target/<nome-do-seu-jar>.jar
        ```

        (Substitua `target/<nome-do-seu-jar>.jar` pelo caminho real para o arquivo `.jar` gerado na pasta `target/` - ou `build/libs/` para Gradle).

      - **Usando Maven Spring Boot Plugin:**

        ```bash
        mvn spring-boot:run
        ```

      - **Usando Gradle Spring Boot Plugin:**

        ```bash
        ./gradlew bootRun
        ```

    O console mostrará logs do Spring Boot, indicando que a aplicação está iniciando. Procure por mensagens como "Started Application in X.XXX seconds" ou "Tomcat initialized with port(s): 8080 (http)".

-----

## Verificação

Uma vez que a aplicação esteja rodando, você pode verificar se ela está acessível:

1.  Abra seu navegador ou uma ferramenta como Postman.

2.  Acesse a URL base da sua API. Se o servidor estiver configurado na porta `8080`, geralmente será:
    `http://localhost:8080`

3.  **Verifique a documentação Swagger (se configurada):**
    Se o projeto incluir Swagger/OpenAPI, você pode acessar a documentação interativa em:
    `http://localhost:8080/swagger-ui.html`
    ou
    `http://localhost:8080/v3/api-docs/swagger-ui/index.html` (para SpringDoc OpenAPI)

-----

Parabéns\! Seu projeto Spring Boot deve estar rodando localmente.
