# eBrainSoft-java-portfolio-study

<!-- TOC -->

* [eBrainSoft-java-portfolio-study](#ebrainsoft-java-portfolio-study)
    * [진행 과정](#진행-과정)
    * [ERD (erdCloud 사용)](#erd-erdcloud-사용)
    * [Docker를 통한 MySQL 서버 띄우기](#docker를-통한-mysql-서버-띄우기)
    * [집중적으로 코드 리뷰 받을 부분 정리](#집중적으로-코드-리뷰-받을-부분-정리)
        * [1주차](#1주차)
    * [To-Do List 1주차](#to-do-list-1주차)
        * [게시판 - 목록](#게시판---목록)
        * [게시판 - 보기](#게시판---보기)
        * [비밀번호 확인 팝업](#비밀번호-확인-팝업)
        * [게시판 - 등록](#게시판---등록)
            * [파일 업로드 기능 구현시 참고 자료](#파일-업로드-기능-구현시-참고-자료)
        * [게시판 - 수정](#게시판---수정)
        * [공통](#공통)
    * [To-Do List 2주차](#to-do-list-2주차)
        * [공통](#공통-1)
        * [게시판 - 목록](#게시판---목록-1)
        * [게시판 - 보기](#게시판---보기-1)
        * [비밀번호 확인 팝업](#비밀번호-확인-팝업-1)
        * [게시판 - 등록](#게시판---등록-1)
        * [게시판 - 수정](#게시판---수정-1)
    * [구현하면서 힘들었던 점](#구현하면서-힘들었던-점)
        * [ToDo 1주차](#todo-1주차)
    * [피드백](#피드백)
        * [1주차](#1주차-1)
            * [중요한 부분만 발췌](#중요한-부분만-발췌)
    * [다음 주 차에 진행할 내용에 관해서 설명](#다음-주-차에-진행할-내용에-관해서-설명)
        * [2주차 내용](#2주차-내용)
            * [관련된 내 생각 [23.05.20]](#관련된-내-생각-230520)

<!-- TOC -->



---

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

---

## ERD (erdCloud 사용)

![ERD](https://github.com/Denia-park/eBrainSoft-java-portfolio-study/assets/80137359/671189ff-0300-48e3-9149-097864dd39b0)

- [링크](https://www.erdcloud.com/d/3z7DMGmnur8NzHqGE)

---

## Docker를 통한 MySQL 서버 띄우기

- docker up [MySQL] : `docker-compose up -d`

---

## 집중적으로 코드 리뷰 받을 부분 정리

### 1주차

<details>
    <summary>1주차</summary>

- 프로젝트 파일 구성은 올바른가 ?
- JSTL을 어디까지 사용하는게 좋은지?
- JSP 파일의 내용은 어떻게 구성하는게 잘 구성하는 것인지 ?
    - JSP로 코드 짤때 어디까지 JSP로 사용하고 어디까지는 Class 를 나눠서 기능을 나눠서 구현하는지 잘 모르겠다.

</details>

---

## To-Do List 1주차

<details>
    <summary>1주차</summary>

### 게시판 - 목록

- [X] ~~화면 구성~~
- [X] ~~카테고리 읽어 와서 목록 만들기~~
- [X] ~~페이지 이동시에 검색 조건 유지하기 (보기, 수정, 쓰기 모두 적용 필요)~~
- [X] ~~검색 기능 추가하기~~
    - [X] ~~날짜 필터링~~
    - [X] ~~검색어 필터링~~
    - [X] ~~페이지 옮겨도 검색 조건 유지~~
- [X] ~~제목 80자 넘으면 ... 처리하기~~
- [X] ~~등록 버튼 기능 추가~~
- [X] ~~페이지 이동 구현하기 (페이지당 10개의 게시물)~~
- [X] ~~페이지 버튼 추가~~
- [X] ~~페이지 수 제한 걸기~~
- [X] ~~목록으로 나와도 내가 봤던 페이지로 이동하게 하기~~

### 게시판 - 보기

- [X] ~~화면 출력~~
- [X] ~~DB에서 데이터 불러와서 반영하기~~
- [X] ~~게시글 누르면 해당 페이지로 이동~~
- [X] ~~댓글 등록~~
- [X] ~~댓글 표시 (최근 등록한 댓글이 아래로 가도록)~~
- [X] ~~조회수 적용하기~~
- [X] ~~수정, 삭제시에 팝업이 뜨며 비밀번호 확인 후 수정 및 삭제 가능~~
- [X] ~~목록 / 수정 / 삭제 버튼 구현~~
    - [X] ~~목록 버튼 구현~~
    - [X] ~~삭제~~
    - [X] ~~수정~~
- [X] ~~첨부 파일 표시 (2개 이상 표시, 파일명 + 확장자, 클릭 시 다운로드 , 바이너리 다운로드 형태 [링크 X])~~

### 비밀번호 확인 팝업

- [X] ~~화면 출력~~
- [X] ~~취소 버튼 구현~~
- [X] ~~확인 버튼 구현~~
    - [X] ~~삭제~~
    - [X] ~~수정~~

### 게시판 - 등록

- [X] ~~화면 출력~~
- [X] ~~취소 / 저장 버튼 구현~~
- [X] ~~프론트단에서도 유효성 검증하기~~
    - [X] ~~카테고리~~
    - [X] ~~작성자~~
    - [X] ~~비밀번호 와 비밀번호 확인~~
    - [X] ~~제목~~
    - [X] ~~내용~~
- [X] ~~유효성 검증 실패시 현 페이지 유지~~
- [X] ~~데이터 저장 기능~~
- [X] ~~서버단에서도 유효성 검증하기~~
    - [X] ~~카테고리~~
    - [X] ~~작성자~~
    - [X] ~~비밀번호~~
    - [X] ~~제목~~
    - [X] ~~내용~~
- [X] ~~비밀번호 저장시에 암호화 (SHA-256)~~
- [X] ~~파일 업로드 기능 구현~~

#### 파일 업로드 기능 구현시 참고 자료

- [나동빈님 유튜브 - JSP 파일 업로드 강좌 1강](https://youtu.be/UQVyytDtLzQ)
- [com.oreilly.servlet 라이브러리](http://www.servlets.com/cos/)

### 게시판 - 수정

- [X] ~~게시글 수정~~
    - [X] ~~기존 게시글 내용 읽어오기~~
    - [X] ~~작성자, 제목, 내용만 수정이 가능~~
    - [X] ~~비밀번호는 입력을 위해 비워두기, PlaceHolder 입력 - (입력해야 수정이 가능함)~~
    - [X] ~~파일 첨부~~
        - [X] ~~가져오고 다운로드시에 다운로드 가능해야 함~~
        - [X] ~~삭제 버튼 누르면 파일 삭제 -> 취소하는 경우 파일 삭제도 취소~~
- [X] ~~수정하면 수정일 적용하기~~
- [X] ~~직접 주소로 접근하는 경우 막기~~
- [X] ~~유효성 검증은 프론트, 백 모두 처리~~

### 공통

- [X] ~~사용한 JDBC Connection들을 close 해주기.~~
- [X] ~~refactoring 진행~~

</details>

## To-Do List 2주차

<details>
    <summary>2주차</summary>

### 공통

- [X] ~~command 패턴 적용을 위해 Interface 만들기~~
- [X] ~~프론트 컨트롤러 패턴을 적용하기 위해 프론트 컨트롤러 만들기 [Model-2]~~
    - [X] ~~모든 요청을 다 받도록 하기.~~
    - [X] ~~요청마다 각 해당하는 컨트롤러를 호출해서 처리하도록 하기.~~
- [X] ~~Log 남기기~~
- [X] ~~가능하면 메서드마다 주석 (JavaDoc) 달아주기~~
    - [X] ~~BoardRepository~~
    - [X] ~~CategoryRepository~~
    - [X] ~~CommentRepository~~
    - [X] ~~FileRepository~~
    - [X] ~~FileUtil~~
- [ ] Header 사용해보기

### 게시판 - 목록

- [X] ~~각 해당하는 서비스로 코드 분리하기~~

### 게시판 - 보기

- [X] ~~각 해당하는 서비스로 코드 분리하기~~

### 비밀번호 확인 팝업

- [X] ~~각 해당하는 서비스로 코드 분리하기~~

### 게시판 - 등록

- [X] ~~각 해당하는 서비스로 코드 분리하기~~

### 게시판 - 수정

- [X] ~~각 해당하는 서비스로 코드 분리하기~~

</details>

---

## 구현하면서 힘들었던 점

### ToDo 1주차

<details>
    <summary>1주차</summary>

1. JSP만을 사용해서 구현을 하려니 불편한 점이 너무 많다.
    - MultiPart/form-data 를 받아오려면 무조건 라이브러리가 필요하다
        - 라이브러리를 안 쓸꺼면 InputStream을 받아서 String으로 변환을 해야한다.
        - 라이브러리를 쓰게되면 만약에 전달할 데이터중에 File 데이터가 있을 경우 무조건 저장이 된다.
            - 이 부분 때문에 URL로 PW를 넘긴다고 고생했다. (URL-Safe Base64 인코딩 ... 방법 찾는다고 한참 찾았다.)
    - Java 코드랑 HTML이 섞여있어서 가독성이 정말 별로다.
    - JSTL을 써서 그나마 쉽게 코딩을 했지 이거라도 없었으면 더 힘들었을 것 같다.
        - ${ } 를 쓰려고 하다보니 pageContext에 종속적인 코드가 되어버렸다.
2. 처음부터 제대로 설계를 하지 않고 코드를 짜면 나중에 리팩터링 할때 지옥을 맛본다 .. ㅠㅠ

</details>

<details>
    <summary>2주차</summary>

1. 1주차때 정말 고생해서 열심히 리팩터링 해놨더니 2주차는 생각보다 많이 쉬웠다.
    - 기존 코드에서 약간씩 변환만 해주면 됐다.

</details>

## 피드백

### 1주차

#### 중요한 부분만 발췌

- ★★★ 메서드 관련 주석들을 작성하는 습관을 들이자!!
- ★★★ Log를 남기는 습관을 들이자.
- ★★ 커넥션을 사용하는 경우 쿼리를 날릴때마다 커넥션을 열고 사용을 다했으면 닫는게 나중에 문제 발생 여지가 적다. [Resource는 빠르게 쓰고 해제하자.]
- ★ 경로 같은 것들은 환경 변수로 받으면 좋다 (Ex. 파일 저장 경로)
- ★ 중복되는 Header, Footer를 Include 기능을 통해서 공통 로직을 처리하자.
- ★ 서버에서는 데이터를 View에 맞춰서 가공하기 보다 그대로 내려주고, View에서 해당 데이터들을 가공하자.
- ★ SQL Injection을 막기 위해서 PreparedStatement를 사용하자.
- ★ `Class.forName()`의 역할은 ?

<details>
    <summary>1주차 구체적인 내용</summary>

1. 코드 작성
    - 중복되는 패턴이 나오는 경우에 리팩터링을 생각해보자.
    - Util 이라는 이름은 관련된 처리를 도와주는 로직이 들어있는게 맞다. DB에 접근하는 클래스는 DAO 클래스 명을 쓰자.
        - Util 이나 Dao 는 상태 값을 가지지 않게 코드를 작성하자.
    - ★경로 같은 것들은 환경 변수로 받으면 좋다 (Ex. 파일 저장 경로)
    - ★중복되는 Header, Footer를 Include 기능을 통해서 공통 로직을 처리하자.
    - 잘 변경이 되지 않는 데이터들은 DB로 처리 / 변경이 좀 잦은 것들은 코드로 관리 (Enum 사용)
    - ★서버에서는 데이터를 View에 맞춰서 가공하기 보다 그대로 내려주고, View에서 해당 데이터들을 가공하자.
        - 국제화, View에 따른 보여주는 데이터를 달리 하기 위해서는 해당 방식이 편하다.
    - ★★★메서드 관련 주석들을 작성하는 습관을 들이자!!
        - 소스 전달시에는 해당 문서도 같이 전달해야 하는데 JavaDoc이 있으면 좋다.
            - 주석을 잘 작성해두면, JavaDoc을 쉽게 만들 수 있다.
    - ★★★Log를 남기는 습관을 들이자.
        - 디버깅시에 유용함
        - 기록을 보관하거나, 후처리가 용이하다.
        - 네이밍은 짧게 축약해서 쓰는것보단 Full로 쓰는게 차라리 더 낫다.
2. DB 사용
    - ★★커넥션을 사용하는 경우 쿼리를 날릴때마다 커넥션을 열고 사용을 다했으면 닫는게 나중에 문제 발생 여지가 적다. [Resource는 빠르게 쓰고 해제하자.]
        - try 이후에 finally 를 통해 close를 호출!!
        - DB를 싱글톤으로 사용하게 되면 동시성에 문제가 생긴다.
    - ★SQL Injection을 막기 위해서 PreparedStatement를 사용하자.
    - 실제 DB에서 사용되고 있는 Column이 노출되지 않게 신경쓰자.
3. File 저장
    - File을 저장할 때 (내부 서버를 쓰는 것이라면) 서버 구동과 관련 없는 안전한 곳을 선택하자.
        - 잘못하면 서버를 새롭게 배포할때마다 File들이 초기화 될 수도 있다.
    - File 관련 저장시에는 File 이름 외에도 저장 경로를 따로 저장하면 좋다.
    - File을 요청하는 로직을 사용할때 파일 이름을 사용자가 모르게 File_Id 같은 것을 쓰자.
    -
4. 공부
    - Multipart 요청 같은 것들은 직접 Parsing 같은 것을 해보면 공부에 도움이 된다.
    - ★`Class.forName()`의 역할은 ?
        - 클래스 로더의 역할, 해당 이름 찾아서 ClassLoader에 올려라.
        - ※ mySql.Driver 를 클래스에 로딩하면 해당 Driver에 있는
          static 생성자가 이것 저것 많은 처리들을 한다.

</details>

## 다음 주 차에 진행할 내용에 관해서 설명

### 2주차 내용

1. 커맨드 패턴을 써보자.
    - 호출 서비스들을 하나의 인터페이스를 바탕으로 모두 구현하고, 구현체들을 Map에 넣은 후 나중에 해당 맵에서 구현체를 불러다 쓰는 형식으로 가자.
    - 하나의 모든 URI를 받는 서블릿을 만들고, 밑에서 해당하는 URI 별로 각각의 서비스를 호출해서 돌아가는 식으로 만들어 보자.
        - 디스패처 서블릿 역할.
2. 컨트롤러, 서비스가 따로 존재하고 컨트롤러는 호출만 하고 서비스에서 forward 를 하면 됨.
    - 화면 기준으로 하나의 서비스가 있어야 한다.

#### 관련된 내 생각 [23.05.20]

- 스프링 MVC 프레임워크가 어떻게 만들어진 것인지 알려주기 위함인 것 같다.
