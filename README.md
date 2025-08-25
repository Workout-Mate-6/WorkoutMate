# WorkOut Mate

## ✨ 프로젝트 소개
*****
혼자 운동을 하다 보면,<br>
꾸준히 이어가기도 힘들고 금방 지치기 마련이죠.

🏃‍♂️ *“오늘은 꼭 운동 가야지…”* 다짐해도,<br>
**같이 할 친구가 없어 망설여지는 순간** 있지 않으신가요?

⚽️ 좋아하는 운동 종목은 있어도<br>
**어디서, 누구와 시작해야 할지 막막하고…**

🧍🏻 계획을 세워도<br>
**동기부여가 약해지고 금방 지루해지고…**

> 이런 문제를 해결하기 위해,<br>
> **운동을 ‘함께’로 바꿔주는 플랫폼 — WorkOut Mate**를 만들었습니다!
>

*WorkOut Mate는*<br>
운동 메이트 찾기, 참여 요청 및 수락, 실시간 채팅으로 약속을 잡을 수 있는<br>
운동을 즐기는 사람들을 이어주는 플랫폼입니다.



## ✅ 주요 기능
*****
- ### 유저 서비스
    - 회원 가입 / 로그인 / 로그아웃
    - 이메일 인증 코드 발송 기능
    - 사용자 맞춤 추천 기능
        - 사용자와 비슷한 유형의 게시글, 유저 추천

- ### 🏀 메이트 구하기 기능
    - 게시글 작성
    - 댓글 작성
    - 참여 신청
    - 조회수 기반 Top 10 게시글 제공

- ### 🛒 찜 기능
    - 게시글 찜 생성
    - 게시글 찜 삭제

- ### 💪 팔로우 기능
    - (언) 팔로우 하기
    - 팔로워(잉) 조회

- ### 💬 1:1 채팅 기능
    - 채팅방 생성 / 나가기
    - 내 채팅방 목록 조회
    - 채팅 메시지 전송 / 출력

- ### 🔔 알림 기능
    - 실시간 푸시 알림


## 🛠️ 기술 스택
*****
| 분야                   | 기술 스택                                         |
|:---------------------|:----------------------------------------------|
| Language             | Java 17                                       |
| Backend              | Spring Boot, Spring Data JPA, QueryDSL        |
| IDE                  | IntelliJ                                      |
| Database             | MySQL, Redis, H2(test)                        |
| Real-time            | WebSocket, Stomp, Server-Sent Events          |
| Security             | Spring Security, JWT                          |
| CI/CD                | Github Actions                                |
| Infrastructure       | Docket, AWS EC2, AWS RDS                      |
| Test Tools           | Postman, JMeter                               |
| Monitoring           | Prometheus, Grafana, Loki, Promtail, Pinpoint |
| Collaboration Tools  | Github, Jira, Notion, Slack, Discord, Zep     |
| Design Collaboration | ERD Cloud, Figma, Canva, draw.io              |
| External API         | SendGrid                                      |

## 🏗️ 시스템 아키텍처
![시스템아키텍처](https://file.notion.so/f/f/83c75a39-3aba-4ba4-a792-7aefe4b07895/3de23c58-71f9-437b-a8bb-ea666f46ed32/image.png?table=block&id=2542dc3e-f514-80e0-8b00-f528725cca96&spaceId=83c75a39-3aba-4ba4-a792-7aefe4b07895&expirationTimestamp=1756123200000&signature=joElj8dUFpQ8EgDi655JdeZ2uMJ09K68U3mThuBnCDQ&downloadName=image.png)


## 📱 와이어 프레임
![와이어 프레임](https://file.notion.so/f/f/83c75a39-3aba-4ba4-a792-7aefe4b07895/0acd335e-e142-4d11-af97-c0b36f855e60/image.png?table=block&id=25a2dc3e-f514-8021-89d9-d1f6a1d7b7e8&spaceId=83c75a39-3aba-4ba4-a792-7aefe4b07895&expirationTimestamp=1756123200000&signature=XoabvzTSpaiBdKIM6XjI6GYbzV4C_k9xzG4EnuR8PQw&downloadName=image.png)

## ✏ ERD
![ERD](https://file.notion.so/f/f/83c75a39-3aba-4ba4-a792-7aefe4b07895/7267f74e-d99c-46ac-8c9c-d09599b40718/6%EC%A1%B0.png?table=block&id=2532dc3e-f514-8033-b4a3-fc79b587380b&spaceId=83c75a39-3aba-4ba4-a792-7aefe4b07895&expirationTimestamp=1756123200000&signature=n3Mua_pWZSJ7kvLadhLHt6dHkGHCprU11ONQxGFmWME&downloadName=6%EC%A1%B0.png)


## 📃 API 명세서
*****
<details>
<summary>AUTH</summary>
<div markdown="1">

| 메소드    | 기능                          | EndPoint                                                              |
|:-------|:----------------------------|:----------------------------------------------------------------------|
| POST   | 회원가입                        | /api/auth/signup                                                      |
| POST   | 로그인                         | /api/auth/login                                                       |
| POST   | 로그아웃                        | /api/auth/logout                                                      |
| POST   | 토큰 리프레시                     | /api/auth/refresh                                                     |
| POST   | 이메일 인증                      | /api/auth/signup/verify                                               |
| POST   | 이메일 인증 코드 재전송               | /api/auth/signup/resend                                               |
</div>
</details>

<details>
<summary>USER</summary>
<div markdown="1">

| 메소드    | 기능                          | EndPoint                                                              |
|:-------|:----------------------------|:----------------------------------------------------------------------|
| POST   | 회원 탈퇴                       | /api/users/me/deletion                                                |
| PATCH  | 회원 정보 수정                    | /api/users/me                                                         |
| GET    | 마이페이지                       | /api/users/me                                                         |
</div>
</details>

<details>
<summary>CHATTING</summary>
<div markdown="1">

| 메소드    | 기능                          | EndPoint                                                              |
|:-------|:----------------------------|:----------------------------------------------------------------------|
| POST   | 채팅방 생성                      | /api/chat-rooms/{userId}                                              |
| GET    | 채팅방 목록 조회                   | /api/chat-rooms/me                                                    |
| GET    | 채팅방 채팅 메시지 조회               | /api/chat-rooms/{chatRoomId}                                          |
| DELETE | 채팅방 나가기                     | /api/chat-rooms/{chatRoomId}/deletion                                 |
</div>
</details>


<details>
<summary>BOARD</summary>
<div markdown="1">

| 메소드    | 기능                          | EndPoint                                                              |
|:-------|:----------------------------|:----------------------------------------------------------------------|
| POST   | 게시글 작성                      | /api/boards                                                           |
| GET    | 게시글 단건 조회                   | /api/boards/{boardId}                                                 |
| GET    | 게시글 전체 조회                   | /api/boards                                                           |
| GET    | 팔로잉 유저 게시글 전체 조회            | /api/boards/following                                                 |
| GET    | 운동 종목별 조회                   | /api/boards/category?sportType={운동 종목}                                |
| GET    | 카테고리 항목 조회                  | /api/boards/sportType                                                 |
| PUT    | 게시글 수정                      | /api/boards/{boardId}                                                 |
| DELETE | 게시글 삭제                      | /api/boards/{boardId}                                                 |
| GET    | 인기 게시물 조회(조회수 기반)           | /api/boards/popular                                                   |
| GET    | 내 게시글 조회                    | /api/boards/me                                                        |
| GET    | 추천 게시글                      | /api/recommendation                                                   |
</div>
</details>


<details>
<summary>COMMENT</summary>
<div markdown="1">

| 메소드    | 기능                          | EndPoint                                                              |
|:-------|:----------------------------|:----------------------------------------------------------------------|
| POST   | 댓글 작성                       | /api/boards/{boardId}/comments                                        |
| PUT    | 댓글 수정                       | /api/boards/{boardId}/comments/{commentId}                            |
| GET    | 댓글 조회                       | /api/boards/{boardId}/comments                                        |
| DELETE | 댓글 삭제                       | /api/boards/{boardId}/comments/{commentId}                            |
</div>
</details>


<details>
<summary>PARTICIPATION</summary>
<div markdown="1">

| 메소드     | 기능                          | EndPoint                                                              |
|:--------|:----------------------------|:----------------------------------------------------------------------|
| PATCH   | 참여 요청 보내기                   | /api/boards/{boards}/participations-request                           |
| PATCH   | 참여 요청(수락/거절)                | /api/boards/{boardId}/approvalstatus/participations/{participationId} |
| PATCH   | 불참 보내기                      | /api/boards/{boards}/participations-decline                           |
| GET     | 요청 조회                       | /api/participation                                                    |
| GET     | 파티 조회                       | /api/boards/{boardId}/participations/attends                          |
</div>
</details>


<details>
<summary>FOLLOW</summary>
<div markdown="1">

| 메소드    | 기능                          | EndPoint                                                              |
|:-------|:----------------------------|:----------------------------------------------------------------------|
| POST   | 팔로우                         | /api/follows/{userId}                                                 |
| GET    | 팔로우 조회                      | /api/follows/follower/{userId}                                        |
| GET    | 팔로잉 조회                      | /api/follows/following/{userId}                                       |
| DELETE | 언팔로우                        | /api/follows/unfollower/{userId}                                      |
</div>
</details>


<details>
<summary>ZZIM</summary>
<div markdown="1">

| 메소드    | 기능                          | EndPoint                                                              |
|:-------|:----------------------------|:----------------------------------------------------------------------|
| POST   | 나의 전체 결제 내역 검색              | /api/boards/{boardId}/zzims                                           |
| GET    | 게시글에 찜 누른 회원 조회             | /api/boards/{boardId}/zzims                                           |
| GET    | 찜 갯수 조회                     | /api/boards/{boardId}/zzims/count                                     |
| GET    | 찜 전체 조회                     | /api/boards/zzims/users                                               |
| GET    | 찜 유무 조회                     | /api/boards/{boardId}/zzims/me                                        |
| DELETE | 찜 삭제                        | /api/boards/zzims/{zzimId}                                            |
</div>
</details>


<details>
<summary>NOTIFICATION</summary>
<div markdown="1">

| 메소드    | 기능                          | EndPoint                                                              |
|:-------|:----------------------------|:----------------------------------------------------------------------|
| POST   | 알림 읽음 처리                    | /api/notifications/{notificationId}/read                              |
| GET    | 알림 연결(SSE 연결)               | /api/notifications/subscribe                                          |
| GET    | 읽지 않은 알림 가져오기               | /api/notifications/unread                                             |
</div>
</details>


## 🗺 서비스 플로우
![서비스 플로우](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fb79037a4-fa02-42d6-94b8-4cd57ee15966%2F%EC%82%AC%EC%9A%A9%EC%9E%90_%ED%94%8C%EB%A1%9C%EC%9A%B0.png/size/w=2000?exp=1756188934&sig=OrItTdpCD7M2sGIxwJhH1dHdZkVBYJnYT-MaLAcMlEk&id=2552dc3e-f514-8038-883b-dff61d6d1e6d&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)



## 📅 일정

| 기간          | 일정                 | 상세                      |
|:------------|:-------------------|:------------------------|
| 7/16 ~ 7/21 | 프로젝트 기본 설계         | 프로젝트 주제 선정 및 서비스 기획과 설계 |
| 7/21 ~ 7/29 | MVP 기능 구현          | 기본 기능 CRUD 및 핵심 기능 구현   |
| 8/1 ~ 8/8   | 부가 기능 구현 및 설계 보완   | 부가 기능 구현                |
| 8/8 ~ 8/14  | 부하 테스트 및 성능 개선     | 기능 테스트 후 슬로우쿼리 등 코드 개선  |
| 8/14 ~ 8/20 | 트러블 슈팅 및 기술 문서 정리  | 5분 기록 보드 토대로 문서 정리      |
| 8/20 ~ 8/25 | 발표 자료 준비 및 최종 배포   | 브로셔 및 ppt, ReadMe 작성    |

<details><summary> Jira를 사용한 일정 관리</summary>
<div markdown="1">
![Jira](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F8bd61d29-735b-4d6a-a25f-f8673008cae1%2Fimage.png/size/w=2000?exp=1756188985&sig=QX0vBIbfaZc_L3hCPAtQWLS_6d-W8JnVQdv9vY9kOPQ&id=25a2dc3e-f514-804c-840e-fb4ecab904f9&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)
</div>
</details>

## 💡 기술적 의사결정

<details><summary>💡 사용자 맞춤 게시글 추천 기능</summary>
<div markdown="1">

## ✅ 최종 결정

    - **현재**: 콘텐츠 기반 필터링(Content-Based Filtering)을 메인 알고리즘으로 도입
    - **이유**: 구현 단순성·안정성, 콜드스타트 대응, 개인화 품질 확보, 리랭크 규칙 적용 용이성
    - **향후**: 협업 필터링을 단계적으로 접목하여 하이브리드 추천으로 확장

    ---

## 1️⃣ 도입 배경

게시글과 사용자가 급격히 늘어나면서 **최신순/인기순** 노출만으로는 개별 사용자의 관심사와 성향을 반영하기 어려워졌다.

→ **개인 맞춤형 추천 시스템**이 필요하다.
    
---

## 2️⃣ 요구사항 및 도입 목표

    - **개인화 향상**: 사용자 행동 기반 맞춤 추천 제공
    - **참여율 제고**: 추천 품질을 높여 서비스 체류 시간·참여율 증가
    - **확장성 확보**: 대규모 사용자/게시글 환경에서도 안정적 동작
    - **실험 가능성**: 가중치·보너스·페널티를 설정 기반으로 쉽게 조정
    - **설명** : “친구 2명 참여 중”, “저녁 시간 선호” 같은 이유 노출

    ---

## 3️⃣ 기술적 검토 (방식 비교)

### 협업 필터링

    - **개념**: “비슷한 사용자는 비슷한 아이템을 선호한다”는 전제 아래, 다른 사용자의 행동 데이터를 활용하여 추천을 생성
    - **방식**:
        - Memory-based: 사용자 기반(User-based), 아이템 기반(Item-based)
        - Model-based: 행렬 분해(Matrix Factorization), 딥러닝 기반 잠재 요인 학습
    - **장점**: 다른 사용자들의 집단 행동을 반영하여 추천의 다양성과 탐색성을 강화
    - **한계**: 콜드 스타트, 연산 비용 증가, 인기 편향

### ✅콘텐츠 기반 필터링

    - **개념**: 사용자가 과거에 선호한 아이템과 속성이 유사한 아이템을 추천
    - **구현**:
        - **`Board Vector`**: 게시글 속성(운동 종류, 요일, 시간대, 모집 규모 등)을 해시 인코딩 후 L2 정규화

            ```java
            public float[] encode(Board b) {
                int dim = props.getVector().getDim();
                HashingEncoder enc = new HashingEncoder(dim);
                float[] vector = VectorUtils.zeros(dim);
            
                enc.addFeature(vector, FeatureBuckets.type(String.valueOf(b.getSportType())), 1.0);
                enc.addFeature(vector, FeatureBuckets.timeBucket(b.getStartTime()), 0.6);
                enc.addFeature(vector, FeatureBuckets.dow(b.getStartTime()), 0.4);
                enc.addFeature(vector, FeatureBuckets.sizeBucket(b.getMaxParticipants()), 0.3);
            
                VectorUtils.l2Normalize(vector);
                return vector;
            }
            
            ```

        - **`User Vector`**: 사용자 참여 및 찜 행동을 시간 감쇠 가중치와 함께 누적 후 정규화

            ```java
            @Transactional(readOnly = true)
            public float[] buildFromBehavior(Long userId) {
                int dim = props.getVector().getDim();
                HashingEncoder enc = new HashingEncoder(dim);
                float[] acc = VectorUtils.zeros(dim);
            
                double decay = props.getVector().getDecayPerDay();
                Instant now = Instant.now();
            
                var ps = participationService.findByApplicant_Id(userId);
                for (Participation p : ps) {
                    Board b = p.getBoard();
                    if (b == null) continue;
            
                    long days = Math.max(0, Duration.between(
                        b.getStartTime().atZone(ZoneId.systemDefault()).toInstant(), now).toDays());
                    double w = Math.pow(decay, days);
            
                    enc.addFeature(acc, FeatureBuckets.type(String.valueOf(b.getSportType())), w);
                    enc.addFeature(acc, FeatureBuckets.timeBucket(b.getStartTime()), w * 0.6);
                    enc.addFeature(acc, FeatureBuckets.dow(b.getStartTime()),  w * 0.4);
                }
            
                VectorUtils.l2Normalize(acc);
                return acc;
            }
            
            ```

        - **추천 점수**: 사용자·게시글 벡터 간 코사인 유사도 기반
        - **`리랭크`**: 시간 임박도, 친구 참여 보너스, 정원 임박 페널티, 카테고리 다양성 등을 반영

            ```java
            
            public static double urgencyMultiplier(Board b, RecommendationProperties p) {
                double dtHours = Duration.between(LocalDateTime.now(), b.getStartTime()).toHours();
                double u = 1.0 / (1.0 + Math.exp((dtHours - p.getTime().getCenterHours()) / p.getTime().getSlope()));
                return Math.min(p.getTime().getMaxMultiplier(), u);
            }
            
            ```

    - **무엇을 벡터화했나**:

      운동 종류 / 시간대 / 요일 / 모집 규모 / (옵션) 텍스트 키워드·사회 신호

    - **왜 벡터화했나**:
        - 서로 다른 속성을 고정 길이 벡터로 일원화 → 빠른 코사인 유사도 계산
        - 새로운 속성이 들어와도 확장 용이
    - **장점**: 신규 게시글도 추천 가능, 개인화 수준 높음
    - **한계**: 다양성 부족, 집단 지혜 미활용

    ---

## 4️⃣ 기술적 판단 근거

### 판단 근거

    1. **안정성과 구현 용이성** → 초기 빠른 적용 가능
    2. **콜드 스타트 대응** → 게시글 속성만으로 신규 대응
    3. **개인화 품질 확보** → 코사인 유사도로 선호 성향 반영
    4. **리랭크 적용성** → 임박/친구/정원/다양성 규칙 보완 가능
    5. **확장성** → 협업 필터링 혼합 여지 확보

### 최종 결정

    - **현재**: 콘텐츠 기반 필터링을 메인 알고리즘으로 도입
    - **향후**: 협업 필터링을 단계적으로 접목하여 하이브리드 추천으로 확장

    ---

## 5️⃣ 기대효과 및 추후 개선 사항

### 기대효과

    - 사용자 성향을 반영한 개인화된 추천
    - 신규 게시글·사용자 추천 가능 → 성장 대응
    - 리랭크 파라미터만으로 빠른 실험 가능

### 개선 방향

    - 협업 필터링 점수 혼합 실험
    - 벡터 차원 확장
    - 설명(reason) 품질 개선
    - 다양성 슬롯 위치/빈도 최적화
    - 캐시 전략 검토

    ---

## 6️⃣ 구현 내용 (핵심 흐름 & 코드 요약)

### 전체 흐름

    1. DB 후보 수집 (본인·과거·삭제 제외)
    2. 코사인 유사도(0~100%)로 기본 점수
    3. 리랭크: 임박(↑), 친구(+), 정원(−)
    4. 다양성·친구 슬롯으로 목록 보완
    5. 점수+이유(reason)와 함께 응답

### 핵심 코드 스니펫

`개인화 추천 최종 점수 산출 과정`

    ```java
    for (Board b : candidates) {
         float[] I = vecByBoard.get(b.getId());
         double sim = Math.max(0, VectorUtils.dot(U, I)); // 0~1
         double percent = Math.round(sim * 100);          // 0~100
    
         // 임박 보정(곱)
         percent *= TimeAndRules.urgencyMultiplier(b, props);
    
         // 친구 가산(%)
         int fc = friendCounts.getOrDefault(b.getId(), 0);
         percent += Math.min(
                props.getFriend().getPresenceBonusCap(),
                fc * props.getFriend().getPresenceBonusPerFriend()
         );
    
         // 만석 근접 패널티(%)
         percent -= TimeAndRules.nearFullPenaltyPercent(b, props);
    
         percent = Math.max(0, Math.min(100, percent));
         scored.add(new Scored(b, percent));
    }
    ```

`출력 내용`

    ```java
    List<String> reasons = new ArrayList<>();
    reasons.add("너의 운동패턴과 " + mp + "% 일치");
    
    int fc = friendCounts.getOrDefault(b.getId(), 0);
    if (fc > 0) reasons.add("친구 " + fc + "명 참여 중");
    if (friendExploreTypes.contains(String.valueOf(b.getSportType())))
        reasons.add("친구들이 최근 이 종목을 해봤어요");
    ```
</div>
</details>

<details><summary>💡 일주일 동안 조회수 측정하여 top10 구현</summary>
<div markdown="1">

## 1️⃣ 도입 배경

서비스에서 **사용자가 요즘 트렌드를 쉽게 파악**하고, 활발한 참여를 유도하기 위해 “**최근 7일 인기 게시글 Top10**”을 제공할 필요가 있었습니다.

## 2️⃣  요구사항 및 도입 목표

### 1) 요구사항

    - 최근 **7일간의 조회수 기준 인기 게시글 Top10** 제공
    - **매번 API 호출마다 RDB 집계 쿼리를 실행하지 않고**, 빠른 응답 속도 보장
    - 데이터가 쌓여도 **DB 부하가 최소화**될 것

### 2) 도입 목표

    - **Redis ZSet 기반**으로 조회수 집계 및 Top10 산출 구조 구축
    - **캐시 기반 접근**으로 API 성능과 안정성 확보
    - 추후 좋아요·댓글 등 다양한 지표로 **랭킹 기능 확장 가능**

## 3️⃣ RDB 방식과 Redis 방식 비교

| 구분 | **RDB 기반 집계** | **Redis ZSet 기반 집계** |
| --- | --- | --- |
| **장점** | - 추가 인프라 불필요 (기존 DB 활용)<br>- SQL만으로 구현 가능<br>- 트랜잭션/ACID 보장으로 데이터 정합성 높음 | - 조회수(score) 자동 정렬 지원<br>- 메모리 기반 O(logN) → 빠른 성능<br>- TTL 기반 자동 만료 관리<br>- DB 부하 분산 (API는 Redis만 조회)<br>- 확장성 높음 (대규모 트래픽 대응 유리) |
| **단점** | - 데이터 증가 시 집계 쿼리 성능 저하<br>- 매 요청마다 집계 쿼리 실행 → DB 부하↑<br>- 실시간 반영 시 DB 성능에 영향 | - Redis 메모리 사용량 관리 필요<br>- 운영 인프라 추가 도입 부담<br>- 영속성 보장 한계 (RDB에 비해) |

## 4️⃣  Redis 방식 결정 이유

    - RDB 기반 쿼리는 데이터 증가에 따라 집계 비용이 커지므로, 장기적으로 확장성에 문제가 있습니다.
    - Redis ZSet은 조회수 증가를 실시간으로 반영하면서도, API 호출 시에는 데이터를 정렬이 되어 있고, 빠르게 반환할 수 있습니다.
    - TTL을 이용해 자동으로 오래된 데이터가 정리되며, 일간·주간 단위 키 관리가 가능합니다.
    - 기존 RDB만으로는 인덱스/쿼리 최적화 등 복잡한 관리가 필요하지만, Redis는 단순한 구조로 문제를 해결할 수 있습니다.

## 5️⃣ 기대효과 및 추후 개선 사항

### 1) 기대효과

    - DB 집계 쿼리 대신 이미 합산된 Redis ZSet 결과만 조회 하므로 응답 시간이 단축 됩니다.
    - 데이터가 증가하더라도 Redis는 수평 확장이 가능하며, ZSet 구조는 대규모 데이터 집계에도 유리합니다.
    - TTL을 걸어 유지하고 싶은 기긴 동안만 유지하고, 오래된 데이터는 자동으로 정리되어서 별도의 정리 작업이 필요하지 않습니다.
    - 인기글 API 요청이 많은 상황에서도 DB 성능 저하에 직접적인 영향이 없습니다.

### 2) 추후개선

    - **다양한 지표 반영**: 조회수뿐 아니라 좋아요, 댓글 수 등을 가중치로 합산하여 종합 인기글 랭킹 제공
    - **기간 확장**: 7일 기준 외에도 1일·30일 등 다양한 기간별 인기글 랭킹 제공

## 6️⃣ 구현내용

    1. 날짜 단위로 Key를 분리해 관리합니다.

        ```jsx
        // 날짜별 ZSet Key, 에시 : board:view:ranking:2025-08-07}
        **private** String getDailyRankingKey(LocalDate dateTime) {
            **return** *DAILY_VIEW_RANKING_KEY* + ":" +
             dateTime.format(DateTimeFormatter.*ofPattern*("yyyy-MM-dd")); 
         }
        ```


    1. 게시글이 조회될 때마다 **오늘 날짜 ZSet의 score를 +1** 합니다.
    
    ```jsx
    // 해당 게시글 조회수 증가 (ZSet)
    // value 예시 : [{ "value": "1145", "score": 2 }, { "value": "1324", "score": 2 }]
    @Transactional
    public void incrementViewCount(Long boardId) {
    	String todayKey = getDailyRankingKey(LocalDate.now());
    	stringRedisTemplate.opsForZSet().incrementScore(todayKey, boardId.toString(), 1.0);
    	stringRedisTemplate.expire(todayKey, Duration.ofDays(8));
    }
    ```
    
    1. 일주일 집계 (unionAndStore 활용)
    - `unionAndStore`는 **여러 ZSet(예: 여러 날짜별 ZSet)의 데이터를 합산 해서, 새로운 ZSet으로 저장**하는 연산임
    - 합산 과정에서 **동일한 value(여기선 게시글ID)는 score를 더해서 합침**
    
    ```jsx
    ZSet1: {A: 5, B: 3}
    ZSet2: {A: 2, C: 7}
    union → {A: 7, B: 3, C: 7}
    ```
    
    - 최근 7일치 ZSet 합산 및 Top10 캐싱
    
    ```jsx
    // 최근 7일치 ZSet을 unionAndStore로 합산해서 Top10 캐시
    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional(readOnly = true)
    public void cacheTop10BoardDetails() {
        // 최근 7일 key 리스트 생성
        List<String> zsetKeys = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            zsetKeys.add(getDailyRankingKey(date));
        }
        Collections.reverse(zsetKeys);
    
        String unionKey = WEEKLY_VEIW_RANKING_KEY + LocalDate.now();
    
        // 7일치 ZSet을 unionAndStore로 합산
        if (zsetKeys.size() >= 2) {
            stringRedisTemplate.opsForZSet().unionAndStore(zsetKeys.get(0), zsetKeys.subList(1, zsetKeys.size()), unionKey);
            stringRedisTemplate.expire(unionKey, Duration.ofHours(25));
        } else if (zsetKeys.size() == 1) {
            stringRedisTemplate.opsForZSet().unionAndStore(zsetKeys.get(0), Collections.emptyList(), unionKey);
            stringRedisTemplate.expire(unionKey, Duration.ofMinutes(15));
        }
    
    unionAndStore(합산 기준, 같이 합산할 나머지 키들, 결과가 저장될 새 키) 
    ```
    
    - `@Scheduled`를 통해 **1시간마다 최근 7일 ZSet을 합산**합니다.
    - `unionAndStore`를 사용하면 동일한 게시글 ID의 조회수(score)가 자동 합산됩니다.
    - 합산 결과는 `weekly:ranking` 형태의 ZSet에 저장하고, Top10 API는 이 합산 결과만 읽습니다.
</div>
</details>

<details><summary>💡 이메일 인증 발송</summary> 
<div markdown="1">

## 1️⃣  도입 배경

WorkoutMate 서비스에서는 회원가입 시 **이메일 인증 절차**를 통해 실제 사용자를 확인하고, 가짜 계정이나 스팸 계정 생성을 방지하고자 했습니다.

사용자가 안전하게 서비스에 참여할 수 있도록 하고, 서비스의 신뢰도를 높이기 위한 최소한의 보안 장치로 도입되었습니다.

## 2️⃣  요구사항 및 도입 목표

    - 신규 회원가입 시 **안정적인 인증 메일 발송**
    - SendGrid 도메인 인증(SPF/DKIM)으로 **스팸 최소화 및 도달률 확보**
    - 추후 트래픽 증가에도 확장 가능한 **유연한 인프라 확보**
    - SendGrid Dynamic Template을 통한 **메일 콘텐츠 관리**

## 3️⃣ 기술적 검토(방식비교)

| 항목 | Gmail SMTP  | SendGrid |
      | --- | --- | --- |
| 발송량 | 일 500건 제한 | 수천~수만 건 가능 |
| 브랜드 도메인 | ❌ Gmail 주소만 사용 | ✅ SPF/DKIM 인증 가능 |
| 추적 기능 | ❌ 불가 | ✅ 성공/실패 Webhook 지원 |
| API 지원 | SMTP only | SMTP + REST API |
| 운영 신뢰성 | 낮음 (스팸 처리 가능성) | 높음 (상용 서비스 기반) |
| 비용 | 무료 | 유료 (100건/일 무료 제공) |

## 4️⃣ SendGrid 도입 결정 이유

    - Gmail SMTP는 소규모 테스트 용도로는 충분하지만, 서비스 운영 환경에는 발송 한도와 관리성 한계 존재
    - SendGrid는 **안정성, 확장성, 운영 편의성**을 제공하여 장기적인 서비스 운영에 적합
    - 단순 인증 메일만 필요했음에도, 운영 단계에서 발생할 수 있는 제약을 고려해 SendGrid를 선택

## 5️⃣ 기대 효과 및 추후 개선 사항

### 1) 기대효과

    - **안정적인 이메일 인증 제공** → 신규 사용자 경험 향상
    - 발송 로그 추적을 통한 **운영 관리성 확보**
    - 추후 마케팅/알림 메일로 확장 가능하여 **재사용성 및 확장성 강화**

### 2) 추후 개선 사항

    - 현재는 회원가입 요청 → 바로 SendGrid API 호출 구조
    - SendGrid 도메인 인증(SPF/DKIM)으로 **스팸 최소화**
    - 트래픽이 적을 때는 문제 없지만, 사용자가 **폭발적으로 가입**하거나 SendGrid API 지연/에러가 생기면 가입 흐름에 영향을 줍니다.
    - 이때 **RabbitMQ/Kafka 같은 메시지 큐**를 도입하여
        - “회원가입 성공”과 “메일 발송”을 **비동기 처리**로 분리
        - 발송량 증가에도 안정적으로 처리 가능

</div>
</details>


<details><summary>💡 사용자간 1:1 채팅 기능</summary>
<div markdown="1">

## 1️⃣ 도입 배경

    - 운동메이트를 구할 때 약속 시간, 장소 등 개인적인 이야기를 나눌 공간의 부재
    - 사용자 간 실시간 소통 채널이 없어 매칭 이후 커뮤니케이션이 원활하지 않음
    - 사용자 편의성과 서비스 활용도를 높이기 위해 1:1 웹채팅 기능 추가 필요성 인식

## 2️⃣ 요구사항 및 도입 목표

    - 사용자 간 약속 시간, 장소 등 개인적인 대화를 나눌 수 있는 공간이 필요함
    - 메시지가 지연 없이 즉시 전달되어 대화를 원활하게 나눌 수 있어야 함
    - 각 사용자를 구분할 수 있어야 하고, 세션 만료나 연결 해제 상황을 안정적으로 처리해야 함
    - 별도의 설치 없이 브라우저/모바일 환경에서 즉시 사용 가능해야 함

## 3️⃣ 기술적 검토

### 1. HTTP 기반 실시간 양방향 통신 기법

    - 원래 HTTP는 비연결성(Stateless) 프로토콜 → 요청이 있어야 응답이 가능
    - HTTP 기반으로 실시간성을 흉내내는 기법

### **1-1. Polling (짧은 주기 요청)**

    - **특징**
        - 클라이언트가 일정한 주기로 서버에 요청을 보냄 →
          새로운 데이터가 있으면 응답, 없으면 빈 응답
        - 단순한 구조 (기존 HTTP 요청/응답 그대로 사용)
        - 예: `setInterval()`로 3초마다 `/messages` 요청
    - **장점**
        - 구현이 단순
        - 기존 HTTP 인프라 그대로 활용 가능
    - **단점**
        - 대부분의 요청이 빈 응답이라 불필요한 요청 → 서버 부하 증가
        - 요청 주기에 따라 지연 발생으로 실시간성 낮음

### 1-2. **Long Polling (긴 요청 유지)**

    - **특징**
        - 클라이언트가 서버에 요청 → 서버는 이벤트가 생길 때까지 응답을 지연 → 이벤트 발생 시 응답 후 즉시 재연결
        - 빈 응답을 최소화할 수 있어 Polling보다 효율적
        - 실시간성은 꽤 우수
    - **장점**
        - 비교적 실시간성 보장, HTTP 기반이라 방화벽/프록시 제약 적음
    - **단점**
        - 클라이언트가 주기적으로 요청해야 하므로 서버 리소스 낭비
        - 이벤트가 발생할 때마다 연결을 새로 맺어야 함 → 연결/해제 오버헤드 발생

### 2. HTTP 기반 실시간 단방향 통신 기법

### 2-1. **HTTP Streaming**

    - **특징**
        - 요청 한 번에 서버가 응답을 끊지 않고 여러 이벤트를 순차적으로 전송
    - **장점**
        - 실시간성 우수
        - 연결 재사용 가능
    - **단점**
        - 단방향(서버 → 클라이언트)만 통신만 가능
        - 브라우저/프록시 환경에 따라 버퍼링 문제 발생 가능

### 2-2. Server-Sent Events (SSE)

    - **특징**
        - HTTP 기반 단방향 통신 전용 API 제공
        - 텍스트 기반 프로토콜
    - **장점**
        - 브라우저 기본 지원 (EventSource API)
        - 연결 끊김 시 자동 재연결 지원
        - Streaming보다 안정적이고 표준화된 방식
    - **단점**
        - 단방향 통신만 가능 (클라이언트 → 서버는 별도 HTTP 요청 필요)
        - 바이너리 데이터 직접 전송을 미지원

### 3. WebSocket

    - **특징**
        - 한번 연결을 맺으면 연결을 유지하며 전이중(Full-Duplex) 통신을 제공
        - 초기 연결 시 HTTP Handshake 후 WebSocket 프로토콜로 업그레이드
        - 연결이 유지되므로 연결/해제 비용 절감
    - **장점**
        - 서버와 클라이언트 간 **양방향 통신 지원**
        - 연결이 유지되어 실시간 메시지 전달 지연시간(Latency)이 낮음
        - STOMP 같은 프로토콜을 활용하면 메시지 관리 및 확장 용이
    - **단점**
        - 구현 난이도가 비교적 높음
        - 연결 유지/세션 관리/스케일링 고려 필요
        - 부하 테스트 및 모니터링 툴이 별도로 필요할 수 있음
    - 참고) STOMP의 메시지 구조
        - 클라이언트와 서버가 수행할 동작을 정의하는 **명령어** (Command)
            - 예) `CONNECT`, `DISCONNECT`, `SUBSCRIBE`, `UNSUBSCRIBE`, `SEND` 등
        - 키-값 형태로 부가 정보를 전달하는 **헤더** (Header)
            - 예) `destination: 도착지 정보`, `Authorization: jwt 토큰`, `id: 구독 식별자` 등
        - JSON, 텍스트 등으로 실제 전송할 데이터를 담는 **본문** (Body)
            - 예)

            ```coffeescript
            {
              "sender": "userA",
              "message": "안녕하세요"
            }
            
            ```

![표1](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fd11366d4-cdda-4e2c-a43e-bc76c0e818e8%2Fimage.png/size/w=2000?exp=1756187365&sig=6O4YvvG4sbPi1MnPBkP3AuARx5ctYw9kP4Y4666LX7k&id=2552dc3e-f514-809d-b2ab-d24d843fcc82&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)

    일반적인 HTTP 요청/응답 모델로는 실시간성이 부족하므로, 서버와 클라이언트 간 **지속적인 연결 및 양방향 통신**이 가능한 기술이 필요하다.
    
    ## 4️⃣ 기술적 판단
    
    **최종 결정**
    
    - 사용자에게 불편함을 주지 않기 위해 낮은 지연시간과 실시간성이 뛰어난 **WebSocket**이 가장 적합
    - WebSocket은 기본적으로 단순한 소켓 수준의 양방향 통신만 제공
        - STOMP와 같은 메시징 프로토콜을 같이 적용하면 원하는 정보를 메시지에 담아 전송 및 관리가 쉬움
        - → **Spring WebSocket + STOMP** 조합으로 구현
    
    **이유**
    
    - 1:1 채팅 기능은 실시간 양방향 통신이 핵심
    - **HTTP 기반 실시간 양방향 통신 기법**(Polling, Long Polling)은 불필요한 요청이 많아 서버 부하가 크고, 요청 주기에 따른 지연이 발생하여 실시간 채팅 기능에는 한계가 있음
    - **HTTP 기반 실시간 단방향 통신 기법**(Streaming, SSE)은 양방향 통신이 필수적인 채팅 기능에는 제약이 존재
        - 실시간 알림·공지사항 전송 같은 단방향 시나리오에는 유용하지만, 채팅에는 부적합
    - **WebSocket**은 서버와 클라이언트 간 지속적 연결을 유지하며 양방향 실시간 통신을 지원하므로, 채팅 기능 요구사항에 가장 부합
        - STOMP 같은 메시징 프로토콜을 적용하면 사용자 구분, 채팅방 관리, 메시지 브로커 연동 등 확장이 용이
        - 다만, 세션 관리, 스케일링, 부하 테스트와 같은 운영적 고려가 필요하므로 구현 난이도와 관리 부담이 존재

![채팅예시](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F592d10d6-61e0-4900-b992-4df9befa35ad%2Fimage.png/size/w=2000?exp=1756187517&sig=T4BPRYFCnLRMJdmNjAbm21B_88zSj05lZE6ANrc2Nc4&id=2562dc3e-f514-8090-a6a1-da40de2f80be&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)

    ## 5️⃣ 기대효과 및 추후 개선 사항
    
    **기대 효과**
    
    - 사용자들이 매칭 이후에 개인적으로 소통 가능, 편의성 증가로 서비스 충성도 및 재사용성 증가
    - 단순 매칭 서비스에서 벗어나 커뮤니케이션까지 지원하는 종합 서비스로 발전 가능
    - 추후 단체 채팅방 등 다양한 기능으로 확장 가능
    
    **추후 개선 사항**
    
    - 서버 확장 시 Redis Pub/Sub 또는 외부 메시지 브로커(Kafka, RabbitMQ)와 연계하여 세션 동기화 문제를 해결할 수 있음
        - 현재 단계에서는 단일 서버로 운영되어 추후 분산 서버 운영 시 적용해야 할 필요가 있음
    - 사용자 수 증가 시 성능 향상을 위해 RDB에서 MongoDB와 같은 NoSQL DB로 전환
        - 대규모 채팅 데이터 처리 및 확장성 강화
    - 알림 기능과 연계하여 새로운 메시지가 도착하면 즉시 사용자에게 알림 제공
</div>
</details>


<details><summary> 💡 실시간 채팅 기능에서 토큰 유효성 확인 및 사용자 인증</summary>
<div markdown="1">

## 1️⃣ 도입 배경

    - 웹소켓 CONNECT 시 JWT 토큰 전달 후 서버에서 인증 및 유저 정보 저장 로직이 구현되어 있는 상태
    - 이후에는 별도의 jwt 토큰 유효성 검증 과정이 없음
        - 그 결과, 만료된 토큰을 가진 사용자가 계속 채팅을 이용할 수 있는 보안 취약점 발생

## 2️⃣ 요구사항 및 도입 목표

    - 토큰이 만료되었을 때 세션을 끊어 보안성을 유지해야 함
    - 채팅 서비스 특성상 지연(latency) 최소화가 필수적
        - 메시지 전송마다 인증을 넣으면 부하와 지연이 발생할 수 있으므로, **성능과 보안 간의 균형** 필요

## 3️⃣ 기술적 검토

### 방법 A. 채팅 메시지 전송 시마다 토큰 전송

    - 메시지 헤더에 토큰을 담아 전송 및 서버에서 인증 진행
    - **장점**
        - 메시지 요청 건마다 인증을 진행하여 보안성이 뛰어남
        - 구현했던 `ChannelInterceptor` 코드에 `SEND` 타입의 검증 로직을 추가하면 되므로 구현이 쉬움
    - **단점**
        - 매 메시지마다 검증을 진행하여 서버 부하가 우려됨
        - 서버 부하로 인해 채팅 송·수신에 지연이 생길 수 있음

### 방법 B. 주기적인 ping 기반 검증

    - 일정 주기마다 토큰이 포함된 ping 메시지를 서버로 전송
    - **장점**
        - 부하가 걸리지 않을 정도의 일정 주기마다 토큰 검증을 진행
        - 채팅 송·수신에 지연이 생길 우려가 적음
    - **단점**
        - 핑 주기와 인증 타이밍 사이에 보안 공백 가능성이 존재

![표2](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F6185fa59-42e7-4912-ab16-3b0e34e66818%2Fimage.png/size/w=2000?exp=1756187590&sig=M96oPYGj_-zIbQtgpiwyfGuRg4noGBos9TrhbyelE6A&id=2552dc3e-f514-8063-b921-cdc457983fb3&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)

## 4️⃣ 기술적 판단

**최종 결정**

    - 실시간 채팅의 성격상 지연 최소화가 더 중요하다고 판단
        - 따라서 **주기적 ping 기반 검증**을 채택

**이유**

    - 실시간 채팅 특성상 지연 최소화를 우선순위로 둠
    - 메시지 단위 검증(방법 A)은 보안적으로 가장 확실하지만, 대규모 실시간 채팅에서는 부하로 인해 품질 저하 가능성이 높음
    - 주기적 ping 검증(방법 B)은 보안성에서 이론적 공백이 존재하지만, 실시간 서비스 환경에서는 서버 성능을 유지하는 데 유리

## 5️⃣ 기대 효과 및 추후 개선 사항

**기대 효과**

    - 메시지마다 검증하는 방식 대비 서버 요청량을 크게 줄여, 지연 없는 원활한 채팅 경험 보장
    - `ChannelInterceptor` 에서 저장한 정보를 검증하는 방식으로 기존 코드와 자연스럽게 결합
        - 도입 난이도 감소 및 코드 수정 범위 감소
    - 즉시 예외 발생 및 연결 차단으로 보안 공백을 수용 가능한 수준으로 관리

**추후 개선 사항**

    - 클라이언트에서 핑 누락 시 세션을 종료하는 추가 로직 필요
      (일정 횟수 이상 ping을 받지 못하면 세션 종료 등)
    - 이론적 보안 공백의 존재
        - 추후 보안 민감도가 높은 기능(예: 공개 채팅방, 결제·정산 관련 기능 등)이 추가될 경우, 매 메시지마다 검증하는 방식(방법 A)을 일부 혼합해서 적용하는 등 보완책 필요
    - 적응형 주기(Adaptive Interval) 도입
        - 활동량/리스크에 따라 메시지 전송 주기를 동적으로 조정하는 방안
    - 모니터링 지표 추가
        - 대시보드에 시각화하여 **조기 경보** 필요

## 6️⃣ 구현 내용

**구현한 주기적인 ping 검증 로직**

![코드1](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F2484c79f-b73c-46f0-a163-d906dd59cc12%2Fimage.png/size/w=2000?exp=1756187637&sig=Hl2V20OlCTTCty0_VCKtTbgdlY_vmR5S710cj0C1X0o&id=2552dc3e-f514-80b1-b446-e18d34a0d9d2&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)

프론트에서 일정 주기마다 토큰을 담은 ping 메시지를 서버에 전송

![코드2](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F93db2ce2-f07c-4f72-ad03-20913708281f%2Fimage.png/size/w=2000?exp=1756187661&sig=mKRUG_sr_F7o8_bxOEWcv26tvB500HF4oGXkKYnCDd8&id=2552dc3e-f514-8002-aaa7-c7f12928bbcf&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)
![코드3](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F22bd8a32-4ac4-49e9-aeb4-8735ab1ed364%2Fimage.png/size/w=2000?exp=1756187699&sig=aPfwp9K-__K3pwjQNwS98mBTohlrw9LesRVbljPhZXY&id=2552dc3e-f514-80ee-8070-fc36f87049f5&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)
서버에서는 전달받은 토큰을 검증하여 유저 정보와 비교

**테스트 페이지로 확인하는 예외 발생 시 처리**
![예외처리1](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F0c7b593d-2d67-4056-8836-880d80a56be1%2Fimage.png/size/w=2000?exp=1756187753&sig=QUkTDXE-PUcvUkuVKJQWV9BQ1vm4FmGr2CsIXb-zyhs&id=2552dc3e-f514-80aa-b127-c421ae4df09c&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)
![예외처리2](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fdd85b6ac-0d08-4f00-993f-818fec4afe1b%2Fimage.png/size/w=2000?exp=1756187790&sig=6qxqoPCxTV9JTIZsZygGdGf_VKbCe_qMCNMZ4V8cBKM&id=2552dc3e-f514-803b-af0b-f8f990a0330f&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)
![예외처리3](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F9af537be-f654-452f-8412-63dccb08e64d%2Fimage.png/size/w=2000?exp=1756187827&sig=3vOsIj13ECjSFqutF0KGshRJ5ZM1g1uWiRO0SIuhJho&id=2552dc3e-f514-80d1-9602-e57db38ea9f2&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)

조건문에서 최초 연결 시 저장해둔 사용자 정보와 일치하는지 확인

![예외처리4](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fca1ecf8f-adc3-4701-8bd9-2cbda5fce740%2Fimage.png/size/w=2000?exp=1756187861&sig=w8vsS5XcVqP-lCKsCjqoPv8Upddun304WlC64Y_kZVU&id=2552dc3e-f514-8025-b504-dbe95285a840&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)
![예외처리5](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F3cf5629a-753a-4831-bbb6-bac991b444e0%2Fimage.png/size/w=2000?exp=1756187895&sig=am2FmEr6bu9boTApHa9kaYig2kYnp3vEn1ZPJ9B7lxc&id=2552dc3e-f514-803b-9750-d4c6db8da0a2&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)
만료된 토큰일 시 예외 처리

    - 모두 커스텀 예외를 던져서 클라이언트에서 웹소켓 연결 강제 종료할 수 있도록 처리

**구현 내용 추가 설명**

    - WebSocket은 프로토콜 레벨의 `PING/PONG` 프레임을 통해 연결 상태(keep-alive) 만을 확인하는 heartbeat 메커니즘을 제공
    - 본 구현은 이를 애플리케이션 계층에서 확장한 방식으로, 토큰이 포함된 커스텀 ping 메시지를 주기적으로 전송
    - 서버는 매 ping 수신 시 다음을 검증
        - JWT 토큰 서명·만료·발급자 등 표준 클레임 검증
        - 최초 CONNECT 시 저장된 세션의 사용자 정보와 일치 여부 (이메일 정보 확인)
    - 검증 실패 시: 즉시 예외 → 세션 종료
</div>
</details>


<details><summary>💡 Pinpoint 도입한 이유</summary>
<div markdown="1">

## APM이란?

**APM = Application Performance Monitoring / Management**

**애플리케이션의 성능을 관찰하고 관리하는 도구**

### 1️⃣  도입 배경

    - 현재 운영 중인 서비스에서 심각한 성능 이슈가 발생한 건 아니었지만, **데이터가 쌓이고 트래픽이 늘어날수록 성능 저하가 발생할 가능성**이 있다고 판단했습니다.
    - 특히, API 성능에 큰 영향을 주는 건 결국 **DB 쿼리**이기 때문에 **슬로우 쿼리를 미리 찾아내고 개선할 수 있는 체계**가 필요했습니다.
    - 로그나 단순 모니터링으로는 **쿼리가 느려질 가능성**을 사전에 식별하기 어렵기 때문에, **쿼리 실행 시간과 API 호출 흐름을 추적할 수 있는 APM 도구 도입**을 검토하게 되었습니다.

### 2️⃣ 도입 목표

    - 애플리케이션에서 발생하는 **DB 쿼리 실행 시간을** 추적해, 성능 최적화의 근거 데이터를 확보하는 것 입니다.
    - 특정 API의 응답 속도가 느려질 경우, **어떤 쿼리에서 시간이 지연되는지 한눈에 확인**할 수 있도록 하는 것 입니다.

### 3️⃣  APM 대안 비교

| 기준ç | **Pinpoint** | **Datadog APM** |
      | --- | --- | --- |
| 비용 | 무료 (오픈소스) | 유료 (사용량 기반 과금) |
| 적용 방식 | Agent만 붙이면 자동 추적, 직접 운영 필요 | Agent 설치만 하면 됨, Datadog 서버에서 관리 |
| 추적 범위 | API 응답 시간, 메서드 실행 시간, DB 쿼리까지 상세 추적 | 서비스·트랜잭션 단위 추적 중심, 다양한 클라우드 연동 |
| 장점 | JVM(Spring Boot) 친화적, 코드 수정 불필요, 비용 부담 없음 | 대시보드 완성도 높음, 여러 언어와 클라우드 환경 지원 |
| 단점 | 인프라 직접 운영 필요, JVM 중심 | 트래픽 커질수록 요금 부담 커짐 |

### 4️⃣ Pinpoint로 결정한 이유

    - 프로젝트가 **Spring Boot 기반**이었기 때문에, JVM 환경에 특화된 Pinpoint Agent를 통해 **자동 추적**이 가능했습니다.
    - Datadog 등 상용 APM은 설치가 쉽지만, **트래픽 증가 시 과금 부담**이 크다는 단점이 있어 장기 운영에는 부담이 되었습니다.
    - Pinpoint는 **API 응답 시간, 메서드 실행 시간, DB 쿼리 실행 시간**을 자동으로 수집하여, 이번 도입 목적이었던 **슬로우 쿼리 분석 및 최적화**에 가장 적합했습니다.
    - 오픈소스 기반이라 **비용 부담이 없고**, 자체 서버 환경에서 **안정적으로 장기간 운영 가능**하다는 점도 중요한 선택 기준이었습니다.

### 5️⃣  기대 효과

    - 오래 걸리는 쿼리와 해당 API를 빠르게 파악하여, 성능 최적화 우선순위를 정할 수 있습니다.
    - **단순 평균값이 아닌, 메서드/쿼리 단위의 실행 시간을 추적하여 병목 분석이 가능 합니다.**
</div>
</details>


<details><summary>💡 알림 기능 구현 방식</summary>
<div markdown="1">

## 1️⃣ **도입 배경**

WorkOut Mate는 사용자가 운동할 친구를 모집하고 소통하는 서비스입니다.

이 과정에서 게시글 작성, 댓글, 팔로우 등 다양한 상호작용이 발생합니다.

그러나 사용자가 이러한 활동을 실시간으로 확인하지 못한다면, 모집 과정이 지연되고 소통의 즉시성이 떨어져 서비스 경험이 크게 저하될 수 있습니다.

따라서, 주요 활동을 즉시 알림으로 전달하는 기능은 서비스의 핵심 경험을 강화하기 위해 필수적으로 도입 해야 한다고 생각했습니다.

## 2️⃣ **요구사항 및 도입 목표**

**1) 요구사항**

    - 알림은 즉시 사용자에게 전달되어야 함.
    - 구현은 단순하면서동 유지보수가 용이해야 함.
    - 네트워크 환경에서도 안정적인 연결을 유지할 수 있어야 함.

**2) 도입 목표**

    - 사용자 간 상호작용이 발생했을 때, 실시간 알림을 통해 즉각적인 대응이 가능하도록 함.
    - 지속적인 알림 제공을 통해 서비스 몰입도와 재방문율을 높임.

## 3️⃣ **기술적 검토**

**1) 대안**

    - **WebSocket**
        - 양방향 통신 가능 (서버 ↔ 클라이언트 모두 메시지 전송 가능)
        - 채팅, 협업 툴, 게임과 같이 상호작용이 많은 서비스에 적합
    - **Server-Sent Events (SSE)**
        - 단방향 통신 (서버 → 클라이언트)
        - 브라우저 표준 EventSource API로 간단하게 사용 가능
        - HTTP 기반이라 프록시 / 로드밸런서와 호환성이 좋음

**2) 비교**

| 항목 | WebSocket | Server-Sent Events (SSE) |
      | --- | --- | --- |
| 통신 방식 | 양방향 (서버 ↔ 클라이언트) | 단방향 (서버 → 클라이언트) |
| 복잡도 | 별도 핸드셰이크/프레임 관리 필요 | HTTP 기반, 구현 단순 |
| 사용 사례 | 채팅, 실시간 협업, 게임 | 알림, 피드 업데이트, 이벤트 스트리밍 |
| 브라우저 지원 | 모든 최신 브라우저 지원 | 모든 최신 브라우저 지원 |
| 연결 안정성 | 지속 연결 필요, 프록시 이슈 존재 | HTTP/1.1 기반, 호환성 우수 |
| 요구사항 적합성 | 과도한 기능 (양방향 불필요) | 단순 알림 전달에 최적화 |

## 4️⃣ **기술적 판단**

**1) 최종 결정**

WorkOutMate에서는 알림 기능을 WebSocket이 아닌 SSE(Server-Sent Events)로 구현하기로 결정하였습니다.

운동할 친구를 찾는 서비스 특성상, 게시글 기반으로 사람을 구하는 서비스기 때문에 게시글 기반 소통과 즉시 알림 제공이 중요하다고 생각했습니다.

그래서 SSE의 단방향 통신이 우리 프로그램의 목적에 가장 적합하다고 생각합니다.

**2) 결정 이유**

    - 알림 기능은 서버에서 사용자에게 단방향으로 메시지를 전달하는 구조가 전부이므로, WebSocket의 양방향 기능은 불필요합니다.
    - SSE는 HTTP 기반 스트리밍을 사용하므로, 기존 인프라(Nginx, 프록시)와의 호환성이 높습니다.
    - `EventSource` API를 사용하면 클라이언트에서 간단히 연결 가능하여, 개발 생산성과 유지보수성이 우수합니다.

## 5️⃣ **기대 효과 및 추후 개선 사항**

**1) 기대 효과**

    - 사용자에게 즉시 알림 전달 가능 → 서비스 몰입도 및 반응성 향상.
    - 단순한 구조로 인해 개발/운영 비용 절감.
    - 모바일 및 웹 클라이언트에서 일관된 알림 수신 경험 제공.

**2) 추후 개선 사항**

    - 현재 SSE 연결은 기본 타임아웃 이후 끊어짐 → 클라이언트에서 자동 재연결 로직 보강 필요.
    - 알림 저장/조회 기능과의 연계 강화(예: 과거 알림 내역 조회).
    - 추후 알림 종류가 다양해질 경우, Kafka/Redis 등과 연계하여 확장성 있는 이벤트 처리 구조로 발전 가능.

## 6️⃣ **구현 내용**

**SSE 구독 엔드포인트**

    ```java
    @RestController
    @RequestMapping("/notifications")
    public class NotificationController {
    
        @GetMapping("/subscribe")
        public SseEmitter subscribe(HttpServletRequest request) {
            SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 유지
            emitter.onCompletion(() -> cleanup());
    		    emitter.onTimeout(() -> cleanup());
    
            // 연결 확인용 이벤트 전송
            sendToClient(emitter, emitterId, Map.of("message", "connected"));
            return emitter;
        }
    }
    ```

    - **핵심 포인트**
        - `SseEmitter`를 사용해 연결을 열린 상태로 유지 (기본 1시간).
        - `onCompletion`, `onTimeout`으로 연결 종료 시 리소스를 정리.
        - 연결 직후 더미 이벤트(connected)를 보내 브라우저가 연결 상태를 유지할 수 있도록 설계.
</div>
</details>


<details><summary>💡 로그 모니터링 툴 결정</summary>
<div markdown="1">

## 1️⃣ **도입 배경**

: 예상하지 못한 이슈가 발생할때 문제의 원인을 빠르게 파악하고 대응하기 위함

## **2️⃣** **요구사항 및 도입 목표**

: 지금 수집 가능한 로그가 무엇인지, 그 중 어떤 로그가 실제로 유의미한지 분석 후 도구 도입하고자 함

**현재 시스템의 로그 구성**

: 에러 로그

    - **수집 가능한 로그 항목 정리**

      <사용자 활동 로그>

        - 접속 시점, IP
        - 로그인/로그아웃, 인증 실패 이력

      <게시글 로그>

        - 게시글 삭제 로그
        - 게시글 작성 로그
        - 참여 신청 로그
        - 검색어 로그

      <실시간 채팅 로그>

        - 웹소켓 연결/재연결
            - (와이파이, 인터넷 끊기는 오류로 채팅이 발송되지 않을 수 있음)
            - 메세지 전송 실패시 로그
        - 입장, 퇴장 로그

      <인기 게시물 조회 로그>

        - 인기 게시물 조회 로그

      <알림 기능 로그>

        - 알림 전송시 로그

      <사용자 맞춤형 추천 기능>

        - 이 기능을 통해 추천 된 게시물에 사용자가 접근했을 경우 로그 남겨서 만족도(=접근 확률) 확인

      <시스템 성능 로그>

        - 서버자원 (CPU, 메모리, 디스크)
        - DB 쿼리 속도, 커넥션 풀 상태

      <에러 및 예외 로그>

        - HTTP 에러 (4xx, 5xx)
        - 비즈니스 로직 에러
        - DB/Redis 접속 오류, 웹소켓 끊김 등

모든 로그를 남기는게 좋은가?

처음부터 모든 로그를 남길 필요는 없을 듯.

그 이유는 현재 에러 응답도 단순하고,

로그도 리소스를 소비하기 때문에 꼭 필요한 곳에만 써야함

저장이 되면 저장 공간을 차지하고, 얼마의 주기로 영구 저장할 것인지, 하나의 로그 파일을 얼마의 크기로 만들것인지 등 아직 정하지 못한 부분이 많기 때문에 갑작스러운 장애 발생에 대응하기 위한 로그를 남기고 그 이후에 우선순위가 낮은 것들도 남기는 방향으로 구현할 예정

**우선 순위**

    1. 장애 추적 및 긴급 대응용 로그
    2. 성능 지표, 인증 흐름 등 운영 중심 로그
    3. 사용자 행동 및 비즈니스 분석 목적 로그

⇒ 우선순위 가장 높은 로그부터 남겨

**모니터링 목표**

    - 어플리케이션 로그
    - 시스템 리소스(CPU, 메모리, Redis 등) 실시간 추적
    - 로그와 메트릭을 하나의 대시보드에서 통합하길 원해

⇒ 로그를 분석하거나 시각화 하려면 중앙 로그 수집 시스템 필요

## **3️⃣** **기술적 검토(방식비교)**

| **시스템** | 장점 | 단점 | 특징 |
| --- | --- | --- | --- |
| **ELK + Metricbeat** | - ELK 내에서 로그/메트릭 통합 관리 가능<br>- **강력한 로그 검색/분석 기능**<br>- 로그 구조화(JSON) 및 분석에 강함<br>- **엔터프라이즈 환경에 적합** (보안, 인증, 롤 기반 권한 지원) | - Metricbeat 설정과 유지 필요<br>- **스택이 무겁고 복잡함**<br>- 운영/자원 소비 높음<br>- 로그 + 메트릭이 완전히 통합되어있지 않음 (같은 시간 비교는 가능하지만 도메인 분리) | 로그 분석이 중심 |
| ✅ **Grafana + Loki + Prometheus** | - **로그 + 메트릭**<br>- **경량, 빠름, 단순함 (작은 팀/서비스에 적합)**<br>- Grafana 에서 모든 대시보드와 알림 통합 관리<br>- Prometheus: DevOps 커뮤니티 표준 | - 도입 시 별도 구성 필요<br>- 로그 검색 기능이 Elasticsearch보다 단순<br>- **Loki는 파싱/구조화가 제한적**<br>- 초기 설정 시 YAML이 많고 직관성 부족 | 모니터링 중심 + 로그는 보조 |
| **Datadog / New Relic / Sentry** | 로그 + 메트릭 + APM 올인원 통합 | 유료 | - |

## **4️⃣** **기술적 판단 (결정이유 + 최종 결정)**

    - 로그 + 메트릭 통합
    - 제한된 리소스 환경(저사양 EC2)
    - 무료 서비스 선호

**⇒ Grafana + Prometheus + Loki + Promtail**

## **5️⃣** **기대효과 및 추후 개선 사항**

기대 효과 : 시스템에 오류 발생시 확인, 발생하기 전 병목현상 발생을 미리 확인 가능

대시보드를 통해 시각적으로 서버 동작 추적 가능

개선 사항 :

    - 모니터링 서버 독립적으로 구축 → 다중 서버 환경 고려
    - 오류 발생, 시스템 자원이 일정수치보다 많이 사용될 경우, 알림 설정해 미리 대비
    - 로그 보존 기간 및 자동 삭제를 구현했지만 그 기간이 타당한 선택이었는지 추가 고민 필요

## **6️⃣** **구현 내용 - 핵심 코드, 설명**

Micrometer → Prometheus (메트릭 수집)

Logback JSON 로그 파일로 저장 → Promtail → Loki (로그 수집)

Grafana : 두 가지를 시각화 + 알림 설정

각 서비스를 도커로 띄우고, 도커내부의 파일과 ec2 속 파일을 마운드하여 데이터가 사라지지 않게 구현

핵심 코드 : 불필요한 저장공간을 사용하지 않게 로그 보존 기간을 한달로 설정

    - 코드

        ```java
        # loki-config.yml 일부 내용
        
        schema_config:
          configs:
            - from: 2025-01-01
              store: tsdb
              object_store: filesystem
              schema: v13
              index:
                prefix: index_
                period: 24h
        
        storage_config:
          tsdb_shipper:
            active_index_directory: /loki/index
            cache_location: /loki/index_cache
        
          # 파일 시스템 스토리지 설정 추가
          filesystem:
            directory: /loki/chunks
        
        limits_config:
          retention_period: 744h  # 로그 보존 기간: 31일
        
        compactor:
          working_directory: /loki/compactor
          compaction_interval: 10m
          retention_enabled: true
          retention_delete_delay: 2h
          delete_request_store: filesystem
        ```

</div>
</details>


## 🚨 트러블 슈팅
*****
<details>
<summary>🚨 순환참조 문제</summary>
<div markdown="1">

## 1️⃣ 배경

프로젝트 초기에 각 서비스가 다른 도메인의 DB 데이터가 필요할 때 Repository를 직접 참조하지 않고 해당 도메인의 Service 계층 메서드를 통해 접근하도록 규칙을 정했다.

즉, Service ↔ Service 구조를 채택했다.

**Service ↔ Service 구조를 채택한 이유**

- **역할과 책임 분리**
    - Service는 비즈니스 로직을 담당하고, Repository는 단순히 데이터를 조회/저장하는 역할만 담당
- **독립성 보장**
    - 각 Service는 자신의 Repository만 참조하도록 하여 관심사 분리
- **중복 방지**
    - Repository를 직접 참조하게 되면 동일한 비즈니스 로직이 여러 클래스에 흩어질 수 있음 → Service에서만 로직을 관리하면 한 곳에서 일관되게 유지 가능
- **변경 용이성**
    - 특정 도메인의 데이터 수정/조회 로직이 변경되었을 때 해당 Service만 수정하면 되므로 유지보수가 편리
- **충돌 최소화**
    - 서로 다른 팀원이 동시에 다른 Service를 수정할 때 코드 충돌 가능성을 줄일 수 있음
- **도메인 일관성 유지**
    - 예를 들어, `Board`에 관한 비즈니스 로직이 `UserService` 안에 들어가는 것은 부자연스럽기 때문에 Service 간 참조 규칙을 통해 도메인 경계를 명확히 하고자 함

## 2️⃣ 문제 및 원인

### 문제 상황

`BoardService`에서 유저 정보를 가져오기 위해 `UserService`에 관련 메서드를 추가했다.

이후 `UserService`에서도 특정 사용자가 작성한 게시글 수를 조회하기 위해 `BoardService`를 참조했다.

이로 인해 순환 참조(Circular Dependency) 오류가 발생했다.

![문제및상황](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F2c137966-7087-4c93-8574-e2e75d217755%2F%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-08-19_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_12.25.59.png/size/w=2000?exp=1756185962&sig=UWweXgJKJwk1HVztR3eKtT7FdbfvaXRh15JHspryHZ4&id=2552dc3e-f514-80ac-a9e7-dc480baec3e3&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

### 원인 분석

스프링 컨테이너는 빈(Bean)을 등록할 때 의존성을 주입한다.

`BoardService`는 `UserService` 빈 생성 완료를 기다리고,

`UserService`는 `BoardService` 빈 생성 완료를 기다린다.

결과적으로 서로 대기하는 무한 루프 상태가 발생했다.

## 3️⃣ 해결 과정

### 해결 방법 검토

1. **Repository 직접 참조로 변경**
- **장점**
    - 순환 참조를 근본적으로 방지 가능
- **단점**
    - Service ↔ Service 구조를 택한 본래 취지(역할 분리, 중복 방지, 도메인 경계 유지)와 어긋남
    - 동일한 비즈니스 로직이 여러 클래스에 흩어져 일관성이 깨질 위험 존재
1. **다른 도메인 참조용 전용 서비스 생성**
- 예 : BoardSearchService 생성
- 해당 서비스는 다른 서비스에서 호출 당하기만 하고, 다른 서비스를 참조하지 않음
- API용 메서드와 타 도메인 참조용 메서드를 분리해 가독성과 유지보수성을 높일 수 있음

### 실제 해결 과정

- 문제 상황을 팀원들과 공유하고 해결 방법을 논의
- 다른 도메인 참조용 전용 서비스 생성 방식을 채택함
- **이유**
    - API 로직과 도메인 간 참조 로직을 분리해 코드 구조를 명확하게 유지 가능
    - 향후 유지보수 및 확장 시 가독성 향상
    - Service ↔ Service 설계의 본래 목적(역할과 책임 분리, 로직 집중 관리, 충돌 최소화)을 해치지 않으면서도 순환 참조 문제를 구조적으로 해결

## 4️⃣ 결과 및 회고

**결과**

![결과1](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F7f71575d-dfa4-426b-a509-36d8f550163c%2Fimage.png/size/w=2000?exp=1756185987&sig=D1S493ArNNkPGB_Aw1Y6sch0RCY4dObh0UBIGGxDaSM&id=2552dc3e-f514-8080-8142-f69dd3579b82&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

- Board > Service 패키지에서 서비스 분리

![결과2](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F905f818b-edec-4361-a54b-e75b2e39a2ea%2Fimage.png/size/w=2000?exp=1756186006&sig=282hKhDdpHJ6VfHplK1Pj0hYLezmaC1VeMUAUK8KSg4&id=2552dc3e-f514-80ab-8c97-ff65ecedb621&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

- 다른 도메인의 서비스에서 의존성 주입하여 사용
    - BoardSearchService, BoardPopularityService, BoardViewCountService

**회고**

- 이후 검색을 통해 `@Lazy` 어노테이션으로 생성 시점을 지연하는 방법도 있다는 것을 알게 되었으나, 이는 구조적 해결책보다는 임시 방편에 가까워 채택하지 않음
- 순환참조 오류를 처음 겪어 당황했지만, 원인을 분석하면서 스프링 빈 생성 과정과 의존성 주입 구조에 대해 더 깊게 이해하게 됨
- 순환 참조 오류로 처음에는 Service ↔ Service 규칙을 의심했지만, 오히려 설계 철학을 재확인 →  전용 서비스 도입으로 초기 구조를 지키며 문제를 해결할 수 있었음
- 이번 경험을 통해 코드 규칙 수립 시 순환 참조 가능성도 함께 고려해야 함을 깨달음

</div>
</details>



<details>
<summary>🚨 L2 정규화 시 NaN 발생</summary>
<div markdown="1">

# 트러블슈팅 기록 — L2 정규화 시 NaN 발생

## 1️⃣ 배경

추천 시스템 개발 중, 일부 사용자나 게시글에서 추천 점수가 비정상적으로 계산되는 문제가 발견되었다.

벡터 연산에서 **NaN/Infinity 값**이 발생했고, 이로 인해 추천 점수 정렬이 깨지거나 결과가 뒤틀리는 현상이 나타났다.

문제를 추적한 결과, **벡터 정규화(L2 Normalization) 과정**에서 오류가 발생하고 있음을 확인하였다.

---

## 2️⃣ 문제(상황) 및 원인

- **문제 상황**
    - 특정 유저/게시글 벡터가 정규화 단계에서 NaN을 발생시킴
    - 그 결과 추천 점수가 `NaN`으로 변해 랭킹 정렬 단계에서 이상 동작 발생
- **원인**
    - L2 정규화는 “벡터 ÷ 벡터 길이” 연산이다.
    - 벡터의 길이(노름)가 0인 경우 **0으로 나누기**가 되어 NaN 발생
    - 영벡터가 생기는 경우:
        1. **콜드/희박 유저** → 참여 이력이 거의 없고 시간 감쇠로 가중치 소멸
        2. **특수 게시글** → 속성이 누락되거나 모든 가중치가 0으로 누적

---

## 3️⃣ 방어 로직 없을 때

### (1) UserVectorService

```java
// 기존 (가드 없음)
VectorUtils.l2Normalize(acc);   // norm == 0 → NaN 발생

```

- **결과 문제**
    - norm = 0인 경우 `0으로 나눔` → `NaN` 발생
    - 추천 점수(dot product)에서 NaN 전파 → 정렬 단계 전체가 꼬임

---

### (2) BoardVectorService

```java
// 기존 (가드 없음)
VectorUtils.l2Normalize(v);   // v가 영벡터 → NaN 발생

```

- **결과 문제**
    - 피처 누락/0 누적으로 영벡터 발생 → NaN
    - 해당 게시글이 추천 후보군 전체의 점수를 뒤틀어 버림

---

## 4️⃣ 방어 로직 추가 후

핵심: **정규화 직전 norm 검사 → 0/NaN/Inf이면 최소 대체값 주입 → 다시 정규화**

### (1) UserVectorService

```java
float norm = VectorUtils.l2Norm(acc);
if (norm == 0.0f || Float.isNaN(norm) || Float.isInfinite(norm)) {
    float[] prior = globalPrior(dim);           // 평균 취향 prior
    VectorUtils.addInPlace(acc, prior, 1.0f);   // 최소 주입
}
VectorUtils.l2Normalize(acc);

```

- **효과**
    - 희박 유저도 항상 단위 벡터 보장
    - 코사인 유사도 계산에서 NaN 사라짐

---

### (2) BoardVectorService

```java
float n = VectorUtils.l2Norm(v);
if (n == 0.0f || Float.isNaN(n) || Float.isInfinite(n)) {
    java.util.Arrays.fill(v, 1f);   // 균등값 채움
}
VectorUtils.l2Normalize(v);

```

- **효과**
    - 특수 게시글도 최소한의 방향성을 갖는 단위 벡터 확보
    - 추천 점수 안정적으로 계산 가능

---

## 5️⃣ 결과 (비교 정리)

- **Before (가드 없음)**
    - norm = 0 → NaN 발생
    - 추천 점수 전체 정렬이 깨짐 (랭킹 불안정, 일부 결과 누락/왜곡)
- **After (가드 있음)**
    - 모든 유저/게시글 벡터가 항상 유효한 단위 벡터
    - NaN/Inf 전혀 발생하지 않음
    - 추천 점수 및 랭킹 정렬 안정화

---

## 6️⃣ 회고

- **교훈**
    - 정규화는 단순한 연산 같지만, **데이터 희박/결손 상황에서는 안전하지 않다**.
    - 따라서 벡터 파이프라인에서

      **“정규화 전 노름 체크 → 최소값 주입 → 정규화”**

      를 불변 규칙으로 삼는다.

- **효과**
    - 단순한 가드 추가만으로도 시스템 전반의 안정성이 크게 개선됨
    - 추천 결과 신뢰도가 높아져 이후 실험(A/B) 진행도 수월해짐

</div>
</details>


<details>
<summary>🚨채팅방 생성 시 동시성 문제(경쟁조건)</summary>
<div markdown="1">

## 1️⃣ 배경

채팅 기능 구현 중, 채팅 기능의 부하 테스트를 위해 Postman과 JMeter를 이용해 테스트를 진행함

- Postman에서는 동시성 문제가 발생하지 않았으나, JMeter를 통해 채팅방 생성, 웹소켓 연결, 메시지 전송, 웹소켓 종료 등 일련의 과정을 수행하도록 시나리오를 구성해 테스트한 결과, 동시성 문제가 발생함

환경

- 로컬 : 12 코어 (Window 10) / RAM - 16GB
- 도구 : JMeter

스레드 속성

- 사용자 수: 100
- Ramp-up: 20초
- 루프 카운트: 1회

채팅방 생성된 상태에서 채팅방 생성 로직 실행 시 에러 발생

![에러발생](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F259b7d2b-49a5-4624-b3e5-0907767b2278%2Fimage.png/size/w=2000?exp=1756186132&sig=_sdaDja8ngc65Z3Yp_lO6cRD6bhjXhZjBbD92uqRSOc&id=2562dc3e-f514-80a7-81d7-d5b454a04d8e&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

채팅방 생성 시 에러 응답 확인

![에러응답](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fc64bd460-e79f-4e59-baa8-84b3a2bd23d3%2Fimage.png/size/w=2000?exp=1756186147&sig=6nD-LuTpB8FnEJ5NvMnH98JKz0EdD1BvKpTix7PPihM&id=2562dc3e-f514-8028-b4bf-df2be8509216&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

Intellij에서 에러 로그 확인
→ 쿼리가 1개의 결과값을 반환해야 하는데 2개의 결과값을 반환함

![에러로그](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fe1c72e69-fd29-4def-977d-26be925f23cb%2Fimage.png/size/w=2000?exp=1756186163&sig=7BY5aBVOIUFRH8m_S_Hyzb841KDeePFDcxnWnEisPrc&id=2562dc3e-f514-80c4-a607-deb83a55fb85&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

## 2️⃣ 문제 및 원인

### 문제 상황

채팅방 생성 시 요청한 유저를 `sender`, 채팅 상대 유저를 `receiver`로 지정하여 채팅방을 생성함.

동시에 아래와 같은 요청이 발생하면 문제가 발생함

1. senderId:1, receiverId:2 → 채팅방 생성
2. senderId:2, receiverId:1 → 거의 동시에 채팅방 생성

![채팅방 생성 로직](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fb591f998-ff2c-4f25-bc20-217f1c25cf0d%2Fimage.png/size/w=2000?exp=1756186183&sig=gT3GMOxFQEhklBFAngE7-1a6HG2NjO4jpztVKbgvUG4&id=2552dc3e-f514-8019-a28e-cb9463eabd2d&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)


채팅방 생성 로직

![동일 유저 조합의 채팅방이 2개 생성됨](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F3e54672e-82c2-423c-8ab5-c223e4a6783a%2Fimage.png/size/w=2000?exp=1756186202&sig=V76OGXIjl91JeOE2_0h10Lc5udiEsKPnz8rXILRSuxc&id=2562dc3e-f514-8077-8a94-e73ee92745e5&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)


동일 유저 조합의 채팅방이 2개 생성됨

동시 실행 시 두 요청 모두 DB 조회 시점에서는 채팅방이 존재하지 않는 것으로 판단하여 각각 채팅방을 생성함

결과적으로 동일한 조합(senderId-1, receiverId-2)에 대해 삭제되지 않은 채팅방이 두 개 생성되며,

Optional로 조회할 때 데이터가 하나만 있어야 하는 규칙을 위반하여 에러 발생

![Optional로 채팅방 조회하는 로직](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fbdd6ba9b-c4a9-4c14-a2d1-f4bf3d1f700b%2F58c0a260-f5ec-4c85-aea1-fe10695416ad.png/size/w=2000?exp=1756186217&sig=KxOzxWINS7hyeCWxbJLUbQgF07-df86EfwL39U1o_3k&id=2552dc3e-f514-80cf-99a3-e80683601ca6&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

Optional로 채팅방 조회하는 로직

### 원인 분석

- 기존 로직에서는 senderId와 receiverId 순서를 임의로 처리하여, 같은 유저 조합이지만 반대로 요청되는 경우를 구분하지 못함
- DB 조회 후 INSERT 사이의 동시성 제어가 없어서 두 요청이 동시에 채팅방을 생성할 수 있음
    - 즉, DB 레벨 동시성 문제가 존재함.

## 3️⃣ 해결 과정

### 해결 방법 검토

1. **비관적 락(Pessimistic Lock) 적용**
- 기존 채팅방 조회 로직에 `@Lock(LockModeType.PESSIMISTIC_WRITE)` 적용
- **장점**
    - 조회 시 다른 트랜잭션이 동일한 데이터를 수정하거나 추가하지 못함
- **단점**
    - INSERT 시 발생하는 데드락 현상까지 완전히 방지할 수 없음
1. **DB 제약 조건 활용 (Unique Constraint)**
- 채팅방 테이블에 `(sender_id, receiver_id)` 조합에 대해 고유 제약 조건(UNIQUE)을 걸어두고, INSERT 시 중복이 발생하면 DB에서 에러를 발생시키도록 함
- **장점**
    - DB 레벨에서 중복 방지를 보장
- **단점**
    - 예외 발생 시 트랜잭션 롤백 처리 필요. 여전히 동시 INSERT 요청이 들어오면 하나는 실패함
    - 또한 현재 로직은 `is_deleted=true`인 채팅방은 여러 개 허용하기 때문에 단순 UNIQUE 제약 조건은 맞지 않음

### 실제 해결 과정

- sender/receiver 구분을 없애고 항상 작은 ID를 `user1Id`, 큰 ID를 `user2Id`로 고정하여 동일한 유저 조합은 항상 동일 키로 관리

![image.png](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F115d6a8d-647e-4eba-be73-047c0bf9308f%2Fimage.png/size/w=2000?exp=1756186236&sig=YNRRZ5j8KZTywjuCkJgpDP9fahLs1NtZBEn4MlJ_PaQ&id=2552dc3e-f514-804c-ab1d-c05a51af6d9e&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

- 채팅방 조회 시 비관적 락 적용

![image.png](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fea3e5a00-cb5d-4312-b669-ca1dd53d5ceb%2Fimage.png/size/w=2000?exp=1756186252&sig=KnKz2sEXfNl_I2OEopmqQtVSIHe8EhWPKw7aeUHAGXA&id=2552dc3e-f514-8093-96d3-c513818feafc&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

- 데드락 발생 시 예외 처리를 통해 클라이언트에게 재시도를 유도

![image.png](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Feb4a918f-fc7e-40af-a33e-83b4da4f7224%2Fimage.png/size/w=2000?exp=1756186261&sig=Xg9gHxa4rO-qzvqcrPVZ-lqRViRc7njn6NCWkHS9zko&id=2552dc3e-f514-8008-a15b-ec71fc2e7fee&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

- **이유**
    - 채팅방 중복 생성 방지를 위해 동시성 제어가 필요했음
    - 유저 ID 기준 순서 고정과 락 적용으로 코드 구조 변경 최소화 가능
    - Postman 환경과 JMeter 환경 모두 안정적으로 동작하도록 개선

## 4️⃣ 결과 및 회고

![동일 유저 조합의 채팅방 1개 생성됨](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F48a36d38-aa2a-42f3-ad01-f011da48da35%2Fimage.png/size/w=2000?exp=1756186273&sig=XhuNzwg1qd9ey6JJcFZHBT48cFQFVMuGIa7Npdw7qEQ&id=2562dc3e-f514-800f-9e44-ee8ae09ad1ce&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

동일 유저 조합의 채팅방 1개 생성됨

채팅방 생성된 상태에서 채팅방 생성 로직 실행 시 에러 발생하지 않음

![image.png](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fa4f5093e-8de6-4f91-a71f-37f819a622dd%2Fimage.png/size/w=2000?exp=1756186283&sig=y67gXC3p33GDomk1iaT5CSdHlGK4ZO6iYLXblhb4aLE&id=2562dc3e-f514-808f-b43c-d3b53f8084b7&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

- Postman으로 테스트 했을 시에는 문제가 없어서 처음에는 동시성 문제를 예상하지 못했음
- JMeter 테스트를 통해 다중 요청 환경에서 발생할 수 있는 경쟁 조건(Race Condition)을 경험
- DB 락과 예외 처리 전략을 적용하며 동시성 문제 대응 방법을 학습
- 향후 동시성 로직 설계 시, 트랜잭션과 유저 식별 기준을 명확히 하고, 테스트 환경을 다양화해야 함을 깨달음
- 추후 분산 서버로 운영 시에는 분산 락(Distributed Lock) 도입을 고려해야 함
</div>
</details>

<details>
<summary>🚨 Redis 에서 주기적으로 값이 사라지는 문제</summary>
<div markdown="1">

## 1️⃣  **배경**

EC2 에 올린 Spring boot 서비스를 테스트 하던 도중 다음날 확인해보면 저장해두었던 refresh token의 jti, 인기 검색어 랭킹 값들이 계속 사라지는 현상을 발견

## 2️⃣ **문제(상황) 및 원인**

누구도 만들지 않은 새로운 파일 backup1, backup2, backup3, backup4 는 지워지지 않고 그대로 있고, 서비스를 실행하며 만들어진 파일들은 사라짐

![파일사라짐](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F978b626d-a925-4e4f-8dbb-f25f1f3986ef%2FIMG_CB149A2B095C-1.jpeg/size/w=2000?exp=1756187097&sig=mnWgL01PPK1L-bMeGMTxzLradh1f-HEDaDqXZm01eak&id=2552dc3e-f514-80b4-bb6b-d9a7bcf4c204&table=block&userId=1ced872b-594c-814b-8cea-000216eaaf3c)

→ 레디스 해킹당함 (9번 해결 과정에서 발견)

## 3️⃣ **해결 과정**

    1. **활동을 확인할 수 있는 AOF(Append Only File) 설정**
    2. **Redis 속 data 를 주기적으로 조회하는 로직 추가**

       5분마다 작동되는 스케쥴러 사용해 현재 저장되어있고 ttl 이 약 7일 정도 남은 키 값에 맞는 Value 값을 담는 변수를 만들어, 이 변수를 로그로 출력

       만약 userId 가 없다면 null 값 찍힘

        - 코드

            ```java
            // Redis test (TTL 이전 키들이 사라지는 문제 확인을 위함)
            String userId = stringRedisTemplate.opsForValue().get("refresh:6ae359b6-29ce-4d3b-870a-9cd3eb692213");
            log.warn("Redis test 용 - key(refresh:6ae359b6-29ce-4d3b-870a-9cd3eb692213, 만료예정일 8/25) 존재함 userId={}", userId);
            ```

    3. **EC2 가 재기동 되었는지 확인 - X**

       uptime (19일 전) 확인

    4. **EC2 에 자동으로 재시작 되는 옵션이 있는지 확인 - X**

       Instance reboot migration - Default (On) 옵션 있지만 실제로 발생시 uptime 이 0부터 시작

    5. **Redis 를 Docker 로 실행하기 때문에 Docker 가 재기동 되었는지 확인 - X**

       `docker ps` 로 uptime (3일 전) 확인

    6. **메모리가 부족한지 확인 - X**

       `top` 로 사용량 체크

    7. **누가 수동으로 지우는지 확인 - 코드 내부에 지우는 코드 하나하나 찾았지만 관련 없음**

       그럴 가능성 극히 낮음, 접근 하는 사람 나, 은욱님 뿐, 둘 다 지우지 않았음

    8. **redis.conf 확인 - 이상 없음**
        - 내용

            ```bash
            # redis.conf 
            
            appendonly yes
            ```

    9. **aof 로 내용 확인 - 원인 발견 ❗️**
        - 내용

          flushall 명령어가 존재

          ![IMG_B5BE323FEEC5-1.jpeg](attachment:f3655b09-04e3-438c-a309-ca5c7f8a4af2:IMG_B5BE323FEEC5-1.jpeg)

          ip 주소 확인해보니 NL (네덜란드) 로 나타남

          ![IMG_8A659209BA8E-1.jpeg](attachment:cb4e9763-6a70-4966-8bc6-73ccf4d326a7:IMG_8A659209BA8E-1.jpeg)



    ## 4️⃣ **** **결과 및 회고**
    
    **ElastiCache 사용하려고 외부접근을 열어뒀는데 비용문제로 EC2 내부에 Redis 주입하기로 했고, 보안 설정의 중요성을 느낌**
    
    1. inbound 규칙 주의해서 설정 
        
        redis 포트 열린거 삭제 (기존: 모두가 접근 가능하게 열려있었음)
        
    2. redis 에 비밀번호 추가
    3. port 번호를 변경하기 - default 6379 가 아닌 랜덤한 10000번대 포트번호로 설정
</div>
</details>

<details>
<summary>🚨 Server-Sent Events(SSE) CORS 문제</summary>
<div markdown="1">

## 1️⃣ **배경**

로컬 환경에서 테스트를 진행할 때, 클라이언트(HTML, `http://127.0.0.1:5500`)와 서버(Spring Boot, `http://localhost:8080`)를 각각 실행하였습니다. 이 과정에서 브라우저 콘솔에 CORS 관련 에러가 발생하여, SSE 구독 요청이 서버에 도달하지 못하는 상황을 발견하였습니다.
****

    - 브라우저 콘솔에 아래와 같은 에러 발생:

        ```bash
        CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
        ```


    ## 2️⃣ **문제 및 원인**
    
    - **문제 현상:**
        
        구독 요청(`/notifications/subscribe`)이 브라우저 단계에서 차단되며, 서버 로그에도 요청이 남지 않았습니다.
        
    - **원인:**
        
        브라우저의 보안 정책인 **Same-Origin Policy** 때문이다. 서버와 클라이언트의 도메인/포트가 다를 경우 브라우저가 요청을 차단한다. 서버 측에서 교차 출처 요청(CORS)에 대한 허용 설정이 없었기 때문에 발생한 문제였습니다.
        
    
    ## 3️⃣ **해결 과정**
    
    - 서버에서 특정 엔드포인트(`/notifications/**`)에 대해 CORS 허용을 추가하였습니다.
        - 전역 CORS 설정 (WebMvcConfigurer 사용)
        - 컨트롤러 단위 CORS 설정 (@CrossOrigin)
    - 로컬 테스트 시에는 `http://127.0.0.1:5500`을 허용 도메인으로 지정하고, 실제 배포 시에는 서비스 도메인(`https://myapp.com`)을 허용하도록 설정하였습니다.
        1. **전역 CORS 설정 (WebMvcConfigurer 사용)**
        
        ```java
        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/notifications/**")
                            .allowedOrigins("http://localhost:5500")  //로컬 테스트 도메인 허용
                            .allowedMethods("GET", "POST", "OPTIONS")
                            .allowCredentials(true);
                }
            };
        }
        ```
        
        - 배포 시에는 실제 서비스 도메인(`https://myapp.com`)을 `allowedOrigins`에 추가해야 함.
    
    ## 4️⃣ **결과 및 회고**
    
    - 결과
        - 구독 요청이 차단되지 않고 서버까지 도달하여 SSE 연결 시도가 가능해졌습니다.
        - 클라이언트가 서버 API에 정상적으로 접근 가능해졌고, SSE 구독 요청이 서버까지 전달되었습니다.
    - 회고
        - API 자체 문제가 아니라 브라우저 보안 정책(CORS)을 이해하지 못한 것이 원인이었습니다.
        - 이번 경험을 통해, 클라이언트와 서버가 다른 도메인에서 통신할 때는 항상 CORS 설정을 검토해야 함을 배웠습니다.
</div>
</details>


## ⚡ 성능 개선
<details>
<summary>⚡ 게시글 전체 조회 성능개선</summary>
<div markdown="1">

## 1️⃣ 배경

**WorkoutMate의 게시글 전체 조회 기능**은 사용자가 원하는 운동 종목에 맞춰 **함께 운동할 파트너를 구인·모집하는 기능을 제공**합니다.

이용자는 축구, 러닝, 배구 등 관심 있는 운동 종목별로 모임을 찾거나 직접 사람들을 모집할 수 있고, 모집 글을 올린 사용자들과 쉽게 연결될 수 있습니다.

**< 테스트 조건 >**

환경

- 로컬 : 10코어 / 24GB
- 도구 : JMeter (부하 생성), pinpoint (APM 분석)
- 데이터 : 게시글 10만건

스레드 속성

- 사용자 수: 500
- Ramp-up: 60초
- 루프 카운트: 10회

## 2️⃣ 문제 및 원인

### 1. 첫번째 문제 발생

![첫번째문제](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F18a21ca3-bcaa-4d30-bc33-e3a7ca762255%2FKakaoTalk_Photo_2025-08-20-00-10-57.jpeg/size/w=2000?exp=1756179763&sig=nfvTH0hLd1cLXkco4X4M_mPoH6kJYoA3d-VE5REy9DM&id=2552dc3e-f514-803a-83f0-d24340521852&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

**문제 인식**

- @ManyToOne의 기본 fetch 전략은 `EAGER`입니다.  기본값으로 인해 게시글 조회 시 작성자 정보가 즉시 로딩(EAGER)되면서, 게시글 수만큼 작성자 조회 쿼리가 반복 실행되는 N+1 문제가 발생하였습니다.

```jsx
// 기존코드
@ManyToOne
@JoinColumn(name = "writer_id", nullable = false)
private User writer;
```

**해결 방법**

- Board → User 연관관계를 LAZY로 전환하여, 작성자 정보는 실제로 필요할 때만 조회되도록 변경하였습니다.

```jsx
// 작성자
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "writer_id", nullable = false)
private User writer;
```

- 보통 N+1 문제는 fetch join이나 EntityGraph를 통해 한 번에 연관 데이터를 가져오는 방식으로 해결합니다.
- 하지만 이번 경우에는 `Board → User` 연관관계가 항상 필요한 데이터가 아니었기 때문에, **EAGER → LAZY 전환만으로도 작성자(User) 조회 쿼리 자체가 발생하지 않아 N+1 문제가 사라졌습니다.**

**효과**

- 그 결과, n+1의 문제를 제거하여 불필요한 추가 쿼리 실행을 방지할 수 있었습니다.

### 2. 두번째 문제 발생

![두번째문제](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F0f8974f1-b1f4-4159-8ef4-aae544113fb6%2FKakaoTalk_Photo_2025-08-20-00-11-03.jpeg/size/w=2000?exp=1756179793&sig=vG-dyhacFVY7Sv-o-L6Q41LVtIod41YeB9Z0lkeQdVU&id=2552dc3e-f514-808f-8cb3-cd26ed7a752e&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

**문제 인식**

- N+1은 해결했지만,  여전히 조회하는데 병목현상이 발생 하였습니다.
- 전체 응답 시간 : 3,491ms
- **주요 병목:** 게시글 목록 조회 SQL → **2.537ms 소요 (전체의 약 72%)**

**해결 방법**

- 쿼리 한 줄 성능을 비교 할 수 있는 `EXPLAIN` 을 통해서 쿼리 실행 계획을 보았습니다.

![쿼리실행계획](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F8dff3512-e882-4849-b957-05177210df35%2F%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-08-13_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_4.10.28.png/size/w=2000?exp=1756179977&sig=GFtWJONFmXApNPiVpW6_YWbILsKqycUVod_j2l30PHg&id=2552dc3e-f514-804b-aee7-f260541301e7&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

type이 ALL로 표시되어 있어서, 테이블 전체 스캔(Full Table Scan)이 발생하고 있음을 확인할 수 있습니다.

possible_keys 및 key 항목이 모두 null로 표시되어서 인덱스를 전혀 사용하지 않고 쿼리가 실행되고 있음을 확인 할 수 있었습니다.

**`EXPLAIN ANALYZE` 를 이용하여 쿼리 성능에 대해 더 자세히 보았습니다**

![쿼리성능](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fdfce4def-2e9e-46fd-8748-d063bebe21a4%2F%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-08-13_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_4.10.47.png/size/w=2000?exp=1756180006&sig=5rlGD2SDmm-xUdC0CyPwXbVe1DPG15fKSTVrzDw8y7s&id=2552dc3e-f514-8035-81b4-fc6cfcc38650&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

| 단계 | 설명 | actual time | 소요(ms) |
| --- | --- | --- | --- |
| **Table Scan** | `board` 테이블 전체 스캔 | 0.227..115 | **114.8ms** |
| **Filter** | `is_deleted = 0` 조건 필터링 | 0.23..122 | **7ms** |
| **Sort** | `ORDER BY modified_at DESC` 정렬 | 141..141 | **19ms** |
| **Limit** | `LIMIT 10` | 141..141 | 0ms |

`총 = 141ms`

혼자서 이 api를 요청을 하게 되면 아무런 문제가 없지만 동시 접속자가 증가를 하게 되었을 때는 병목 현상을 발견하게 되어서 인덱스를 생성하여 쿼리가 인덱스를 타도록 수정을 했습니다.

- is_deleted, modified_at, id를 기준으로 복합 인덱스를 생성하여, 삭제 여부 조건과 최신순 정렬을 인덱스 레벨에서 처리할 수 있도록 개선하였습니다.

```sql
CREATE INDEX idx_board_feed
ON board (is_deleted, modified_at DESC, id DESC);
```

### 인덱싱 적용 후

![인덱싱적용후1](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fec44af07-be65-4068-be0e-2c185feba596%2F%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-08-11_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_4.45.52.png/size/w=2000?exp=1756180028&sig=uvrgYTintm1KEn8r0iI_F2LkfvgwwV5ezWTqgixYiME&id=2552dc3e-f514-80c0-8fb1-ec7318dd3088&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

![인덱싱적용후2](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F9c8d667b-7b98-47a7-bc7b-7a0b99753ad6%2F%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-08-13_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_4.46.04_(1).png/size/w=2000?exp=1756180042&sig=BFsgR1TMDbkmAuKmaK1XTvnQRvK80ZVeLOK9HNDCqkw&id=2552dc3e-f514-80d0-9d5d-c3b04f4a5e12&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

![인덱싱적용후3](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F05fd506a-6211-4836-850c-992128b39f59%2F%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-08-13_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_4.45.48_(1).png/size/w=2000?exp=1756180058&sig=tMeS9DW9uO662enwOkFMVHm9D1nABIBFJtEuDPbunfM&id=2552dc3e-f514-8046-b7c1-eabbd86bd848&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

### **효과**

- 실행 계획이 Table Scan → Index Lookup으로 전환
- 스캔 데이터 수가 100,000건에서 10건으로 줄어듬
- 단일 쿼리 실행 시간은 **141ms → 10ms**로 단축됨

## 3️⃣  성능 개선 결과

### **실행 계획 비교**

| 항목 | 기존 실행 계획 | 개선된 실행 계획 |
| --- | --- | --- |
| 쿼리 실행 시간 | 141 ms | 10 ms |
| 스캔 방식 | Table scan | Index lookup |
| 스캔 데이터 수 | 100,000 rows | **10 rows** |

### **JMeter 부하 테스트 지표 비교**

개선전

![개선전](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F429242f3-d0a3-4db1-97d4-1b9022c53f3d%2F%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-08-13_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_4.36.48.png/size/w=2000?exp=1756180074&sig=npSLQXDG6dXlMCVFCUmszBxDvqlNsEZ0bIZuHvAXwIQ&id=2552dc3e-f514-807c-9c07-eb5fd150017b&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

개선후

![개선후](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F76cec895-32ab-4124-a9ea-9ecceb1e0380%2F%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-08-13_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_4.26.08.png/size/w=2000?exp=1756179741&sig=jCe_6IcdwT7RJskjHJleSl-9NWjsvbWV-gJ4Dmruers&id=2552dc3e-f514-8058-99b0-efa33da2fffc&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)


| 항목 | 개선 전 | 개선 후 | 변화 |
| --- | --- | --- | --- |
| **Average** | 8,744 ms | 52 ms | ↓99.41% |
| p95 | 14,499 ms | 120 ms | ↓99.17% |
| Throughput | 33.4 건 | 83.0 건 | +148.5% |

</div>
</details>

<details>
<summary>⚡ CI/CD 워크플로우 경량화</summary>
<div markdown="1">

## 1️⃣ **배경**

cicd workflow 내부에 불변의 시스템 (monitoring) 세팅까지 함께 들어있어서 코드가 변경될 때 마다 전체 환경이 세팅되는 상황

## 2️⃣ **테스트 조건(테스트 환경)**

github actions 를 사용하여 EC2 서버 내에 Spring boot 를 Docker container 로 띄우는 cicd 구현함

release/2.1.0 버전으로 테스트 진행

## 3️⃣ **문제 인식**

같은 서버 내에서 코드만 수정되는데 불변의 시스템(grafana, prometheus, loki, promtail, redis)을 재시작 할 필요 없음.

프로그램을 종료하고 시작하는 것은 서버 입장에서 부담스러운 일 → 줄이는게 좋음

## 4️⃣ **해결 방법**

spring boot 에는 서비스 관련 내용만 담고

EC2 내부에 모니터링, DB 관련 파일들 분리해놓기

## **5️⃣** **효과**

배포 시간이 줄어들고, CPU 사용량이 줄어듦 = 서버에 부담이 덜해짐

## **6️⃣ 개선 전후 지표**

![개선전후지표](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F6ab27425-1601-4822-9388-118a0f4b1dad%2FIMG_D284580B0008-1.jpeg/size/w=2000?exp=1756179706&sig=1TD0UiSh9F-xkMdGiKWs19rs_PENpdHDTNjiQqZS4h0&id=2552dc3e-f514-8081-bbd7-d0a3e16b401e&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

**기존 CICD workflow 실행 (release/2.1.0)**

CPU Usage: 0.135

![기존CICD](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F4b1badac-f55b-4f48-b7f4-6277fe6055be%2FIMG_12DBE800ECE6-1.jpeg/size/w=2000?exp=1756179689&sig=TObiq9z8OuPo1eNtAXNHD-RlZpDJb7BpOjv7JqhcPug&id=2552dc3e-f514-801e-b3ec-cffc3c841327&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

**경량화 후 CICD workflow 실행 (release/2.1.0)**

CPU Usage: 0.0496

![경량화후CICD](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F16549e9c-ce6a-4b81-bec5-53b4891576ed%2FIMG_FBA7E0432B19-1.jpeg/size/w=2000?exp=1756179661&sig=cFIhZFFEAyxiSMEC0YmZfxuR42yPZ1WefUBm7AtL660&id=2552dc3e-f514-8001-bef4-c4a4fdba83e8&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

</div>
</details>

<details>
<summary>⚡ 마이페이지 조회</summary>
<div markdown="1">

## 마이페이지 조회

## 1️⃣ 배경

마이페이지 화면에서는 사용자의 **팔로워 수 / 팔로잉 수** 를 함께 보여줍니다.

하지만 단순히 "숫자"만 필요한 상황에서, 잘못된 접근 방식으로 인해 불필요하게 **팔로워/팔로잉 전체 행을 전부 조회**하는 문제가 발생했습니다.

**< 테스트 조건 >**

[환경]

    - 로컬 : 10코어 / 24GB
    - 도구 : JMeter (부하 생성), pinpoint (APM 분석)
    - 데이터 : 팔로우 5만, 팔로워 5만

[스레드 속성]

    - **사용자 수**: 500
    - **Ramp-up**: 60초
    - **루프 카운트**: 10회

## 2️⃣ 문제 및 원인

![문제및원인](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fdac7f99c-b475-4ce9-9e87-14352aecadc2%2FUntitled_Notebook_(1)-6.jpg/size/w=2000?exp=1756179408&sig=5CKyc4ZvtKb-Ixa55Ef_TGDc84T1bF9RZXPNDTV2jZw&id=2552dc3e-f514-804a-a820-f99729bafceb&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

### 문제인식

    - user.getFollowers().size() / user.getFollowings().size() 호출 시, JPA는 **단순히 개수(COUNT)만 필요함에도 불구하고, 연관된 모든 행(row)과 컬럼(column)을 전부 조회하게 되었습니**다.
    - 즉, 팔로워 수라는 “숫자 하나”만 필요했지만, 실제로는 팔로워 전체 데이터를 SELECT하여 엔티티 객체로 변환하는 비효율이 발생 했습니다.

  ```jsx
  // 기존 코드 
  @JsonIgnore
  @OneToMany(mappedBy = "follower")
  private List<Follow> followers;
  
  @JsonIgnore
  @OneToMany(mappedBy = "following")
  private List<Follow> followings;
  
  int followerCount = user.getFollowers() != null ? user.getFollowers().size() : 0;
  int followingCount = user.getFollowings() != null ? user.getFollowings().size() : 0;
  ```

    - 이런식으로 follwers, following을 가져오게 되면 **팔로워/팔로잉 전체 행(및 컬럼)을 전부 가져옴**

실제 날라가는 쿼리문

  ```
  select
    f1_0.follower_id,
    f1_0.id,
    f1_0.created_at,
    f1_0.following_id,
    f1_0.modified_at
  from
    follows f1_0
  where
    f1_0.follower_id = '1'
  ```

전체 응답 시간: **7,339ms**

    - 세부 실행 시간:
        - **팔로워 조회 쿼리**
            - 실행 시간: **3,471ms + 449ms = 3,920ms**
        - **팔로잉 조회 쿼리**
            - 실행 시간: **354ms**
        - **게시글 count 조회 쿼리**
            - 실행 시간: **3,030ms**

## **해결 방법**

    - .size() 대신 COUNT 쿼리 메서드를 별도로 작성하여, 팔로워/팔로잉 수를 직접 조회하도록 변경하였습니다.
    - 즉, 컬렉션 전체를 불러오는 대신 `SELECT COUNT(*) FROM follows WHERE ...` 형태의 쿼리로 필요한 숫자만 가져오도록 했습니다.

  ```jsx
  **int** followerCount = followCountService.countByFollowingId(user.getId());
  **int** followingCount = followCountService.countByFollowerId(user.getId());
  ```

![해결 방법](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F5edab336-fb99-47e5-9447-e632a8444294%2FUntitled_Notebook_(1)-7.jpg/size/w=2000?exp=1756179543&sig=jO2hZ69xlMq2UocLOBQYwKQ2dgrLeKMk--5HhUjn7cE&id=2552dc3e-f514-80c2-9a97-e5380a87f672&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

### **효과**

    - **팔로잉 수 조회**
        - 실행 시간: **10ms**
    - **팔로워 수 조회**
        - 실행 시간: **9ms**
    - **작성 게시글 수 조회**
        - 실행 시간: **2ms**

## 3️⃣  성능 개선 결과

### **JMeter 부하 테스트 지표 비교**

개선전

![개선전](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2Fa3072f2e-9438-49bf-88f2-82edbd5ff93a%2F%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-08-13_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_10.07.18.png/size/w=2000?exp=1756179597&sig=MPrTXMzUTqgtH622tj-2v3ZbqwmLGcdwH8foV4SCmmA&id=2552dc3e-f514-80ee-9368-dba0d2e2fab9&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

개선후

![개선후](https://img.notionusercontent.com/s3/prod-files-secure%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F7ab431b3-5ac6-43de-b6f6-a2a4ce158cf9%2F%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2025-08-13_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_10.09.36.png/size/w=2000?exp=1756179623&sig=YcsKlIfYkKm6O4PUkd35Y3W7PSAg9oucQKPiSvxw0j8&id=2552dc3e-f514-80ca-a193-e54ac3d6f8bf&table=block&userId=6ef99c76-bcd6-475b-a4a0-9cec68ab6ad6)

| 항목 | 개선 전 | 개선 후 | 변화 |
    | --- | --- | --- | --- |
| **Average** | 39,336 ms | 34 ms | ↓**99.91%** |
| p95 | 52,998 ms | 49 ms | ↓**99.91%** |
| Throughput | 2.1 건 | 16.6 건 | ↑**690.48%** |


</div>
</details>



## 👥 팀원

|   이름   | 직책  | 역할                            |
|:------:|:----|:------------------------------|
|  김두하   | 리더  | 유저 인증/인가, 배포 인프라 구축, 모니터링/로깅  |
|  김태현   | 부리더 | 팔로잉 기능, 참여 요청 기능, 개인 맞춤 추천 기능 |
|  곽현민   | 팀원  | 게시글 CRUD, 찜 CRUD, 알림 기능       |
|  안은욱   | 팀원  | 댓글 CRUD, 이메일 인증 기능, 인기글 조회 기능 |
|  이현하   | 팀원  | 유저 RUD, 1:1 채팅 기능             |