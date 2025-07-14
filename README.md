# TechCatalog

Sistema de catálogo desenvolvido em Spring Boot com MySQL.

https://github.com/user-attachments/assets/0f0db5b1-d56f-4eb1-9b46-4a8c64cb1db1

## 🚀 Como executar o projeto

### Pré-requisitos

- Java 11 ou superior
- MySQL 8.0 ou superior
- Maven 3.6 ou superior

### Configuração do Ambiente

#### 1. Clone o repositório
```bash
git clone [URL_DO_REPOSITORIO]
cd techcatalog
```

#### 2. Configuração do Banco de Dados

**2.1. Criar o banco de dados no MySQL:**
```sql
CREATE DATABASE techCatalog;
```

**2.2. Configurar as credenciais do banco:**
Abra o arquivo `src/main/resources/application.properties` e configure as credenciais do MySQL:

```properties
spring.datasource.username=root
spring.datasource.password=root
```

> **Nota:** Substitua `root` pelo seu usuário e senha do MySQL conforme sua configuração local.

#### 3. Executar a aplicação
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 📋 Primeiro Acesso

### Cadastro do Administrador

Na primeira execução do sistema, você será direcionado para uma página de cadastro de administrador, pois o sistema ainda não possui nenhum usuário administrador cadastrado.

**Importante:** Esta página de cadastro de administrador aparecerá apenas uma vez. Após o cadastro do primeiro administrador, ela não será mais exibida e o sistema seguirá o fluxo normal de login.

### Fluxo Normal

Após o cadastro do primeiro administrador, o sistema funcionará normalmente com:
- Página de login
- Funcionalidades do sistema conforme perfil de usuário
- Gerenciamento através do painel administrativo

## 🔧 Tecnologias Utilizadas

- **Backend:** Spring Boot
- **Banco de Dados:** MySQL
- **Build:** Maven

## 📝 Observações

- Certifique-se de que o MySQL esteja rodando antes de iniciar a aplicação
- Verifique se as credenciais do banco estão corretas no arquivo `application.properties`
- O banco de dados será criado automaticamente pelo Spring Boot na primeira execução

## 🆘 Suporte

Em caso de problemas na execução, verifique:
1. Se o MySQL está rodando
2. Se o banco `techCatalog` foi criado
3. Se as credenciais no `application.properties` estão corretas
4. Se todas as dependências foram baixadas corretamente pelo Maven
