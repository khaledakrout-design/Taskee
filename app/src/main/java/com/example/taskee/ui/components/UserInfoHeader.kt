package com.example.taskee.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskee.R
import com.example.taskee.ui.theme.TextPrimary
import com.example.taskee.ui.theme.TextSecondary

@Composable
fun UserInfoHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("user_info_header")
    ) {
        Text(
            text = "Plan your Day !",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 24.sp
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_profile),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 24.dp,
                            bottomStart = 24.dp,
                            bottomEnd = 24.dp,
                            topEnd = 10.dp
                        )
                    )
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hi, Good Morning",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = "Jacobe",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Action",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
