# 💰 Financial Control IA Api

API REST desenvolvida com **Spring Boot** para gerenciamento financeiro, permitindo a criação de tabelas, funcionalidades dinâmicas, parâmetros e integração com IA para execução de comandos por texto e voz.

---

## 🚀 Tecnologias

* ☕ Java 21+
* 🌱 Spring Boot
* 🗄️ Spring Data JPA
* 🐘 PostgreSQL
* 🤖 Spring AI / OpenAI
* 📦 Maven
* 🧠 Ollama (IA)
* 🗣️ Whisper (IA)

---

## ✨ Funcionalidades

* 📊 Cadastro de tabelas financeiras
* ⚙️ Cadastro de funcionalidades dinâmicas
* 📝 Cadastro de parâmetros por funcionalidade
* 💬 Processamento de comandos em linguagem natural
* 🎤 Transcrição de áudio
* 🤖 Integração com IA para automação de ações
* 🔍 Consulta de funcionalidades e seus parâmetros

---

## 📂 Estrutura do Projeto

```text
src/main/java
├── controller
├── service
├── domain
│   ├── model
│   └── repository
├── dto
└── config
```

---

## 🔧 Configuração

Configure as variáveis de ambiente:

```properties
spring.ai.openai.api-key=YOUR_API_KEY

spring.datasource.url=jdbc:postgresql://localhost:5432/budgeting
spring.datasource.username=postgres
spring.datasource.password=password
```

---

## ▶️ Executando o Projeto

### 1️⃣ Clonar o repositório

```bash
git clone <url-do-repositorio>
```

### 2️⃣ Entrar na pasta

```bash
cd budgeting
```

### 3️⃣ Executar a aplicação

```bash
./mvnw spring-boot:run
```

ou

```bash
mvn spring-boot:run
```

---

## 🌐 Endpoints

### ❤️ Health Check

```http
GET /api/v1/ai/health
```

### 💬 Chat com IA

```http
POST /api/v1/ai/chat
```

Exemplo:

```json
{
  "prompt": "Crie uma tabela de despesas"
}
```

### 🎤 Transcrição de Áudio

```http
POST /api/v1/audio/transcribe
```

---

## 🗄️ Banco de Dados

### ⚙️ tb_functionalities

| Campo       | Tipo    |
| ----------- | ------- |
| id          | bigint  |
| name        | varchar |
| description | varchar |

### 📝 tb_parameters

| Campo            | Tipo    |
| ---------------- | ------- |
| id               | bigint  |
| name             | varchar |
| value            | varchar |
| functionality_id | bigint  |

### 🔗 Relacionamentos

* Uma funcionalidade ➜ possui vários parâmetros.
* Um parâmetro ➜ pertence a uma funcionalidade.

---

## 🤖 Exemplo de Funcionalidade

```json
{
  "name": "abrirModal",
  "description": "Abre uma janela modal",
  "parameters": [
    {
      "name": "titulo",
      "value": "Cadastro de Cliente"
    }
  ]
}
```

---

## 📈 Roadmap

* [x] Cadastro de funcionalidades
* [x] Cadastro de parâmetros
* [x] Integração com IA
* [x] Transcrição de áudio
* [ ] Execução automática de ações no frontend
* [ ] Histórico de comandos
* [ ] Dashboard financeiro

---

## 👨‍💻 Autor

Desenvolvido por **Gabriel M. Santos** 🚀
