package com.example.engineeringthesis.chart


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.engineeringthesis.others.Constants

class LeftFootView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
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
        var myLeftFoot: Array<IntArray> = arrayOf(
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
        myLeftFoot = assignDataToFoot(myLeftFoot, controllerData)
        myLeftFoot = FunctionsForFootView.calculateAverageForEmptyPoint(myLeftFoot)
        val paint = Paint()
        for (col in 0..numberOfCol) {
            for (row in 0..numberOfRow) {
                paint.color = FunctionsForFootView.createColors(myLeftFoot[row][numberOfCol - col])
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
        myLeftFoot: Array<IntArray>,
        dataFromSensors: IntArray,
    ): Array<IntArray> {
        myLeftFoot[1][2] = dataFromSensors[6]
        myLeftFoot[2][2] = dataFromSensors[6]
        myLeftFoot[1][4] = dataFromSensors[7]
        myLeftFoot[2][4] = dataFromSensors[7]
        myLeftFoot[4][1] = dataFromSensors[5]
        myLeftFoot[5][1] = dataFromSensors[5]
        myLeftFoot[4][4] = dataFromSensors[8]
        myLeftFoot[5][4] = dataFromSensors[8]
        myLeftFoot[4][7] = dataFromSensors[15]
        myLeftFoot[5][7] = dataFromSensors[15]
        myLeftFoot[7][3] = dataFromSensors[4]
        myLeftFoot[8][3] = dataFromSensors[4]
        myLeftFoot[7][5] = dataFromSensors[11]
        myLeftFoot[8][5] = dataFromSensors[11]
        myLeftFoot[7][7] = dataFromSensors[14]
        myLeftFoot[8][7] = dataFromSensors[14]
        myLeftFoot[11][3] = dataFromSensors[0]
        myLeftFoot[12][3] = dataFromSensors[0]
        myLeftFoot[11][5] = dataFromSensors[10]
        myLeftFoot[12][5] = dataFromSensors[10]
        myLeftFoot[11][7] = dataFromSensors[13]
        myLeftFoot[12][7] = dataFromSensors[13]
        myLeftFoot[15][2] = dataFromSensors[1]
        myLeftFoot[16][2] = dataFromSensors[1]
        myLeftFoot[15][4] = dataFromSensors[9]
        myLeftFoot[16][4] = dataFromSensors[9]
        myLeftFoot[15][6] = dataFromSensors[12]
        myLeftFoot[16][6] = dataFromSensors[12]
        myLeftFoot[19][2] = dataFromSensors[3]
        myLeftFoot[20][2] = dataFromSensors[3]
        myLeftFoot[19][4] = dataFromSensors[2]
        myLeftFoot[20][4] = dataFromSensors[2]
        return myLeftFoot
    }
}
