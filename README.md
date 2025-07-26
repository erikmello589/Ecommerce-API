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

# Manual de Configuração Local do Projeto Spring Boot

Este manual irá guiá-lo através dos passos necessários para configurar e rodar o projeto Spring Boot localmente em sua máquina.

## 📑 Índice

* [Pré-requisitos](#pré-requisitos)

* [Clonando o Repositório](#clonando-o-repositório)

* [Gerando as Chaves `.pem` (Privada e Pública)](#gerando-as-chaves-pem-privada-e-pública)

* [Configurando o Banco de Dados (PostgreSQL)](#configurando-o-banco-de-dados-postgresql)

* [Configurando o `application.properties` ou `application.yml`](#configurando-o-applicationproperties-ou-applicationyml)

* [Construindo e Rodando o Projeto](#construindo-e-rodando-o-projeto)

* [Verificação](#verificação)

## Pré-requisitos

Antes de começar, certifique-se de ter as seguintes ferramentas instaladas em sua máquina:

* **Java Development Kit (JDK) 17 ou superior**: Você pode baixar a versão mais recente do OpenJDK ou da Oracle.

  * [Download OpenJDK](https://openjdk.org/install/)

* **Apache Maven** (se o projeto usa Maven) ou **Gradle** (se o projeto usa Gradle):

  * [Download Maven](https://maven.apache.org/download.cgi)

  * [Download Gradle](https://gradle.org/install/)

* **Git**: Para clonar o repositório.

  * [Download Git](https://git-scm.com/downloads)

* **PostgreSQL**: O banco de dados utilizado pelo projeto.

  * [Download PostgreSQL](https://www.postgresql.org/download/)

* **OpenSSL**: Ferramenta para gerar as chaves criptográficas. Geralmente já vem pré-instalado em sistemas Linux/macOS. Para Windows, você pode instalá-lo via Chocolatey (`choco install openssl`) ou baixar um instalador.

  * [Download OpenSSL (para Windows, procure por "Light" ou "Win64OpenSSL")](https://wiki.openssl.org/index.php/Binaries)

## Clonando o Repositório

1. Abra seu terminal ou prompt de comando.

2. Navegue até o diretório onde deseja clonar o projeto.

3. Clone o repositório do GitHub usando o seguinte comando:

   ```bash
   git clone <URL_DO_SEU_REPOSITORIO_GITHUB>
