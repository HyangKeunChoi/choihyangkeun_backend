## 프로젝트 설명

# ## 사용 기술
- Java 17
- Spring Boot & JPA (Hibernate) & QueryDsl
- Docker 및 Docker Compose
- Redis
- H2 DB

### 사전 준비
+ redis를 실행하기 위해 docker 컴포즈를 이용합니다.

```java
docker-compose up -d
```

+ redis 사용 이유
    - 분산 환경에 대한 동시성 제어 (spin lock 방식)
    - 출금 시 한도 체크 속도 고려 (일 한도: 1일 최대 1,000,000원)

## 1. API SPEC
### 1. 계좌 등록하기
+ URL : localhost:8080/api/v1/wirebarley/accounts (POST)
+ Content-Type: application/json
+ Response : 200 OK 

### 2. 삭제하기 API
+ URL : localhost:8080/api/v1/wirebarley/accounts/{account_id} (DELETE)
+ Response : 200 OK

### 3. 입금 API
+ URL : localhost:8080/api/v1/wirebarley/accounts/deposit (POST)
+ Content-Type: application/json
+ Response : 200 OK

### 4. 출금 API
+ URL : localhost:8080/api/v1/wirebarley/accounts/{account_id}/withdraw (POST)
+ Content-Type: application/json
+ Response : 200 OK

### 5. 이체 API
+ URL : localhost:8080/api/v1/wirebarley/accounts/transfer (POST)
+ Content-Type: application/json
+ Response : 200 OK

### 6. 거래내역 조회하기 API
+ URL : localhost:8080/api/v1/wirebarley/accounts/{account_id}/transaction (GET)
+ Content-Type: application/json
+ Response
```json
{
    "content": [
        {
            "id": 1,
            "senderAccountId": 1,
            "receiverAccountId": 101,
            "transferAmount" : 10000,
            "description" : "test",
            "transferAt" : "2025-04-16T01:54:22.057733",
            "createdAt": "2025-04-16T01:54:22.057733",
            "updatedAt": "2025-04-16T01:54:22.057733"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "size": 10,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "first": true,
    "last": true,
    "numberOfElements": 1,
    "empty": false
}
```

## 2. 구현 고려 사항
+ 도메인 엔티티와 jpa엔티티를 분리하여 설계 하였습니다.
    - 순수 도메인에 비즈니스 로직이 존재하도록 하여 소형 테스트를 쉽게 할 수 있도록 설계 하였습니다.
    - 분리 이유 : jpa 엔티티도 변할수 있는 구조로 생각하였습니다. 


+ 프로젝트 아키텍쳐(멀티 모듈 구성)
    - API 모듈 : 실행 모듈이 존재하는 영역 입니다. (controller와 service 레이어 존재)
    - core 모듈 : 순수 도메인 엔티티가 존재하는 영역입니다.
    - infra 모듈 : jpa 엔티티와 queryDsl등이 존재하는 레이어 입니다.
    - service와 repository에 인터페이스를 두어 다른 모듈로 쉽게 변경 할수 있게 설계 하였습니다. (DIP 적용)
      - (확장성 고려) 


+ 이체 내역 조회는 데이터량이 많아질것을 고려하여 slice로 조회하도록 설계 하였습니다.
    - 데이터가 많아질 것을 고려해 파티션닝을 고려해보는것도 좋을것 같습니다.
    - 또는 UI상 시작 ~ 종료 기간을 선택하여 조회하도록 하는것도 방법일것 같습니다.


+ 금액(잔액)의 경우 BigDecimal 타입으로 사용하여 정확한 계산이 가능하도록 설계 하였습니다.


+ 테스트 코드는 서비스 레이어와 도메인에 대한 테스트를 주로 작성하였습니다.


### 추가적으로 구현하지 못한 validation / 로직

+ 계좌 등록 : 이미 등록되어 있는 계좌 번호 입니다.
+ 계좌 삭제 : 잔액이 존재하여 삭제할 수 없습니다.
+ 입금 : 입금액은 0보다 커야 합니다.
+ 이체 : 수수료 1% 추가 부과
