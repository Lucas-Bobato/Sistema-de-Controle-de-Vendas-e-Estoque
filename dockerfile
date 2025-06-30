# Estágio 1: Compilar a aplicação com Maven e Java 17 (JDK completo)
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace/app

# Copia apenas o necessário para baixar as dependências
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

# Copia o resto do código-fonte e compila
COPY src ./src
RUN ./mvnw -Dmaven.test.skip=true clean package

# Estágio 2: Criar a imagem final, leve, apenas com o Java para rodar (JRE)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia o arquivo .jar compilado do estágio anterior
COPY --from=build /workspace/app/target/sistema-vendas-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java","-jar","app.jar"]