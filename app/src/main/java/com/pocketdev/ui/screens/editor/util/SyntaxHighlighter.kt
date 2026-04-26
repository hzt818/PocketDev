package com.pocketdev.ui.screens.editor.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.pocketdev.domain.model.FileType

object SyntaxHighlighter {

    private val keywordColor = Color(0xFF0033B3)
    private val stringColor = Color(0xFF067D17)
    private val commentColor = Color(0xFF808080)
    private val numberColor = Color(0xFF1750EB)
    private val typeColor = Color(0xFF2676B0)
    private val annotationColor = Color(0xFF826868)
    private val functionColor = Color(0xFF7451A6)

    fun highlight(code: String, fileType: FileType): AnnotatedString {
        return when (fileType) {
            FileType.KOTLIN -> highlightKotlin(code)
            FileType.JAVA -> highlightJava(code)
            FileType.PYTHON -> highlightPython(code)
            FileType.JAVASCRIPT, FileType.TYPESCRIPT -> highlightJavaScript(code)
            FileType.GO -> highlightGo(code)
            FileType.RUST -> highlightRust(code)
            FileType.C, FileType.CPP -> highlightC(code)
            FileType.CSHARP -> highlightCSharp(code)
            FileType.SWIFT -> highlightSwift(code)
            FileType.RUBY -> highlightRuby(code)
            FileType.PHP -> highlightPhp(code)
            FileType.SCALA -> highlightScala(code)
            FileType.GRADLE -> highlightGroovy(code)
            FileType.HTML -> highlightHtml(code)
            FileType.CSS -> highlightCss(code)
            FileType.JSON -> highlightJson(code)
            FileType.XML -> highlightXml(code)
            FileType.YAML -> highlightYaml(code)
            FileType.MARKDOWN -> highlightMarkdown(code)
            FileType.SHELL -> highlightShell(code)
            FileType.SQL -> highlightSql(code)
            else -> AnnotatedString(code)
        }
    }

    private fun highlightKotlin(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightStrings(this, code, "\"", "\\")
        highlightNumbers(this, code)
        highlightKeywords(this, code, kotlinKeywords)
        highlightTypes(this, code, kotlinTypes)
        highlightAnnotations(this, code)
        highlightFunctions(this, code)
    }

    private fun highlightJava(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightStrings(this, code, "\"", "\\")
        highlightNumbers(this, code)
        highlightKeywords(this, code, javaKeywords)
        highlightTypes(this, code, javaTypes)
        highlightAnnotations(this, code)
        highlightFunctions(this, code)
    }

    private fun highlightPython(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "#", "\"\"\"", "\"\"\"")
        highlightStrings(this, code, "\"", "\\", triple = true)
        highlightStrings(this, code, "'", "\\", triple = true)
        highlightNumbers(this, code)
        highlightKeywords(this, code, pythonKeywords)
        highlightTypes(this, code, pythonTypes)
        highlightFunctions(this, code)
        highlightDecorators(this, code)
    }

    private fun highlightJavaScript(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightStrings(this, code, "\"", "\\")
        highlightStrings(this, code, "'", "\\")
        highlightTemplateStrings(this, code)
        highlightNumbers(this, code)
        highlightKeywords(this, code, jsKeywords)
        highlightTypes(this, code, jsTypes)
        highlightFunctions(this, code)
    }

    private fun highlightGo(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightStrings(this, code, "\"", "\\")
        highlightRawStrings(this, code, "`", "`")
        highlightNumbers(this, code)
        highlightKeywords(this, code, goKeywords)
        highlightTypes(this, code, goTypes)
        highlightFunctions(this, code)
    }

    private fun highlightRust(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightStrings(this, code, "\"", "\\")
        highlightNumbers(this, code)
        highlightKeywords(this, code, rustKeywords)
        highlightTypes(this, code, rustTypes)
        highlightFunctions(this, code)
        highlightLifetimes(this, code)
    }

    private fun highlightC(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightStrings(this, code, "\"", "\\")
        highlightStrings(this, code, "'", "\\")
        highlightNumbers(this, code)
        highlightKeywords(this, code, cKeywords)
        highlightTypes(this, code, cTypes)
        highlightPreprocessor(this, code)
        highlightFunctions(this, code)
    }

    private fun highlightCSharp(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightStrings(this, code, "\"", "\\")
        highlightVerbatimStrings(this, code, "@\"")
        highlightNumbers(this, code)
        highlightKeywords(this, code, csharpKeywords)
        highlightTypes(this, code, csharpTypes)
        highlightAnnotations(this, code)
        highlightFunctions(this, code)
    }

    private fun highlightSwift(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightStrings(this, code, "\"", "\\")
        highlightNumbers(this, code)
        highlightKeywords(this, code, swiftKeywords)
        highlightTypes(this, code, swiftTypes)
        highlightAttributes(this, code)
        highlightFunctions(this, code)
    }

    private fun highlightRuby(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "#", "=begin", "=end")
        highlightStrings(this, code, "\"", "\\")
        highlightStrings(this, code, "'", "\\")
        highlightNumbers(this, code)
        highlightKeywords(this, code, rubyKeywords)
        highlightSymbols(this, code)
        highlightFunctions(this, code)
    }

    private fun highlightPhp(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightComments(this, code, "#", null, null)
        highlightStrings(this, code, "\"", "\\")
        highlightHeredocs(this, code)
        highlightNumbers(this, code)
        highlightKeywords(this, code, phpKeywords)
        highlightVariables(this, code)
        highlightFunctions(this, code)
    }

    private fun highlightScala(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightStrings(this, code, "\"", "\\")
        highlightNumbers(this, code)
        highlightKeywords(this, code, scalaKeywords)
        highlightTypes(this, code, scalaTypes)
        highlightAnnotations(this, code)
        highlightFunctions(this, code)
    }

    private fun highlightGroovy(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "//", "/*", "*/")
        highlightComments(this, code, "#", null, null)
        highlightStrings(this, code, "\"", "\\")
        highlightStrings(this, code, "'", "\\")
        highlightNumbers(this, code)
        highlightKeywords(this, code, groovyKeywords)
        highlightTypes(this, code, groovyTypes)
        highlightAnnotations(this, code)
        highlightFunctions(this, code)
    }

    private fun highlightHtml(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightHtmlTags(this, code)
        highlightHtmlAttributes(this, code)
        highlightComments(this, code, "<!--", "<!--", "-->")
        highlightStrings(this, code, "\"", "\\")
        highlightStrings(this, code, "'", "\\")
    }

    private fun highlightCss(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "/*", "/*", "*/")
        highlightCssSelectors(this, code)
        highlightCssProperties(this, code)
        highlightStrings(this, code, "\"", "\\")
        highlightStrings(this, code, "'", "\\")
        highlightNumbers(this, code)
        highlightCssKeywords(this, code)
    }

    private fun highlightJson(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightJsonKeys(this, code)
        highlightStrings(this, code, "\"", "\\")
        highlightNumbers(this, code)
        highlightJsonBooleans(this, code)
        highlightJsonNull(this, code)
    }

    private fun highlightXml(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightXmlTags(this, code)
        highlightComments(this, code, "<!--", "<!--", "-->")
        highlightStrings(this, code, "\"", "\\")
        highlightStrings(this, code, "'", "\\")
        highlightXmlDirectives(this, code)
    }

    private fun highlightYaml(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "#", null, null)
        highlightYamlKeys(this, code)
        highlightStrings(this, code, "\"", "\\")
        highlightStrings(this, code, "'", "\\")
        highlightNumbers(this, code)
        highlightYamlBooleans(this, code)
    }

    private fun highlightMarkdown(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightMarkdownHeaders(this, code)
        highlightMarkdownBold(this, code)
        highlightMarkdownItalic(this, code)
        highlightMarkdownCode(this, code)
        highlightMarkdownLinks(this, code)
    }

    private fun highlightShell(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "#", null, null)
        highlightStrings(this, code, "\"", "\\")
        highlightStrings(this, code, "'", "\\")
        highlightKeywords(this, code, shellKeywords)
        highlightVariables(this, code)
    }

    private fun highlightSql(code: String): AnnotatedString = buildAnnotatedString {
        append(code)
        highlightComments(this, code, "--", "/*", "*/")
        highlightStrings(this, code, "'", "\\")
        highlightNumbers(this, code)
        highlightKeywords(this, code, sqlKeywords)
        highlightFunctions(this, code)
    }

    private fun highlightComments(
        builder: AnnotatedString.Builder,
        code: String,
        singleLine: String,
        multiLineStart: String?,
        multiLineEnd: String?
    ) {
        if (multiLineStart != null && multiLineEnd != null) {
            val multiLinePattern = Regex(
                """${Regex.escape(multiLineStart)}[\s\S]*?${Regex.escape(multiLineEnd)}"""
            )
            multiLinePattern.findAll(code).forEach { match ->
                builder.addStyle(
                    SpanStyle(color = commentColor, fontStyle = FontStyle.Italic),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }

        val lines = code.split("\n")
        var offset = 0
        lines.forEach { line ->
            val commentIndex = line.indexOf(singleLine)
            if (commentIndex >= 0) {
                builder.addStyle(
                    SpanStyle(color = commentColor, fontStyle = FontStyle.Italic),
                    offset + commentIndex,
                    offset + line.length
                )
            }
            offset += line.length + 1
        }
    }

    private fun highlightStrings(
        builder: AnnotatedString.Builder,
        code: String,
        quote: String,
        escape: String,
        triple: Boolean = false
    ) {
        val pattern = if (triple) {
            """${Regex.escape(quote)}[^"\\]*(\\.[^"\\]*)*${Regex.escape(quote)}"""
        } else {
            """${Regex.escape(quote)}[^"$escape\n]*(${escape}.[^"$escape\n]*)*${Regex.escape(quote)}"""
        }
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = stringColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightTemplateStrings(builder: AnnotatedString.Builder, code: String) {
        val pattern = """`[^`\\]*(\\.[^`\\]*)*`"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = stringColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightRawStrings(builder: AnnotatedString.Builder, code: String, prefix: String, suffix: String) {
        val pattern = """${Regex.escape(prefix)}[^"$suffix]*${Regex.escape(suffix)}"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = stringColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightVerbatimStrings(builder: AnnotatedString.Builder, code: String, prefix: String) {
        val pattern = """${Regex.escape(prefix)}[^"]*" """
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = stringColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightHeredocs(builder: AnnotatedString.Builder, code: String) {
        val pattern = """<<<['"]?(\w+)['"]?\s*\n[\s\S]*?\n\1"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = stringColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightNumbers(builder: AnnotatedString.Builder, code: String) {
        val pattern = """\b\d+\.?\d*([eE][+-]?\d+)?[fFdDlL]?\b|\b0x[0-9a-fA-F]+\b|\b0b[01]+\b"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = numberColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightKeywords(builder: AnnotatedString.Builder, code: String, keywords: Set<String>) {
        keywords.forEach { keyword ->
            val pattern = """\b${Regex.escape(keyword)}\b""".toRegex()
            pattern.findAll(code).forEach { match ->
                builder.addStyle(
                    SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }
    }

    private fun highlightTypes(builder: AnnotatedString.Builder, code: String, types: Set<String>) {
        types.forEach { type ->
            val pattern = """\b${Regex.escape(type)}\b""".toRegex()
            pattern.findAll(code).forEach { match ->
                builder.addStyle(
                    SpanStyle(color = typeColor, fontWeight = FontWeight.Medium),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }
    }

    private fun highlightAnnotations(builder: AnnotatedString.Builder, code: String) {
        val pattern = """@\w+"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = annotationColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightFunctions(builder: AnnotatedString.Builder, code: String) {
        val pattern = """\b\w+(?=\s*\()"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = functionColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightDecorators(builder: AnnotatedString.Builder, code: String) {
        val pattern = """@\w+"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = annotationColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightAttributes(builder: AnnotatedString.Builder, code: String) {
        val pattern = """@\w+"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = annotationColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightVariables(builder: AnnotatedString.Builder, code: String) {
        val pattern = """\$\w+|\$\{[^}]+\}"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = typeColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightSymbols(builder: AnnotatedString.Builder, code: String) {
        val pattern = """:\w+"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = typeColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightLifetimes(builder: AnnotatedString.Builder, code: String) {
        val pattern = """'\w+"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = annotationColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightPreprocessor(builder: AnnotatedString.Builder, code: String) {
        val pattern = """^\s*#\w+.*$""".toRegex(RegexOption.MULTILINE)
        pattern.findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = annotationColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightHtmlTags(builder: AnnotatedString.Builder, code: String) {
        val pattern = """</?[\w-]+"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Color(0xFF186800)),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightHtmlAttributes(builder: AnnotatedString.Builder, code: String) {
        val pattern = """\s[\w-]+(?==)"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Color(0xFF0747A6)),
                match.range.first + 1,
                match.range.last + 1
            )
        }
    }

    private fun highlightCssSelectors(builder: AnnotatedString.Builder, code: String) {
        val pattern = """^[\w\s,#.+\-*:[\]='"]+(?=\{)"""
        Regex(pattern, RegexOption.MULTILINE).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Color(0xFF8000)),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightCssProperties(builder: AnnotatedString.Builder, code: String) {
        cssProperties.forEach { prop ->
            val pattern = """(?<=:\s*)\b$prop\b""".toRegex()
            pattern.findAll(code).forEach { match ->
                builder.addStyle(
                    SpanStyle(color = Color(0xFF0451A5)),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }
    }

    private fun highlightCssKeywords(builder: AnnotatedString.Builder, code: String) {
        cssKeywords.forEach { keyword ->
            val pattern = """\b${Regex.escape(keyword)}\b""".toRegex()
            pattern.findAll(code).forEach { match ->
                builder.addStyle(
                    SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }
    }

    private fun highlightJsonKeys(builder: AnnotatedString.Builder, code: String) {
        val pattern = """"\w+"\s*(?=:)"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Color(0xFF0451A5)),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightJsonBooleans(builder: AnnotatedString.Builder, code: String) {
        val pattern = """\b(true|false)\b"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightJsonNull(builder: AnnotatedString.Builder, code: String) {
        val pattern = """\bnull\b"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Color(0xFF0000)),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightXmlTags(builder: AnnotatedString.Builder, code: String) {
        val pattern = """</?[\w:]+"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Color(0xFF186800)),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightXmlDirectives(builder: AnnotatedString.Builder, code: String) {
        val pattern = """<\?[\w-]+"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Color(0xFF8000)),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightYamlKeys(builder: AnnotatedString.Builder, code: String) {
        val pattern = """^[\w-]+(?=\s*:)""".toRegex(RegexOption.MULTILINE)
        pattern.findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Color(0xFF0451A5)),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightYamlBooleans(builder: AnnotatedString.Builder, code: String) {
        val pattern = """\b(true|false|null|yes|no|on|off)\b"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightMarkdownHeaders(builder: AnnotatedString.Builder, code: String) {
        val pattern = """^#{1,6}\s+.*$""".toRegex(RegexOption.MULTILINE)
        pattern.findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Color(0xFF186800), fontWeight = FontWeight.Bold),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightMarkdownBold(builder: AnnotatedString.Builder, code: String) {
        val pattern = """\*\*[^*]+\*\*|\*\*[^*]+\*"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(fontWeight = FontWeight.Bold),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightMarkdownItalic(builder: AnnotatedString.Builder, code: String) {
        val pattern = """(?<!\*)\*[^*]+\*(?!\*)|(?<!_) _[^_]+_ (?![_])"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(fontStyle = FontStyle.Italic),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightMarkdownCode(builder: AnnotatedString.Builder, code: String) {
        val pattern = """`[^`]+`"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = stringColor, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private fun highlightMarkdownLinks(builder: AnnotatedString.Builder, code: String) {
        val pattern = """\[[^\]]+\]\([^)]+\)"""
        Regex(pattern).findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Color(0xFF186800)),
                match.range.first,
                match.range.last + 1
            )
        }
    }

    private val kotlinKeywords = setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if",
        "in", "interface", "is", "null", "object", "package", "return", "super", "this",
        "throw", "true", "try", "typealias", "typeof", "val", "var", "when", "while",
        "by", "catch", "constructor", "delegate", "dynamic", "field", "file", "finally",
        "get", "import", "init", "param", "property", "receiver", "set", "setparam",
        "where", "actual", "abstract", "annotation", "companion", "const", "crossinline",
        "data", "enum", "expect", "external", "final", "infix", "inline", "inner",
        "internal", "lateinit", "noinline", "open", "operator", "out", "override",
        "private", "protected", "public", "reified", "sealed", "suspend", "tailrec",
        "vararg", "annotation"
    )

    private val kotlinTypes = setOf(
        "Any", "Boolean", "Byte", "Char", "Double", "Float", "Int", "Long", "Nothing",
        "Short", "String", "Unit", "Array", "ByteArray", "CharArray", "DoubleArray",
        "FloatArray", "IntArray", "List", "LongArray", "Map", "Set", "ShortArray"
    )

    private val javaKeywords = setOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
        "class", "const", "continue", "default", "do", "double", "else", "enum",
        "extends", "final", "finally", "float", "for", "goto", "if", "implements",
        "import", "instanceof", "int", "interface", "long", "native", "new", "null",
        "package", "private", "protected", "public", "return", "short", "static",
        "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "true", "try", "void", "volatile", "while", "var", "record"
    )

    private val javaTypes = setOf(
        "Boolean", "Byte", "Character", "Double", "Float", "Integer", "Long", "Number",
        "Object", "Short", "String", "Void", "Array", "List", "Map", "Set", "Collection",
        "HashMap", "HashSet", "ArrayList", "LinkedList", "Optional", "Stream", "CompletableFuture"
    )

    private val pythonKeywords = setOf(
        "and", "as", "assert", "async", "await", "break", "class", "continue", "def",
        "del", "elif", "else", "except", "False", "finally", "for", "from", "global",
        "if", "import", "in", "is", "lambda", "None", "nonlocal", "not", "or",
        "pass", "raise", "return", "True", "try", "while", "with", "yield", "self"
    )

    private val pythonTypes = setOf(
        "int", "float", "str", "bool", "list", "dict", "tuple", "set", "frozenset",
        "bytes", "bytearray", "complex", "type", "object", "property", "classmethod",
        "staticmethod", "Ellipsis", "NotImplemented"
    )

    private val jsKeywords = setOf(
        "async", "await", "break", "case", "catch", "class", "const", "continue",
        "debugger", "default", "delete", "do", "else", "export", "extends", "false",
        "finally", "for", "function", "if", "import", "in", "instanceof", "let",
        "new", "null", "return", "static", "super", "switch", "this", "throw",
        "true", "try", "typeof", "undefined", "var", "void", "while", "with", "yield",
        "of", "get", "set"
    )

    private val jsTypes = setOf(
        "Array", "Boolean", "Date", "Error", "Function", "JSON", "Map", "Math",
        "Number", "Object", "Promise", "Proxy", "RegExp", "Set", "String", "Symbol",
        "WeakMap", "WeakSet", "console", "window", "document", "globalThis"
    )

    private val goKeywords = setOf(
        "break", "case", "chan", "const", "continue", "default", "defer", "else",
        "fallthrough", "for", "func", "go", "goto", "if", "import", "interface",
        "map", "package", "range", "return", "select", "struct", "switch", "type",
        "var", "true", "false", "nil", "iota"
    )

    private val goTypes = setOf(
        "bool", "byte", "complex64", "complex128", "error", "float32", "float64",
        "int", "int8", "int16", "int32", "int64", "rune", "string", "uint",
        "uint8", "uint16", "uint32", "uint64", "uintptr", "any", "comparable"
    )

    private val rustKeywords = setOf(
        "as", "async", "await", "break", "const", "continue", "crate", "dyn", "else",
        "enum", "extern", "false", "fn", "for", "if", "impl", "in", "let", "loop",
        "match", "mod", "move", "mut", "pub", "ref", "return", "self", "Self",
        "static", "struct", "super", "trait", "true", "type", "unsafe", "use",
        "where", "while", "macro_rules"
    )

    private val rustTypes = setOf(
        "bool", "char", "f32", "f64", "i8", "i16", "i32", "i64", "i128", "isize",
        "str", "u8", "u16", "u32", "u64", "u128", "usize", "String", "Vec", "Option",
        "Result", "Box", "Rc", "Arc", "Cell", "RefCell", "Mutex", "HashMap", "HashSet"
    )

    private val cKeywords = setOf(
        "auto", "break", "case", "char", "const", "continue", "default", "do",
        "double", "else", "enum", "extern", "float", "for", "goto", "if", "inline",
        "int", "long", "register", "restrict", "return", "short", "signed", "sizeof",
        "static", "struct", "switch", "typedef", "union", "unsigned", "void",
        "volatile", "while", "_Bool", "_Complex", "_Imaginary", "NULL"
    )

    private val cTypes = setOf(
        "int8_t", "int16_t", "int32_t", "int64_t", "uint8_t", "uint16_t", "uint32_t",
        "uint64_t", "size_t", "ssize_t", "ptrdiff_t", "intptr_t", "uintptr_t",
        "FILE", "bool", "char", "double", "float", "long", "short", "void"
    )

    private val csharpKeywords = setOf(
        "abstract", "as", "base", "bool", "break", "byte", "case", "catch", "char",
        "checked", "class", "const", "continue", "decimal", "default", "delegate",
        "do", "double", "else", "enum", "event", "explicit", "extern", "false",
        "finally", "fixed", "float", "for", "foreach", "goto", "if", "implicit",
        "in", "int", "interface", "internal", "is", "lock", "long", "namespace",
        "new", "null", "object", "operator", "out", "override", "params", "private",
        "protected", "public", "readonly", "ref", "return", "sbyte", "sealed",
        "short", "sizeof", "stackalloc", "static", "string", "struct", "switch",
        "this", "throw", "true", "try", "typeof", "uint", "ulong", "unchecked",
        "unsafe", "ushort", "using", "virtual", "void", "volatile", "while"
    )

    private val csharpTypes = setOf(
        "Boolean", "Byte", "Char", "DateTime", "Decimal", "Double", "Int16", "Int32",
        "Int64", "Object", "SByte", "Single", "String", "UInt16", "UInt32", "UInt64",
        "List", "Dictionary", "HashSet", "Queue", "Stack", "Task", "Action", "Func"
    )

    private val swiftKeywords = setOf(
        "actor", "any", "as", "associatedtype", "async", "await", "break", "case",
        "catch", "class", "continue", "default", "defer", "deinit", "do", "else",
        "enum", "extension", "fallthrough", "false", "fileprivate", "final", "for",
        "func", "get", "guard", "if", "import", "in", "indirect", "infix", "init",
        "inout", "internal", "is", "lazy", "let", "mutating", "nil", "nonisolated",
        "nonmutating", "open", "operator", "optional", "override", "postfix", "precedencegroup",
        "prefix", "private", "protocol", "public", "repeat", "required", "rethrows",
        "return", "self", "Self", "set", "some", "static", "struct", "subscript",
        "super", "switch", "throw", "throws", "true", "try", "typealias", "var", "where",
        "while", "willSet", "didSet"
    )

    private val swiftTypes = setOf(
        "Any", "AnyObject", "Array", "Bool", "Character", "Dictionary", "Double",
        "Float", "Int", "Int8", "Int16", "Int32", "Int64", "Never", "Optional",
        "Result", "Set", "String", "UInt", "UInt8", "UInt16", "UInt32", "UInt64",
        "Void", "Error", "Codable", "Equatable", "Hashable", "Identifiable"
    )

    private val rubyKeywords = setOf(
        "BEGIN", "END", "alias", "and", "begin", "break", "case", "class", "def",
        "defined?", "do", "else", "elsif", "end", "ensure", "false", "for", "if",
        "in", "module", "next", "nil", "not", "or", "redo", "rescue", "retry",
        "return", "self", "super", "then", "true", "undef", "unless", "until",
        "when", "while", "yield", "__FILE__", "__LINE__", "__ENCODING__", "attr_reader",
        "attr_writer", "attr_accessor"
    )

    private val phpKeywords = setOf(
        "abstract", "and", "array", "as", "break", "callable", "case", "catch",
        "class", "clone", "const", "continue", "declare", "default", "die", "do",
        "echo", "else", "elseif", "empty", "enddeclare", "endfor", "endforeach",
        "endif", "endswitch", "endwhile", "eval", "exit", "extends", "final",
        "finally", "fn", "for", "foreach", "function", "global", "goto", "if",
        "implements", "include", "include_once", "instanceof", "insteadof", "interface",
        "isset", "list", "match", "namespace", "new", "or", "print", "private",
        "protected", "public", "require", "require_once", "return", "static", "switch",
        "throw", "trait", "try", "unset", "use", "var", "while", "xor", "yield",
        "true", "false", "null", "self", "parent", "mixed", "void", "never"
    )

    private val scalaKeywords = setOf(
        "abstract", "case", "catch", "class", "def", "do", "else", "enum", "extends",
        "export", "false", "final", "finally", "for", "forSome", "given", "if",
        "implicit", "import", "lazy", "match", "new", "null", "object", "package",
        "private", "protected", "return", "sealed", "super", "then", "this", "throw",
        "trait", "true", "try", "type", "val", "var", "while", "with", "yield",
        "@main", "as", "inline", "opaque", "open", "override", "transparent"
    )

    private val scalaTypes = setOf(
        "Any", "AnyRef", "AnyVal", "Boolean", "Byte", "Char", "Double", "Float",
        "Int", "Long", "Nothing", "Null", "Short", "String", "Symbol", "Unit",
        "List", "Map", "Set", "Seq", "Vector", "Array", "Option", "Either", "Future"
    )

    private val groovyKeywords = setOf(
        "abstract", "assert", "break", "case", "catch", "class", "const", "continue",
        "default", "do", "else", "enum", "extends", "false", "final", "finally",
        "for", "goto", "if", "implements", "import", "in", "instanceof", "interface",
        "native", "new", "null", "package", "private", "protected", "public",
        "return", "static", "strictfp", "super", "switch", "synchronized", "this",
        "threadsafe", "throw", "throws", "transient", "true", "try", "volatile", "while",
        "def", "as", "in", "trait", "mixin"
    )

    private val groovyTypes = setOf(
        "Boolean", "Byte", "Character", "Double", "Float", "Integer", "Long", "Number",
        "Object", "Short", "String", "Void", "List", "Map", "Set", "ArrayList",
        "HashMap", "HashSet", "TreeMap", "TreeSet", "Closure"
    )

    private val sqlKeywords = setOf(
        "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "CREATE", "DROP",
        "ALTER", "TABLE", "INDEX", "VIEW", "DATABASE", "SCHEMA", "INTO", "VALUES",
        "SET", "AND", "OR", "NOT", "NULL", "IS", "IN", "LIKE", "BETWEEN", "JOIN",
        "LEFT", "RIGHT", "INNER", "OUTER", "FULL", "CROSS", "ON", "AS", "ORDER",
        "BY", "ASC", "DESC", "GROUP", "HAVING", "LIMIT", "OFFSET", "UNION", "ALL",
        "DISTINCT", "COUNT", "SUM", "AVG", "MIN", "MAX", "AVG", "CASE", "WHEN",
        "THEN", "ELSE", "END", "EXISTS", "PRIMARY", "KEY", "FOREIGN", "REFERENCES",
        "CONSTRAINT", "DEFAULT", "CHECK", "UNIQUE", "CASCADE", "TRUNCATE"
    )

    private val shellKeywords = setOf(
        "if", "then", "else", "elif", "fi", "case", "esac", "for", "select", "while",
        "until", "do", "done", "in", "function", "time", "coproc", "export", "readonly",
        "local", "declare", "typeset", "unset", "shift", "exit", "return", "break",
        "continue", "eval", "exec", "source", "alias", "unalias", "set", "shopt",
        "trap", "wait", "true", "false"
    )

    private val cssProperties = setOf(
        "color", "background", "background-color", "background-image", "background-position",
        "border", "border-color", "border-width", "border-radius", "border-style",
        "margin", "margin-top", "margin-right", "margin-bottom", "margin-left", "padding",
        "padding-top", "padding-right", "padding-bottom", "padding-left", "width",
        "height", "max-width", "max-height", "min-width", "min-height", "display",
        "flex", "flex-direction", "flex-wrap", "justify-content", "align-items",
        "align-content", "gap", "grid", "grid-template-columns", "grid-template-rows",
        "grid-area", "font", "font-family", "font-size", "font-weight", "font-style",
        "text-align", "text-decoration", "text-transform", "line-height", "overflow",
        "position", "top", "right", "bottom", "left", "z-index", "opacity", "visibility",
        "transform", "transition", "animation", "box-shadow", "cursor"
    )

    private val cssKeywords = setOf(
        "important", "inherit", "initial", "unset", "revert", "auto", "none", "normal",
        "solid", "dashed", "dotted", "hidden", "visible", "absolute", "relative",
        "fixed", "sticky", "static", "block", "inline", "inline-block", "flex",
        "grid", "table", "row", "column", "center", "left", "right", "top", "bottom"
    )
}
