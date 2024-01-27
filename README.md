# API de Upload e Download de arquivos 

## 📌 Versão
1.0.0

## 👨‍💻 Autor
* <div style="display: flex; align-items: center;">
    <p style="margin: 0; font-size: 18px;">Hiago Soares | </p>
    <a href="https://www.linkedin.com/in/hiago-soares-96840a271/" style="margin: 10px; margin-top: 15px">
        <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" alt="LinkedIn Badge">
    </a>
</div>

## 🔎 Sobre o projeto
O projeto consiste de uma API que possibilita ao usuário fazer upload ou download de arquivos, tanto em diretório local quanto em banco de dados devidamente configurado.

## 🛠️ Tecnologias utilizadas
* Maven 3.9.0
* Java 17 
* Spring Boot 3.2.2
* Spring Web
* Spring Data JPA | Hibernate
* Banco de dados H2
* JUnit 5
* Swagger (OpenAPI) 2.3.0

## 🔧 Instalação

1. Clone o repositório

````bash
git clone https://github.com/h-Soares/upload-download-api.git
````

2. Navegue até o diretório do projeto

```bash
cd upload-download-api
```
3. Navegue até `src/main/resources/application.properties` e configure as propriedades necessárias para o funcionamento da API tanto em um diretório local quanto em banco de dados.


4. Construir a aplicação:
```bash
mvn clean install
```
O comando irá baixar todas as dependências do projeto e criar um diretório target com os artefatos construídos, que incluem o arquivo jar do projeto. Além disso, serão executados os testes unitários, e se algum falhar, o Maven exibirá essa informação no console.

5. Executar a aplicação:
```bash
mvn spring-boot:run
```
A porta utilizada é a padrão: 8080.

## 🧪 Testes
Para executar todos os testes:
```bash
mvn test
```

## 📖 Documentação com Swagger (OpenAPI)
Com o projeto instalado, para acessar a documentação, vá até:

`http://localhost:8080/swagger-ui/index.html`

## ⚙️ Utilização da API

### **POST** `/api/v1/in-system/files/upload`

**Requer:** arquivo multipart/form-data via corpo da requisição.

**Descrição:**  
Realiza o upload de um arquivo em um diretório.

**Respostas (exemplo):**

Código `200`
````json
{
    "fileName": "string",
    "fileDownloadUri": "string",
    "fileType": "string",
    "fileSize": "string"
}
````
Código `400`
````json
{
  "timestamp": "2024-01-26T20:53:56.216Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````
Código `500`
````json
{
  "timestamp": "2024-01-26T20:53:56.217Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````

### **POST** `/api/v1/in-system/files/uploads`

**Requer:** arquivos multipart/form-data via corpo da requisição.

**Descrição:**  
Realiza o upload de arquivos em um diretório.

**Respostas (exemplo):**

Código `200`
````json
[ 
  {
    "fileName": "string",
    "fileDownloadUri": "string",
    "fileType": "string",
    "fileSize": "string"
  }
] 
````
Código `400`
````json
{
  "timestamp": "2024-01-26T20:53:56.216Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````
Código `500`
````json
{
  "timestamp": "2024-01-26T20:53:56.217Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````

### **GET** `/api/v1/in-system/files`

**Descrição:**  
Lista todos os arquivos do diretório de upload.

**Respostas (exemplo):**

Código `200`
````json
[ 
  {
    "fileName": "string",
    "fileDownloadUri": "string",
    "fileType": "string",
    "fileSize": "string"
  }
] 
````
Código `500`
````json
{
  "timestamp": "2024-01-26T20:53:56.217Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````

### **GET** `/api/v1/in-system/files/download/{fileName}`

**Requer:** parâmetro *fileName* do tipo string.

**Descrição:**  
Faz download de um arquivo do diretório de upload.

**Respostas (exemplo):**

Código `200` download do arquivo.

Código `400`
````json
{
  "timestamp": "2024-01-26T20:53:56.216Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````
Código `404`
````json
{
  "timestamp": "2024-01-26T20:53:56.216Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````
Código `500`
````json
{
  "timestamp": "2024-01-26T20:53:56.216Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````

### **POST** `/api/v1/in-database/files/upload`

**Requer:** arquivo multipart/form-data via corpo da requisição.

**Descrição:**  
Realiza o upload de um arquivo em banco de dados.

**Respostas (exemplo):**

Código `200`
````json
{
    "fileName": "string",
    "fileDownloadUri": "string",
    "fileType": "string",
    "fileSize": "string"
}
````
Código `400`
````json
{
  "timestamp": "2024-01-26T20:53:56.216Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````

### **POST** `/api/v1/in-database/files/uploads`

**Requer:** arquivos multipart/form-data via corpo da requisição.

**Descrição:**  
Realiza o upload de arquivos em banco de dados.

**Respostas (exemplo):**

Código `200`
````json
[ 
  {
    "fileName": "string",
    "fileDownloadUri": "string",
    "fileType": "string",
    "fileSize": "string"
  }
] 
````
Código `400`
````json
{
  "timestamp": "2024-01-26T20:53:56.216Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````

### **GET** `/api/v1/in-database/files`

**Descrição:**  
Lista todos os arquivos do banco de dados.

**Respostas (exemplo):**

Código `200`
````json
[ 
  {
    "fileName": "string",
    "fileDownloadUri": "string",
    "fileType": "string",
    "fileSize": "string"
  }
] 
````

### **GET** `/api/v1/in-database/files/download/{fileName}`

**Requer:** parâmetro *fileName* do tipo string.

**Descrição:**  
Faz download de um arquivo do banco de dados.

**Respostas (exemplo):**

Código `200` download do arquivo.

Código `400`
````json
{
  "timestamp": "2024-01-26T20:53:56.216Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````
Código `404`
````json
{
  "timestamp": "2024-01-26T20:53:56.216Z",
  "status": 0,
  "error": "string",
  "message": "string",
  "path": "string"
}
````