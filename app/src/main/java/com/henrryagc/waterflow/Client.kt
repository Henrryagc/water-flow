package com.henrryagc.waterflow

import java.io.Serializable

data class Client (
        var idClient: Int,
        val fullName: String,
        val mz: String,
        val lt: String,
        val date: String,
        val cylinder: String,
        val bucket: String,
        val total: String
        ) : Serializable