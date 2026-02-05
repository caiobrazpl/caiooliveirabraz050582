# Etapa 1: build da aplicação usando Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Copia o código fonte e o arquivo pom.xml
COPY . /app
WORKDIR /app

# Compila o projeto e gera o .jar
RUN mvn clean package -U -DskipTests=true

# Etapa 2: imagem final com apenas o .jar e o JDK
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia apenas o .jar gerado da etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Define o ponto de entrada da aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
