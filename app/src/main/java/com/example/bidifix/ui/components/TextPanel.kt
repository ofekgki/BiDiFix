package com.example.bidifix.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.example.bidifix.ui.theme.BiDiTheme

/**
 * One labelled panel: a title, a short subtitle, a multiline text field and an action
 * button (optionally a secondary one) beneath it.
 *
 * The panel is tinted with [panelColor] (the input vs. transformed semantic surface) so the
 * two panels are distinguishable by more than color alone — each also carries its own title
 * and subtitle. The field scrolls internally for long multiline text.
 */
@Composable
fun TextPanel(
    title: String,
    subtitle: String,
    value: String,
    readOnly: Boolean,
    fieldContentDescription: String,
    buttonLabel: String,
    buttonEnabled: Boolean,
    onButtonClick: () -> Unit,
    panelColor: Color,
    buttonColors: ButtonColors,
    modifier: Modifier = Modifier,
    textDirection: TextDirection = TextDirection.Content,
    onValueChange: (String) -> Unit = {},
    secondaryButtonLabel: String? = null,
    onSecondaryButtonClick: () -> Unit = {},
) {
    val accent = BiDiTheme.colors.accent
    val border = BiDiTheme.colors.border

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = panelColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, border),
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp),
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true)
                    .heightIn(min = 150.dp)
                    .semantics { contentDescription = fieldContentDescription },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textDirection = textDirection,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accent,
                    unfocusedBorderColor = border,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = accent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
            Button(
                onClick = onButtonClick,
                enabled = buttonEnabled,
                colors = buttonColors,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp)
                    .padding(top = 14.dp),
            ) {
                Text(buttonLabel, style = MaterialTheme.typography.labelLarge)
            }
            if (secondaryButtonLabel != null) {
                OutlinedButton(
                    onClick = onSecondaryButtonClick,
                    enabled = buttonEnabled,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, accent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = accent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .padding(top = 8.dp),
                ) {
                    Text(secondaryButtonLabel, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
