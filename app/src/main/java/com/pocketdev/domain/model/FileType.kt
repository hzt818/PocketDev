package com.pocketdev.domain.model

enum class FileType(
    val displayName: String,
    val extensions: List<String>
) {
    KOTLIN("Kotlin", listOf("kt", "kts")),
    JAVA("Java", listOf("java")),
    PYTHON("Python", listOf("py", "pyw")),
    JAVASCRIPT("JavaScript", listOf("js", "mjs", "cjs")),
    TYPESCRIPT("TypeScript", listOf("ts", "tsx", "mts")),
    GO("Go", listOf("go")),
    RUST("Rust", listOf("rs")),
    C("C", listOf("c", "h")),
    CPP("C++", listOf("cpp", "hpp", "cc", "hh", "cxx", "hxx")),
    CSHARP("C#", listOf("cs")),
    SWIFT("Swift", listOf("swift")),
    OBJECTIVE_C("Objective-C", listOf("m", "mm")),
    RUBY("Ruby", listOf("rb")),
    PHP("PHP", listOf("php")),
    HTML("HTML", listOf("html", "htm")),
    CSS("CSS", listOf("css", "scss", "sass", "less")),
    JSON("JSON", listOf("json")),
    XML("XML", listOf("xml")),
    YAML("YAML", listOf("yaml", "yml")),
    MARKDOWN("Markdown", listOf("md", "mdx")),
    TOML("TOML", listOf("toml")),
    GRADLE("Gradle", listOf("gradle")),
    PROPERTIES("Properties", listOf("properties")),
    SHELL("Shell", listOf("sh", "bash", "zsh")),
    SQL("SQL", listOf("sql")),
    SCALA("Scala", listOf("scala", "sc")),
    TEXT("Text", listOf("txt")),
    UNKNOWN("Unknown", emptyList());

    companion object {
        fun fromExtension(extension: String): FileType {
            val ext = extension.lowercase()
            return entries.find { it.extensions.contains(ext) } ?: UNKNOWN
        }

        fun fromFileName(fileName: String): FileType {
            val extension = fileName.substringAfterLast('.', "")
            return fromExtension(extension)
        }
    }
}
