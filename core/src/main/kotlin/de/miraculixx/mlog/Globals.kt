package de.miraculixx.mlog

import de.miraculixx.mlog.utils.cHighlight
import de.miraculixx.mlog.utils.cmp
import java.io.File

lateinit var configFolder: File
val prefix = cmp("MLog", cHighlight) + cmp(" >>", "#555555") + cmp(" ")

val permissionCode = "mlog.send"