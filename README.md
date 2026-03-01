🛠️ Requisitos de Sistema
Certifique-se de ter instalado:

Java JDK 17 (Versão utilizada no projeto).
MySQL Server 8.0+.
Maven 3.8+ (Para gerenciamento de dependências).
(assim que rodar o projeto tudo de neessario será baixado automaticamente em sua maquina)

⚙️ Configuração Passo a Passo
1. Clonar o Repositório
Abra o seu terminal e execute:

Bash
git clone https://github.com/seu-usuario/barbertime-backend.git
cd barbertime-backend
2. Configurar o Banco de Dados
Acesse o arquivo src/main/resources/application.properties e atualize as configurações com as suas credenciais locais do MySQL:

Properties
spring.datasource.url=jdbc:mysql://localhost:3306/barbertime_db?createDatabaseIfNotExist=true
spring.datasource.username=seu_usuario_mysql
spring.datasource.password=sua_senha_mysql
O banco de dados será criado automaticamente na primeira execução.

3. Fuso Horário e Sincronização
O projeto está configurado para operar no fuso horário de Fortaleza (America/Fortaleza).
Isso garante que as validações de horários de agendamento batam com o relógio local.
Certifique-se de que o relógio do seu sistema operacional esteja correto.

🚀 Executando o Servidor
Para iniciar a API, use o Maven no diretório raiz do projeto:

Bash
mvn spring-boot:run
O servidor estará rodando em: http://localhost:8080.

💡 Informações para Integração
CORS: A API já está liberada para receber requisições do Front-end rodando em http://localhost:5173 (padrão Vite/React).
Autenticação: As telas que exigem login necessitam do envio do token JWT no cabeçalho das requisições via Authorization: Bearer <TOKEN>.
Documentação das Telas: Para saber quais endpoints chamar em cada funcionalidade, consulte os guias de integração específicos que foram enviados separadamente.
