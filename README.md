# CampusConnect

CampusConnect, kampüs odaklı sosyal ağ uygulaması için mikroservis tabanlı bir platformdur. Depo; Spring Boot tabanlı servisler, gerçek zamanlı sohbet servisi ve web arayüzünü içerir.

## Mimari ve Servisler

- **api_gateway**: Dış erişim için API geçidi.
- **auth_service**: Kimlik doğrulama ve yetkilendirme.
- **user_service**: Kullanıcı profili ve hesap işlemleri.
- **post_service**: Gönderi işlemleri.
- **comment-service**: Yorum işlemleri.
- **like-service**: Beğeni işlemleri.
- **friend_service**: Arkadaşlık/bağlantı işlemleri.
- **story_service**: Hikaye (story) işlemleri.
- **media_service**: Medya yükleme ve yönetimi.
- **event_service**: Etkinlik işlemleri.
- **notification_service**: Bildirim yönetimi.
- **logging_service**: Loglama ve gözlemlenebilirlik.
- **admin_service**: Yönetim servisleri.
- **chat-service**: Gerçek zamanlı sohbet (Node.js + TypeScript).
- **campusconnect-web**: Web arayüzü (React + Vite).

## Teknoloji Yığını

- **Java 17 + Spring Boot**: Çekirdek mikroservisler.
- **Node.js + TypeScript**: Chat servisi.
- **React + Vite**: Web arayüzü.
- **Prisma**: Chat servisi veri erişimi.

## Önkoşullar

- JDK 17
- Maven (veya servis klasöründeki `mvnw`)
- Node.js + npm
- Servislere özel dış bağımlılıklar (veritabanı, cache, vb.) ilgili servis konfigürasyonlarına göre hazırlanmalıdır.

## Lokal Çalıştırma

### Spring Boot servisleri

Her servis kendi klasöründe çalıştırılır:

```bash
cd auth_service
./mvnw spring-boot:run
```

> `mvnw` olmayan servislerde `mvn spring-boot:run` komutunu kullanın.

### chat-service

```bash
cd chat-service
npm install
npm run prisma:generate
npm run prisma:migrate
npm run dev
```

### campusconnect-web

```bash
cd campusconnect-web
npm install
npm run dev
```

## Klasör Yapısı

- `api_gateway/` - API Gateway servisi
- `auth_service/` - Auth servisi
- `user_service/` - Kullanıcı servisi
- `post_service/` - Post servisi
- `comment-service/` - Comment servisi
- `like-service/` - Like servisi
- `friend_service/` - Friend servisi
- `story_service/` - Story servisi
- `media_service/` - Media servisi
- `event_service/` - Event servisi
- `notification_service/` - Notification servisi
- `logging_service/` - Logging servisi
- `admin_service/` - Admin servisi
- `chat-service/` - Chat servisi
- `campusconnect-web/` - Web istemcisi
