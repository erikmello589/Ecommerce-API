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

(https://github.com/user-attachments/assets/1f2eddcb-31e3-4453-94cb-ee09aa4d68fd)

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
