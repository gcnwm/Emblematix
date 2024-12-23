package cn.ac.lz233.emblematix.util.ktx

import android.annotation.SuppressLint
import androidx.exifinterface.media.ExifInterface
import cn.ac.lz233.emblematix.logic.dao.ConfigDao
import cn.ac.lz233.emblematix.util.LogUtil
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

fun ExifInterface.getManufacturer() = getAttribute(ExifInterface.TAG_MAKE)

fun ExifInterface.getModel(): String? {
    var model = getAttribute(ExifInterface.TAG_MODEL)
    if (model != null) {
        if (model.contains("NIKON")) {
            model = model.replace("_2", "Ⅱ")
            model = model.replace("_3", "Ⅲ")
        }
    }
    return model
}
fun ExifInterface.getLensModel() = getAttribute(ExifInterface.TAG_LENS_MODEL)

fun ExifInterface.getDevice() = StringBuilder().apply {
//    if (ConfigDao.showManufacturer && getManufacturer() != null) append("${getManufacturer()} ")
    if (ConfigDao.showModel && getModel() != null) append("${getModel()}")
    if (ConfigDao.showModel && ConfigDao.showLens && getModel() != null && getLensModel() != null) append(" | ")
    if (ConfigDao.showLens && getLensModel() != null) append("${getLensModel()}")
}.toString().trim()

fun ExifInterface.getFNumber() = getAttribute(ExifInterface.TAG_F_NUMBER)

fun ExifInterface.getShutterSpeed() = getAttribute(ExifInterface.TAG_EXPOSURE_TIME)?.let {
    if (it.toDouble() >= 1) it else "1/${(1 / it.toDouble()).toInt()}"
}

fun ExifInterface.getFocalLength() = getAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM) ?: getAttribute(ExifInterface.TAG_FOCAL_LENGTH)?.let {
    val parts = it.split('/')
    if (parts.size == 2) {
        (parts[0].toFloat() / parts[1].toFloat()).toString().run {
            safeSubString(0, this.indexOf('.') + 3).run {
                if (last() == '.') {
                    substring(0, lastIndex)
                } else {
                    this
                }
            }
        }
    } else {
        it
    }
}

fun ExifInterface.getISO() = getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY)

fun ExifInterface.getPhotoInfo() = StringBuilder().apply {
//    if (ConfigDao.showFNumber && getFNumber() != null) append("ƒ/${getFNumber()} • ")
//    if (ConfigDao.showShutterSpeed && getShutterSpeed() != null) append("${getShutterSpeed()} • ")
//    if (ConfigDao.showFocalLength && getFocalLength() != null) append("${getFocalLength()}mm • ")
//    if (ConfigDao.showISO && getISO() != null) append("ISO${getISO()}")
    if (ConfigDao.showPhotoInfo) {
        if (getFNumber() != null) append("ƒ/${getFNumber()} • ")
        if (getShutterSpeed() != null) append("${getShutterSpeed()} • ")
        if (getFocalLength() != null) append("${getFocalLength()}mm • ")
        if (getISO() != null) append("ISO${getISO()}")
    }
    if (endsWith(" • ")) delete(length - 3, length - 1)
}.toString()

fun ExifInterface.getAuthor() = if (ConfigDao.copyright != "") {
    ConfigDao.copyright
} else if (getAttribute(ExifInterface.TAG_COPYRIGHT) != null) {
    getAttribute(ExifInterface.TAG_COPYRIGHT) ?: ""
} else {
    ""
}

@SuppressLint("RestrictedApi")
fun ExifInterface.getCopyRight() = StringBuilder().apply {
    val dateTime = dateTimeOriginal?.let {
        SimpleDateFormat("yyyy|MM/dd |HH:mm:ss", Locale.getDefault()).format(Date(it))
    }?.split('|')
    val author = getAuthor()
    if (ConfigDao.showDate && dateTime != null) append("${dateTime[1]}  ")
    if (ConfigDao.showTime && dateTime != null) append("${dateTime[2]}  ")
    if (ConfigDao.showCopyright) {
        if (author != "") {
            append("Image © ")
            if (dateTime != null) append("${dateTime[0]} ")
//            append("${author}.")
        }
    }
}.toString().trim()