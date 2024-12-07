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
##### INTRO
![](https://private-user-images.githubusercontent.com/98071131/391516107-74c91565-9c2d-404b-b771-c69a0192d738.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzM1MDAzMTUsIm5iZiI6MTczMzUwMDAxNSwicGF0aCI6Ii85ODA3MTEzMS8zOTE1MTYxMDctNzRjOTE1NjUtOWMyZC00MDRiLWI3NzEtYzY5YTAxOTJkNzM4LnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNDEyMDYlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjQxMjA2VDE1NDY1NVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWQ0YWU1MGJlNTgwNWFlZWVlZDE0YjRjZWU5OTkwODlhYTY3NjBhZjEwNDc2MTVlZDUxZWU0ZGY4MGZiYzgzZmMmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.m_dI3HJ0K2bPcPpmV-vRrvUWhMwSdCeQbyuy_QVa3j0)

- 개발 환경 구성
- Spring Cloud Gateway 설정
- Jenkins를 사용한 CI/CD 구성

##### WEEK1(유저 기능 개발)
![](https://private-user-images.githubusercontent.com/98071131/391542627-0366dd47-168a-4199-8d86-d5f79051c85d.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzM1MDAzMzksIm5iZiI6MTczMzUwMDAzOSwicGF0aCI6Ii85ODA3MTEzMS8zOTE1NDI2MjctMDM2NmRkNDctMTY4YS00MTk5LThkODYtZDVmNzkwNTFjODVkLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNDEyMDYlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjQxMjA2VDE1NDcxOVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWU1OTRlZmM5NDQxMTMyN2Q0ZjBiMmE3ZjM3NTBkOWEwMjM2ZTI3NGI1MzJlYjQ2OTBkNmE2NDQ0YTI2YzAyZmEmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.sI7RDE6mcpczwGux_rPHFGpMJU7vkG4Ilm-7qtrYqWQ)

- 카카오 로그인
- 카카오 회원가입
- 네이버 로그인
- 네이버 회원가입
- 로그아웃
- 이름 수정
- 프로필 사진 수정