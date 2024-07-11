# 프로젝트 개요
이미지 업로드 및 텍스트 번역 기능을 제공하는 웹 애플리케이션입니다. 주요 기능은 이미지에서 텍스트 추출과 번역입니다.

## 백엔드
 - Spring Boot: 백엔드 프레임워크
 - spring-boot-starter-web: 웹 애플리케이션 개발
 - spring-boot-starter-thymeleaf: 템플릿 엔진
 - spring-boot-starter-test: 테스트 라이브러리
 - spring-boot-devtools: 개발 도구
 - OkHttp: HTTP 클라이언트 라이브러리 (com.squareup.okhttp3:okhttp:4.9.3)
 - Jackson: JSON 데이터 파싱 및 생성 (com.fasterxml.jackson.core:jackson-databind)
 - Tess4J: OCR 라이브러리 (net.sourceforge.tess4j:tess4j:4.5.5)
 - Apache Tika: 언어 감지 (org.apache.tika:tika-core:1.27)

## 프론트엔드
 - HTML/CSS: UI 구성 및 스타일링
 - JavaScript: 클라이언트 측 스크립팅
### 기타
 - Gradle: 빌드 도구
 - Thymeleaf: 서버사이드 템플릿 엔진

### 주요 설정 파일
application.properties
- spring.application.name, spring.servlet.multipart.max-file-size, spring.servlet.multipart.max-request-size, deepl.api.key

### 주요 클래스 및 파일
 - 유틸리티 클래스
   - OCRUtil: 이미지에서 텍스트를 추출하는 기능 제공
 - 서비스 클래스
   - TranslationService: 텍스트 번역 기능 제공
 - 컨트롤러 클래스
   - TranslationController: HTTP 요청을 처리하고 서비스 호출
 - HTML 파일
    - index.html: 기본 웹 페이지로, 이미지 업로드 및 번역 UI 요소 포함
