# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only pom.xml first so dependency downloads are cached
COPY pom.xml .
RUN mvn -q -B dependency:go-offline || true

# Now copy the source and build
COPY src ./src
RUN mvn -q -B dependency:copy-dependencies -DoutputDirectory=/app/target/dependency
RUN mvn -q -B compile

# ---------- Run stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Compiled classes + all runtime dependency jars
COPY --from=build /app/target/classes ./target/classes
COPY --from=build /app/target/dependency ./target/dependency

# Static frontend + JSPs, kept at the same relative path WebServer.java expects
COPY src/main/webapp ./src/main/webapp

# Render/Railway/Fly all set PORT; WebServer.java reads it, default 8080
ENV PORT=8080
EXPOSE 8080

CMD ["java", "-cp", "target/classes:target/dependency/*", "com.org.server.WebServer"]
