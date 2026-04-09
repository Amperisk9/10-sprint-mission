# BUILDER STAGE
# 멀티플랫폼 빌드시 에뮬레이터로 빌드 방지용
FROM --platform=$BUILDPLATFORM gradle:8.14-jdk17 AS builder
WORKDIR /build

# Gradle 메타 복사
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle

# 의존성만 다운로드
RUN ./gradlew --no-daemon dependencies || true

# 파일복사(프로젝트 루트에 있는 파일들을 /build에 복사)
COPY . .
# 빌드
# --no-daemon: Gradle 데몬을 실행시키지 않고 일회용으로 실행
RUN ./gradlew --no-daemon clean bootJar

# Amazon Linux 2023 기반의 서버용 JRE
FROM amazoncorretto:17-al2023-headless
# 환경변수
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""
# 작업 디렉토리 설정
WORKDIR /app
# 파일복사(프로젝트 루트에 있는 파일들을 /app에 복사)
COPY --from=builder /build/build/libs/$PROJECT_NAME-$PROJECT_VERSION.jar app.jar

# 서비스 노출포트 설정
EXPOSE 80
# sh -c: 쉘을 실행해서 환경변수 읽어오기
#    -l: 리눅스 프로필 설정 읽기
#  exec: 현재 쉘 프로세스를 java 프로세스로 완전히 교체(즉, 쉘은 java를 실행하고 실행종료)
ENTRYPOINT ["sh", "-lc", "exec java $JVM_OPTS -jar app.jar"]