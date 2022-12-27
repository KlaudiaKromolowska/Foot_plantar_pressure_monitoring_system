package com.example.engineeringthesis.chart


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.engineeringthesis.others.Constants

class RightFootView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    lateinit var sharedPreferences: SharedPreferences

    private var controllerData = IntArray(16)
    private var originX = Constants.originX
    private var originY = Constants.originY
    private var cellSide = Constants.cellSide
    private val numberOfRow = Constants.numberOfRow
    private val numberOfCol = Constants.numberOfCol

    fun upDateControllerData(controllerData: IntArray) {
        this.controllerData = controllerData
        invalidate()
    }

    /**
     * onDraw creates view of foot
     */
    override fun onDraw(canvas: Canvas?) {
        var myRightFoot: Array<IntArray> = arrayOf(
            intArrayOf(0, 1, 1, 1, 1, 0, 0, 0, 0, 0),
            intArrayOf(0, 1, 2, 1, 2, 1, 0, 0, 0, 0),
            intArrayOf(1, 1, 2, 1, 2, 1, 1, 0, 0, 0),
            intArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
            intArrayOf(1, 2, 1, 1, 2, 1, 1, 2, 1, 0),
            intArrayOf(1, 2, 1, 1, 2, 1, 1, 2, 1, 0),
            intArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
            intArrayOf(0, 1, 1, 2, 1, 2, 1, 2, 1, 1),
            intArrayOf(0, 1, 1, 2, 1, 2, 1, 2, 1, 1),
            intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0),
            intArrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0),
            intArrayOf(0, 0, 0, 2, 1, 2, 1, 2, 0, 0),
            intArrayOf(0, 0, 0, 2, 1, 2, 1, 2, 0, 0),
            intArrayOf(0, 0, 0, 1, 1, 1, 1, 1, 0, 0),
            intArrayOf(0, 0, 1, 1, 1, 1, 1, 0, 0, 0),
            intArrayOf(0, 0, 2, 1, 2, 1, 2, 0, 0, 0),
            intArrayOf(0, 1, 2, 1, 2, 1, 2, 0, 0, 0),
            intArrayOf(0, 1, 1, 1, 1, 1, 1, 0, 0, 0),
            intArrayOf(0, 1, 1, 1, 1, 1, 1, 0, 0, 0),
            intArrayOf(0, 1, 2, 1, 2, 1, 0, 0, 0, 0),
            intArrayOf(0, 1, 2, 1, 2, 1, 0, 0, 0, 0),
            intArrayOf(0, 0, 1, 1, 1, 0, 0, 0, 0, 0)
        )
        if (controllerData.all { it == 0 }) {
            controllerData = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
        }
        myRightFoot = assignDataToFoot(myRightFoot, controllerData)
        myRightFoot = FunctionsForFootView.calculateAverageForEmptyPoint(myRightFoot)
        val paint = Paint()
        for (row in 0..numberOfRow) {
            for (col in 0..numberOfCol) {
                paint.color = FunctionsForFootView.createColors(myRightFoot[row][col])
                canvas?.drawRect(
                    originX + col * cellSide,
                    originY + row * cellSide,
                    originX + (col + 1) * cellSide,
                    originY + (row + 1) * cellSide,
                    paint
                )
            }

        }
    }


    /**
     * assignDataToFoot function updates table 'myLeftFoot' - insert data from microcontroller,
     * return new 'myLeftFoot' table
     */
    private fun assignDataToFoot(
        myRightFoot: Array<IntArray>,
        dataFromSensors: IntArray,
    ): Array<IntArray> {
        myRightFoot[1][2] = dataFromSensors[6]
        myRightFoot[2][2] = dataFromSensors[6]
        myRightFoot[1][4] = dataFromSensors[7]
        myRightFoot[2][4] = dataFromSensors[7]
        myRightFoot[4][1] = dataFromSensors[5]
        myRightFoot[5][1] = dataFromSensors[5]
        myRightFoot[4][4] = dataFromSensors[8]
        myRightFoot[5][4] = dataFromSensors[8]
        myRightFoot[4][7] = dataFromSensors[15]
        myRightFoot[5][7] = dataFromSensors[15]
        myRightFoot[7][3] = dataFromSensors[4]
        myRightFoot[8][3] = dataFromSensors[4]
        myRightFoot[7][5] = dataFromSensors[11]
        myRightFoot[8][5] = dataFromSensors[11]
        myRightFoot[7][7] = dataFromSensors[14]
        myRightFoot[8][7] = dataFromSensors[14]
        myRightFoot[11][3] = dataFromSensors[0]
        myRightFoot[12][3] = dataFromSensors[0]
        myRightFoot[11][5] = dataFromSensors[10]
        myRightFoot[12][5] = dataFromSensors[10]
        myRightFoot[11][7] = dataFromSensors[13]
        myRightFoot[12][7] = dataFromSensors[13]
        myRightFoot[15][2] = dataFromSensors[1]
        myRightFoot[16][2] = dataFromSensors[1]
        myRightFoot[15][4] = dataFromSensors[9]
        myRightFoot[16][4] = dataFromSensors[9]
        myRightFoot[15][6] = dataFromSensors[12]
        myRightFoot[16][6] = dataFromSensors[12]
        myRightFoot[19][2] = dataFromSensors[3]
        myRightFoot[20][2] = dataFromSensors[3]
        myRightFoot[19][4] = dataFromSensors[2]
        myRightFoot[20][4] = dataFromSensors[2]
        return myRightFoot
    }
}
