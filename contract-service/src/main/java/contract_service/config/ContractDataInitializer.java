package contract_service.config;

import contract_service.entity.Contract;
import contract_service.enums.ContractType;
import contract_service.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Uygulama başlatıldığında gerekli varsayılan sözleşmeleri otomatik olarak oluşturur.
 * Eğer aynı contractType ve version kombinasyonu zaten veritabanında varsa tekrar oluşturmaz.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContractDataInitializer implements CommandLineRunner {

    private final ContractRepository contractRepository;

    private static final String DEFAULT_VERSION = "1.0";

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Sözleşme veri başlatıcısı çalışıyor...");

        Map<ContractType, ContractSeed> seeds = buildContractSeeds();
        int created = 0;

        for (Map.Entry<ContractType, ContractSeed> entry : seeds.entrySet()) {
            ContractType type = entry.getKey();
            ContractSeed seed = entry.getValue();

            boolean exists = contractRepository.existsByContractTypeAndVersion(type, DEFAULT_VERSION);
            if (!exists) {
                Contract contract = Contract.builder()
                        .contractType(type)
                        .title(seed.title)
                        .content(seed.content)
                        .version(DEFAULT_VERSION)
                        .isRequired(seed.required)
                        .isActive(true)
                        .effectiveDate(LocalDateTime.now())
                        .build();

                contractRepository.save(contract);
                created++;
                log.info("Sözleşme oluşturuldu: {} - {}", type, seed.title);
            } else {
                log.debug("Sözleşme zaten mevcut, atlanıyor: {} v{}", type, DEFAULT_VERSION);
            }
        }

        log.info("Sözleşme veri başlatıcısı tamamlandı. {} yeni sözleşme oluşturuldu.", created);
    }

    private Map<ContractType, ContractSeed> buildContractSeeds() {
        Map<ContractType, ContractSeed> seeds = new LinkedHashMap<>();

        seeds.put(ContractType.TERMS_OF_SERVICE, new ContractSeed(
                "Kullanım Koşulları",
                """
                CAMPUSCONNECT KULLANIM KOŞULLARI

                Son Güncelleme: %s

                1. GENEL HÜKÜMLER
                1.1. Bu kullanım koşulları, CampusConnect platformunu ("Platform") kullanan tüm kullanıcılar ("Kullanıcı") için geçerlidir.
                1.2. Platforma kayıt olarak bu koşulları kabul etmiş sayılırsınız.

                2. HİZMET TANIMI
                2.1. CampusConnect, üniversite öğrencileri ve akademik topluluklar için tasarlanmış bir sosyal ağ platformudur.
                2.2. Platform; paylaşım, mesajlaşma, etkinlik yönetimi ve topluluk oluşturma hizmetleri sunar.

                3. KULLANICI YÜKÜMLÜLÜKLERİ
                3.1. Kullanıcılar, doğru ve güncel bilgilerle kayıt olmakla yükümlüdür.
                3.2. Kullanıcılar, platformda yasa dışı, hakaret içeren, ayrımcı veya zararlı içerik paylaşamaz.
                3.3. Kullanıcılar, başkalarının hesaplarını izinsiz kullanamaz.
                3.4. Kullanıcılar, platformun güvenliğini tehlikeye atacak eylemlerden kaçınmalıdır.

                4. İÇERİK POLİTİKASI
                4.1. Kullanıcılar tarafından paylaşılan içerikler, paylaşan kişinin sorumluluğundadır.
                4.2. Platform, uygunsuz bulunan içerikleri önceden haber vermeksizin kaldırma hakkını saklı tutar.

                5. HESAP ASKIYA ALMA VE SONLANDIRMA
                5.1. Platform, koşulları ihlal eden hesapları geçici veya kalıcı olarak askıya alabilir.
                5.2. Kullanıcı, hesabını istediği zaman silme talebinde bulunabilir.

                6. SORUMLULUK SINIRLAMASI
                6.1. Platform, kullanıcılar arası etkileşimlerden doğabilecek zararlardan sorumlu değildir.
                6.2. Platform, hizmet kesintileri veya veri kayıpları nedeniyle oluşabilecek dolaylı zararlardan sorumlu tutulamaz.

                7. DEĞİŞİKLİKLER
                7.1. Bu koşullar önceden bildirimde bulunarak güncellenebilir.
                7.2. Güncellemeler sonrası platformu kullanmaya devam etmek, yeni koşulların kabul edildiği anlamına gelir.
                """.formatted(LocalDateTime.now().toLocalDate()),
                true
        ));

        seeds.put(ContractType.PRIVACY_POLICY, new ContractSeed(
                "Gizlilik Politikası",
                """
                CAMPUSCONNECT GİZLİLİK POLİTİKASI

                Son Güncelleme: %s

                1. VERİ SORUMLUSU
                1.1. CampusConnect platformu olarak kişisel verilerinizin korunmasına büyük önem veriyoruz.

                2. TOPLANAN VERİLER
                2.1. Kimlik Bilgileri: Ad, soyad, kullanıcı adı, e-posta adresi.
                2.2. İletişim Bilgileri: E-posta adresi, telefon numarası (opsiyonel).
                2.3. Eğitim Bilgileri: Üniversite, bölüm, sınıf bilgisi.
                2.4. Kullanım Verileri: Oturum bilgileri, IP adresi, tarayıcı tipi, platform kullanım istatistikleri.
                2.5. İçerik Verileri: Paylaşımlar, mesajlar, yorumlar ve beğeniler.

                3. VERİ İŞLEME AMAÇLARI
                3.1. Hesap oluşturma ve yönetimi.
                3.2. Platform hizmetlerinin sunulması ve iyileştirilmesi.
                3.3. Güvenlik ve dolandırıcılık önleme.
                3.4. Yasal yükümlülüklerin yerine getirilmesi.
                3.5. İstatistiksel analiz ve raporlama (anonimleştirilmiş).

                4. VERİ SAKLAMA SÜRESİ
                4.1. Kişisel verileriniz, hesabınız aktif olduğu sürece saklanır.
                4.2. Hesap silme talebi üzerine verileriniz yasal zorunluluklar saklı kalmak kaydıyla 30 gün içinde silinir.

                5. VERİ GÜVENLİĞİ
                5.1. Verileriniz şifrelenmiş bağlantılar (SSL/TLS) üzerinden iletilir.
                5.2. Veritabanlarımız güvenlik duvarları ve erişim kontrolü ile korunmaktadır.

                6. ÜÇÜNCÜ TARAF PAYLAŞIMI
                6.1. Kişisel verileriniz, yasal zorunluluklar dışında üçüncü taraflarla paylaşılmaz.
                6.2. Anonimleştirilmiş istatistiksel veriler araştırma amaçlı kullanılabilir.

                7. HAKLARINIZ
                7.1. Verilerinize erişim, düzeltme, silme ve taşıma haklarınız bulunmaktadır.
                7.2. Veri işlemeye itiraz etme ve işlemenin kısıtlanmasını talep etme hakkınız vardır.
                """.formatted(LocalDateTime.now().toLocalDate()),
                true
        ));

        seeds.put(ContractType.KVKK, new ContractSeed(
                "KVKK Aydınlatma Metni",
                """
                KİŞİSEL VERİLERİN KORUNMASI KANUNU (KVKK) AYDINLATMA METNİ

                Son Güncelleme: %s

                6698 sayılı Kişisel Verilerin Korunması Kanunu ("KVKK") uyarınca, CampusConnect olarak
                kişisel verilerinizin işlenmesine ilişkin sizi bilgilendirmek istiyoruz.

                1. VERİ SORUMLUSU
                CampusConnect platformu, KVKK kapsamında veri sorumlusu sıfatıyla hareket etmektedir.

                2. KİŞİSEL VERİLERİN İŞLENME AMACI
                Kişisel verileriniz aşağıdaki amaçlarla işlenmektedir:
                - Üyelik kaydının oluşturulması ve hesap yönetimi
                - Platform hizmetlerinin sunulması
                - Kullanıcı deneyiminin iyileştirilmesi
                - İletişim faaliyetlerinin yürütülmesi
                - Bilgi güvenliği süreçlerinin yönetimi
                - Hukuki yükümlülüklerin yerine getirilmesi

                3. KİŞİSEL VERİLERİN AKTARILMASI
                Kişisel verileriniz; yasal yükümlülüklerimiz çerçevesinde yetkili kamu kurum ve kuruluşlarına,
                hizmet aldığımız iş ortaklarına (sunucu hizmetleri, e-posta hizmetleri) aktarılabilecektir.

                4. KİŞİSEL VERİ TOPLAMA YÖNTEMİ VE HUKUKİ SEBEBİ
                Kişisel verileriniz; elektronik ortamda platform üzerinden, KVKK'nın 5. maddesinde belirtilen
                açık rızanız, sözleşmenin ifası ve meşru menfaat hukuki sebeplerine dayanılarak toplanmaktadır.

                5. VERİ SAHİBİNİN HAKLARI (KVKK Madde 11)
                KVKK'nın 11. maddesi kapsamında aşağıdaki haklara sahipsiniz:
                a) Kişisel verilerinizin işlenip işlenmediğini öğrenme
                b) Kişisel verileriniz işlenmişse buna ilişkin bilgi talep etme
                c) Kişisel verilerinizin işlenme amacını ve amacına uygun kullanılıp kullanılmadığını öğrenme
                d) Kişisel verilerinizin yurt içinde veya yurt dışında aktarıldığı üçüncü kişileri bilme
                e) Kişisel verilerinizin eksik veya yanlış işlenmiş olması hâlinde düzeltilmesini isteme
                f) KVKK'nın 7. maddesinde öngörülen şartlar çerçevesinde silinmesini veya yok edilmesini isteme
                g) Verilerinizin aktarıldığı üçüncü kişilere bildirilmesini isteme
                h) İşlenen verilerin aleyhine bir sonuç doğması durumunda itiraz etme
                i) Kanuna aykırı işleme nedeniyle zarara uğramanız hâlinde tazminat talep etme
                """.formatted(LocalDateTime.now().toLocalDate()),
                true
        ));

        seeds.put(ContractType.EXPLICIT_CONSENT, new ContractSeed(
                "Açık Rıza Metni",
                """
                AÇIK RIZA METNİ

                Son Güncelleme: %s

                CampusConnect platformu tarafından sunulan KVKK Aydınlatma Metni'ni okudum ve anladım.

                Bu doğrultuda, aşağıda belirtilen kişisel verilerimin işlenmesine açık rızam ile onay veriyorum:

                1. RIZA VERİLEN VERİ KATEGORİLERİ
                - Kimlik bilgileri (ad, soyad, kullanıcı adı)
                - İletişim bilgileri (e-posta adresi)
                - Eğitim bilgileri (üniversite, bölüm)
                - Profil fotoğrafı ve paylaşılan medya içerikleri
                - Platform kullanım verileri ve istatistikleri

                2. İŞLEME AMAÇLARI
                - Sosyal ağ hizmetlerinin sağlanması
                - Kişiselleştirilmiş içerik önerileri
                - Platform kullanım analitiği
                - Akademik topluluk eşleştirme

                3. RIZA BEYANI
                Yukarıda belirtilen kişisel verilerimin, belirtilen amaçlarla işlenmesine, saklanmasına
                ve gerekli durumlarda aktarılmasına özgür iradem ile açık rıza veriyorum.

                Bu rızamı istediğim zaman, herhangi bir gerekçe belirtmeksizin geri çekebileceğimi biliyorum.
                Rızamı geri çekmem halinde, geri çekme tarihine kadar yapılan veri işleme faaliyetlerinin
                hukuka uygunluğunun etkilenmeyeceğini kabul ediyorum.
                """.formatted(LocalDateTime.now().toLocalDate()),
                true
        ));

        seeds.put(ContractType.COOKIE_POLICY, new ContractSeed(
                "Çerez Politikası",
                """
                CAMPUSCONNECT ÇEREZ POLİTİKASI

                Son Güncelleme: %s

                1. ÇEREZ NEDİR?
                Çerezler, web sitelerini ziyaret ettiğinizde tarayıcınız aracılığıyla cihazınıza
                yerleştirilen küçük metin dosyalarıdır.

                2. KULLANILAN ÇEREZ TÜRLERİ

                2.1. Zorunlu Çerezler
                Platformun temel işlevlerinin çalışması için gereklidir. Oturum yönetimi ve güvenlik
                amacıyla kullanılır. Bu çerezler devre dışı bırakılamaz.

                2.2. İşlevsel Çerezler
                Kullanıcı tercihlerini (dil, tema, bildirim ayarları) hatırlamak için kullanılır.
                Kişiselleştirilmiş bir deneyim sağlar.

                2.3. Analitik Çerezler
                Platform kullanım istatistiklerini toplamak ve hizmetlerimizi iyileştirmek amacıyla kullanılır.
                Toplanan veriler anonimleştirilir.

                3. ÇEREZ YÖNETİMİ
                Tarayıcı ayarlarınızdan çerezleri yönetebilir, silebilir veya engelleyebilirsiniz.
                Ancak zorunlu çerezlerin engellenmesi platformun düzgün çalışmasını engelleyebilir.

                4. ÜÇÜNCÜ TARAF ÇEREZLERİ
                Platformumuzda üçüncü taraf çerezleri kullanılmamaktadır. Tüm çerezler
                doğrudan CampusConnect tarafından yönetilir.
                """.formatted(LocalDateTime.now().toLocalDate()),
                false
        ));

        seeds.put(ContractType.COMMUNICATION_PERMISSION, new ContractSeed(
                "İletişim İzni",
                """
                İLETİŞİM İZNİ

                Son Güncelleme: %s

                CampusConnect platformu olarak, size aşağıdaki konularda elektronik ileti göndermek istiyoruz:

                1. İLETİŞİM KONULARI
                - Platform güncellemeleri ve yeni özellik duyuruları
                - Etkinlik bildirimleri ve hatırlatmalar
                - Topluluk aktiviteleri ve öneriler
                - Güvenlik uyarıları ve hesap bildirimleri
                - Akademik fırsatlar ve duyurular

                2. İLETİŞİM KANALLARI
                - E-posta bildirimleri
                - Platform içi bildirimler (push notification)

                3. İZİN BEYANI
                Yukarıda belirtilen konularda, belirtilen kanallar aracılığıyla tarafıma ticari
                elektronik ileti gönderilmesine izin veriyorum.

                4. İZNİN GERİ ÇEKİLMESİ
                Bu iznimi istediğim zaman, hesap ayarlarım üzerinden veya gelen iletilerdeki
                "abonelikten çık" bağlantısını kullanarak geri çekebilirim.
                """.formatted(LocalDateTime.now().toLocalDate()),
                false
        ));

        return seeds;
    }

    /**
     * Sözleşme tohum verisi için dahili yardımcı sınıf.
     */
    private record ContractSeed(String title, String content, boolean required) {}
}
