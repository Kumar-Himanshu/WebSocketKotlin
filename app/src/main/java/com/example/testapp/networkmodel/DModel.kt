package com.example.testapp.networkmodel


/**
 * Created by Kumar Himanshu t-kumar.himanshu@ocltp.com on 22/6/19.
 * Copyright (c) 2018 Paytm. All rights reserved.
 */
data class DModel(val connId: String,val serverInterval: Int,val serverAttempts: Int,val clientInterval: Int,
                  val clientAttempts:Int,val topic: String,val event: String, val data: DataModel)