package dev.baseio.composeplayground.ui.animations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.baseio.composeplayground.R
import dev.baseio.composeplayground.contributors.AnmolVerma
import dev.baseio.composeplayground.ui.theme.Typography
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


val offGray = Color(45, 44, 46)
val textSecondary = Color(157, 156, 167)


@Preview
@Composable
fun PreviewIOSSleepSchedule() {
  MaterialTheme() {
    IOSSleepSchedule()
  }
}

@Composable
fun IOSSleepSchedule() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(offGray)
  ) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Spacer(modifier = Modifier.padding(16.dp))
      Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
        VerticalGroupTime(isStart = true)
        VerticalGroupTime(isStart = false)
      }

      Spacer(modifier = Modifier.padding(28.dp))


      TouchMoveControlTrack()

      Spacer(modifier = Modifier.padding(28.dp))

      Text(
        text = "8 hr",
        style = Typography.h5.copy(color = Color.White)
      )


      Spacer(modifier = Modifier.padding(8.dp))

      Text(
        text = "This schedule meets your sleep goal.",
        style = Typography.subtitle1.copy(color = textSecondary)
      )

    }

    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
    ) {
      AnmolVerma(Modifier.align(Alignment.Center))
    }

  }
}

@Composable
private fun TouchMoveControlTrack() {
  val constraintsScope = rememberCoroutineScope()

  val clockRadius = with(LocalDensity.current) {
    LocalConfiguration.current.screenWidthDp.div(3.5).dp.toPx()
  }

  val knobTrackStrokeWidth = with(LocalDensity.current) {
    LocalConfiguration.current.screenWidthDp.div(6).dp.toPx()
  }

  val knobStrokeWidth = with(LocalDensity.current) {
    LocalConfiguration.current.screenWidthDp.div(8).dp.toPx()
  }

  var shapeCenter by remember {
    mutableStateOf(Offset.Zero)
  }

  var angle by remember {
    mutableStateOf(0.0)
  }

  var startIconOffset by remember {
    mutableStateOf(Offset.Zero)
  }

  var endIconOffset by remember {
    mutableStateOf(Offset.Zero)
  }

  val reduceOffsetIcon = with(LocalDensity.current) {
    24.dp.toPx()
  }


  val sweepAngleForKnow = remember {
    mutableStateOf(120f)
  }


  Box(Modifier) {
    Canvas(modifier = Modifier
      .size(300.dp), onDraw = {
      drawKnobBackground(knobTrackStrokeWidth)
      drawClockCircle(clockRadius)
    })

    Canvas(modifier = Modifier
      .size(300.dp)
      .pointerInput(Unit) {
        constraintsScope.launch {
          detectDragGestures(onDrag = { change, dragAmount ->
            startIconOffset += dragAmount
            angle = getRotationAngle(startIconOffset, shapeCenter)
            change.consumeAllChanges()
          })
        }
      }, onDraw = {
      shapeCenter = center

      val radius = size.minDimension / 2

      val startIconX = (shapeCenter.x + cos(Math.toRadians(angle)) * radius).toFloat()
      val startIconY = (shapeCenter.y + sin(Math.toRadians(angle)) * radius).toFloat()

      val endIconX =
        (shapeCenter.x + cos(Math.toRadians(angle + sweepAngleForKnow.value)) * radius).toFloat()
      val endIconY =
        (shapeCenter.x + sin(Math.toRadians(angle + sweepAngleForKnow.value)) * radius).toFloat()

      startIconOffset = Offset(startIconX, startIconY)
      endIconOffset = Offset(endIconX, endIconY)
      drawRotatingKnob(angle, knobStrokeWidth, sweepAngleForKnow.value)

    })

    Box(Modifier.size(300.dp)) {
      StartIcon(startIconOffset, reduceOffsetIcon)

      EndIcon(endIconOffset, reduceOffsetIcon)
    }
  }

}

@Composable
private fun EndIcon(
  endIconOffset: Offset,
  reduceOffsetIcon: Float
) {
  SleepBedTimeIcon(false,
    Modifier
      .padding(0.dp)
      .offset {
        IntOffset(
          endIconOffset.x
            .toInt()
            .minus(reduceOffsetIcon / 2)
            .toInt(),
          endIconOffset.y
            .toInt()
            .minus(reduceOffsetIcon / 2)
            .toInt()
        )
      })
}

@Composable
private fun StartIcon(
  startIconOffset: Offset,
  reduceOffsetIcon: Float
) {
  SleepBedTimeIcon(true,
    Modifier
      .padding(0.dp)
      .offset {
        IntOffset(
          startIconOffset.x
            .toInt()
            .minus(reduceOffsetIcon / 2)
            .toInt(),
          startIconOffset.y
            .toInt()
            .minus(reduceOffsetIcon / 2)
            .toInt()
        )
      })
}

private fun DrawScope.drawClockCircle(clockRadius: Float) {
  drawCircle(color = offGray, radius = clockRadius)
}

private fun DrawScope.drawKnobBackground(knobTrackStrokeWidth: Float) {
  drawArc(
    Color(1, 0, 0), 0f, 360f,
    useCenter = true, style = Stroke(width = knobTrackStrokeWidth)
  )
}

private fun DrawScope.drawRotatingKnob(angle: Double, knobStrokeWidth: Float, value: Float) {
  drawArc(
    offGray,
    angle.toFloat(),
    value,
    false,
    style = Stroke(width = knobStrokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
  )
}

@Composable
fun VerticalGroupTime(isStart: Boolean) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
      SleepBedTimeIcon(isStart, Modifier)
      Spacer(modifier = Modifier.padding(end = 2.dp))
      Text(
        text = if (isStart) "BEDTIME" else "WAKE UP",
        style = Typography.subtitle2.copy(color = textSecondary)
      )
    }
    Spacer(modifier = Modifier.padding(top = 2.dp))
    Text(
      text = if (isStart) "9:30 PM" else "5:30 AM",
      style = Typography.h5.copy(color = Color.White)
    )
    Spacer(modifier = Modifier.padding(top = 2.dp))
    Text(
      text = if (isStart) "Today" else "Tomorrow",
      style = Typography.subtitle2.copy(color = textSecondary)
    )
  }
}

@Composable
private fun SleepBedTimeIcon(isStart: Boolean, modifier: Modifier = Modifier) {
  Icon(
    painter = painterResource(isStart),
    tint = textSecondary,
    contentDescription = null, modifier = modifier
  )
}

@Composable
private fun painterResource(isStart: Boolean) =
  if (isStart) painterResource(id = R.drawable.ic_bed) else painterResource(id = R.drawable.ic_alarm)

private fun getRotationAngle(currentPosition: Offset, center: Offset): Double {
  val theta = radians(currentPosition, center)

  var angle = Math.toDegrees(theta)

  if (angle < 0) {
    angle += 360.0
  }
  return angle
}

private fun radians(
  currentPosition: Offset,
  center: Offset
): Double {
  val (dx, dy) = currentPosition - center
  return atan2(dy, dx).toDouble()
}