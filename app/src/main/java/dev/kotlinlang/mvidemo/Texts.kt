package dev.kotlinlang.mvidemo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text



import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val boldRegex = Regex("(?<!\\*)\\*\\*(?!\\*).*?(?<!\\*)\\*\\*(?!\\*)")

@Composable
fun TutorialHeader(text: String, modifier: Modifier = Modifier) {

    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 6.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        text = text
    )
}

/**
 * [Text] for describing tutorial contents for specific section which ads bold to first
 * 3 chars which should be as 1- and turns any substring that is between pair of **
 *
 */
@Composable
fun StyleableTutorialText(text: String, modifier: Modifier = Modifier, bullets:Boolean = true) {

    var results: MatchResult? = boldRegex.find(text)
    val boldIndexes = mutableListOf<Pair<Int, Int>>()
    val keywords = mutableListOf<String>()

    var finalText = text

    while (results != null) {
        keywords.add(results.value)
        results = results.next()
    }

    keywords.forEach { keyword ->
        val newKeyWord = keyword.removeSurrounding("**")
        finalText = finalText.replace(keyword, newKeyWord)
        val indexOf = finalText.indexOf(newKeyWord)
        boldIndexes.add(Pair(indexOf, indexOf + newKeyWord.length))
    }

    val annotatedString = buildAnnotatedString {

        append(finalText)

        if (bullets) {
            addStyle(style = SpanStyle(fontWeight = FontWeight.Bold), start = 0, end = 3)
        }

        // Add bold style to keywords that has to be bold
        boldIndexes.forEach {
            addStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xff64B5F6),
                    fontSize = 15.sp

                ),
                start = it.first,
                end = it.second
            )

        }
    }

    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        fontSize = 16.sp,
        text = annotatedString
    )
}


@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun TutorialTextPreview() {
    StyleableTutorialText("Sample text for demonstrating this text")
}