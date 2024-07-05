package com.example.task2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.task2.ui.theme.Task2Theme
import kotlinx.coroutines.delay
import java.util.List
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var scores = listOf<Int>()
        saveScores(scores)
        setContent {
            Task2Theme {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Column (modifier = Modifier
                        .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(text = "TOM", color = Color(0xFF7f8585), fontSize = 49.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 50.dp))
                        Text(text = "&", color = Color.Black, fontSize = 49.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 50.dp))
                        Text(text = "JERRY", color = Color(0xFFd1820c), fontSize = 49.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 50.dp))

                        highscore(scores = scores)

                        Button(onClick = {
                        val intent = Intent(this@MainActivity, MainActivity2::class.java)
                        startActivity(intent) }
                    ){ Text(text = "PLAY GAME", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold)}
                    }
                }
            }
        }
    }
    private fun saveScores(scores: kotlin.collections.List<Int>) {
        val sharedPreferences = getSharedPreferences("ScoresPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("scores", scores.joinToString(","))
        editor.apply()
    }

}


var highestScore : Int =0
@Composable
fun highscore(scores: kotlin.collections.List<Int>){
    highestScore = scores.maxOrNull() ?: 0
    Text(text = "Highest Score: $highestScore ", modifier = Modifier.padding(top = 150.dp), fontSize = 38.sp,)
}