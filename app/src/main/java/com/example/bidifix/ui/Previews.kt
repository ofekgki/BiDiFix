package com.example.bidifix.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bidifix.bidi.BidiTransformer
import com.example.bidifix.bidi.SampleInputs
import com.example.bidifix.ui.components.TextPanel
import com.example.bidifix.ui.theme.BiDiFixTheme

/** Preview of a single input panel with a sample Hebrew/English string. */
@Preview(showBackground = true, widthDp = 360, heightDp = 320)
@Composable
private fun InputPanelPreview() {
    BiDiFixTheme {
        Surface {
            TextPanel(
                title = "Original Text",
                value = SampleInputs.DEFAULT,
                readOnly = false,
                fieldContentDescription = "Original text input",
                buttonLabel = "Transform",
                buttonEnabled = true,
                onButtonClick = {},
                modifier = Modifier.fillMaxSize().padding(16.dp),
            )
        }
    }
}

/** Preview of both panels stacked, showing real transformer output. */
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun StackedPanelsPreview() {
    val sample = SampleInputs.DEFAULT
    val transformed = remember { BidiTransformer().transform(sample) }
    BiDiFixTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextPanel(
                    title = "Original Text",
                    value = sample,
                    readOnly = false,
                    fieldContentDescription = "Original text input",
                    buttonLabel = "Transform",
                    buttonEnabled = true,
                    onButtonClick = {},
                    modifier = Modifier.height(280.dp),
                )
                TextPanel(
                    title = "Transformed Text",
                    value = transformed,
                    readOnly = true,
                    fieldContentDescription = "Transformed text output",
                    buttonLabel = "Copy to Clipboard",
                    buttonEnabled = true,
                    onButtonClick = {},
                    modifier = Modifier.height(280.dp),
                )
            }
        }
    }
}
