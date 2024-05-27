# Usa una imagen base con JDK 17
FROM eclipse-temurin:17-jdk-jammy

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de Maven y el archivo pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Da permisos de ejecución al Maven Wrapper
RUN chmod +x ./mvnw

# Instala dos2unix para convertir los finales de línea
RUN apt-get update && apt-get install -y dos2unix
RUN dos2unix ./mvnw

# Resuelve las dependencias
RUN ./mvnw dependency:resolve

# Copia el resto del código fuente
COPY src ./src

# Exponer el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["./mvnw", "spring-boot:run"]