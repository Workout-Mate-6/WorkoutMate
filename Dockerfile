# 1. 베이스 이미지 선택 (JDK 17, MAC 기반)
FROM eclipse-temurin:17-jdk

# ARG 받도록 구성 (환경변수 주입)
ARG SECRET_KEY
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD

# 2. JAR 파일이 생성될 경로를 변수로 지정
ARG JAR_FILE_PATH=build/libs/*.jar

# 3. build/libs/ 에 있는 JAR 파일을 WorkoutMate.jar 라는 이름으로 복사
COPY ${JAR_FILE_PATH} WorkoutMate.jar

# 4. 컨테이너가 시작될 때 이 명령어를 실행
ENTRYPOINT ["java", "-jar", "/WorkoutMate.jar"]

#FROM: 어떤 환경을 기반으로 이미지를 만들지 선택.
#COPY: 내 컴퓨터에 있는 파일(빌드된 .jar 파일)을 도커 이미지 안으로 복사하는 명령어
#ENTRYPOINT: 도커 컨테이너가 시작될 때 실행될 명령어. 즉, java -jar WorkoutMate.jar 명령으로 스프링 부트 애플리케이션을 실행