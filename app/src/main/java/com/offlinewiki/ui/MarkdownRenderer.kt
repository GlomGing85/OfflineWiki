package com.offlinewiki.ui

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.jetbrains.markdown.Markdown

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MarkdownRenderer(
    markdown: String,
    modifier: Modifier = Modifier,
    onLinkClick: (String) -> Unit = {},
    onImageNotFound: (String) -> Unit = {}
) {
    val htmlContent = buildHtmlContent(markdown)

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                setBackgroundColor(0xFF0F1210)
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        url?.let {
                            if (it.contains("wikipedia.org/wiki/")) {
                                onLinkClick(it)
                                return true
                            }
                        }
                        return false
                    }
                }
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier.fillMaxSize(),
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    )
}

private fun buildHtmlContent(markdown: String): String {
    val parsedHtml = Markdown.parse(markdown)
    return buildString {
        append("<!DOCTYPE html>")
        append("<html><head>")
        append("<meta charset='UTF-8'>")
        append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
        append("<style>")
        append("* { box-sizing: border-box; }")
        append("body { font-family: system-ui, sans-serif; background: #0F1210; color: #E0E3DE; padding: 20px; line-height: 1.75; font-size: 16px; margin: 0; }")
        append("h1, h2, h3 { color: #6FF7F7; border-bottom: 1px solid #3F4948; padding-bottom: 8px; font-weight: 400; margin-top: 24px; margin-bottom: 16px; }")
        append("h1 { font-size: 32px; line-height: 40px; letter-spacing: 0; }")
        append("h2 { font-size: 28px; line-height: 36px; }")
        append("h3 { font-size: 22px; line-height: 28px; }")
        append("a { color: #6FF7F7; text-decoration: underline; word-break: break-word; }")
        append("img { max-width: 100%; border-radius: 8px; height: auto; display: block; margin: 12px 0; }")
        append("pre { background: #1D3534; padding: 16px; border-radius: 8px; overflow-x: auto; font-size: 14px; line-height: 1.5; }")
        append("blockquote { border-left: 4px solid #6FF7F7; padding-left: 16px; margin-left: 0; margin-top: 16px; margin-bottom: 16px; color: #BEC9C6; font-style: italic; }")
        append("code { background: #1D3534; padding: 2px 6px; border-radius: 4px; font-size: 14px; color: #B6CCC8; }")
        append("ul, ol { padding-left: 24px; }")
        append("li { margin-bottom: 8px; }")
        append("table { border-collapse: collapse; width: 100%; margin: 16px 0; }")
        append("th, td { border: 1px solid #3F4948; padding: 8px; text-align: left; }")
        append("th { background: #1D3534; color: #6FF7F7; }")
        append(".placeholder-box { background: #1D3534; border: 1px dashed #3F4948; border-radius: 8px; padding: 32px; text-align: center; color: #8A948F; font-size: 14px; margin: 12px 0; }")
        append("</style>")
        append("</head><body>")
        append(parsedHtml)
        append("</body></html>")
    }
}
