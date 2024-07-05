package com.example.task2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import kotlin.random.Random
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import okhttp3.OkHttpClient
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberImagePainter
import com.example.task2.ui.theme.apiinterface2
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainActivity2 : ComponentActivity() {
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        val scores = getScores()
        super.onCreate(savedInstanceState)
        setContent {
            GameScreen(::navigateToSecondActivity, scores)
        }
    }

    private fun navigateToSecondActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun getScores(): List<Int> {
        val sharedPreferences = getSharedPreferences("ScoresPref", Context.MODE_PRIVATE)
        val scoresString = sharedPreferences.getString("scores", "") ?: ""
        return scoresString.split(",").mapNotNull { it.toIntOrNull() }
    }

}

data class GameState(
    var jerryLane: Int = 1,
    var tomLane: Int = 1,
)

var col1 = false
var col2 = false


data class Obstacle(val lane: Int, var y: Float)

val newObstacle = Obstacle(
    lane = 2,
    y = 0f
)

var scored : Int = 0
var obstacles = mutableStateListOf<Obstacle>()
var jerryLane : Int=1

object RetrofitInstance {
    private const val BASE_URL = "https://chasedeux.vercel.app"

    val api: apiinterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(apiinterface::class.java)
    }

}

var obstacleLimit:  Int? = 0
fun fetchObstacleLimit() {
    val apiService = RetrofitInstance.api
    // Launching a coroutine to handle the API call asynchronously
    apiService.getData().enqueue(object : Callback<Aobstacle> {
        override fun onResponse(call: Call<Aobstacle>, response: Response<Aobstacle>) {
            if (response.isSuccessful) {
                obstacleLimit=response.body()?.obstacleLimit
            } else {
                println("Failed to get obstacle limit: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Aobstacle>, t: Throwable) {
            println("Error: ${t.message}")
        }
    })
}

object RetrofitClient {
    private const val BASE_URL = "https://chasedeux.vercel.app/"

    private val client = OkHttpClient.Builder().build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val imageService: apiinterface2 by lazy {
        retrofit.create(apiinterface2::class.java)
    }
}

class ImageViewModel : ViewModel() {
    var imageUrl: ByteArray? = null

    fun fetchImage(character: String, onComplete: (ByteArray?) -> Unit) {
        viewModelScope.launch {
            val response: Response<ResponseBody>? = try {
                withContext(Dispatchers.IO) {
                    RetrofitClient.imageService.getimage(character)
                }
            } catch (e: Exception) {
                null
            }

            imageUrl = if (response?.isSuccessful == true) {
                response.body()?.bytes()
            } else {
                null
            }
            onComplete(imageUrl)
        }
    }
}


@Composable
fun ImageFromBytes(bytes: ByteArray?) {
    if (bytes != null) {
        val bitmap = remember(bytes) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        Bitmap.createScaledBitmap(bitmap, 200, 200, true)

        if (bitmap != null) {

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Loaded Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(200.dp)
                    .height(200.dp)
            )
        }
    } else {
        Text("Loading...")
    }
}



@Composable
fun DisplayImageScreen(viewModel: ImageViewModel = viewModel()) {
    var character by remember { mutableStateOf("jerry") }
    var imageUrl by remember { mutableStateOf<ByteArray?>(null) }
    var showimage by remember { mutableStateOf(true) }

    LaunchedEffect(character) {
        viewModel.fetchImage(character) { bytes ->
            imageUrl = bytes
        }
    }
    LaunchedEffect(Unit) {
        delay(2000)
        showimage = false
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (showimage) {
            ImageFromBytes(imageUrl)
        }
    }
}



@Composable
fun gameboard(gameState: GameState, onMoveJerry: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val lane = (offset.x / (size.width / 3)).toInt()
                        jerryLane = lane.coerceIn(0, 2)
                        gameState.tomLane = lane.coerceIn(0, 2)
                    }
                )
            }) {

            // Width and height of the lanes
            val laneWidth = size.width / 3
            val laneHeight = size.height

            for (i in 0..2) {
                drawRect(
                    color = Color(0xFFe5dccd),
                    topLeft = Offset(i * laneWidth, 0f),
                    size = Size(laneWidth, laneHeight)
                )
            }
            for (i in 1..2) {
                drawLine(
                    color = Color(0xFFf9efdf),
                    start = Offset(i * laneWidth, 0f),
                    end = Offset(i * laneWidth, laneHeight),
                    strokeWidth = 50f
                )
            }
            obstacles.add(newObstacle)

            drawJerry(jerryLane, laneWidth, laneHeight)

            if (col1){
                drawTom(gameState.tomLane, laneWidth, laneHeight-75f)
            }
            else if(col2){
                drawTom(gameState.tomLane, laneWidth, laneHeight-150f)
            }
            else{
                drawTom(gameState.tomLane, laneWidth, laneHeight)
            }

            obstacles.forEach { obstacle ->
                drawObstacle(obstacle, laneWidth, laneHeight)
            }


            val boxWidth = 200f
            val boxHeight = 100f
            val boxX = size.width - boxWidth - 20
            val boxY = 20f

            val boxPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                style = Paint.Style.FILL
            }

            val textPaint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 35f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.LEFT
            }

            drawContext.canvas.nativeCanvas.drawRect(
                boxX,
                boxY,
                boxX + boxWidth,
                boxY + boxHeight,
                boxPaint
            )

            val textX = boxX + 20
            val textY = boxY + 65

            drawContext.canvas.nativeCanvas.drawText(
                "Score: $scored",
                textX,
                textY,
                textPaint
            )
        }
        DisplayImageScreen()
        fetchObstacleLimit()
        MyApp()
    }
}


@Composable
fun MyApp() {
    var showText by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000) // 2000 milliseconds = 2 seconds
        showText = false
    }
    Box(
        modifier = Modifier.padding(top = 300.dp, start = 150.dp)
    ) {
        if (showText) {
            Text(
                text = "Obstacle Limit: $obstacleLimit",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}



fun DrawScope.drawJerry(lane: Int, laneWidth: Float, laneHeight: Float) {
    val x = 15f + lane * laneWidth + laneWidth / 2
    val y = laneHeight - 400f
    drawRect(color = Color(0xFFd1820c), topLeft = Offset(x - 50f, y ), size = Size(75f, 75f))
}

fun DrawScope.drawTom(lane: Int, laneWidth: Float, laneHeight: Float) {
    val x = lane * laneWidth + laneWidth / 2 - 10f
    val y = laneHeight - 200f
    drawRect(color = Color(0xFF7f8585), topLeft = Offset(x - 50f, y ), size = Size(125f, 125f))
}

fun DrawScope.drawObstacle(obstacle: Obstacle, laneWidth: Float, laneHeight: Float) {
    val x = obstacle.lane * laneWidth + laneWidth / 2 - 10f
    drawRect(color = Color(0xFF2b83eb), topLeft = Offset(x - 25f, obstacle.y), size = Size(100f, 100f))
    drawRect(
        color = Color.Black,
        topLeft = Offset(x - 25f, obstacle.y), size = Size(100f, 100f),
        style = Stroke(
            width = 3f, // Width of the outline
            cap = StrokeCap.Butt,
            join = StrokeJoin.Miter
        )
    )
}


@Composable
fun GameScreen(onNavigate: () -> Unit, scores: List<Int>) {
    val gameState = remember { mutableStateOf(GameState()) }
    var collisionCount by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var tomLane by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }


    LaunchedEffect(Unit) {
        while (!gameOver) {
            delay(200)
            with(gameState.value) {

                val newObstacle1 = Obstacle(
                    lane = Random.nextInt(0, 3),
                    y = 0f)


                obstacles.forEach { obstacle ->
                    obstacle.y += 200f
                    if (obstacle.y > 400f){
                        obstacles.add(newObstacle1)
                    }
                }
                obstacles.removeAll { it.y > 2400f }
            }
            obstacles.filter { it.y > 2000f }.forEach { score += 10 }
            scored = score

            val jerryObstacle = obstacles.find { it.lane == jerryLane && it.y > 1900f && it.y < 2100f }
            if (jerryObstacle != null) {
                collisionCount++
                obstacles.remove(jerryObstacle)
                if (collisionCount==1){
                    col1=true
                }
                if (collisionCount== obstacleLimit){
                    col1=false
                    col2=true
                    gameOver=true
                }
            }
        }
    }

    gameboard(gameState.value) {lane ->
        gameState.value = gameState.value.copy(jerryLane = lane)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (gameOver) {
            scores+score
            GameOverDialog(score=score, onNavigate) {}
        }
    }
}



@Composable
fun GameOverDialog(score: Int, onRestart: () -> Unit, onNavigate1: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val activity = context as? Activity ?: return
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        shape = RoundedCornerShape(16.dp),
        title = null,
        text = { Column(
            modifier = Modifier
                .background(Color(0xfff9eee0), shape = RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "GAME OVER", fontSize = 30.sp, color = Color.Black, fontWeight = FontWeight.Bold )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "SCORE: $score", fontSize = 24.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { restartActivity(activity)
                col2=false
                obstacles.clear()
                jerryLane=1},
                colors = ButtonDefaults.buttonColors(containerColor =  Color(0xFF7f8585)),
                modifier = Modifier.fillMaxWidth()) {
                Text("Play Again", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        },
        containerColor = Color(0xFFf9eee0),
        confirmButton = {Button(onClick = onNavigate1,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFd1820c)),
            modifier = Modifier.fillMaxWidth()) {
            Text("Home", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }})
}


fun restartActivity(activity: Activity) {
    val intent = Intent(activity, activity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    activity.finish()
    activity.startActivity(intent)
}



