package com.netsensia.rivalchess.config

const val FRACTIONAL_EXTENSION_FULL = 12
const val FRACTIONAL_EXTENSION_THREAT = 12
const val FRACTIONAL_EXTENSION_CHECK = 12
const val MAX_FRACTIONAL_EXTENSIONS = MAX_EXTENSION_DEPTH * FRACTIONAL_EXTENSION_FULL

const val LAST_EXTENSION_LAYER = 4

val maxNewExtensionsTreePart = intArrayOf(12, 6, 3, 0, 0)
