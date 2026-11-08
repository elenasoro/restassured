# REST Assured: тестирование Reqres

Учебный проект для практики автоматизированного тестирования REST API на Java.
Используются REST Assured, TestNG, AssertJ, Hamcrest, Jackson, Lombok и Maven.

## Структура

```text
src/test/java/
├── apitests/
│   ├── UsersTests.java       # Получение, обновление и удаление пользователей
│   └── AuthTests.java        # Регистрация и вход
├── config/
│   └── ApiConfig.java        # HTTP-конфигурация
├── models/                  # Модели запросов и ответов
└── testdata/
    └── AuthTestData.java     # Публичные демонстрационные данные Reqres
```

## Требования

- JDK 18 или новее: в `pom.xml` указаны `source` и `target` 18.
- Maven, доступный через команду `mvn`. Maven Wrapper пока отсутствует.
- Доступ к тестируемому API.
- Собственный API-ключ Reqres в переменной окружения `REQRES_API_KEY`.

Тесты обращаются к внешнему сервису. Перед запуском проверьте актуальные требования
к авторизации и лимитам в [документации Reqres](https://reqres.in/openapi.json).
`ApiConfig` передаёт ключ в заголовке `x-api-key` во всех тестах.

## Компиляция без запуска тестов

```bash
mvn test-compile
```

## Запуск тестов

Перед запуском задайте `REQRES_API_KEY`. Ключ можно получить в личном кабинете
[Reqres](https://app.reqres.in/).

В терминале zsh на macOS можно ввести ключ без отображения на экране
и без сохранения его значения в истории команд:

```bash
read -rs 'REQRES_API_KEY?Введите API-ключ Reqres: '
export REQRES_API_KEY
```

В IntelliJ IDEA откройте `Run → Edit Configurations`, выберите конфигурацию
запуска TestNG и добавьте `REQRES_API_KEY` со своим значением в `Environment variables`.
Для запуска обоих классов задайте переменную в обеих конфигурациях либо в шаблоне
TestNG для новых конфигураций. Не сохраняйте ключ в общих файлах конфигураций проекта.
Переменная, заданная в терминале, не обязательно доступна уже запущенной IDE.

При отсутствующем или пустом ключе тест завершится с понятной ошибкой до HTTP-запроса.

Все тесты:

```bash
mvn test
```

Отдельные классы:

```bash
mvn -Dtest=UsersTests test
mvn -Dtest=AuthTests test
```

Параметризованный тест некорректных ID:

```bash
mvn '-Dtest=UsersTests#getUserByInvalidIdTest' test
```

Также классы и методы можно запускать средствами TestNG в IntelliJ IDEA.

## Настройки

По умолчанию запросы направляются на `https://reqres.in/api`.
Свойство `api.baseUri` задаёт адрес сервера без `/api`:

```bash
mvn -Dapi.baseUri=https://reqres.in test
```

Таймаут подключения — 5 секунд, чтения — 10 секунд. При провале проверок
REST Assured логирует заголовки, скрывая `Authorization`, `x-api-key`, `Cookie`
и `Set-Cookie`. Тела запросов и ответов не логируются.

`AuthTestData` содержит публичные данные демонстрационного API. Настоящие пароли,
токены и API-ключи не следует сохранять в репозитории.

## Allure Report

- [x] Подключён Allure Report для всех тестов TestNG.

После изменения `pom.xml` обновите Maven-проект в IntelliJ IDEA.
Адаптер Allure подключается автоматически; результаты сохраняются в
`target/allure-results` при запуске через Maven и через TestNG в IDE
(при рабочей директории, равной корню проекта).

Для нового прогона без результатов предыдущих запусков:

```bash
mvn clean test
```

Сформировать HTML-отчёт по уже полученным результатам:

```bash
mvn allure:report
```

Отчёт сохраняется в `target/allure-report`. Для просмотра в браузере:

```bash
mvn allure:serve
```

Команды `allure:report` и `allure:serve` не запускают тесты. Их можно выполнить
отдельно после упавшего прогона. Maven-плагин скачивает необходимую версию Allure
при первом использовании; отдельная установка Allure CLI не требуется.

В отчёте отображаются результаты, длительность, ошибки и параметры DataProvider.
Каждый HTTP-запрос получает текстовое вложение с методом и адресом, ответ —
со строкой статуса. Заголовки и тела во вложения не включаются.
Результаты, отчёт и скачанный CLI находятся в `target/`, который исключён из Git.

Интеграция основана на [Allure TestNG](https://allurereport.org/docs/testng/)
и [Allure Maven](https://github.com/allure-framework/allure-maven/tree/2.18.0).
