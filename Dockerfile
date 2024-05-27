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
RUN apk update && \
    apk add dos2unix && \
    dos2unix ./mvnw

# Resuelve las dependencias
RUN ./mvnw dependency:resolve

# Copia el resto del código fuente
COPY src ./src

# Exponer el puerto 8080
EXPOSE 8080

# Define el perfil a utilizar (por defecto, production)
ARG PROFILE=production

# Comando para ejecutar la aplicación con el perfil especificado
CMD ["./mvnw", "spring-boot:run", "-P${PROFILE}"]