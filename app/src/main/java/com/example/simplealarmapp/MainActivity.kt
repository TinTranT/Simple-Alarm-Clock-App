package com.example.simplealarmapp

import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

/**
 * Custom theme composable function for the Simple Alarm App.
 * This function applies styling to the app's content.
 *
 * @param content The composable content to which the theme should be applied
 */
@Composable
fun SimpleAlarmAppTheme(content: @Composable () -> Unit) {
    content()
}

/**
 * AppColors object defines the color palette used throughout the app.
 * Centralizing colors helps maintain consistency in the UI design.
 */
object AppColors {
    val Primary = Color(0xFFFF9800) // Orange
    val Text = Color(0xFF3C3C3C) // Dark gray
    val Background = Color(0xFFF5F5F5) // Light Gray
}

/**
 * MainActivity is the entry point of the application.
 * It sets up the user interface and handles the overall app configuration.
 */
class MainActivity : ComponentActivity() {
    /**
     * onCreate is called when the activity is starting.
     * It initializes the activity, sets up edge-to-edge display,
     * and configures the content view with Jetpack Compose.
     *
     * @param savedInstanceState Contains data about the previous state if the activity is being reinitialized
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleAlarmAppTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .paint(
                            painterResource(id = R.drawable.background), // Use your image
                            contentScale = ContentScale.Crop // Scale the image properly
                        )
                ) {
                    Scaffold(
                        containerColor = Color.Transparent, // Make scaffold transparent
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        AlarmScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

/**
 * AlarmScreen is the main UI component containing the alarm functionality.
 * It includes a time picker, alarm title input, and a button to set the alarm.
 *
 * @param modifier Modifier to be applied to the component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(modifier: Modifier = Modifier) {
    var time by remember { mutableStateOf("No time") }
    var alarmTitle by remember { mutableStateOf("Wake up for class!") }

    val context = LocalContext.current
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val minute = Calendar.getInstance().get(Calendar.MINUTE)
    val timePickerState = rememberTimePickerState(hour, minute, is24Hour = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Alarm Icon", tint = AppColors.Primary, modifier = Modifier.size(64.dp))

        Spacer(modifier = Modifier.height(10.dp))

        Text("Simple Alarm Clock", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AppColors.Text)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = alarmTitle,
            onValueChange = { alarmTitle = it },
            label = { Text("Alarm Title", fontSize = 18.sp, color = AppColors.Primary) },
            textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                containerColor = Color(0x66000000)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(8.dp, RoundedCornerShape(10.dp)),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        containerColor = Color.White,
                        timeSelectorSelectedContainerColor = AppColors.Primary,
                        timeSelectorUnselectedContainerColor = Color.White,
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContentColor = AppColors.Text
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Selected time: ", fontSize = 22.sp, color = Color.White)
            Text(text = time, color = AppColors.Primary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        /**
         * Function to set an alarm using the system's AlarmClock API.
         *
         * @param context The application context
         * @param hour The hour to set the alarm
         * @param minute The minute to set the alarm
         * @param message The title of the alarm
         */
        Button(
            onClick = {
                time = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                setTime(context, timePickerState.hour, timePickerState.minute, alarmTitle)
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = AppColors.Primary
            ),
            modifier = Modifier
                .size(180.dp, 60.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Set Alarm", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Set Alarm", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Function to set an alarm using the system's AlarmClock API.
 *
 * @param context The application context
 * @param hour The hour to set the alarm
 * @param minute The minute to set the alarm
 * @param message The title of the alarm
 */
fun setTime(context: Context, hour: Int, minute: Int, message: String) {
    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtra(AlarmClock.EXTRA_HOUR, hour)
        putExtra(AlarmClock.EXTRA_MINUTES, minute)
        putExtra(AlarmClock.EXTRA_MESSAGE, message)
    }

    if (intent.resolveActivity(context.packageManager) == null) {
        context.startActivity(intent)
        Toast.makeText(context, "Alarm set for $hour:$minute with title: $message", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "No alarm app available", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    SimpleAlarmAppTheme {
        AlarmScreen()
    }
}
