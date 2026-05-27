# Media Service

`media_service`, CampusConnect platformunda medya dosyalarının yüklenmesi, saklanması, doğrulanması ve yönetilmesinden sorumlu Spring Boot servisidir.

## Özellikler

- Görsel, video ve genel dosya yükleme akışları
- Yerel disk veya Cloudinary tabanlı saklama desteği
- Dosya boyutu ve türü doğrulamaları
- Medya kullanım ve istatistik servisleri
- Temel güvenlik, sağlık kontrolü ve servis keşfi entegrasyonları
- Scalar üzerinden API dokümantasyonu

## Teknoloji Yığını

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- Spring Security
- Spring Web / WebFlux
- PostgreSQL
- Spring Cloud Consul Discovery
- Cloudinary
- Lombok

## Varsayılan Çalışma Ortamı

Varsayılan ayarlara göre servis:

- **Port:** `8087`
- **Uygulama adı:** `media-service`
- **Veritabanı:** `campusconnect_media`
- **Consul:** `localhost:8500`
- **Scalar doküman yolu:** `/scalar`

## Önkoşullar

- JDK 17
- Maven veya proje içindeki `mvnw`
- PostgreSQL
- Consul (servis keşfi kullanılıyorsa)
- Cloudinary hesabı ve kimlik bilgileri (Cloudinary saklama modu için)

## Yapılandırma

Tüm temel ayarlar `src/main/resources/application.properties` dosyasında bulunur.

Önemli değerler:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `app.media.storage-provider` — `LOCAL` veya `CLOUDINARY`
- `app.media.local-upload-dir`
- `app.media.public-base-url`
- `cloudinary.cloud-name`
- `cloudinary.api-key`
- `cloudinary.api-secret`

### Yerel saklama modu

Yerel disk kullanmak istiyorsanız:

1. `app.media.storage-provider=LOCAL` olarak güncelleyin.
2. `app.media.local-upload-dir` değerini istediğiniz klasör adıyla ayarlayın.
3. Gerekirse `app.media.public-base-url` değerini güncelleyin.

### Cloudinary modu

Cloudinary kullanmak için:

1. `app.media.storage-provider=CLOUDINARY` olarak bırakın ya da ayarlayın.
2. `cloudinary.cloud-name`, `cloudinary.api-key` ve `cloudinary.api-secret` değerlerini doldurun.

## Çalıştırma

Proje klasöründe aşağıdaki komutlardan birini kullanın:

```bash
./mvnw spring-boot:run
```

veya

```bash
mvn spring-boot:run
```

## Derleme

```bash
./mvnw clean package
```

veya

```bash
mvn clean package
```

## Sağlık Kontrolü

Actuator health uç noktası etkinleştirilmiştir:

- `GET /actuator/health`

## Proje Yapısı

- `config/` — uygulama ve altyapı yapılandırmaları
- `service/` — iş mantığı katmanı
- `storage/` — yerel ve Cloudinary saklama implementasyonları
- `repository/` — veri erişim katmanı
- `entity/` — JPA entity sınıfları
- `dto/` — veri transfer nesneleri
- `mapper/` — dönüşüm yardımcıları
- `security/` — güvenlik yapılandırması
- `exception/` — özel hata sınıfları
- `util/` — yardımcı araçlar

## Notlar

- `HELP.md` dosyası Spring Initializr tarafından oluşturulmuş genel yardım metnini içerir.
- Uygulama açıldığında servis, Consul'a kendini kaydetmeye çalışır.
- Medya saklama seçeneğini değiştirdikten sonra ilgili ortam değişkenlerini ve konfigürasyonları kontrol edin.

