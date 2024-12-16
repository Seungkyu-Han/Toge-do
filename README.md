# Toge-do
### 친구와 일정을 공유 할 수 있는 일정관리 플랫폼입니다.

---

### 개발 기간
##### 2024.12.01 ~

---

### 개발자
##### 프론트엔드: [도안탄히엔](https://github.com/thanhhien234)
##### 백엔드: [한승규](https://github.com/Seungkyu-Han)

---
### 기술 스택
##### 프론트엔드
<img src="https://img.shields.io/badge/React_Native-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="REACT NATIVE">
<img src="https://img.shields.io/badge/React_Query-FF4154?style=for-the-badge&logo=reactquery&logoColor=white" alt="REACT QUERY">
<img src="https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white" alt="TYPE SCRIPT">
<img src="https://img.shields.io/badge/Zustand-000000?style=for-the-badge&logo=placeholder&logoColor=white" alt="ZUSTAND">
<img src="https://img.shields.io/badge/Jest-C21325?style=for-the-badge&logo=jest&logoColor=white" alt="JEST">



##### 백엔드
<img src="https://img.shields.io/badge/kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="KOTLIN">
<img src="https://img.shields.io/badge/Spring Webflux-6DB33F?style=for-the-badge&logo=Spring&logoColor=white" alt="WEBFLUX">
<img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=spring security&logoColor=white" alt="SPRING SECURITY">
<img src="https://img.shields.io/badge/jsonwebtokens-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="SPRING SECURITY">
<img src="https://img.shields.io/badge/reactivex-B7178C?style=for-the-badge&logo=reactivex&logoColor=white" alt="REACTIVEX">
<img src="https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUNIT"> <br>
<img src="https://img.shields.io/badge/mongoDB-47A248?style=for-the-badge&logo=mongoDB&logoColor=white" alt="MONGODB">
<img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white" alt="REDIS">
<img src="https://img.shields.io/badge/apache kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white" alt="APACHE KAFKA">


##### DEVOPS
<img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white" alt="AMAZON EC2">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white" alt="DOCKER">
<img src="https://img.shields.io/badge/jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white" alt="JENKINS">
<img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white" alt="SWAGGER">

---
### 기록
##### INTRO (초기 세팅)
![](image/intro.png)

- 개발 환경 구성
- Spring Cloud Gateway 설정
- Jenkins를 사용한 CI/CD 구성

<br>

##### 스프린트1 (유저 기능 개발)
기간: 2024.12.01 - 2024.12.07 

![](image/스프린트1.png)

- 카카오 로그인
- 카카오 회원가입
- ~~네이버 로그인~~ 구글 로그인으로 변경
- ~~네이버 회원가입~~ 구글 회원가입으로 변경
- 사용자 정보 수정
- 인증 메일 발송
- 이메일 유효성 검사
- 사용자 정보 수정

<br>

##### 스프린트2 (친구 기능 개발)
기간: 2024.12.08 - 2024.12.10

![](image/스프린트2.png)

- 친구 조회
- 친구 신청(이메일 and QR코드)
- 친구 삭제

<br>

##### 스프린트 2-1 (Gateway에 Security 설정)
기간: 2024.12.11 - 2024.12.11

![](image/스프린트2-1.png)

- Gateway에 Security를 설정하고, Filter로 헤더에서 토큰 추출

<br>

##### 스프린트 3 (알림 기능 개발)
기간: 2024.12.12 - 2024.12.14

![](image/스프린트3.png)

- notification 서버에서 SSE API를 사용해 사용자에게 실시간 알림 기능 구현
- notification 서버와 FCM을 연동하여 앱이 실행 중이지 않은 상태에서도 사용자에게 알림 기능 구현

<br>

##### 스프린트 4 (개인 고정 일정 관리 서비스 개발)
기간: 2024.12.15 - 2024.12.16

![](image/스프린트4.png)

- 개인 일정 업로드 기능
- 개인 일정 수정 기능
- 개인 일정 삭제 기능

<br>

##### 스프린트 4-1 (Gateway에 Circuit Breaker 설정)
기간: 2024.12.16 - 2024.12.16

![](image/스프린트4-1.png)

- Gateway에 장애 전파 방지를 위한 Circuit Breaker 설정