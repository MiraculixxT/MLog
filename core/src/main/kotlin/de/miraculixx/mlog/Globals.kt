package de.miraculixx.mlog

import de.miraculixx.mlog.utils.cHighlight
import de.miraculixx.mlog.utils.cmp
import java.io.File
import java.util.logging.Logger

lateinit var configFolder: File
val prefix = cmp("MLog", cHighlight) + cmp(" >>", "#555555") + cmp(" ")
lateinit var LOGGER: Logger

const val permissionCode = "mlog.send"