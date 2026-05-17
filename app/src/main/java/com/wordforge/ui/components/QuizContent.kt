package com.wordforge.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordforge.data.Word
import com.wordforge.ui.theme.Success
import com.wordforge.ui.theme.SuccessContainer
import com.wordforge.ui.theme.TierColors
import kotlin.random.Random

@Composable
fun QuizContent(
    word: Word,
    onCorrect: () -> Unit,
    onIncorrect: () -> Unit,
    onAdvance: () -> Unit,
    advanceLabel: String = "Done",
    modifier: Modifier = Modifier,
) {
    var revealed by remember(word.id) { mutableStateOf(false) }
    var answered by remember(word.id) { mutableStateOf(false) }
    var wasCorrect by remember(word.id) { mutableStateOf<Boolean?>(null) }

    // Random per-word flip — sometimes the word is the prompt, sometimes
    // the meaning is. Re-rolls when word.id changes (batch flows).
    val promptIsWord = remember(word.id) { Random.nextBoolean() }
    val promptText = if (promptIsWord) word.word else word.meaning
    val revealText = if (promptIsWord) word.meaning else word.word
    val recallQuestion = if (promptIsWord)
        "Do you remember what this means?"
    else
        "Do you remember the word?"
    val revealButtonLabel = if (promptIsWord) "Reveal meaning" else "Reveal word"

    val tierColor = TierColors.getOrElse(word.currentTier) { TierColors.last() }

    var heroVisible by remember(word.id) { mutableStateOf(false) }
    LaunchedEffect(word.id) { heroVisible = true }
    val heroScale by animateFloatAsState(
        targetValue = if (heroVisible) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "heroScale"
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(tierColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${word.currentTier}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        val promptStyle = if (promptIsWord) {
            MaterialTheme.typography.displayLarge.copy(
                fontSize = 56.sp,
                lineHeight = 64.sp,
                letterSpacing = (-1).sp
            )
        } else {
            MaterialTheme.typography.headlineMedium
        }
        Text(
            text = promptText,
            style = promptStyle,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .scale(heroScale)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!revealed) {
            Text(
                text = recallQuestion,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { revealed = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = revealButtonLabel,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else if (!answered) {
            val revealStyle = if (promptIsWord) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.headlineMedium
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text(
                    text = revealText,
                    style = revealStyle,
                    fontWeight = if (promptIsWord) FontWeight.Normal else FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Did you get it right?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = {
                        onIncorrect()
                        wasCorrect = false
                        answered = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nope", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        onCorrect()
                        wasCorrect = true
                        answered = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Success
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Got it!", style = MaterialTheme.typography.titleMedium)
                }
            }
        } else {
            val feedbackColor = if (wasCorrect == true) Success else MaterialTheme.colorScheme.error
            val bgColor = if (wasCorrect == true) SuccessContainer else MaterialTheme.colorScheme.errorContainer
            val feedbackText = if (wasCorrect == true)
                "Nice! Moving to the next tier."
            else
                "No worries — you'll see this one again sooner."
            val feedbackIcon = if (wasCorrect == true) Icons.Rounded.Check else Icons.Rounded.Close

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(feedbackColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = feedbackIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = feedbackText,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAdvance,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = advanceLabel,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
