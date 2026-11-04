package com.example.taskee.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskee.ui.theme.BorderColor
import com.example.taskee.ui.theme.TabGrey
import com.example.taskee.ui.theme.TextPrimary
import com.example.taskee.ui.theme.TextSecondary

@Composable
fun PillTabBar(
    selectedTab: Int,
    tasksCount: Int,
    notesCount: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(BorderColor)
            .padding(4.dp)
            .testTag("pill_tab_bar")
    ) {
        TabItem(
            title = "Tasks",
            count = tasksCount,
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f)
        )
        TabItem(
            title = "Notes",
            count = notesCount,
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabItem(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) TabGrey else Color.Transparent,
        label = "tab_bg"
    )
    val textColor = if (isSelected) TextPrimary else TextSecondary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 15.sp
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$count",
                        color = textColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
