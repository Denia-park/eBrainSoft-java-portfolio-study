# eBrainSoft-java-portfolio-study

## 진행 과정

1. JSP 게시판 만들기
    - Model - 1
    - [JSP, JDBC, MySQL]
2. Servlet 게시판 만들기
    - Model - 2 MVC 패턴 적용
    - [Servlet, JSP JDBC, MySQL]
3. Spring MVC Framework 게시판 만들기
    - [SpringBoot - MVC Framework, MyBatis, MySQL]
4. Spring + Vue.js 게시판 만들기
    - Restful API를 통한 Client/Server 분리
    - [SpringBoot, Vue.js, MyBatis, MySQL, NginX]
5. 나만의 게시판 게시판 만들기
    - 지금껏 써본 기술들을 바탕으로 자유롭게 만들기.
    - Restful API를 통한 Client/Server 분리
    - [SpringBoot, Vue.js, MyBatis, MySQL, NginX]

## ERD (erdCloud 사용)

- 완성되면 업로드 예정
- [링크](https://www.erdcloud.com/d/3z7DMGmnur8NzHqGE)

## TO-Do List 1주차

### 목록 게시판

- [X] ~~화면 구성~~
- [X] ~~카테고리 읽어 와서 목록 만들기~~
- [ ] 페이지 이동 구현하기 (페이지당 10개의 게시물)
- [ ] 페이지 이동시에 검색 조건 유지하기 (보기, 수정, 쓰기 모두 적용 필요)
- [ ] 검색 기능 추가하기
- [ ] 제목 80자 넘으면 ... 처리하기

### 보기 게시판

- [X] ~~화면 출력~~
- [ ] 첨부 파일 표시 (2개 이상 표시, 파일명 + 확장자, 클릭 시 다운로드 , 바이너리 다운로드 형태 [링크 X])
- [ ] 댓글 구현 (최근 등록한 댓글이 아래로 가도록)
- [ ] 목록 / 수정 / 삭제 버튼 구현
- [ ] 수정, 삭제시에 팝업이 뜨며 비밀번호 확인 후 수정 및 삭제 가능
- [ ] 조회수 적용하기

### 비밀번호 확인 팝업

- [X] ~~화면 출력~~
- [ ] 확인 버튼 구현 -> 비밀번호 틀리면 alert
- [ ] 취소 버튼 구현

### 등록 게시판

- [X] ~~화면 출력~~
- [ ] 비밀번호 저장시에 암호화 (SHA-256)
- [ ] 취소 / 저장 버튼 구현
- [ ] 프론트단에서도 유효성 검증하기
    - [ ] 카테고리
    - [ ] 작성자
    - [ ] 비밀번호 와 비밀번호 확인
    - [ ] 제목
    - [ ] 내용
- [ ] 서버단에서도 유효성 검증하기
    - [ ] 카테고리
    - [ ] 작성자
    - [ ] 비밀번호 와 비밀번호 확인
    - [ ] 제목
    - [ ] 내용
- [ ] 유효성 검증 실패시 현 페이지 유지

### 수정 게시판

- [ ] 수정을 위해 기존에 있는 글 내용 그대로 읽어오기
    - [ ] 작성자, 제목, 내용만 수정이 가능
    - [ ] 비밀번호는 입력을 위해 비워두기, PlaceHolder 입력 - (입력해야 수정이 가능함)
    - [ ] 파일 첨부
        - [ ] 가져오고 다운로드시에 다운로드 가능해야 함
        - [ ] 삭제 버튼 누르면 파일 삭제 -> 취소하는 경우 파일 삭제도 취소 안됨
- [ ] 수정하면 수정일 적용하기

# Docker를 통한 MySQL 서버 띄우기

- docker up [MySQL]

> `docker-compose up -d`
