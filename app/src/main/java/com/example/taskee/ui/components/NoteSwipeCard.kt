package com.example.taskee.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskee.data.Note
import com.example.taskee.ui.theme.AccentLight
import com.example.taskee.ui.theme.DarkBackground
import com.example.taskee.ui.theme.TextDark
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteSwipeCard(
    note: Note,
    currentIndex: Int,
    totalNotes: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 280.dp, max = 380.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = AccentLight.copy(alpha = 0.4f)
                )
                .testTag("note_card_${note.id}"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AccentLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Top header: icon badge + edit/delete buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.1f))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Note",
                            tint = TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit note",
                                tint = TextDark.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("delete_note_${note.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete note",
                                tint = TextDark.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextDark,
                            fontSize = 17.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom row: date chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = formatNoteDate(note.createdAtMillis),
                            color = TextDark.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "${currentIndex + 1} of $totalNotes",
                        color = TextDark.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Navigation controls for swipe/deck
        if (totalNotes > 1) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable(onClick = onPrevious),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous note",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AccentLight)
                        .clickable(onClick = onNext),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next note",
                        tint = DarkBackground
                    )
                }
            }
        }
    }
}

private fun formatNoteDate(millis: Long): String {
    val date = Date(millis)
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return formatter.format(date)
}
