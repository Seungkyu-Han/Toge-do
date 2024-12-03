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
<img src="https://img.shields.io/badge/React_Native-20232A?style=for-the-badge&logo=react&logoColor=61DAFB">
<img src="https://img.shields.io/badge/React_Query-FF4154?style=for-the-badge&logo=reactquery&logoColor=white">
<img src="https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white">
<img src="https://img.shields.io/badge/Zustand-000000?style=for-the-badge&logo=placeholder&logoColor=white">
<img src="https://img.shields.io/badge/Jest-C21325?style=for-the-badge&logo=jest&logoColor=white">



##### 백엔드
<img src="https://img.shields.io/badge/kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
<img src="https://img.shields.io/badge/Spring Webflux-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">
<img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=spring security&logoColor=white">
<img src="https://img.shields.io/badge/reactivex-B7178C?style=for-the-badge&logo=reactivex&logoColor=white">
<img src="https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white"> <br>
<img src="https://img.shields.io/badge/mongoDB-47A248?style=for-the-badge&logo=mongoDB&logoColor=white">
<img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/apache kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white">


##### DEVOPS
<img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white">
<img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white">

---
### 기록
##### INTRO
![](https://private-user-images.githubusercontent.com/98071131/391516107-74c91565-9c2d-404b-b771-c69a0192d738.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzMxMzc1NzAsIm5iZiI6MTczMzEzNzI3MCwicGF0aCI6Ii85ODA3MTEzMS8zOTE1MTYxMDctNzRjOTE1NjUtOWMyZC00MDRiLWI3NzEtYzY5YTAxOTJkNzM4LnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNDEyMDIlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjQxMjAyVDExMDExMFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTQ3NGY5OTQ2MWJlOWZiZmI2Mzg1YTcwZmE4MGYwNTEzNDA4ZWU1MDUyNGE4MTZiZjljMDEzN2M3ZWM5OTZiMWYmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.7piiWZ_H5N2eKlrnshIomelQsuI4vE7uzt808HIUqR0)

- 개발 환경 구성
- Spring Cloud Gateway 설정
- Jenkins를 사용한 CI/CD 구성

##### WEEK1(유저 기능 개발)
![](https://private-user-images.githubusercontent.com/98071131/391542627-0366dd47-168a-4199-8d86-d5f79051c85d.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzMxNDMwNTUsIm5iZiI6MTczMzE0Mjc1NSwicGF0aCI6Ii85ODA3MTEzMS8zOTE1NDI2MjctMDM2NmRkNDctMTY4YS00MTk5LThkODYtZDVmNzkwNTFjODVkLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNDEyMDIlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjQxMjAyVDEyMzIzNVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTBmODhhZGI0NWU1MzUwNzA0ZWFjYTY2YzFlMjg5ODdkYTNlODdlM2ZkMGQxNjFlMDdkODFjM2MxNzg4ZmRjMTUmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0._Kbz-LyC0p_KxPgQb6govB2SVD000XwG00ce-Bb3Y2I)

- 카카오 로그인
- 카카오 회원가입
- 네이버 로그인
- 네이버 회원가입
- 로그아웃
- 이름 수정
- 프로필 사진 수정