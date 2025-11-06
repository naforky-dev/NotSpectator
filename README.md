# NotSpectator
마인크래프트에서 특정 플레이어의 관전 모드 사용 상태를 숨기는 플러그인

>[!IMPORTANT]
> 이 플러그인을 유튜브와 같은 영상 플랫폼에 사용할 경우 플러그인 링크와 다음의 출처를 남겨주세요.
>
> 아이디어: `나포키`
>
> 제작자: `naforky-dev`


### License
이 플러그인은 GNU의 GPL-3.0 라이선스 하에 배포됩니다.

### 실행 환경
PaperMC `1.21.10`

Java 25 `temurin`(Oracle의 [최신 Java](https://www.oracle.com/java/technologies/downloads/#java25) 버전)

ProtocolLib `5.4.0`(dmulloy2의 [`com.comphenix.protocol.ProtocolLib`](https://repo.dmulloy2.net/#browse/browse:public:com))

>[!TIP]
>이 플러그인은 `ProtocolLib` 플러그인이 없을 경우 실행되지 않습니다.

### Usage
`/notspectator <command>`

`<command>`:
  - `whitelist` - 화이트리스트 설정
    - `whitelist <player>` - 1명의 플레이어를 화이트리스트에 추가
    - `whitelist set <players...>` - 기존 화이트리스트를 삭제, 1명 이상의 플레이어 추가
    - `whitelist remove <players...>` - 1명 이상의 플레이어를 화이트리스트에서 삭제
    - `whitelist check` - 현재 화이트리스트에 있는 플레이어 확인
  - `gamemodemsg <true|false>` - 게임모드 변경 시 표시되는 메시지 출력 또는 숨김 설정

`config.yml` - `/plugins/NotSpectator` 경로에 생성 가능한 설정 파일(서버 새로고침, 재시작 시 설정 리셋 방지)

### 설정 파일
>[!NOTE]
>`config.yml` 파일을 아래의 방법 또는 구조와 다르게 생성할 경우 기본값이 로드됩니다.
>서버 로그에서 설정이 적용되었는지 확인할 수 있습니다.

```yml
# NotSpectator 플러그인 설정
# 화이트리스트에 관전 현황을 숨길 플레이어 추가(소문자)
# 게임 내에서 수정하려면 /notspectator whitelist 사용.
whitelist:
  - "naforky"

# '/gamemode spectator' 사용 시 출력되는 메시지를 숨기려면 false로 설정
# 게임룰(gamerule) 'sendCommandFeedback'를 변경.
show-gamemode-messages: true
```

### 기본 옵션
>[!NOTE]
>아래의 설정은 위의 `config.yml` 파일이 없거나 잘못 작성된 경우
>플러그인이 기본적으로 로드하는 설정입니다.
```yml
whitelist: # 없음
show-gamemode-messages: true
```

---
> (c) 2025 [나포키(naforky)](https://youtube.com/@나포키), [naforky-dev](https://github.com/naforky-dev). All rights reserved.
