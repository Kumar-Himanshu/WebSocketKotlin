package com.example.testapp.networkmodel


/**
 * Created by Kumar Himanshu t-kumar.himanshu@ocltp.com on 22/6/19.
 * Copyright (c) 2018 Paytm. All rights reserved.
 */
data class DataModel(val imei: Long,
val device_type: String,
val date_time_data: String,
val lat: Double,
val lng:Double,
val speed: Int,
val hdop:Int,
val pdop: Int,
val direction: Int,
val sat_count: Int,
val odometer:Long,
val ignition_status: Int,
val x_axis: Int,
val y_axis : Int,
val z_axis: Int,
val server_datetime: String,
val total_distance: Float
)

//val io_data: {
//
//},