# ☕ Portal Mulheres na Ciência - Back-end (Java / Spring Boot)

API REST do **Portal Mulheres na Ciência**, desenvolvida com Java e Spring Boot.

## 📋 Sobre o Projeto

O Portal Mulheres na Ciência reúne informações sobre cientistas brasileiras e internacionais, dando visibilidade às suas trajetórias e contribuições nas áreas de STEM (Ciência, Tecnologia, Engenharia e Matemática).

### Endpoints Disponíveis

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/cientistas` | Lista resumida de todas as cientistas (Catálogo) |
| `GET` | `/cientistas/{id}` | Dados detalhados de uma cientista (Perfil) |

> Consulte o [Contrato de API](https://github.com/Portal-Mulheres-na-Ciencia/api-portal/wiki) para ver os formatos de resposta JSON.

## 🛠️ Tecnologias

- [Java 17+](https://www.oracle.com/java/technologies/downloads/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- Spring Data JPA
- Banco de Dados Relacional (MySQL / PostgreSQL)
- Maven

## 🚀 Como Rodar o Projeto

### Pré-requisitos

- [JDK 17](https://www.oracle.com/java/technologies/downloads/) ou superior
- [Maven](https://maven.apache.org/) (ou use o wrapper `./mvnw` incluso no projeto)
- Banco de Dados MySQL ou PostgreSQL rodando localmente

### Instalação

```bash
# Clone o repositório
git clone https://github.com/Portal-Mulheres-na-Ciencia/api-portal.git

# Entre na pasta do projeto
cd api-portal

# Rode a aplicação
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

## 🌳 Estratégia de Branches

| Branch | Finalidade |
|---|---|
| `main` | Versão estável e final (entrega) |
| `develop` | Branch de integração da equipe |
| `feature/*` | Desenvolvimento de novas funcionalidades |
| `bugfix/*` | Correção de bugs |

> ⚠️ **Regra:** Nunca faça push direto na `main` ou `develop`. Sempre abra um Pull Request!

## 👩‍💻 Equipe

Arthur Vieira, Cleber Borges, Davi Vinicius, Enzo Theodoro, Guilherme Rodrigues, Jonathan Santos.
