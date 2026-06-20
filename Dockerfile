# ---- 构建阶段 ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Asia/Shanghai
ENV LANG=C.UTF-8
COPY --from=build /build/target/fair-scale.jar app.jar
EXPOSE 5120
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
