package ru.ddstudio.voicerecording.extensions

fun Long.toStringTime() : String{
    return String.format("%02d:%02d:%02d", this / 1000 / 3600, this / 1000 / 60 % 60, this / 1000 % 60)
}