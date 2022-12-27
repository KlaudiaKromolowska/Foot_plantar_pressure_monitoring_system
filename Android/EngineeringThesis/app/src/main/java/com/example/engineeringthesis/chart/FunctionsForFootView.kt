package com.example.engineeringthesis.chart

import android.graphics.Color
import com.example.engineeringthesis.others.Constants

object FunctionsForFootView {
//    val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    /**
     * createColors - use if color for square point is needed.
     * input 'n' - it is data from square point
     * return color code for square point
     */
    fun createColors(n: Int): Int {
        val tableOfColors: Array<String> = Constants.colors
        return when (n) {
            0 -> Color.parseColor("#f6e9f2")
            in 1..5 -> Color.parseColor(tableOfColors[0])
            in 6..10 -> Color.parseColor(tableOfColors[1])
            in 11..15 -> Color.parseColor(tableOfColors[2])
            in 16..20 -> Color.parseColor(tableOfColors[3])
            in 21..25 -> Color.parseColor(tableOfColors[4])
            in 26..30 -> Color.parseColor(tableOfColors[5])
            in 31..35 -> Color.parseColor(tableOfColors[6])
            in 36..40 -> Color.parseColor(tableOfColors[7])
            in 41..45 -> Color.parseColor(tableOfColors[8])
            in 26..50 -> Color.parseColor(tableOfColors[9])
            else -> Color.BLACK
        }
    }

    /**
     * calculateAverageForEmptyPoint function is calculating average from 8 nearest neighbours of point
     */
    fun calculateAverageForEmptyPoint(myFoot: Array<IntArray>): Array<IntArray> {
        val numberOfRows = Constants.numberOfRow - 1
        val numberOfCols = Constants.numberOfCol - 1
        val tableMyFoot = myFoot.clone()

        for (r in 0..Constants.numberOfRow) {
            for (c in 0..Constants.numberOfCol) {
                if (tableMyFoot[r][c] == 1) {
                    var sum = 0
                    var num = 0
                    if (r - 1 in 0..numberOfRows && c - 1 in 0..numberOfCols) {
                        if (tableMyFoot[r - 1][c - 1] != 1 && tableMyFoot[r - 1][c - 1] != 0) {
                            sum += tableMyFoot[r - 1][c - 1]
                            num += 1
                        }
                    }
                    if (r - 1 in 0..numberOfRows && c in 0..numberOfCols) {
                        if (tableMyFoot[r - 1][c] != 1 && tableMyFoot[r - 1][c] != 0) {
                            sum += tableMyFoot[r - 1][c]
                            num += 1
                        }
                    }
                    if (r in 0..numberOfRows && c - 1 in 0..numberOfCols) {
                        if (tableMyFoot[r][c - 1] != 1 && tableMyFoot[r][c - 1] != 0) {
                            sum += tableMyFoot[r][c - 1]
                            num += 1
                        }
                    }
                    if (r - 1 in 0..numberOfRows && c + 1 in 0..numberOfCols) {
                        if (tableMyFoot[r - 1][c + 1] != 1 && tableMyFoot[r - 1][c + 1] != 0) {
                            sum += tableMyFoot[r - 1][c + 1]
                            num += 1
                        }
                    }
                    if (r in 0..numberOfRows && c + 1 in 0..numberOfCols) {
                        if (tableMyFoot[r][c + 1] != 1 && tableMyFoot[r][c + 1] != 0) {
                            sum += tableMyFoot[r][c + 1]
                            num += 1
                        }
                    }
                    if (r + 1 in 0..numberOfRows && c - 1 in 0..numberOfCols) {
                        if (tableMyFoot[r + 1][c - 1] != 1 && tableMyFoot[r + 1][c - 1] != 0) {
                            sum += tableMyFoot[r + 1][c - 1]
                            num += 1
                        }
                    }
                    if (r + 1 in 0..numberOfRows && c in 0..numberOfCols) {
                        if (tableMyFoot[r + 1][c] != 1 && tableMyFoot[r + 1][c] != 0) {
                            sum += tableMyFoot[r + 1][c]
                            num += 1
                        }
                    }
                    if (r + 1 in 0..numberOfRows && c + 1 in 0..numberOfCols) {
                        if (tableMyFoot[r + 1][c + 1] != 1 && tableMyFoot[r + 1][c + 1] != 0) {
                            sum += tableMyFoot[r + 1][c + 1]
                            num += 1
                        }
                    }
                    if (num != 0) {
                        tableMyFoot[r][c] = sum / num
                    } else tableMyFoot[r][c] = 1
                }
            }
        }
        return tableMyFoot
    }
}