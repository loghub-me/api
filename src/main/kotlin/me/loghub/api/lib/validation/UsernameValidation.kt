package me.loghub.api.lib.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

private const val USERNAME_REGEX = "[a-zA-Z0-9]+$"

@Constraint(validatedBy = [])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = "유저네임은 필수 입력 항목입니다.")
@Size(min = 4, max = 12, message = "유저네임은 4자 이상 12자 이하이어야 합니다.")
@Pattern(regexp = USERNAME_REGEX, message = "유저네임은 영문 소문자와 숫자로만 입력해주세요.")
@Trimmed
annotation class UsernameValidation(
    val message: String = "잘못된 유저네임입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

private val RESERVED_USERNAMES: Set<String> = setOf(
    // System / Admin
    "root", "admin", "administrator", "superadmin", "superuser", "owner", "master",
    "moderator", "mod", "staff", "team", "developers", "devs",

    // Dashboard / Access
    "dashboard", "adminpanel", "system", "sys",
    "config", "configs", "configuration",
    "internal", "private", "public",

    // API / Tech
    "api", "apis", "api-doc", "api-docs",
    "graphql", "rest", "soap", "rpc", "ws", "websocket",
    "static", "assets", "fonts", "icons", "images", "media",
    "file", "files", "upload", "uploads",
    "download", "downloads",

    // Environments
    "dev", "prod", "test",

    // Exec / Command injection
    "sh", "shell", "cmd", "command", "commands",
    "exec", "execute", "run", "eval",
    "php", "bin",
    "cdn", "cdn-cgi", "cgi",

    // Security / Logs / Monitoring
    "secure", "security", "audit",
    "log", "logs", "error", "errors",
    "status", "statuspage",
    "health", "healthcheck",
    "metrics",
    "monitor", "monitoring",
    "trace", "debug", "debugger",

    // Billing / Payment
    "billing", "payment", "payments",
    "subscribe", "subscription",

    // Common pages
    "index", "home", "about", "contact", "help", "faq",
    "docs", "manual", "documentation",
    "terms", "privacy", "policy",
    "feed", "rss", "sitemap", "robots", "license",

    // Auth / Account
    "auth", "authenticate", "authorize",
    "account", "accounts",
    "profile", "profiles",
    "user", "users", "my", "me", "self",
    "register", "signup", "signin",
    "join", "login", "logout",
    "password", "forgot", "reset",
    "verify", "confirm",
    "oauth", "oauth2", "callback",
    "session", "sessions",
    "token", "tokens",
    "setting", "settings",
    "legal", "support",

    // Content routes
    "article", "articles",
    "series",
    "question", "questions",
    "topic", "topics",
    "tag", "tags",
    "comment", "comments",
    "search", "post", "edit",
    "unpublish", "unpublished",

    // Additional conflict-prone words
    "new", "create", "update", "delete",
    "remove", "add", "list",
    "page", "pages",
    "reply", "replies",
    "inbox", "outbox",
    "message", "messages",
    "chat", "chats",
    "notification", "notifications",

    // Anti-phishing
    "admin123", "support-team", "helpdesk",
    "service", "services",
    "official", "official-team",
    "secure-login",

    // Domain
    "loghub",
).map { it.lowercase() }.toSet()

fun String.isReservedUsername(): Boolean =
    this.lowercase() in RESERVED_USERNAMES