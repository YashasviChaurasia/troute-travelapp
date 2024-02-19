package com.example.troute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var randomDistances: List<Double>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        randomDistances = generateRandomDoubles(12, 0.0, 100.0)
        setContent {
            TravelPreview(randomDistances)
        }
    }
}

class TravelStop(val id:Int, val name:String, var distance:Double)

fun convertDistanceToMiles(travelStops: MutableList<TravelStop>) {
    travelStops.forEach { it.distance = kmToMiles(it.distance) }
}

fun kmToMiles(km: Double): Double {
    return km * 0.621370
}

@Composable
fun ScrollableRow(items: MutableList<TravelStop>, showDistanceInMiles: Boolean) {
    var checkedStateList by remember { mutableStateOf(List(items.size) { false }) }
    var traveledDistance by remember { mutableStateOf(0.0) }

    fun onCheckedChange(index: Int, isChecked: Boolean) {
        val newList = checkedStateList.toMutableList()
        if (isChecked) {
            // If a stop is checked, also check all preceding stops
            for (i in 0..index) {
                newList[i] = true
            }
        } else {
            // If a stop is cleared, clear all following stops
            for (i in index until newList.size) {
                newList[i] = false
            }
        }
        checkedStateList = newList
        val checkedDistances = items.filterIndexed { idx, _ -> checkedStateList[idx] }.map { it.distance }
        traveledDistance = checkedDistances.sum()

    }

    val totalDistance = items.sumOf { it.distance }
    val targetValue = if (showDistanceInMiles) {
        kmToMiles(traveledDistance/totalDistance).toFloat()
    } else {
        (traveledDistance / totalDistance).toFloat()
    }

    val progress by animateFloatAsState(targetValue = targetValue, label = "")

    Column {

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .width(4.dp)
                .height(4.dp)
        )
        LazyRow {
            items(checkedStateList.size) { index ->
                RowItem(
                    travelStop = items[index],
                    checked = checkedStateList[index],
                    onCheckedChange = { isChecked -> onCheckedChange(index, isChecked) },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        Spacer(modifier=Modifier.padding(bottom = 10.dp))

        if (showDistanceInMiles){
            Text("Total Distance: ${"%.2f".format(totalDistance)} Miles", fontWeight = FontWeight.Normal, fontSize = 20.sp)
            Text("Traveled Distance: ${"%.2f".format(kmToMiles(traveledDistance))} Miles", fontWeight = FontWeight.Normal, fontSize = 20.sp)
            Text("Remaining Distance: ${"%.2f".format(totalDistance-kmToMiles(traveledDistance))} Miles", fontWeight = FontWeight.Normal, fontSize = 20.sp)
        }
        else{
            Text("Total Distance: ${"%.2f".format(totalDistance)} Km", fontWeight = FontWeight.Normal, fontSize = 20.sp)
            Text("Traveled Distance: ${"%.2f".format(traveledDistance)} Km", fontWeight = FontWeight.Normal, fontSize = 20.sp)
            Text("Remaining Distance: ${"%.2f".format(totalDistance-traveledDistance)} Km", fontWeight = FontWeight.Normal, fontSize = 20.sp)
        }
        Text("bloody Travelled Distance: ${"%.2f".format(traveledDistance)} Km", fontWeight = FontWeight.Normal, fontSize = 20.sp)

    }
}
@Composable
fun NormalRow(items: MutableList<TravelStop>, showDistanceInMiles: Boolean) {
    var checkedStateList by remember { mutableStateOf(List(items.size) { false }) }
    var traveledDistance by remember { mutableStateOf(0.0) }

    fun onCheckedChange(index: Int, isChecked: Boolean) {
        val newList = checkedStateList.toMutableList()
        if (isChecked) {
            // If a stop is checked, also check all preceding stops
            for (i in 0..index) {
                newList[i] = true
            }
        } else {
            // If a stop is cleared, clear all following stops
            for (i in index until newList.size) {
                newList[i] = false
            }
        }
        checkedStateList = newList
        val checkedDistances = items.filterIndexed { idx, _ -> checkedStateList[idx] }.map { it.distance }
        traveledDistance = checkedDistances.sum()

    }

    val totalDistance = items.sumOf { it.distance }
    val targetValue = if (showDistanceInMiles) {
        kmToMiles(traveledDistance/totalDistance).toFloat()
    } else {
        (traveledDistance / totalDistance).toFloat()
    }

    val progress by animateFloatAsState(targetValue = targetValue, label = "")

    Column {

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .width(4.dp)
                .height(4.dp)
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            for (index in checkedStateList.indices) {
                RowItem(
                    travelStop = items[index],
                    checked = checkedStateList[index],
                    onCheckedChange = { isChecked -> onCheckedChange(index, isChecked) },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }


        Spacer(modifier=Modifier.padding(bottom = 10.dp))

        if (showDistanceInMiles){
            Text("Total Distance: ${"%.2f".format(totalDistance)} Miles", fontWeight = FontWeight.Normal, fontSize = 20.sp)
            Text("Traveled Distance: ${"%.2f".format(kmToMiles(traveledDistance))} Miles", fontWeight = FontWeight.Normal, fontSize = 20.sp)
            Text("Remaining Distance: ${"%.2f".format(totalDistance-kmToMiles(traveledDistance))} Miles", fontWeight = FontWeight.Normal, fontSize = 20.sp)
        }
        else{
            Text("Total Distance: ${"%.2f".format(totalDistance)} Km", fontWeight = FontWeight.Normal, fontSize = 20.sp)
            Text("Traveled Distance: ${"%.2f".format(traveledDistance)} Km", fontWeight = FontWeight.Normal, fontSize = 20.sp)
            Text("Remaining Distance: ${"%.2f".format(totalDistance-traveledDistance)} Km", fontWeight = FontWeight.Normal, fontSize = 20.sp)
        }
        Text("bloody Travelled Distance: ${"%.2f".format(traveledDistance)} Km", fontWeight = FontWeight.Normal, fontSize = 20.sp)

    }
}
@Composable
fun RowItem(
    travelStop: TravelStop,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = Modifier.padding(6.dp)) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(2.dp)
            )
            Column {
                Text(text = travelStop.name, modifier = modifier)
                Text(text = "Distance: ${"%.2f".format(travelStop.distance)}")
            }
        }
    }
}

fun generateRandomDoubles(count: Int, minValue: Double, maxValue: Double): List<Double> {
    require(count >= 0) { "Count must be non-negative" }
    require(minValue < maxValue) { "minValue must be less than maxValue" }

    val random = Random.Default
    return List(count) {
        val randomInt = random.nextInt((minValue * 100).toInt(), (maxValue * 100).toInt() + 1)
        randomInt.toDouble() / 100
    }
}

@Composable
fun UnitRow(roundUp: Boolean, onRoundUpChanged:(Boolean)->Unit,modifier: Modifier=Modifier) {
    Row (
        modifier= modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Convert to Miles", style = MaterialTheme.typography.displaySmall, fontSize = 18.sp, fontStyle = FontStyle.Italic)
        Switch(
            checked = roundUp,
            onCheckedChange =onRoundUpChanged,
            modifier= modifier.wrapContentWidth(Alignment.End)
        )
    }
}

@Composable
fun Travel(randomDistances: List<Double>,modifier: Modifier = Modifier) {
    val travelStops = mutableListOf<TravelStop>()
    for (i in 0 until randomDistances.size) {
        val name = "TravelStop${i + 1}"
        val stop = TravelStop(i + 1, name, randomDistances[i])
        travelStops.add(stop)
    }

    var miles by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (miles) {
            convertDistanceToMiles(travelStops)
        }
        Spacer(modifier=Modifier.padding(bottom = 30.dp))
        Text(text="Delhi\nto\nBangalore",modifier=modifier.padding(8.dp), fontSize = 56.sp,textAlign = TextAlign.Start, fontWeight = FontWeight.Black)
        Spacer(modifier=Modifier.padding(bottom = 10.dp))
        Row(modifier= Modifier
            .padding(1.dp)
            .align(Alignment.End), horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "LazyList ")
            Image(painter = painterResource(id = R.drawable.direction), contentDescription = null,modifier=Modifier.size(40.dp))
        }
        ScrollableRow(items = travelStops, showDistanceInMiles = miles)

        Spacer(modifier=Modifier.padding(20.dp))
        Row(modifier= Modifier
            .padding(1.dp)
            .align(Alignment.End), horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "NormalList")
            Image(painter = painterResource(id = R.drawable.direction), contentDescription = null,modifier=Modifier.size(40.dp))
        }
        NormalRow(items = travelStops, showDistanceInMiles = miles)
        UnitRow(roundUp = miles, onRoundUpChanged ={miles=it},modifier=modifier)


    }

}


@Composable
fun TravelPreview(randomDistances: List<Double>,) {
    Travel(randomDistances,modifier=Modifier.fillMaxSize())
}
