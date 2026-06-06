package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Campaign
import com.example.model.DriveFile
import com.example.model.SocialPost
import com.example.model.SocialPostChannel
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DriveCampaignScreen(
    viewModel: DriveCampaignViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val campaigns by viewModel.campaignsList.collectAsState()
    val socialPosts by viewModel.socialPosts.collectAsState()

    var showConfigPanel by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(0) } // 0: Instagram, 1: Facebook, 2: Automation Logs

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Studio Icon",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Drive Campaign Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Syncing with Google Drive",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showConfigPanel = !showConfigPanel },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .testTag("config_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Toggle config",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. configuration overlay panel
            item {
                AnimatedVisibility(
                    visible = showConfigPanel,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("config_panel"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Developer & Access Credentials",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Our app has been initialized with the Drive scopes you approved. Toggle the setting below to run in Simulated Sandbox or paste your real OAuth access token.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.updateOauthToken("simulated") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (viewModel.oauthToken == "simulated") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (viewModel.oauthToken == "simulated") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = "Simulated", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Sim Sandbox", fontSize = 12.sp)
                                }
                                
                                Button(
                                    onClick = { 
                                        if (viewModel.oauthToken == "simulated") {
                                            viewModel.updateOauthToken("") // Clear token to prompt text box entry
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (viewModel.oauthToken != "simulated") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (viewModel.oauthToken != "simulated") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Real", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Real Account", fontSize = 12.sp)
                                }
                            }

                            if (viewModel.oauthToken != "simulated") {
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = viewModel.oauthToken,
                                    onValueChange = { viewModel.updateOauthToken(it) },
                                    label = { Text("Paste Google OAuth Token") },
                                    placeholder = { Text("ya29.a0Ac...") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("oauth_token_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedLabelColor = MaterialTheme.colorScheme.primary
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                                )
                            }
                        }
                    }
                }
            }

            // 2. browser row - browse files in Google Drive
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Select Photo from Google Drive",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = if (viewModel.oauthToken == "simulated") "Simulated workspace photos" else "Real Drive query active",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(
                            onClick = { viewModel.loadDriveFiles() },
                            modifier = Modifier.testTag("refresh_drive_btn")
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh files",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    if (viewModel.isLoadingFiles) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else if (viewModel.driveFiles.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No image files found in Drive.\nVerify permissions/folders or use Sim Sandbox.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        // Horizontal list of photo card widgets
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(viewModel.driveFiles) { file ->
                                val isSelected = viewModel.selectedFile?.id == file.id
                                Card(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .testTag("drive_file_${file.id}")
                                        .clickable { viewModel.selectedFile = file },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(90.dp)
                                        ) {
                                            AsyncImage(
                                                model = file.thumbnailLink,
                                                contentDescription = file.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .background(Color.Black.copy(0.6f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = file.size ?: "N/A",
                                                    fontSize = 8.sp,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                        Text(
                                            text = file.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. custom guidelines & run analysis trigger
            viewModel.selectedFile?.let { selected ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guidelines_trigger_card"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = selected.thumbnailLink,
                                    contentDescription = "Selected product image",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Selected Photo Source:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = selected.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Modified: ${selected.modifiedTime}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Analysis Guideline Tone (Optional)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val presets = listOf("Urban energetic", "Cozy luxury", "Minimal high-tech", "Organic clean")
                                presets.forEach { hint ->
                                    val isSelected = viewModel.customPromptInstruction.equals(hint, ignoreCase = true)
                                    SuggestionChip(
                                        onClick = { viewModel.customPromptInstruction = hint },
                                        label = { Text(text = hint, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            labelColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        border = SuggestionChipDefaults.suggestionChipBorder(
                                            enabled = true,
                                            borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = viewModel.customPromptInstruction,
                                onValueChange = { viewModel.customPromptInstruction = it },
                                placeholder = { Text("E.g., highlight deep focus mood, vibrant cyan shadows...") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_instruction_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary
                                ),
                                trailingIcon = {
                                    if (viewModel.customPromptInstruction.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.customPromptInstruction = "" }) {
                                            Icon(Icons.Default.Delete, "clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Large Pulsing CTA button
                            Button(
                                onClick = { viewModel.generateCampaign() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("generate_campaign_btn"),
                                shape = RoundedCornerShape(16.dp),
                                enabled = !viewModel.isAnalyzing
                            ) {
                                if (viewModel.isAnalyzing) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Gemini Visual Scanning...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Run")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ANALYZE PHOTO & CREATE CAMPAIGN", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 4. loader layout for visual processing
            if (viewModel.isAnalyzing) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("visual_analyzer_loader"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Gemini Cognitive Visual Inspection",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Visual pulsing animation dots
                            val infiniteTransition = rememberInfiniteTransition()
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 0.8f,
                                targetValue = 1.3f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(800, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 12.dp)
                            ) {
                                repeat(5) { i ->
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .graphicsLayer(scaleX = scale, scaleY = scale)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        MaterialTheme.colorScheme.primary,
                                                        MaterialTheme.colorScheme.secondary
                                                    )
                                                )
                                            )
                                    )
                                }
                            }

                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            val mockTickers = listOf(
                                "Investigating visual symmetry guides...",
                                "Sampling RGB & shade distributions...",
                                "Applying brand positioning anchors...",
                                "Synthesizing cross-channel promotional copy..."
                            )
                            val selectedTicker = remember { derivedStateOf { mockTickers[System.currentTimeMillis().toInt() / 2000 % mockTickers.size] } }
                            Text(
                                text = selectedTicker.value,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // 5. active generated output workspace
            viewModel.activeCampaign?.let { campaign ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("active_campaign_workspace"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            
                            // Header badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        "ACTIVE CAMPAIGN BRIEF",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteCampaign(campaign) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, "Delete campaign", tint = Color.Red.copy(0.7f))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = campaign.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = campaign.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Color palette circles
                            Text(
                                text = "EXTRACTED VISUAL COLOR PALETTE (TAP TO COPY)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                campaign.colorPalette.split(",").forEach { rgbHex ->
                                    val cleaned = rgbHex.trim()
                                    if (cleaned.startsWith("#")) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                                    .background(Color(android.graphics.Color.parseColor(cleaned)))
                                                    .clickable {
                                                        clipboardManager.setText(AnnotatedString(cleaned))
                                                        Toast.makeText(context, "$cleaned Copied!", Toast.LENGTH_SHORT).show()
                                                    }
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = cleaned,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Recommended Target Audience Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Audience Target",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "TARGET SEGMENT AUDIENCE",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = campaign.targetAudience,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Core Concept Angle
                            Text(
                                text = "CORE STRATEGY & VISUAL AD ANGLE",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = campaign.generatedAdIdea,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // ACTION EXPORTS ROW
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Save to Folder back onto drive
                                Button(
                                    onClick = { viewModel.exportToDrive() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (campaign.isExportedBackToDrive) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
                                        contentColor = if (campaign.isExportedBackToDrive) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("export_drive_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !viewModel.isExporting
                                ) {
                                    if (viewModel.isExporting) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                                    } else {
                                        Icon(
                                            imageVector = if (campaign.isExportedBackToDrive) Icons.Default.Check else Icons.Default.Share,
                                            contentDescription = "Export"
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            if (campaign.isExportedBackToDrive) "Saved to Drive" else "Export back Drive",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Trigger Instagram/FB automation post
                                Button(
                                    onClick = { 
                                        viewModel.pushSocialAutomation()
                                        Toast.makeText(context, "Published to Auto Pipeline!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (campaign.isPostedSocialInstance) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
                                        contentColor = if (campaign.isPostedSocialInstance) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("publish_social_btn"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (campaign.isPostedSocialInstance) Icons.Default.Check else Icons.Default.PlayArrow,
                                        contentDescription = "Publish"
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        if (campaign.isPostedSocialInstance) "Posts Live" else "AutoPost Social",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // If saved back to drive folder, show location
                            if (campaign.isExportedBackToDrive) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, M3SuccessBg, RoundedCornerShape(12.dp))
                                        .background(M3SuccessBg.copy(0.35f))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, "success", tint = M3SuccessText, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "FOLDER CREATED IN GOOGLE DRIVE",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                color = M3SuccessText,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "📍 Workspace: ${campaign.designatedFolderName}\n🔖 Asset Reference: ${campaign.exportedFileId ?: "uploaded"}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = M3SuccessText,
                                            modifier = Modifier.padding(start = 24.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // TABS FOR PREVIEWS
                            TabRow(
                                selectedTabIndex = activeTab,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                Tab(
                                    selected = activeTab == 0,
                                    onClick = { activeTab = 0 },
                                    text = { Text("Instagram Post", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = activeTab == 1,
                                    onClick = { activeTab = 1 },
                                    text = { Text("Facebook Post", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = activeTab == 2,
                                    onClick = { activeTab = 2 },
                                    text = { Text("Pipeline Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Tab view bodies
                            when (activeTab) {
                                0 -> {
                                    // Instagram mockup feed
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("C", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text("campaign_pilot", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                                Text("Sponsored", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }

                                        AsyncImage(
                                            model = campaign.originalPhotoUrl,
                                            contentDescription = "Post image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(Icons.Default.Check, "like", tint = CyberPink)
                                            Icon(Icons.Default.Share, "share", tint = MaterialTheme.colorScheme.onBackground)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Liked by aistudio and 12,402 others", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        // The Instagram Caption text
                                        Text(
                                            text = campaign.instagramCaption,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                                1 -> {
                                    // Facebook mock post
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(MyColorGradientBrush()),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("F", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("Campaign Studio Hub", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("Just now", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(10.dp)) // Replace Cloud with Check
                                                }
                                            }
                                        }

                                        Text(
                                            text = campaign.facebookCaption,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )

                                        AsyncImage(
                                            model = campaign.originalPhotoUrl,
                                            contentDescription = "Post photo",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                                2 -> {
                                    // Live automation scheduling reports
                                    Column {
                                        Text(
                                            "AUTOMATED INTEGRATION TIER (ACTIVE SCHEDULING)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        val activeCampaignPosts = socialPosts.filter { it.campaignId == campaign.id }
                                        if (activeCampaignPosts.isEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "No posts active for this campaign. Tap 'AutoPost Social' above to schedule publishing immediately.",
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 11.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        } else {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                activeCampaignPosts.forEach { post ->
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                                            .padding(10.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .background(
                                                                            if (post.channel == SocialPostChannel.INSTAGRAM) CyberPink.copy(0.15f) else MaterialTheme.colorScheme.primary.copy(0.15f),
                                                                            CircleShape
                                                                        )
                                                                        .padding(8.dp)
                                                                ) {
                                                                    Text(
                                                                        text = if (post.channel == SocialPostChannel.INSTAGRAM) "IG" else "FB",
                                                                        color = if (post.channel == SocialPostChannel.INSTAGRAM) CyberPink else MaterialTheme.colorScheme.primary,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 10.sp
                                                                    )
                                                                }
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Column {
                                                                    Text("Post id: ${post.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                                                    Text("Status: LIVE", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                                                }
                                                            }
                                                            Text(
                                                                text = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(post.timestamp)),
                                                                fontSize = 10.sp,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. Campaign archive list (Room local database archive)
            if (campaigns.isNotEmpty()) {
                item {
                    Text(
                        text = "SAVED CAMPAIGNS ARCHIVE (OFFLINE-FIRST STORE)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                items(campaigns) { oldCamp ->
                    val isActive = viewModel.activeCampaign?.id == oldCamp.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("archive_campaign_item_${oldCamp.id}")
                            .clickable { viewModel.selectActiveCampaign(oldCamp) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini image circle
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    AsyncImage(
                                        model = oldCamp.originalPhotoUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = oldCamp.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onBackground,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val formattedTime = java.text.DateFormat.getDateInstance().format(java.util.Date(oldCamp.createdAt))
                                    Text(
                                        text = "Generated on $formattedTime",
                                        fontSize = 11.sp,
                                        color = if (isActive) MaterialTheme.colorScheme.onSecondaryContainer.copy(0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (oldCamp.isExportedBackToDrive) {
                                    Icon(
                                        imageVector = Icons.Default.Share, // Replace Cloud with Share
                                        contentDescription = "Exported",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(end = 6.dp)
                                    )
                                }
                                if (oldCamp.isPostedSocialInstance) {
                                    Icon(
                                        imageVector = Icons.Default.Check, // Replace CheckCircle with Check for 100% safety
                                        contentDescription = "Posted",
                                        tint = CyberPink,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(end = 6.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteCampaign(oldCamp) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, "Delete", tint = Color.Red.copy(0.7f))
                                }
                            }
                        }
                    }
                }
            }

            // Simple workspace static footer logs
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Drive Campaign Studio v1.2",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Encrypted Offline SQLite Hub with active Gemini cognitive modeling pipelines.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MyColorGradientBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            SoftViolet,
            NeonTeal,
            CyberPink
        )
    )
}
