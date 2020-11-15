package com.example

data class Course (val id: Int,
                   val title: String,
                   val complexity: Int = 0,
                   var active: Boolean = false)