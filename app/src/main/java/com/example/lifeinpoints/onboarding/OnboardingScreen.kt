package com.example.lifeinpoints.onboarding

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.lifeinpoints.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val onboardingItems = listOf(
        OnboardingItem(
            title = stringResource(R.string.start_sc_title),
            description = stringResource(R.string.start_sc_text),
            color = Color(0xFF7E6DF8)
        ),
        OnboardingItem(
            title = stringResource(R.string.sec_sc_title),
            description = stringResource(R.string.sec_sc_text),
            color = Color(0xFF4ECDC4)
        ),
        OnboardingItem(
            title = stringResource(R.string.cal_sc_title),
            description = stringResource(R.string.cal_sc_text),
            color = Color(0xFFFF6B6B)
        ),
        OnboardingItem(
            title = stringResource(R.string.stat_sc_title),
            description = stringResource(R.string.stat_sc_text),
            color = Color(0xFF96CEB4)
        ),
        OnboardingItem(
            title = stringResource(R.string.set_sc_title),
            description = stringResource(R.string.set_sc_text),
            color = Color(0xFF45B7D1)
        )
    )

    val pagerState = rememberPagerState(pageCount = { onboardingItems.size })
    val scope = rememberCoroutineScope()
    //val coroutineScope = scope // для совместимости

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Индикаторы
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { page ->
                    val color = if (pagerState.currentPage == page)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(item = onboardingItems[page])
            }

            // Кнопки навигации
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage < pagerState.pageCount - 1) {
                    TextButton(
                        onClick = {
                            Log.d("Onboarding", "Skip clicked")
                            scope.launch {
                                try {
                                    pagerState.animateScrollToPage(pagerState.pageCount - 1)
                                } catch (e: Exception) {
                                    Log.e("Onboarding", "Skip animation failed", e)
                                    pagerState.scrollToPage(pagerState.pageCount - 1)
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.onboarding_skip))
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < pagerState.pageCount - 1) {
                            Log.d("Onboarding", "Next clicked, current=${pagerState.currentPage}")
                            scope.launch {
                                try {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                } catch (e: Exception) {
                                    Log.e("Onboarding", "Next animation failed", e)
                                    pagerState.scrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        } else {
                            Log.d("Onboarding", "Start clicked, calling onComplete")
                            onComplete()
                        }
                    }
                ) {
                    Text(
                        if (pagerState.currentPage < pagerState.pageCount - 1)
                            stringResource(R.string.onboarding_next)
                        else
                            stringResource(R.string.onboarding_start)
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(item: OnboardingItem) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = item.color.copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .padding(24.dp)
        ) { /* иконка */ }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = item.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = item.description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

data class OnboardingItem(
    val title: String,
    val description: String,
    val color: Color
)