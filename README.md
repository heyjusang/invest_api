# Invest

## 환경
* Spring Boot 2.4.3
  * Dependency
    * spring-boot-starter-web (Spring MVC)
    * mybatis-spring-boot-starter (Mybatis)
    * h2 (H2 in-memory database (mode: oracle))
    * mockito-kotlin (Mockito for Kotlin)
    * spring-boot-starter-security (Spring Authentication)
    * jjwt-api, jjwt-impl, jjwt-jackson (JWT)
* Java 11
* Kotlin 1.4.30
 
## 실행
* mvn spring-boot:run
  * 세팅 후, src/main/resources/data/에 있는 schema.sql, data.sql 수행
* 단위테스트, 통합테스트 실행
  * mvn test

## API 목록
* 모집 기간 내 투자 상품 조회
  * [GET] /products
  * 리턴
  ``` json
  [{"id":1,"title":"Normal product","totalInvestingAmount":2000000,"currentInvestingAmount":100,"investorCount":1,"startedAt":"2021-03-10T12:00:00","finishedAt":"2022-04-15T12:00:00","soldOut":"N"}, ...]
  ```
* 유저의 투자 내역 조회
  * [GET] /investments (Header: {X-USER-ID: Int, X-AUTH-TOKEN: String})
  * 리턴
  ``` json
  [{"id":1,"userId":10,"productId":3,"productTitle":"Sold out product","totalInvestingAmount":2000000,"amount":1000000,"createdAt":"2021-03-13T22:01:01"}, ...]
  ```
* 투자 하기
  * [POST] /investment (Header: {X-USER-ID: Int, X-AUTH-TOKEN: String}, Param: {product_id: Int, amount: Int})
  * 리턴
  ``` json
  {"success":true}
  ```
* 에러 발생 및 처리 실패 시
  * 리턴
  ``` json
  {"errorCode": 90005,"message":"Amount exceeded total investing amount"}
  ```
  
## DB Schema
### Table
* Product (상품정보)

|Name|Null|Type|            
|---|---|---| 
|ID                      | NOT NULL| NUMBER |       
|TITLE                   | NOT NULL| VARCHAR2(100) |
|TOTAL_INVESTING_AMOUNT  |NOT NULL| NUMBER |       
|CURRENT_INVESTING_AMOUNT|        | NUMBER |       
|INVESTOR_COUNT          |         | NUMBER |       
|STARTED_AT              |NOT NULL| DATE   |       
|FINISHED_AT             |NOT NULL| DATE   |       
|CREATED_AT              |       | DATE   |       

* Investment (투자정보)

|Name|         Null |      Type  |   
|----------| --------| ------ |
|ID        | NOT NULL| NUMBER| 
|USER_ID    |NOT NULL| NUMBER| 
|PRODUCT_ID |NOT NULL| NUMBER| 
|AMOUNT    | NOT NULL| NUMBER| 
|CREATED_AT |        | DATE  | 

* Investor (유저정보)

|Name|         Null |      Type  |   
|----------| --------| ------ |
|ID        | NOT NULL| NUMBER| 
|NAME    |NOT NULL| VARCHAR2(20)| 
|PASSWORD |NOT NULL| VARCHAR2(200)| 
|ROLE    | NOT NULL| VARCHAR2(10)| 
|CREATED_AT |        | DATE  | 

### Index

|Table Name| Index Name| Column Name|
|----------| --------| ------ |
|PRODUCT	|IDX_PRODUCT	|STARTED_AT
|PRODUCT	|IDX_PRODUCT	|FINISHED_AT
|PRODUCT	|SYS_C0020491	|ID
|INVESTMENT	|IDX_INVESTMENT	|USER_ID
|INVESTMENT	|SYS_C0020497	|ID
|INVESTMENT	|UQ_USER_ID_PRODUCT_ID	|USER_ID
|INVESTMENT	|UQ_USER_ID_PRODUCT_ID	|PRODUCT_ID
|INVESTOR| SYS_C0024762| ID
|INVESTOR| SYS_C0024763| NAME

### Constraint

|Table Name| Constraint|
|----------| --------|
|PRODUCT	|"ID" IS NOT NULL|
|PRODUCT	|"TITLE" IS NOT NULL|
|PRODUCT	|"TOTAL_INVESTING_AMOUNT" IS NOT NULL|
|PRODUCT	|"STARTED_AT" IS NOT NULL|
|PRODUCT	|"FINISHED_AT" IS NOT NULL|
|PRODUCT	|total_investing_amount >= current_investing_amount|
|INVESTMENT	|"ID" IS NOT NULL|
|INVESTMENT	|"USER_ID" IS NOT NULL|
|INVESTMENT	|"PRODUCT_ID" IS NOT NULL|
|INVESTMENT	|"AMOUNT" IS NOT NULL|
|INVESTOR   |"ID" IS NOT NULL|
|INVESTOR|"NAME IS NOT NULL|
|INVESTOR|"PASSWORD IS NOT NULL|

## 프로젝트 구성
### models/
* Product (상품 정보)
  * id, title, totalInvestingAmount, currentInvestingAmount, investorCount, startedAt, finishedAt, soldOut
* Investment (투자 정보)
  * id, userId, productId, productTitle, totalInvestingAmount, amount, createdAt
* User (유저 정보)
  * id, name, password, role, createdAt
  
### controllers/
* InvestmentController
  * getProducts()
    * [GET] /products
    * 상품 모집 기간 내의 전체 투자 상품 조회
    * Product 리스트 리턴
  * getInvestments()
    * [GET] /investments (Header: {X-USER-ID: Int})
    * X-USER-ID에 해당하는 유저가 투자한 모든 투자 내역 반환
    * Authentication 정보의 user id와 X-USER-ID가 일치해야함
    * Investment 리스트 리턴
  * createInvestment()
    * [POST] /investment (Header: {X-USER-ID: Int}, Param: {product_id: Int, amount: Int})
    * X-USER-ID에 해당하는 유저가 product_id에 해당하는 상품에 amount만큼의 금액을 투자
    * Authentication 정보의 user id와 X-USER-ID가 일치해야함
    * 성공 시, {success: true} 형태로 리턴
    * 실패 시, 실패 원인에 따라 {erroCode: Int, message: String} 형태로 리턴
  * sqlException() (ExceptionHandler)
    * DB 에러를 핸들링
    * 에러 발생 시 {errorCode: Int, message: String} 형태로 리턴 (Http Status Code: 500)
  * baseException() (ExceptionHandler)
    * service layer 로직상에서 발생한 에러 핸들링
    * 에러 발생 시 {errorCode: Int, message: String) 형태로 리턴 (Http Status Code: 400 or 404)
* SignController
  * signIn()
    * [POST] /signin (Param: {name: String, password: String})
    * 로그인
    * User Token 리턴
  * signUp()
    * [POST] /signup (Param: {name: String, password: String})
    * 회원가입
    * 성공 시, {success: true} 형태로 리턴
  * sqlException() (ExceptionHandler)
  * baseException() (ExceptionHandler)
    
### services/
* InvestmentService
* InvestmentServiceImpl
  * getProducts()
    * 전체 투자 상품 조회 처리 로직
  * getInvestments()
    * 유저의 투자 내역 조회 처리 로직
  * createInvestment()
    * 투자하기 처리 로직
    * 0 이하의 금액 투자 시도 -> InvalidAmountException
    * 없는 투자 상품에 투자 시도 -> ProductNotFoundException
    * 아직 열리지 않은 상품에 투자 시도 -> ProductNotOpenedException
    * 닫힌 상품에 투자 시도 -> ProductClosedException
    * 매진된 상품에 투자하거나, 투자 금액이 남은 투자 가능 금액을 넘어설 때 -> TotalInvestingAmountExceedException
* SignService
* SignServiceImpl
  * signIn()
    * 로그인
    * 없는 유저 -> UserNotFoundException
    * 비밀번호 오류 -> UserAlreadyExistedException
  * signUp()
    * 회원가입
    * 있는 유저 -> UserAlreadyExistedException
    
### repositories/
* InvestmentRepository
  * DB와 통신을 위한 쿼리를 정의한 Mybatis Mapper
  * selectProducts()
    * 전체 투자 상품 조회를 위한 쿼리  
    ``` sql
    SELECT id, title, total_investing_amount, current_investing_amount, investor_count, started_at, finished_at,
      CASE WHEN total_investing_amount > current_investing_amount THEN 'N'
      ELSE 'Y'
      END AS sold_out
    FROM product WHERE SYSDATE >= started_at AND SYSDATE < finished_at
    ```
  * selectInvestments()
    * 유저의 투자 내역 조회를 위한 쿼리
    ``` sql
    SELECT i.id, i.user_id, i.product_id, p.title AS product_title, p.total_investing_amount, i.amount, i.created_at
    FROM investment i, product p WHERE i.user_id = #{userId} AND p.id = i.product_id
    ```
  * selectProductForUpdate()
    * 투자를 위한 Product를 가져오는 쿼리
    ``` sql
    SELECT * FROM product WHERE id = #{productId} FOR UPDATE
    ```
  * updateProduct()
    * 투자한 Product를 업데이트하기 위한 쿼리
    ``` sql
    UPDATE product SET current_investing_amount = current_investing_amount + #{amount},
      investor_count = investor_count + 1
    WHERE id = #{productId}"
    ```
  * insertInvestment()
    * 투자 내역을 생성하는 쿼리
    ``` sql
    INSERT INTO investment(user_id, amount, product_id) VALUES (#{userId}, #{amount}, #{productId})
    ```
* SignRepository
  * selectUserByName()
    * 유저정보 가져오는 쿼리
    ``` sql
    SELECT * FROM investor WHERE name = #{name}
    ```
  * selectUserCountByName()
    * 유저 이름에 해당하는 유저가 있나 확인하는 쿼리
    ``` sql
    SELECT COUNT(*) FROM investor WHERE name = #{name}
    ```
  * insertUser()
    * 새로운 유저 생성하는 쿼리
    ``` sql
    INSERT INTO investor(name, password) VALUES (#{name}, #{password})
    ```
### exceptions/
* 내부 로직에서 발생할 때 throw할 에러를 정의
* ErrorCode
  * 에러들의 에러코드 정의
* BaseException
  * 내부에서 사용할 에러 클래스 목록
  * BaseException을 정의하고, 실제 exception들은 BaseException을 상속해서 정의
* ErrorMessage
  * 에러 발생 시 리턴해줄 메시지 정의

### utils/
* JwtTokenProvider
  * JWT의 생성, 추출, 검증 및 User Authentication 생성을 담당

### filters/
* JwtAuthenticationFilter
  * JwtTokenProvider를 이용하여 Http request로부터 토큰 추출, 검증 후 Authentication 설정

### configs/
* SecurityConfiguration
  * UsernamePasswordAutenticationFilter 수행 전 JwtAutenticationFilter 사용

## 동시성 문제
### 동시에 여러 유저가 동일한 상품에 투자하고자 할 때, 어떻게 처리할 것인가?
* 동시에 한 상품 정보를 수정하게 되면, 총 투자 모집 금액을 넘어서 투자되는 등의 문제가 발생할 수 있음

### 해결방안 1 - DB Constraint
* Product Table의 상품 정보를 업데이트할 때, current_investing_amount가 total_investing_amount 넘지 못하도록 Constraint 설정

### 해결방안 2 - Row-level Lock
* 투자를 할 때, 투자할 상품을 SELECT FOR UPDATE 쿼리를 통해 가져옴
* 해당 쿼리를 통해, 다른 사용자들은 동일한 상품의 투자 시도에 대해 wait 상태
* 투자에 성공하거나 에러 등의 이유로 실패한 후, 다른 사용자들이 투자 가능

### 해결방안 3 - @Transactional
* 투자하기 로직인 InvestmentService의 createInvestment() 로직은 크게 다음과 같이 3단계로 나눠짐
  * InvestmentRepository.selectForUpdate() -> InvestmentRepository.insertInvestment() -> InvestmentRepository.updateProduct()
* 위 로직은 Transactional annotation을 통해 트랜잭션으로 묶어서, investment 생성이나 product 업데이트 중 하나라도 실패 시 모두 롤백

## 잘못된 투자정보 생성
### 잘못된 데이터가 들어오는 경우 방어로직
* 0 보다 작은 금액을 투자하는 경우
* 없는 상품에 투자하는 경우
* 아직 열리지 않는 상품에 투자하는 경우
* 닫힌 상품에 투자하는 경우
* 투자 금액이 너무 커서, 현재 모집 금액이 총 모집 금액을 넘어서는 경우
* 동일한 상품에 대해 동일한 사용자가 투자하는 경우

### 해결방안 1 - DB Constraints
* DB에 Constraint를 걸어 잘못된 데이터가 들어오는것을 막음
  * Product Table
    * CHECK (total_investing_amount >= current_investing_amount)
  * Investment Table
    * CHECK (amount > 0)
    * UNIQUE (user_id, product_id)
* 에러 발생 시 SQLException 리턴

### 해결방안 2 - Service Layer Exception
* DB에 Insert, Update를 시도하기 전에 1차적으로 Service Layer에서 값을 확인 후, 문제 있으면 Exception 발생시킴
* 에러 발생 시 BaseException 리턴

## Test
* 총 72개 테스트
* 테스트에 필요한 sql script는 src/main/resources/data/에 있음
  * schema.sql - 테스트에 필요한 스키마 생성
  * data.sql - 테스트에 필요한 데이터 생성
  * drop_db.sql - 테스트 완료 후 스키마 삭제
* 위 sql script는 항상 수행하는 것은 아니고, 실제 DB와 통신이 필요한 Repository Layer 단위테스트와 Application 통합테스트에서만 사용

### InvestmentRepositoryTests
* 11개 테스트
* Repository Layer 기능 검사를 위한 단위 테스트

| 테스트 이름 | 테스트 내용 |
|---|---|
|testRepository should be configured() | 환경 테스트 |
|we should get 23 products (including 1 sold out) when requesting a list of product within the period()| getProducts() 테스트|
|we should get 2 investments when requesting a list of investment for user with an ID of 10()| getInvestments() 테스트|
|we should get product with an ID of 1| selectProductForUpdate() 테스트|
|we cannot get product without proper ID| seleectProductForUpdate() 실패 테스트 - 없는 상품|
|we should update product with an ID of 1| updateProduct() 테스트|
|we cannot update product over total investing amount| updateProduct() 실패 테스트 - 금액 초과|
|we should create investment| createInvestment() 테스트|
|we cannot create investment having same user id and same product id| createInvestment() 실패 테스트 - 동일 유저가 동일 상품 투자|
|we cannot create investment having negative amount| createInvestment() 실패 테스트 - 음수의 금액 투자|
|we cannot create investment having zero amount| createInvestment() 실패 테스트 - 0원 투자|

### SignRepositoryTests
* 5개 테스트

| 테스트 이름 | 테스트 내용 |
|---|---|
|testRepository should be configured() | 환경 테스트 |
|we should get 1 investor when requesting investor whose name is hey| selectUserByName() 테스트|
|we cannot get investor without proper name| selectUserByName() 없는 이름 테스트|
|we should get 1 when requesting count of investor whose name is hey| selectUserCountByName() 테스트|
|we should create investor| insertUser() 테스트|

### InvestmentServiceTests
* 9개 테스트
* Service Layer 기능 검사를 위한 단위 테스트

| 테스트 이름| 테스트 내용|
|---|---|
|mock should be configured| 환경 테스트|
|we should get products| getProducts() 테스트|
|we should get investments of user by user id| getInvestments() 테스트|
|we should create investment| createInvestment() 테스트|
|we should get InvalidAmountException while creating investment with invalid amount| createInvestment() 실패 처리 테스트 - 0 이하의 금액|
|we should get ProductNotFoundException while creating investment with invalid product id| createInvestment() 실패 처리 테스트 - 없는 상품|
|we should get ProductNotOpenedException while creating investment with not opened product| createInvestment() 실패 처리 테스트 - 안열린 상품|
|we should get ProductClosedException while creating investment with closed product| createInvestment() 실패 처리 테스트 - 닫힌 상품|
|we should get TotalInvestingAmountExceededException while creating investment with exceed amount| createInvestment() 실패 처리 테스트 - 금액 초과|

### SignServiceTests
* 6개 테스트

| 테스트 이름| 테스트 내용|
|---|---|
|mock should be configured| 환경 테스트|
|we should get token when signing in| signIn() 테스트|
|we should sign up| signUp() 테스트|
|we should get UserNotFoundException when signing in with wrong name| signIn() 실패 테스트 - 없는 유저|
|we should get WrongPasswordException when signing in with wrong password signIn() 실패 테스트 - 비밀번호 오류|
|we should get UserAlreadyExistedException when sign up with existed name| signUp() 실패 테스트 - 이미 있는 유저 이름|

### InvestmentControllerTests
* 16개 테스트
* Controller Layer 기능 검사를 위한 단위 테스트

| 테스트 이름| 테스트 내용|
|---|---|
|mock mvc should be configured|환경 테스트|
|we should get products|[GET]/products 테스트|
|we should get investments of user by user id|[GET] /investments 테스트|
|we should create investment|[POST] /investment 테스트|
|we should handle SQLException while getting products with database problem| [GET] /products 중 DB 에러 리턴 테스트|
|we should handle SQLException while getting investments with database problem| [GET] /investments 중 DB 에러 리턴 테스트|
|we should handle SQLException while creating investment with database problem| [POST] /investment 중 DB 에러 리턴 테스트|
|we should handle InvalidAmountException while creating investment with invalid amount| [POST] /investment 중 InvalidAmountException 에러 리턴 테스트 |
|we should handle ProductNotFoundException while creating investment with invalid product id| [POST] /investment 중 ProductNotFoundException 에러 리턴 테스트 |
|we should handle ProductNotOpenedException while creating investment with not opened product| [POST] /investment 중 ProductNotOpenedException 에러 리턴 테스트 |
|we should handle ProductClosedException while creating investment with closed product| [POST] /investment 중 ProductClosedException 에러 리턴 테스트 |
|we should handle TotalInvestingAmountExceededException while creating investment with exceeded amount| [POST] /investment 중 TotalInvestingAmountExceededException 에러 리턴 테스트 |
|we cannot create investment without authentication| 인증 없이 [POST] /investment 수행 불가|
|we cannot create investment of others| 인증된 유저와 다른 id로 [POST] /investment 수행 불가|
|we cannot get investments without authentication| 인증 없이 [GET] /investments 수행 불가|
|we cannot get investments of others| 인증된 유저와 다른 id로 [GET] /investments 수행 불가|

### SignControllerTests
* 6개 테스트

| 테스트 이름| 테스트 내용|
|---|---|
|mock mvc should be configured|환경 테스트|
|we should sign in| [POST] /signin 테스트|
|we should sign up| [POST] /signup 테스트|
|we should handle UserNotFoundException while signing in with wrong name| [POST] /signin 중 UserNotFoundException 에러 리턴 테스트|
|we should handle WrongPasswordException while signing in with wrong password| [POST] /signin 중 WrongPasswordException 에러 리턴 테스트|
|we should handle UserAlreadyExistedException while signing up with existed name| [POST] /signup 중 UserAlreadyExistedException 에러 리턴 테스트|

### InvestApplicationTests
* 19개 테스트
* 실제 Application 단위의 통합 테스트

| 테스트 이름| 테스트 내용|
|---|---|
|application should be configured| 환경 테스트 |
|we should get 23 products (including 1 sold out) when requesting a list of product within the period| 상품 조회 테스트|
|we should get 2 investments when requesting a list of investment for user with an ID of 10| 투자 내역 조회 테스트|
|we should create investment and get 1 investment when requesting a list of investment| 투자 후 투자내역 생겼는지 테스트|
|we should create investment and updated product should be returned when request a list of product| 투자 후 상품 업데이트 되었는지 테스트|
|we cannot create investment with invalid amount| 0 이하 금액 투자 테스트|
|we cannot create investment with invalid product id| 없는 상품 투자 테스트|
|we cannot create investment with not opened product| 안 열린 상품 투자 테스트|
|we cannot create investment with closed product| 닫힌 상품 투자 테스트|
|we cannot create investment with exceeded amount| 금액 초과 테스트|
|we cannot create investment having same user id and same product id| 동일 유저, 동일 상품 투자 테스트|
|we should handle multiple request for getting products| 여러 사람이 동시에 상품 조회하는 테스트|
|we should handle multiple request for getting investments| 여러 사람이 동시에 투자 내역 조회하는 테스트|
|we should handle multiple request for creating investments| 여러 사람이 동시에 투자하는 테스트|
|we should handle multiple request for creating investments of same product| 1원 남은 투자 상품에 여러 사람이 동시에 투자하는 테스트|
|we should sign up and cannot sign up with same user name| 회원가입 테스트|
|we should sign in| 로그인 테스트|
|we cannot sign in with wrong user name| 없는 아이디로 로그인 테스트|
|we cannot sign in with wrong password| 잘못된 비밀번호로 로그인 테스트|





