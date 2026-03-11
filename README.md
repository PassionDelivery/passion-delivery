# Passion Delivery

배달 주문 플랫폼 백엔드 API 서버

## 프로젝트 소개

음식 배달 서비스의 핵심 기능(회원, 가게, 메뉴, 장바구니, 주문, 결제, 리뷰)을 구현한 Spring Boot 기반 REST API 서버입니다.

- 모듈 간 **Provider/Requirer 패턴**으로 의존성을 분리하여, 향후 마이크로서비스 전환에 용이한 구조
- **Google Gemini AI** 연동을 통한 메뉴 설명 자동 생성
- **GitHub Actions + AWS ECR/EC2** 기반 CI/CD 자동 배포

## 팀원 및 역할분담

| 이름  | 담당                                               |
|-----|--------------------------------------------------|
| 이현규 | Store, Menu, AI(Gemini), Review, Cart, CI/CD, 배포 |
| 이혜인 | Order, Payment, API 테스트                          |
| 안정후 | User, 인증/인가(JWT, Security), API 테스트              |
| 백상은 | Payment(중도 이탈)                                   |
| 이준우 | Shared, Review, Cart(중도 이탈)                      |

## 기술 스택

| 구분            | 기술                                                              |
|---------------|-----------------------------------------------------------------|
| **Backend**   | Spring Boot 3.5.4 · Java 17 · Spring Data JPA · Spring Security |
| **Database**  | PostgreSQL 16 · Querydsl 5.1                                    |
| **AI**        | Google Gemini 2.5 Flash (Spring AI)                             |
| **Infra**     | AWS EC2 · AWS ECR · AWS RDS                                     |
| **CI/CD**     | GitHub Actions                                                  |
| **Container** | Docker · Docker Compose                                         |
| **API 문서**    | SpringDoc OpenAPI (Swagger UI)                                  |
| **Testing**   | JUnit 5 · Mockito · H2                                          |
| **Build**     | Gradle                                                          |

## 서비스 구성 및 실행 방법

### 로컬 실행

```bash
# 1. .env 파일 생성
cp .env.example .env
# .env 파일에 환경변수 설정

# 2. Docker Compose로 실행
docker-compose up -d
```

### 배포 환경

- `develop` 브랜치에 push 시 GitHub Actions가 자동으로 빌드 → ECR 푸시 → EC2 배포
- EC2에서 Docker 컨테이너로 실행, RDS(PostgreSQL)에 연결

### 링크

- **API 문서 (Swagger UI)**: http://43.203.217.12:8080/swagger-ui/index.html
- **Health Check**: http://43.203.217.12:8080/health

## 프로젝트 구조

```
com.example.pdelivery/
├── shared/                  # 공통 모듈
│   ├── security/            # JWT 인증/인가, SecurityConfig
│   ├── ai/                  # Gemini AI 연동
│   ├── error/               # 공통 에러 처리
│   ├── jpa/                 # BaseEntity, JPA Auditing
│   ├── annotations/         # @Provider, @Requirer
│   ├── enums/               # OrderStatus 등 공유 enum
│   └── runner/              # 시드 데이터 (Master 계정, 카테고리)
│
├── user/                    # 회원가입, 로그인, JWT 발급, CRUD
├── store/                   # 가게 등록/수정/삭제, 카테고리, 검색
├── menu/                    # 메뉴 CRUD, AI 설명 생성
├── cart/                    # 장바구니 추가/수정/삭제/조회
├── order/                   # 주문 생성, 상태 변경
├── payment/                 # 결제 생성/조회/승인/취소
└── review/                  # 리뷰 작성/수정/삭제, 평점 통계
```

### 전체 패키지 구조 (상세)

```
com.example.pdelivery/
├── shared/                                    # 공통 모듈
│   ├── security/
│   │   ├── AuthUser.java                      # 인증 사용자 record (userId, username)
│   │   ├── JwtUtil.java                       # JWT 생성/검증
│   │   ├── JwtAuthFilter.java                 # JWT 인증 필터
│   │   ├── SecurityConfig.java                # Spring Security 설정
│   │   ├── UserDetailsServiceImpl.java        # UserDetailsService 구현
│   │   ├── JwtAuthenticationEntryPoint.java   # 401 처리
│   │   └── JwtAccessDeniedHandler.java        # 403 처리
│   ├── ai/
│   │   ├── AiService.java                     # AI 서비스 인터페이스
│   │   ├── AiServiceImpl.java                 # Gemini AI 호출 구현
│   │   ├── AiRequestEntity.java               # AI 요청/응답 영속화
│   │   ├── AiRequestJpaRepository.java
│   │   └── AiResponse.java
│   ├── error/
│   │   ├── ErrorCode.java                     # 에러코드 인터페이스
│   │   └── PDeliveryException.java            # 공통 예외 베이스
│   ├── jpa/
│   │   ├── AbstractEntity.java                # UUID ID + equals/hashCode
│   │   ├── BaseEntityOnlyCreated.java         # 생성 감사 (createdAt, createdBy)
│   │   ├── BaseEntity.java                    # 전체 감사 + 소프트 딜리트
│   │   ├── JpaAuditConfig.java                # JPA Auditing 설정
│   │   ├── AuditorAwareImpl.java              # SecurityContext → createdBy
│   │   └── ApiControllerAdvice.java           # 전역 예외 처리
│   ├── annotations/
│   │   ├── Provider.java                      # @Provider 메타 어노테이션
│   │   └── Requirer.java                      # @Requirer 메타 어노테이션
│   ├── enums/
│   │   └── OrderStatus.java                   # 주문 상태 (PENDING ~ CANCELLED)
│   ├── docs/
│   │   └── SwaggerConfig.java                 # OpenAPI 설정
│   ├── runner/
│   │   ├── MasterSeedRunner.java              # MASTER 계정 자동 생성
│   │   └── LocalTestDataSeedRunner.java       # 로컬 테스트 시드 데이터
│   ├── ApiResponse.java                       # 단건 응답 래퍼
│   ├── PageResponse.java                      # 페이지 응답 래퍼 (Slice 기반)
│   ├── HealthController.java                  # 헬스체크 (GET /health)
│   └── QuerydslConfig.java                    # Querydsl 설정
│
├── user/
│   ├── application/service/
│   │   ├── AuthService.java                   # 회원가입
│   │   ├── LoginService.java                  # 로그인/JWT 발급
│   │   └── UserService.java                   # 유저 CRUD
│   ├── domain/
│   │   ├── entity/UserEntity.java
│   │   ├── entity/UserRole.java               # CUSTOMER, OWNER, MANAGER, MASTER
│   │   └── repository/UserRepository.java
│   ├── error/
│   │   ├── UserErrorCode.java
│   │   ├── UserException.java
│   │   ├── AuthErrorCode.java
│   │   └── AuthException.java
│   └── presentation/
│       ├── controller/UserController.java
│       ├── controller/AuthController.java
│       ├── controller/AuthTokenController.java
│       └── dto/                               # Request/ResponseDto
│
├── store/
│   ├── application/
│   │   ├── StoreService.java
│   │   ├── StoreServiceImpl.java
│   │   ├── CategoryService.java
│   │   ├── CategoryServiceImpl.java
│   │   └── provided/
│   │       ├── StoreProvider.java             # 타 모듈 제공 인터페이스
│   │       ├── StoreProviderImpl.java
│   │       └── StoreInfo.java                 # 제공 DTO (storeId, ownerId, storeName)
│   ├── domain/
│   │   ├── StoreEntity.java
│   │   ├── Store.java
│   │   ├── StoreStatus.java                   # PENDING, APPROVED, REJECTED, CANCELLED
│   │   ├── CategoryEntity.java
│   │   └── StoreRepository.java
│   ├── error/
│   │   ├── StoreErrorCode.java
│   │   └── StoreException.java
│   ├── infrastructure/
│   │   ├── StoreJpaRepository.java
│   │   ├── StoreJpaPersistence.java
│   │   └── CategoryJpaRepository.java
│   └── presentation/
│       ├── StoreController.java
│       ├── CategoryController.java
│       └── dto/                               # Create/Update/Search/Response
│
├── menu/
│   ├── application/
│   │   ├── MenuService.java
│   │   ├── MenuServiceImpl.java
│   │   └── provided/
│   │       ├── MenuProvider.java
│   │       ├── MenuProviderImpl.java
│   │       └── MenuInfo.java                  # 제공 DTO (menuId, storeId, name, price, ...)
│   ├── domain/
│   │   ├── MenuEntity.java
│   │   ├── Menu.java
│   │   └── MenuRepository.java
│   ├── error/
│   │   ├── MenuErrorCode.java
│   │   └── MenuException.java
│   ├── infrastructure/
│   │   ├── MenuJpaRepository.java
│   │   ├── MenuJpaPersistence.java
│   │   └── required/store/
│   │       ├── MenuStoreRequirer.java         # Store 데이터 소비 인터페이스
│   │       ├── MenuStoreRequirerImpl.java
│   │       └── StoreData.java                 # 소비 DTO (storeId, ownerId)
│   └── presentation/
│       ├── MenuController.java
│       ├── MenuSearchController.java
│       └── dto/                               # Create/Update/Response + AI Description
│
├── cart/
│   ├── application/
│   │   ├── CartService.java
│   │   ├── CartServiceImpl.java
│   │   └── provided/
│   │       ├── CartProvider.java
│   │       ├── CartProviderImpl.java
│   │       ├── CartInfo.java                  # 제공 DTO
│   │       └── CartLineInfo.java
│   ├── domain/
│   │   ├── CartEntity.java
│   │   ├── CartLineEntity.java
│   │   └── CartRepository.java
│   ├── error/
│   │   ├── CartErrorCode.java
│   │   └── CartException.java
│   ├── infrastructure/
│   │   ├── CartJpaRepository.java
│   │   ├── CartJpaPersistence.java
│   │   └── required/menu/
│   │       ├── CartMenuRequirer.java          # Menu 데이터 소비 인터페이스
│   │       ├── CartMenuRequirerImpl.java
│   │       └── MenuData.java                  # 소비 DTO (menuId, storeId, name, price)
│   └── presentation/
│       ├── CartController.java
│       └── dto/                               # AddItem/UpdateItem/Response
│
├── order/
│   ├── application/
│   │   ├── OrderService.java
│   │   ├── OrderServiceImpl.java
│   │   ├── OrderRequest.java
│   │   └── provider/
│   │       ├── OrderProvider.java
│   │       ├── OrderProviderImpl.java
│   │       └── OrderInfo.java                 # 제공 DTO
│   ├── domain/
│   │   ├── Order.java
│   │   ├── OrderLine.java
│   │   ├── OrderLineVO.java
│   │   ├── OrderType.java
│   │   └── OrderRepository.java
│   ├── error/
│   │   ├── OrderErrorCode.java
│   │   └── OrderException.java
│   ├── infrastructure/
│   │   ├── OrderJpaRepository.java
│   │   ├── OrderJpaPersistence.java
│   │   └── required/
│   │       ├── cart/
│   │       │   ├── OrderCartRequirer.java
│   │       │   ├── OrderCartRequirerImpl.java
│   │       │   └── CartData.java              # 소비 DTO
│   │       ├── menu/
│   │       │   ├── OrderMenuRequirer.java
│   │       │   ├── OrderMenuRequirerImpl.java
│   │       │   └── MenuData.java
│   │       ├── store/
│   │       │   ├── OrderStoreRequirer.java
│   │       │   └── OrdeStoreRequirerImpl.java
│   │       ├── payment/
│   │       │   ├── OrderPaymentRequirer.java
│   │       │   └── OrderPaymentRequirerImpl.java
│   │       └── address/
│   │           ├── OrderAddressRequirer.java
│   │           └── OrderAddressRequirerImpl.java
│   └── presentation/
│       ├── OrderController.java
│       └── OrderResponse.java
│
├── payment/
│   ├── application/
│   │   ├── PaymentService.java
│   │   ├── PaymentServiceImpl.java
│   │   ├── PaymentValidator.java
│   │   ├── dto/                               # Create/Approve/Search/Response
│   │   └── provider/
│   │       ├── PaymentProvider.java
│   │       └── PaymentProviderImpl.java
│   ├── domain/
│   │   ├── Payment.java
│   │   ├── PaymentMethod.java
│   │   ├── PaymentProvider.java
│   │   ├── PaymentStatus.java
│   │   └── PaymentRepository.java
│   ├── error/
│   │   ├── PaymentErrorCode.java
│   │   └── PaymentException.java
│   ├── infrastructure/
│   │   ├── PaymentJpaRepository.java
│   │   ├── PaymentPersistence.java
│   │   └── required/order/
│   │       ├── PaymentOrderRequirer.java
│   │       ├── PaymentOrderRequirerImpl.java
│   │       ├── PaymentOrderSummary.java       # 소비 DTO
│   │       ├── PaymentStoreRequirer.java
│   │       └── PaymentStoreRequirerImpl.java
│   └── presentation/
│       └── PaymentController.java
│
└── review/
    ├── application/
    │   ├── ReviewService.java
    │   ├── ReviewServiceImpl.java
    │   ├── ReviewValidator.java
    │   └── CreateReviewRequest.java
    ├── domain/
    │   ├── ReviewEntity.java
    │   ├── Review.java
    │   ├── ReviewRepository.java
    │   ├── RatingStatEntity.java
    │   └── RatingStatRepository.java
    ├── ReviewErrorCode.java
    ├── ReviewException.java
    ├── infrastructure/
    │   ├── ReviewJpaRepository.java
    │   ├── ReviewJpaPersistence.java
    │   ├── RatingStatJpaRepository.java
    │   ├── RatingStatJpaPersistence.java
    │   ├── ReviewStoreRequirer.java           # Store 데이터 소비
    │   ├── ReviewStoreRequirerImpl.java
    │   ├── ReviewOrderRequirer.java           # Order 데이터 소비
    │   ├── ReviewOrderRequirerImpl.java
    │   └── OrderData.java                     # 소비 DTO
    └── presentation/
        ├── ReviewController.java
        └── dto/                               # Review/RatingStat/StoreReview Response
```

## 모듈 간 연동 (Provider/Requirer)

각 모듈은 직접 의존하지 않고, **Provider(제공)**가 Info를 노출하면 **Requirer(소비)**가 Data로 변환하여 사용합니다.

```
Store ──StoreInfo──→ Menu (StoreData)
Store ──StoreInfo──→ Order (storeName, ownerId)
Store ──StoreInfo──→ Review (existsBy, findStoreIds)
Menu  ──MenuInfo───→ Cart (MenuData)
Menu  ──MenuInfo───→ Order (MenuData)
Cart  ──CartInfo───→ Order (CartData)
Order ──OrderInfo──→ Payment (PaymentOrderSummary)
Order ──OrderInfo──→ Review (OrderData)
Payment ──PaymentProvider──→ Order (결제 처리/취소)

User는 Provider/Requirer를 사용하지 않고,
JWT 인증(@AuthenticationPrincipal AuthUser)으로 전 모듈에서 참조
```

## API 엔드포인트

### User

| Method | Path               | 권한  | 설명           |
|--------|--------------------|-----|--------------|
| POST   | `/api/users`       | ALL | 회원가입         |
| POST   | `/api/auth/tokens` | ALL | 로그인 (JWT 발급) |
| GET    | `/api/users/me`    | 인증  | 내 정보 조회      |
| PATCH  | `/api/users/me`    | 인증  | 내 정보 수정      |
| DELETE | `/api/users/me`    | 인증  | 회원 탈퇴        |

### Store

| Method | Path                           | 권한              | 설명               |
|--------|--------------------------------|-----------------|------------------|
| POST   | `/api/stores/me`               | OWNER           | 가게 등록 (PENDING)  |
| POST   | `/api/stores`                  | MANAGER, MASTER | 가게 등록 (APPROVED) |
| GET    | `/api/stores`                  | ALL             | 가게 검색            |
| GET    | `/api/stores/{storeId}`        | ALL             | 가게 상세 조회         |
| PATCH  | `/api/stores/{storeId}/status` | MANAGER, MASTER | 가게 상태 변경         |
| PUT    | `/api/stores/me/{storeId}`     | OWNER           | 내 가게 수정          |
| PUT    | `/api/stores/{storeId}`        | MANAGER, MASTER | 가게 수정            |
| DELETE | `/api/stores/me/{storeId}`     | OWNER           | 내 가게 삭제          |
| DELETE | `/api/stores/{storeId}`        | MANAGER, MASTER | 가게 삭제            |

### Menu

| Method | Path                                   | 권한    | 설명       |
|--------|----------------------------------------|-------|----------|
| POST   | `/api/stores/{storeId}/menus`          | OWNER | 메뉴 등록    |
| GET    | `/api/stores/{storeId}/menus`          | ALL   | 메뉴 목록 조회 |
| GET    | `/api/stores/{storeId}/menus/{menuId}` | ALL   | 메뉴 상세 조회 |
| PATCH  | `/api/stores/{storeId}/menus/{menuId}` | OWNER | 메뉴 수정    |
| DELETE | `/api/stores/{storeId}/menus/{menuId}` | OWNER | 메뉴 삭제    |

### Cart

| Method | Path                      | 권한       | 설명       |
|--------|---------------------------|----------|----------|
| POST   | `/api/carts`              | CUSTOMER | 장바구니 추가  |
| GET    | `/api/carts`              | CUSTOMER | 장바구니 조회  |
| PATCH  | `/api/carts/{cartLineId}` | CUSTOMER | 수량 변경    |
| DELETE | `/api/carts/{cartLineId}` | CUSTOMER | 항목 삭제    |
| DELETE | `/api/carts`              | CUSTOMER | 장바구니 비우기 |

### Order

| Method | Path                           | 권한       | 설명       |
|--------|--------------------------------|----------|----------|
| POST   | `/api/orders`                  | CUSTOMER | 주문 생성    |
| GET    | `/api/orders`                  | CUSTOMER | 주문 목록 조회 |
| GET    | `/api/orders/{orderId}`        | CUSTOMER | 주문 상세 조회 |
| PATCH  | `/api/orders/{orderId}/status` | OWNER    | 주문 상태 변경 |

### Payment

| Method | Path                        | 권한       | 설명       |
|--------|-----------------------------|----------|----------|
| GET    | `/api/payments`             | CUSTOMER | 결제 내역 조회 |
| GET    | `/api/payments/{paymentId}` | CUSTOMER | 결제 상세 조회 |

### Review

| Method | Path                            | 권한       | 설명         |
|--------|---------------------------------|----------|------------|
| POST   | `/api/reviews`                  | CUSTOMER | 리뷰 작성      |
| GET    | `/api/reviews/stores/{storeId}` | ALL      | 가게 리뷰 조회   |
| GET    | `/api/reviews/me`               | CUSTOMER | 내 리뷰 조회    |
| GET    | `/api/reviews/me/stores`        | OWNER    | 내 가게 리뷰 조회 |
| PATCH  | `/api/reviews/{reviewId}`       | CUSTOMER | 리뷰 수정      |
| DELETE | `/api/reviews/{reviewId}`       | CUSTOMER | 리뷰 삭제      |

### Category

| Method | Path              | 권한  | 설명         |
|--------|-------------------|-----|------------|
| GET    | `/api/categories` | ALL | 카테고리 목록 조회 |

## ERD

![ERD](assets/ERD.png)

> 인터랙티브 버전: [ERDCloud](https://www.erdcloud.com/d/xevoHpovyZTMh2Lfh)

## 환경변수

| 변수                     | 설명                  |
|------------------------|---------------------|
| `DB_URL`               | PostgreSQL JDBC URL |
| `DB_USERNAME`          | DB 사용자명             |
| `DB_PASSWORD`          | DB 비밀번호             |
| `JWT_SECRET`           | JWT 서명 시크릿 키        |
| `JWT_EXPIRATION`       | JWT 만료시간 (ms)       |
| `GENAI_API_KEY`        | Google Gemini API 키 |
| `CORS_ALLOWED_ORIGINS` | CORS 허용 오리진         |
| `MASTER_USERNAME`      | 마스터 계정 아이디          |
| `MASTER_PASSWORD`      | 마스터 계정 비밀번호         |
| `MASTER_NICKNAME`      | 마스터 계정 닉네임          |
| `MASTER_EMAIL`         | 마스터 계정 이메일          |
