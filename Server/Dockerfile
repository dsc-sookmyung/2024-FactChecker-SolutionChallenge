# Stage 1: Gradle을 사용하여 프로젝트 빌드
FROM gradle:7.2-jdk11 AS builder

WORKDIR /app

# 프로젝트 소스 코드를 복사합니다.
COPY . .

# Gradle 빌드를 실행합니다.
RUN ./gradlew bootJar
# build/libs/ 디렉토리의 내용을 출력합니다.
RUN ls -l /app/build/libs/

# Stage 2: JAR 파일 실행을 위한 이미지 구성
FROM openjdk:11-jdk-slim-buster

# 빌드된 JAR 파일을 복사합니다.
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]



