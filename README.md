# NIO Number Guess Game – Client/Server Uygulaması

Bu projede Java'nın **non-blocking I/O (java.nio)** yapısını kullanarak basit bir client-server uygulaması geliştirdim. Proje Maven tabanlı ve iki modülden oluşuyor: `server` ve `client`.

## Nasıl Çalışır?

- **Sunucu (server)** tarafı bağlantı isteklerini dinliyor ve her bağlantı için 0-9 arasında rastgele bir sayı üretiyor.
- **İstemci (client)** sunucuya bağlanıyor ve arka arkaya üç sayı gönderiyor.
- Sunucu her sayı için yanıt veriyor:
  - Eğer sayı doğruysa: `"Congratulations!"`
  - Eğer yanlışsa: `"Auto generated value was X. Try less/bigger than Y."`

Tüm veri alışverişi **non-blocking I/O (Selector, SocketChannel)** ile yapılıyor. Herhangi bir framework kullanılmadı.

## Kurulum

- Java 17+
- Maven 3.x

## Derleme

```bash
mvn clean install

