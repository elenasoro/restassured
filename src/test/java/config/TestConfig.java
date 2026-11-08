package config;

public final class TestConfig {

    private TestConfig() {
    }

    public static String baseUri() {
        return requiredEnvironmentVariable("QA101_BASE_URI");
    }

    public static String accessToken() {
        return requiredEnvironmentVariable("QA101_ACCESS_TOKEN");
    }

    public static long userId() {
        return Long.parseLong(requiredEnvironmentVariable("QA101_USER_ID"));
    }

    public static String lowBalanceAccessToken() {
        return requiredEnvironmentVariable("QA101_LOW_BALANCE_ACCESS_TOKEN");
    }

    public static long lowBalanceUserId() {
        return Long.parseLong(requiredEnvironmentVariable("QA101_LOW_BALANCE_USER_ID"));
    }

    public static String influencerId() {
        return requiredEnvironmentVariable("QA101_INFLUENCER_ID");
    }

    public static String concurrentAccessToken() {
        return requiredEnvironmentVariable("QA101_CONCURRENT_ACCESS_TOKEN");
    }

    public static long concurrentUserId() {
        return Long.parseLong(requiredEnvironmentVariable("QA101_CONCURRENT_USER_ID"));
    }

    public static String dbUrl() {
        return requiredEnvironmentVariable("QA101_DB_URL");
    }

    public static String dbUser() {
        return requiredEnvironmentVariable("QA101_DB_USER");
    }

    public static String dbPassword() {
        String password = System.getenv("QA101_DB_PASSWORD");
        if (password == null || password.isEmpty()) {
            throw new IllegalStateException("Set the QA101_DB_PASSWORD environment variable before running tests.");
        }
        return password;
    }

    private static String requiredEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Set the " + name + " environment variable before running tests.");
        }
        return value.strip();
    }
}
