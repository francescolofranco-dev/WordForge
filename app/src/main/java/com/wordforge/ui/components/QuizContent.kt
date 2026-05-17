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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.wordforge.data.Word
import com.wordforge.ui.theme.ForgeOrangeDeep
import com.wordforge.ui.theme.ForgeOrangeSoft
import com.wordforge.ui.theme.Sage
import com.wordforge.ui.theme.SageSoft
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

    var heroVisible by remember(word.id) { mutableStateOf(false) }
    LaunchedEffect(word.id) { heroVisible = true }
    val heroScale by animateFloatAsState(
        targetValue = if (heroVisible) 1f else 0.92f,
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
        Text(
            text = "TIER ${word.currentTier} OF 8",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.W600,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(18.dp))

        val promptStyle = if (promptIsWord) {
            MaterialTheme.typography.displayLarge
        } else {
            MaterialTheme.typography.headlineMedium
        }
        Text(
            text = promptText,
            style = promptStyle,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .scale(heroScale)
        )

        Spacer(modifier = Modifier.height(36.dp))

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
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
            ) {
                Text(
                    text = revealButtonLabel,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else if (!answered) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = ForgeOrangeSoft,
            ) {
                Text(
                    text = revealText,
                    style = if (promptIsWord)
                        MaterialTheme.typography.titleLarge
                    else
                        MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Did you get it right?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        onIncorrect()
                        wasCorrect = false
                        answered = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ForgeOrangeDeep,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nope", style = MaterialTheme.typography.titleLarge)
                }

                Button(
                    onClick = {
                        onCorrect()
                        wasCorrect = true
                        answered = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Sage,
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Got it!", style = MaterialTheme.typography.titleLarge)
                }
            }
        } else {
            val feedbackAccent = if (wasCorrect == true) Sage else ForgeOrangeDeep
            val feedbackBg = if (wasCorrect == true) SageSoft else ForgeOrangeSoft
            val feedbackText = if (wasCorrect == true)
                "Nice — moving to the next tier."
            else
                "No worries — you'll see this one again sooner."
            val feedbackIcon = if (wasCorrect == true) Icons.Rounded.Check else Icons.Rounded.Close

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = feedbackBg,
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(feedbackAccent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = feedbackIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = feedbackText,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAdvance,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
            ) {
                Text(
                    text = advanceLabel,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}
